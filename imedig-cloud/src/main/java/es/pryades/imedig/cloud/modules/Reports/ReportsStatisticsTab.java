package es.pryades.imedig.cloud.modules.Reports;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesEstadisticasInformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleEstadisticaInforme;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings({"serial","unchecked"})
public class ReportsStatisticsTab extends FilteredContent implements ModalParent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ReportsStatisticsTab.class );

	@Setter @Getter private Integer desde;
	@Setter @Getter private Integer hasta;
	
	private BeanItem<ReportsStatisticsTab> bi;
	
	private DetallesEstadisticasInformesManager estadisticasManager;
	private List<DetalleEstadisticaInforme> estadisticas;
	
	private VerticalLayout dataLayout;
	private HorizontalLayout modeLayout;
	
	private Chart chart;
	private Table table;
	
	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public ReportsStatisticsTab( ImedigContext ctx )
	{
		super( ctx );
		
		estadisticasManager = (DetallesEstadisticasInformesManager) IOCManager.getInstanceOf( DetallesEstadisticasInformesManager.class );
	}

	public String getTabTitle()
	{
		return getContext().getString( "words.statistics" );
	}

	public Component getQueryFecha()
	{
		ComboBox combo = new ComboBox();
		combo.setWidth( "120px" );
		combo.setNullSelectionAllowed( false );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "fecha" ) );
		combo.addItem( 0 );
		combo.setItemCaption( 0, getContext().getString( "words.all" ) );
		combo.addItem( 1 );
		combo.setItemCaption( 1, getContext().getString( "words.today" ) );
		combo.addItem( 2 );
		combo.setItemCaption( 2, getContext().getString( "words.yesterday" ) );
		combo.addItem( 3 );
		combo.setItemCaption( 3, getContext().getString( "words.lastweek" ) );
		combo.addItem( 4 );
		combo.setItemCaption( 4, getContext().getString( "words.lastmonth" ) );
		combo.addItem( 5 );
		combo.setItemCaption( 5, getContext().getString( "words.lastyear" ) );
		
		Label label = new Label( getContext().getString( "words.date" ) );

		HorizontalLayout rowFecha = new HorizontalLayout();
		rowFecha.addComponent( label );
		rowFecha.setComponentAlignment( label, Alignment.MIDDLE_LEFT );
		rowFecha.addComponent( combo );
		rowFecha.setComponentAlignment( combo, Alignment.MIDDLE_LEFT );
		rowFecha.setSpacing( true );
		rowFecha.setMargin( false );

		return rowFecha;
	}
	
	public Component getQueryComponent()
	{
		desde = 4;
		
		bi = new BeanItem<ReportsStatisticsTab>( this );
		
		VerticalLayout column = new VerticalLayout();
		column.setMargin( false );
		column.setSpacing( true );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing( true );
		row1.setMargin( false );
		row1.addComponent( getQueryFecha() );
		
/*		HorizontalLayout row2 = new HorizontalLayout();
		row2.setSpacing( true );
		row2.setMargin( false );
		row2.addComponent( getQueryCentro() );
		row2.addComponent( getQueryModalidad() );
		row2.addComponent( getQueryEstudio() );
		row2.addComponent( getQueryPaciente() );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setSpacing( true );
		row3.setMargin( false );
		row3.addComponent( getQueryCodigo() );
		row3.addComponent( getQueryClaves() ); */

		column.addComponent( row1 );
/*		column.addComponent( row2 );
		column.addComponent( row3 );*/

		return null; //column;
	}

	public void initSpecificContents()
	{
		dataLayout = new VerticalLayout();
		dataLayout.setSizeFull();
		dataLayout.setMargin( new MarginInfo( false, false, true, false ) );

		modeLayout = new HorizontalLayout();
		modeLayout.setMargin( new MarginInfo( true, false, false, false ) );
		modeLayout.setSpacing( true );
		
		Button button = new Button();
		button.setStyleName( "borderless icon-on-top" );
		button.setDescription( getContext().getString( "words.show.table" ) );
		button.setIcon( new ThemeResource( "images/table.png" ) );
		button.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -7375575241909210318L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				showTable();
			}
		} );
		modeLayout.addComponent( button );			

		button = new Button();
		button.setStyleName( "borderless icon-on-top" );
		button.setDescription( getContext().getString( "words.show.graph" ) );
		button.setIcon( new ThemeResource( "images/bars.png" ) );
		button.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5795734292575885562L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				showBarsChart();
			}
		} );
		modeLayout.addComponent( button );

		/*button = new Button();
		button.setStyleName( "borderless icon-on-top" );
		button.setDescription( getContext().getString( "words.show.pie" ) );
		button.setIcon( new ThemeResource( "images/pie.png" ) );
		button.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -7375575241909210318L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				showPieChart();
			}
		} );
		modeLayout.addComponent( button );*/

		mainLayout.addComponent( modeLayout );
		mainLayout.setComponentAlignment( modeLayout, Alignment.TOP_RIGHT );
		
		mainLayout.addComponent( dataLayout );
		mainLayout.setExpandRatio( dataLayout, 1.0f );
	}

	private void buildChart( String title, ChartType type )
	{
		Configuration config = new Configuration();
		
		config.setTitle( new Title( title ) );

		Style style = new Style();
		//style.setFontFamily( "droidsans, Verdana, sans-serif" );
		style.setFontSize( "12px" );
		style.setFontWeight( FontWeight.BOLD );

		config.getTitle().setStyle( style );
		config.getCredits().setEnabled( false );
		config.setExporting( true );
		
		chart = new Chart( type );

		chart.setConfiguration( config );

		chart.setSizeFull();
		chart.setImmediate( true );
	}

	public int getRedFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 1, 3 ), 16 );
	}

	public int getGreenFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 3, 5 ), 16 );
	}

	public int getBlueFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 5, 7 ), 16 );
	}

	public GradientColor createHalfRadialGradient( Color start )
	{
		String rgb = start.toString();
		
		GradientColor color = GradientColor.createRadial( 0.5, 0.5, 0.5 );
		color.addColorStop( 0, (SolidColor)start );
		color.addColorStop( 1, new SolidColor( getRedFromRgb( rgb )/2, getGreenFromRgb( rgb )/2, getBlueFromRgb( rgb )/2 ) );
		
		return color;
	}

	protected void showPiePoints()
	{
		DataSeries dataSeries = new DataSeries();

		List<EstadisticaFecha> estadisticaFecha = getEstadisticaPorFecha();

		EstadisticaFecha estadisticaTotales = getTotales( estadisticaFecha );

		double sum = estadisticaTotales.getTotal();
		
		for ( DetalleCentro centro : getContext().getCentros() )
		{
			Double value = (estadisticaTotales.getCantidad( centro.getNombre() )/sum) * 100; 

			if ( value != 0 )
			{
				DataSeriesItem item = new DataSeriesItem( centro.getNombre(), value );
	
				dataSeries.add( item );
			}
		}

		PlotOptionsPie plotOptions = new PlotOptionsPie();
		plotOptions.setAllowPointSelect( true );
		plotOptions.setCursor( Cursor.POINTER );
		plotOptions.setShowInLegend( true );

		Labels dataLabels = new Labels();
		dataLabels.setEnabled( true );
		dataLabels.setColor( new SolidColor( 0, 0, 0 ) );
		dataLabels.setConnectorColor( new SolidColor( 0, 0, 0 ) );
		dataLabels.setFormatter( "''+ $wnd.Highcharts.numberFormat(this.percentage, 1) +' %'" );
		plotOptions.setDataLabels( dataLabels );

		dataSeries.setPlotOptions( plotOptions );

		Tooltip t = new Tooltip();
		t.setEnabled( false );
		chart.getConfiguration().setTooltip( t );

		chart.getConfiguration().setSeries( dataSeries );

		chart.drawChart();
	}
	
	private LinkedHashMap<String, List<DetalleEstadisticaInforme>> getStatisticsByCenter()
	{
		LinkedHashMap<String, List<DetalleEstadisticaInforme>> map = new LinkedHashMap<String, List<DetalleEstadisticaInforme>>();
		
		for ( DetalleEstadisticaInforme estadistica : estadisticas )
		{
			List<DetalleEstadisticaInforme> current = map.get( estadistica.getCentro_nombre() );
			
			if ( current == null )
			{
				current = new ArrayList<DetalleEstadisticaInforme>();
				
				map.put( estadistica.getCentro_nombre(), current );
			}
			
			current.add( estadistica );
		}
		
		return map;
	}
	
	private List<DataSeriesItem> getSeriesItems( List<DetalleEstadisticaInforme> values )
	{
		List<DataSeriesItem> data = new ArrayList<DataSeriesItem>();
		
		for ( DetalleEstadisticaInforme value : values )
			data.add( new DataSeriesItem( Utils.getMonthFromInt( value.getFecha() ), value.getCantidad() ) );

		return data;
	}

	private void showBarsPoints()
	{
		LinkedHashMap<String, List<DetalleEstadisticaInforme>> map = getStatisticsByCenter();
		
		chart.getConfiguration().getxAxis().setType(AxisType.DATETIME);
		chart.getConfiguration().getxAxis().setDateTimeLabelFormats(new DateTimeLabelFormats("%b %Y", ""));
		
		YAxis yaxis = new YAxis();
		yaxis.setLabels( new Labels() ); 
		yaxis.setTitle( getContext().getString( "words.reports" ) );
		chart.getConfiguration().addyAxis( yaxis );

		for ( Entry<String, List<DetalleEstadisticaInforme>> entry : map.entrySet() )
		{
			PlotOptionsColumn plotOptions = new PlotOptionsColumn();
			plotOptions.setStacking(Stacking.NORMAL);
			plotOptions.setAnimation( true );

			DataSeries seriesData = new DataSeries( entry.getKey() );
			seriesData.setPlotOptions( plotOptions );
			seriesData.setData( getSeriesItems( entry.getValue() ) );
			
			chart.getConfiguration().addSeries( seriesData );
		}

		String formatter = "function(){return '<b>" + getContext().getString( "words.date" ) + ": </b>' + $wnd.Highcharts.dateFormat('%b %Y', this.x) + '<br/><b>' + this.series.name + ': </b>' + this.y + ', ' + $wnd.Highcharts.numberFormat((this.y/this.point.stackTotal)*100,2) + ' % <br/><b>' + 'Total: </b>' + this.point.stackTotal;}";

		Tooltip tooltip = new Tooltip();

		tooltip.setFormatter( formatter );

		chart.getConfiguration().setTooltip( tooltip );

		chart.drawChart();
	}

	@SuppressWarnings("unused")
	private void showPieChart()
	{
		dataLayout.removeAllComponents();

		buildChart( getContext().getString( "ReportStatisticsTab.chartPieTitle" ), ChartType.PIE );
		showPiePoints();

		dataLayout.addComponent( chart );
		dataLayout.setExpandRatio( chart, 1.0f );
	}

	private void showBarsChart()
	{
		dataLayout.removeAllComponents();

		buildChart( getContext().getString( "ReportStatisticsTab.chartTitle" ), ChartType.BAR );
		showBarsPoints();

		dataLayout.addComponent( chart );
		dataLayout.setExpandRatio( chart, 1.0f );
	}
	
	@Override
	public void refreshVisibleContent()
	{
		applySelectedFilters();

		showTable();
	}

	public void setDateFilter( InformeQuery queryObj )
	{
		switch ( desde )
		{
			case 1:
				queryObj.setDesde( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 2:
				queryObj.setDesde( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 3:
				queryObj.setDesde( Long.parseLong( Utils.getLastWeekDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 4:
				queryObj.setDesde( Long.parseLong( Utils.getLastMonthDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 5:
				queryObj.setDesde( Long.parseLong( Utils.getLastYearDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			default:
			break;
		}
	}
	
	@Data
	class EstadisticaFecha
	{
		private Integer fecha;
		private LinkedHashMap<String, Integer> cantidades = new LinkedHashMap<String, Integer>();
		
		public Integer getCantidad( String centro )
		{
			Integer cantidad = cantidades.get( centro );
			
			return cantidad != null ? cantidad : 0;
		}

		public Integer getTotal()
		{
			int sum = 0;
			for ( Entry<String, Integer> entry : cantidades.entrySet() )
				sum += entry.getValue();
			return sum;
		}
		
		public Object[] getCols()
		{
			Object [] ret = new Object[getContext().getCentros().size() + 2];
			
			ret[0] = Utils.getFormatedMonth( fecha, "MMM yyyy" );
			ret[1] = getTotal();
			int i = 2;
			for ( DetalleCentro centro : getContext().getCentros() )
				ret[i++] = getCantidad( centro.getNombre() );
			
			return ret;
		}
	}

	private List<EstadisticaFecha> getEstadisticaPorFecha()
	{
		List<EstadisticaFecha> ret = new ArrayList<EstadisticaFecha>();
		
		EstadisticaFecha ef = null; 
		
		for ( DetalleEstadisticaInforme estadistica : estadisticas )
		{
			if ( ef == null || !estadistica.getFecha().equals( ef.getFecha() ) )
			{
				ef = new EstadisticaFecha();

				ef.setFecha( estadistica.getFecha() );
				
				ret.add( ef );
			}

			ef.getCantidades().put( estadistica.getCentro_nombre(), estadistica.getCantidad() );
		}
		
		return ret;
	}

	private EstadisticaFecha getTotales( List<EstadisticaFecha> estadisticaFecha )
	{
		EstadisticaFecha totales = new EstadisticaFecha(); 
		
		for ( DetalleCentro centro : getContext().getCentros() )
		{
			Integer total = 0;
			
			for ( EstadisticaFecha estadistica : estadisticaFecha )
				total += estadistica.getCantidad( centro.getNombre() );
			
			totales.getCantidades().put( centro.getNombre(), total );
		}
		
		return totales;
	}
	
	private void settingTable()
	{
		List<EstadisticaFecha> estadisticaFecha = getEstadisticaPorFecha();

		EstadisticaFecha estadisticaTotales = getTotales( estadisticaFecha );
		
		table = new Table();
		
		table.setSizeFull();

		table.addContainerProperty( getContext().getString( "words.date" ), String.class, null);
		table.addContainerProperty( getContext().getString( "words.total" ), Integer.class, null);
		for ( DetalleCentro centro : getContext().getCentros() )
			table.addContainerProperty( centro.getNombre(), Integer.class, null);

		table.setFooterVisible(true);
		table.setColumnFooter( getContext().getString( "words.date" ), "Total" );
		table.setColumnFooter( getContext().getString( "words.total" ), Integer.toString( estadisticaTotales.getTotal() ) );
		for ( DetalleCentro centro : getContext().getCentros() )
			table.setColumnFooter( centro.getNombre(), Integer.toString( estadisticaTotales.getCantidad( centro.getNombre() ) ));
		
		table.setSelectable( true );
		table.setStyleName( "striped" );
		table.setMultiSelect(false);
		table.setImmediate( true );

        table.setColumnReorderingAllowed(false);
        table.setColumnCollapsingAllowed(false);
        
        Align[] aligns = new Align[getContext().getCentros().size() + 2];
        for ( int i = 0; i < getContext().getCentros().size() + 2; i++ )
        	aligns[i] = Align.CENTER;
        table.setColumnAlignments( aligns );
        
        int i = 0;
		for ( EstadisticaFecha estadistica : estadisticaFecha )
			table.addItem( estadistica.getCols(), new Integer( i++ ) );
	}
	
	private void showTable()
	{
		dataLayout.removeAllComponents();
		
		settingTable();
		
		dataLayout.addComponent( table );
		dataLayout.setExpandRatio( table, 1.0f );
	}
	
	public void applySelectedFilters()
	{
		InformeQuery queryObj = new InformeQuery();

		queryObj.setCentros( getContext().getCentros() );
		
		try
		{
			estadisticas = estadisticasManager.getRows( getContext(), queryObj );
		}
		catch ( Throwable e )
		{
			estadisticas = new ArrayList<DetalleEstadisticaInforme>();
		}
	}

	@Override
	public boolean isAddAvailable()
	{
		return false;
	}

	@Override
	public TableImedigPaged getTableRows()
	{
		return null;
	}

	@Override
	public String[] getVisibleCols()
	{
		return null;
	}

	@Override
	public String getResouceKey()
	{
		return null;
	}

	@Override
	public void onAddRow()
	{
	}

	@Override
	public void onModifyRow( Object row )
	{
	}

	@Override
	public void onDeleteRow( Object row )
	{
	}

	@Override
	public Query getQueryObject()
	{
		return null;
	}

	@Override
	public void onSelectedRow( Object row )
	{
	}
}
