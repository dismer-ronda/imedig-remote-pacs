package es.pryades.imedig.cloud.submodules.centers;

import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class CentersView extends VerticalLayout 
{
    private static final Logger LOG = Logger.getLogger( CentersView.class );

    private static final long serialVersionUID = 2648127876428772910L;

	private ImedigContext ctx;
	
	private CssLayout centerLayout;
	
	private Refresher refresher;
	private HashMap<Integer, Long> lastsChecked;
	
	private HashMap<Integer, CenterCard> cards;
	private HashMap<Integer, Boolean> availability;
	private HashMap<Integer, Boolean> reports;
	
	private DetallesCentrosManager centrosManager;
	private InformesManager informesManager;
	
	@Setter
	@Getter
	private ListenerCenterSelect listenerCenterSelect;
	
	private CheckAvailability thread;
	
	public CentersView(ImedigContext ctx)
	{
		this.ctx 		= ctx;
		
		centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );
		informesManager = (InformesManager)IOCManager.getInstanceOf( InformesManager.class );

		cards = new HashMap<Integer, CenterCard>();
		
		availability = new HashMap<Integer, Boolean>();
		lastsChecked = new HashMap<Integer, Long>();	
		reports = new HashMap<Integer, Boolean>();
		
		for ( DetalleCentro centro : ctx.getCentros() )
		{
			reports.put( centro.getId(),  Boolean.FALSE );
			lastsChecked.put( centro.getId(), Utils.getTodayFirstSecondAsLong( centro.getHorario_nombre() ) );
		}
		
		setWidth( "100%" );
		setSpacing( false );
		
		addComponent( buildTop() );
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( false );
		layout.setWidth( "100%" );
		addComponent( layout );
		
		centerLayout = new CssLayout();
		buildCentersCards();
		centerLayout.setWidth( "100%" );
		
		layout.addComponent( centerLayout );
		
		refresher = new Refresher();
		
		refresher.setRefreshInterval( (int)(3 * Utils.ONE_SECOND) );
		refresher.addListener( new RefreshListener()
		{
			private static final long serialVersionUID = 7662179976731342251L;
			@Override
			public void refresh( Refresher source )
			{
				onRefresh();

				refresher.setRefreshInterval( (int)(30 * Utils.ONE_SECOND) );
			}
		} );

		addExtension( refresher );

		thread = new CheckAvailability();
		thread.start();
	}
	
	private Component buildTop()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing( true );
		layout.setWidth( "100%" );
		
		return layout;
	}
	
	private void buildCentersCards()
	{
		centerLayout.removeAllComponents();
		
		synchronized ( availability )
		{
			for ( DetalleCentro centro : ctx.getCentros() ) 
			{
				CenterCard card = new CenterCard( centro, ctx, this );
				
				card.setAvailable( availability.get( centro.getId() ), false );
			
				card.addLayoutClickListener(new LayoutClickListener()
				{
					private static final long serialVersionUID = 2464291628303745172L;
	
					@Override
					public void layoutClick( LayoutClickEvent event )
					{
						Centro centro = ((CenterCard)event.getComponent()).getCentro();
						
						boolean available = availability.get( centro.getId() ) != null && availability.get( centro.getId() );
						
						if ( available )
							listenerCenterSelect.centerSelected( centro.getId() );
						else
						{
							Notification.show( ctx.getString( "error.unnavailable" ), Notification.Type.WARNING_MESSAGE );
						}
						
					}
				} );
				
				centerLayout.addComponent( card );
				
				cards.put( centro.getId(), card );
			}
		}
	}
	
	private void checkNewReports()
	{
		SqlSession session = null;
		
		ImedigContext newContext = new ImedigContext();
		
		try
		{
			session = newContext.getSessionCloud();
			
			for ( DetalleCentro centro : ctx.getCentros() ) 
			{
				if ( !reports.get( centro.getId() ).booleanValue() )
				{
					InformeQuery query = new InformeQuery();
				
					query.setCentro( centro.getId() );
					query.setFecha( lastsChecked.get( centro.getId() ) );
					
					if ( ctx.hasRight( "informes.aprobar" ) )
						query.setEstados( Arrays.asList( new Integer[]{0,1} ) );
					else
						query.setEstado( 2 );
					
					Informe informe = (Informe)informesManager.getNextRow( newContext, query );
					
					reports.put( centro.getId(), informe != null );
				}
			}	
		}
		catch ( Throwable e )
		{
		}
		finally
		{
			if ( session != null )
			{
				try
				{
					newContext.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	 }

	private void onRefresh()
	{
		synchronized ( availability )
		{
			checkNewReports();
			
			for ( DetalleCentro centro : ctx.getCentros() )
				cards.get( centro.getId() ).setAvailable( availability.get( centro.getId() ), reports.get( centro.getId() ) );
		}
		
		if ( !thread.isAlive() )
		{
			thread = new CheckAvailability();
			thread.start();
		}
	}
	
	
	public void notifyShowReports()
	{
		for ( DetalleCentro centro : ctx.getCentros() )
		{
			reports.put( centro.getId(), Boolean.FALSE );
			cards.get( centro.getId() ).setAvailable( availability.get( centro.getId() ), false );
			lastsChecked.put( centro.getId(), Utils.getTodayAsLong( centro.getHorario_nombre() ) );
		}
	}
	
	class CheckAvailability extends Thread 
	{
	    public void run () 
	    {
			LOG.debug( "checking centers availability starting " );
			
			SqlSession session = null;
			
			/*
			 * Cada hilo debe usar su propio contexto, de lo contrario habria que sincronizar con el contexto
			 * y provocar interbloqueos. 
			 */
			ImedigContext newContext = new ImedigContext();
			
			try
			{
				session = newContext.getSessionCloud();
				
				for ( DetalleCentro centro : ctx.getCentros() ) 
				{
					boolean available = centrosManager.echoCenter( newContext, centro );

					synchronized ( availability )
					{
						availability.put( centro.getId(), available );
					}
				}
			}
			catch ( Throwable e )
			{
			}
			finally
			{
				if ( session != null )
				{
					try
					{
						newContext.closeSessionCloud();
					}
					catch ( ImedigException e )
					{
					}
				}
			}
			
			LOG.debug( "checking centers availabitit done" );
	    }
	}
}
