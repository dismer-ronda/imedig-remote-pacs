package es.pryades.imedig.cloud.modules.Configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.modules.Configuration.tabs.CentrosConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.EquiposConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.ImagenesConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.InformesPlantillasConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.StudiesListTab;
import es.pryades.imedig.cloud.modules.Configuration.tabs.TiposEstudiosConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.TiposHorariosConfig;
import es.pryades.imedig.cloud.modules.Configuration.tabs.UsuariosConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Dismer Ronda
 * 
 */
@Setter
@Getter
public class ImedigTabbedContainerConfig implements TabSheet.SelectedTabChangeListener
{
	private static final long serialVersionUID = -4520860282820394060L;

	private static final Logger LOG = Logger.getLogger( ImedigTabbedContainerConfig.class );

	private ImedigContext context;
	private Layout mainLayout;

	private VerticalLayout content;
	private TabSheet tabsheet;
	private List<FilteredContent> tabContentList;
	
	private final ClickListener closeListener;
	
	public ImedigTabbedContainerConfig(ClickListener closeListener){
		this.closeListener = closeListener;
	}
	
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab( tabsheet.getSelectedTab() );
		
		FilteredContentCloseable configuration = (FilteredContentCloseable)tab.getComponent();
		configuration.initComponents(closeListener);
	}

	private void initComponents() throws ImedigException
	{
		try
		{
			ImedigContext ctx = getContext();

			if ( content == null )
			{
				content = new VerticalLayout();
			}

			if ( tabsheet == null )
				tabsheet = new TabSheet();

			if ( tabContentList == null )
			{
				tabContentList = new ArrayList<FilteredContent>();

				if ( ctx.hasRight( "configuracion.centros" ) )
				{
					tabContentList.add( new CentrosConfig( ctx ) );
				}

				if ( ctx.hasRight( "configuracion.imagenes" ) )
				{
					tabContentList.add( new ImagenesConfig( ctx ) );
				}

				if ( ctx.hasRight( "configuracion.usuarios" ) )
				{
					tabContentList.add( new UsuariosConfig( ctx ) );
				}

				if ( ctx.hasRight( "configuracion.informes.plantillas" ) )
				{
					tabContentList.add( new InformesPlantillasConfig( ctx ) );
				}

				if ( ctx.hasRight( "configuracion.tipos.estudios" ) )
				{
					tabContentList.add( new TiposEstudiosConfig( ctx ) );
				}
				if ( ctx.hasRight( "configuracion.tipos.horarios" ) )
				{
					tabContentList.add( new TiposHorariosConfig( ctx ) );
				}
				
				if ( ctx.hasRight( "configuracion.instalaciones" ) )
				{
					tabContentList.add( new EquiposConfig( ctx ) );
				}
				
				tabContentList.add( new StudiesListTab( ctx ) );

				for ( FilteredContent item : tabContentList )
				{
					item.setSizeFull();
					tabsheet.addTab( item );
					tabsheet.getTab( item ).setCaption( item.getTabTitle() );
				}

				// Inicilizando y seleccionado el rpimer tab
				FilteredContentCloseable configuration = (FilteredContentCloseable)tabContentList.get( 0 );
				configuration.initComponents(closeListener);
				tabsheet.setSelectedTab( configuration );
				tabsheet.addSelectedTabChangeListener( this );
			}
		}
		catch ( Exception exc )
		{
			throw new ImedigException( exc, LOG, ImedigException.UNKNOWN );
		}
	}

	public void render() throws ImedigException
	{
		this.initComponents();

		content.setWidth( "100%" );
		content.setHeight( "100%" );
		//content.setMargin( true );
		content.setSpacing( false );
		content.setSizeFull();

		tabsheet.setSizeFull();

		content.addComponent( tabsheet );
		content.setExpandRatio( tabsheet, 1.0f );

		getMainLayout().removeAllComponents();
		getMainLayout().addComponent( content );

		((VerticalLayout)getMainLayout()).setHeight( "100%" );
		((VerticalLayout)getMainLayout()).setExpandRatio( content, 1.0f );
	}
}
