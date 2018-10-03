package es.pryades.imedig.cloud.modules.Administration.tabs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.AccesoQuery;
import es.pryades.imedig.cloud.vto.AccesosVto;
import es.pryades.imedig.cloud.vto.controlers.AccesosControlerVto;
import es.pryades.imedig.cloud.vto.refs.AccesosVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class AccesosAdmin extends AdminContent implements ModalParent, Property.ValueChangeListener
{
	private static final long serialVersionUID = 8504187372601306700L;
	
	private TableImedigPaged tableResultadoBusquedaAccesos;
	private String[] visibleCols;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public AccesosAdmin( ImedigContext ctx )
	{
		super( ctx );
		
		visibleCols = AccesosControlerVto.getVisibleCols();

		try
		{
			initComponents();
		}
		catch ( ImedigException e )
		{
		}
	}

	/**
	 * 
	 * 
	 */
	public AccesosAdmin()
	{
		super();
		visibleCols = AccesosControlerVto.getVisibleCols();

		try
		{
			initComponents();
		}
		catch ( ImedigException e )
		{
		}
	}

	/**
	 * 
	 * 
	 */
	@Override
	protected void initComponents() throws ImedigException
	{
		super.initComponents();

		super.cellDispositivo.setVisible( false );

		tabTitle = getContext().getString( "accesosAdmin.tabName" );

		addButtonApplyFilterClickListener();

		HashMap<String, String> tableHeadersName = new HashMap<String, String>();

		for ( String atribute : visibleCols )
		{
			tableHeadersName.put( atribute, getContext().getString( "accesosAdmin.tableAccesosAdmin.headerName." + atribute ) );
		}

		tableResultadoBusquedaAccesos = new TableImedigPaged( AccesosVto.class, new AccesosVto(), new AccesosVtoFieldRef(), new QueryFilterRef( new AccesoQuery() ), context, Constants.DEFAULT_PAGE_SIZE );

		tableResultadoBusquedaAccesos.setMargin( new MarginInfo( false, true, true, true ) );
		tableResultadoBusquedaAccesos.setTableHeadersNames( tableHeadersName );
		tableResultadoBusquedaAccesos.setVisibleCols( new ArrayList<String>( Arrays.asList( visibleCols ) ) );
		tableResultadoBusquedaAccesos.getTable().addValueChangeListener( this );
		tableResultadoBusquedaAccesos.initializeTable();
		tableResultadoBusquedaAccesos.getTablePageOppContainer().setMargin( false );

		adminCntResultLayout.addComponent( tableResultadoBusquedaAccesos );
		adminCntResultLayout.setExpandRatio( tableResultadoBusquedaAccesos, 1.0f );
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public TableImedigPaged getTableValues()
	{
		return tableResultadoBusquedaAccesos;
	}

	// ###########################################################################################//
	// # EVENT(S) #//
	// ###########################################################################################//

	public void valueChange( ValueChangeEvent event )
	{
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	protected void addPopUpDateSelectedValueChangerListener()
	{
		popUpDateSelected.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -6673189366165877605L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
			}
		} );
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	protected void addComboBoxDispoValueChangerListener()
	{
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	public void addButtonApplyFilterClickListener()
	{
		bttnAplicaFiltro.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7888031501683659302L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				String validateResult = validateBeforeApplyFilters();

				if ( validateResult.compareTo( "" ) == 0 )
				{
					refreshVisibleContent();
				}
				else
				{
					Notification.show( validateResult, Notification.Type.ERROR_MESSAGE );
				}
			}
		} );

	}

	// ###########################################################################################//
	// # OTHER METHODS #//
	// ###########################################################################################//

	public void refreshVisibleContent( List<ImedigDto> listadoElementos )
	{

	}

	/**
	 * 
	 * 
	 */
	@Override
	public void refreshVisibleContent()
	{
		applySelectedFilters();

		try
		{
			tableResultadoBusquedaAccesos.refreshVisibleContent( tableResultadoBusquedaAccesos.getCurrPage() );
		}
		catch ( Throwable e )
		{

		}
	}

	public Query applySelectedFilters()
	{
		Query queryObj = null;
		QueryFilterRef queryFilteRef = new QueryFilterRef( new AccesoQuery() );
		queryObj = queryFilteRef.getFilterQueryObject();

		// aplicar filtro fecha
		Object value = popUpDateSelected.getValue();
		SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyyMMdd" );

		String strfecha = (value == null ? null : dateFormatter.format( value ));

		String strFechaDesde = (strfecha == null ? null : dateFormatter.format( value ).concat( "000000" ));
		String strFechaHasta = (strfecha == null ? null : dateFormatter.format( value ).concat( "235959" ));

		((AccesoQuery)queryObj).setDesde( strfecha != null ? Long.parseLong( strFechaDesde ) : null );
		((AccesoQuery)queryObj).setHasta( strfecha != null ? Long.parseLong( strFechaHasta ) : null );

		tableResultadoBusquedaAccesos.getImedigPaginatorFilter().setPaginatorQueryFilterRef( queryFilteRef );

		return queryObj;
	}
}
