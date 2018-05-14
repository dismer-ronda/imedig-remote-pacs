package es.pryades.imedig.cloud.backend;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Background;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotBand;
import com.vaadin.addon.charts.model.PlotOptionsGauge;
import com.vaadin.addon.charts.model.TickPosition;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.StorageConfiguration;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.pacs.dal.StudiesManager;
import es.pryades.imedig.pacs.dto.query.StudyQuery;

public class StorageStatusDlg extends Window implements ModalParent
{
	private static final long serialVersionUID = -3015958813689928788L;

	private static final Logger LOG = Logger.getLogger( StorageStatusDlg.class );
	
	private VerticalLayout mainLayout;

	private Chart chartDiskUsage;
	private Chart chartNasUsage;

	@Getter
	private ImedigContext context;
	
	private StorageConfiguration storageConfiguration;
	
	private Long sizeMain;
	private Long usedMain;
	
	private Long sizeNAS = 0L;
	private Long usedNAS = 0L;
	
	protected Button bttnApply;
	protected HorizontalLayout rowQuery;

	@Getter @Setter	private Integer queryDate;	
	
	private Label labelNumberReports;
	private Label labelNumberStudies;
	private Label labelNumberImages;
	private Label labelAverageImagePerStudy;
	
	private BeanItem<StorageStatusDlg> bi;
	
	private DetallesInformesManager informesManager;
	private StudiesManager studiesManager;
	
	public StorageStatusDlg( ImedigContext context ) throws Exception
	{
		this.context = context;
		
		informesManager = (DetallesInformesManager) IOCManager.getInstanceOf( DetallesInformesManager.class );
		studiesManager = (StudiesManager) IOCManager.getInstanceOf( StudiesManager.class );

		setCaption( context.getString( "StorageStatusDlg.title" ) );
		
		setResizable( false );
		setModal( true );
		setClosable( true );
		addCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "1024px" );
		setHeight( "-1px" );
		
		center();

		mainLayout = new VerticalLayout();
		mainLayout.setWidth( "100%" );
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		setContent( mainLayout );
		
		readStorageConfiguration();
		readStorageUse();
		
		buildMainLayout();
	}

	public void initQueryComponents()
	{
		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );
		rowButton.setHeight( "100%" );
		
		bttnApply = new Button();
		bttnApply.setStyleName( "borderless icon-on-top" );
		bttnApply.setDescription( context.getString( "words.search" ) );
		bttnApply.setIcon( new ThemeResource( "images/search.png" ) );
		bttnApply.setClickShortcut( KeyCode.ENTER );
		
		rowButton.addComponent( bttnApply );
		rowButton.setComponentAlignment( bttnApply, Alignment.MIDDLE_CENTER );
		
		addButtonApplyFilterClickListener();
		
		Component component = getQueryComponent();
		
		if ( component != null )
		{
			rowQuery = new HorizontalLayout();
			rowQuery.setSpacing( true );
			rowQuery.setMargin( false );
			rowQuery.addComponent( component );
			rowQuery.addComponent( bttnApply );
		}
	}

	public void buildMainLayout()
	{
		addStyleName( "mainwnd" );

		VerticalLayout layoutSettings = new VerticalLayout();
		layoutSettings.setWidth( "100%" );
		layoutSettings.setSpacing( true );

		chartDiskUsage = (Chart)getUsageChart( context.getString( "StorageStatusDlg.main" ), sizeMain, usedMain );
		chartNasUsage = (Chart)getUsageChart( context.getString( "StorageStatusDlg.nas" ), sizeNAS, usedNAS );
		chartNasUsage.setVisible( storageConfiguration.getEnableExternalStorage() );
		
		HorizontalLayout rowGraphs = new HorizontalLayout();
		rowGraphs.setWidth( "100%" );
		rowGraphs.setSpacing( true );
		rowGraphs.addComponent( chartDiskUsage );
		rowGraphs.addComponent( chartNasUsage );
		
		Label labelReports = new Label( "Número de informes terminados" );
		labelReports.setWidth( "320px" );
		labelNumberReports = new Label();
		
		Label labelStudies = new Label( "Número de estudios realizados" );
		labelStudies.setWidth( "320px" );
		labelNumberStudies = new Label();

		Label labelImages = new Label( "Número de imágenes realizadas" );
		labelImages.setWidth( "320px" );
		labelNumberImages = new Label();

		Label labelAverage = new Label( "Promedio de imágenes por estudio" );
		labelAverage.setWidth( "320px" );
		labelAverageImagePerStudy = new Label();

		VerticalLayout column = new VerticalLayout();
		column.setMargin( true );
		column.setSpacing( true );
		column.setWidth( "100%" );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.addComponent( labelStudies );
		row1.addComponent( labelNumberStudies );
		row1.setExpandRatio( labelNumberStudies, 1.0f );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.addComponent( labelReports );
		row2.addComponent( labelNumberReports );
		row2.setExpandRatio( labelNumberReports, 1.0f );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.addComponent( labelImages );
		row3.addComponent( labelNumberImages );
		row3.setExpandRatio( labelNumberImages, 1.0f );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.addComponent( labelAverage );
		row4.addComponent( labelAverageImagePerStudy );
		row4.setExpandRatio( labelAverageImagePerStudy, 1.0f );

		initQueryComponents();
		
		HorizontalLayout row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.addComponent( row1 );
		row5.addComponent( row2 );

		HorizontalLayout row6 = new HorizontalLayout();
		row6.setWidth( "100%" );
		row6.addComponent( row3 );
		row6.addComponent( row4 );

		column.addComponent( rowQuery );
		column.addComponent( row5 );
		column.addComponent( row6 );

		layoutSettings.addComponent( rowGraphs );
		layoutSettings.addComponent( column );

		mainLayout.addComponent( layoutSettings );
		
		refreshVisibleContent();
	}

	private String getOnlineStorageMinimunFreeDiskSpace() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileSystemMgt,group=ONLINE_STORAGE\" MinimumFreeDiskSpace" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
		{
			Notification.show( context.getString( "StorageSetupDlg.error.connection" ), Notification.Type.ERROR_MESSAGE );
		
			throw new Exception( "Cannot communicate with PACS" );
		}

		return parts[1];
	}

	private String getMoveStudyIfNotAccessedFor() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileMove\" MoveStudyIfNotAccessedFor" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
		{
			Notification.show( context.getString( "StorageSetupDlg.error.connection" ), Notification.Type.ERROR_MESSAGE );
		
			throw new Exception( "Cannot communicate with PACS" );
		}

		return parts[1];
	}

	private void readStorageConfiguration() throws Exception
	{
		storageConfiguration = (StorageConfiguration)Utils.readObject( Settings.HOME_dir + "/conf/storage.conf" );
		
		if ( storageConfiguration == null )
		{
			storageConfiguration = new StorageConfiguration();
			
			storageConfiguration.setEnableExternalStorage( false );
			storageConfiguration.setExternalStorageDirectory( "192.168.254.254:/Public" ); 
			storageConfiguration.setExternalStorageFilesystem( "nfs" ); 
			storageConfiguration.setKeepStudyMax( 60 ); 
		}
		
		storageConfiguration.setMinimunFreeDiskSpace( getOnlineStorageMinimunFreeDiskSpace() );
		storageConfiguration.setMoveStudyIfNotAccessedFor( getMoveStudyIfNotAccessedFor() ); 

		LOG.info( "storageConfiguration " + storageConfiguration );
	}

	private void readStorageUse()
	{
		String res = Utils.cmdExec( "df | awk {'print $6 \" \" $2 \" \" $3 \" \" $4'}" );
		
		String lines[] = res.split( "\n" );
		
		for ( String line : lines )
		{
			String fields[] = line.split( " " );
			
			if ( fields.length == 4 )
			{
				if ( fields[0].equals( Settings.IMAGE_directory ) )
				{
					sizeMain = Long.parseLong( fields[1] ) / (1024 * 1024);
					usedMain = Long.parseLong( fields[2] ) / (1024 * 1024);
				}
				else if ( fields[0].equals( "/home/imedig/" + Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() ) ) )
				{
					sizeNAS = Long.parseLong( fields[1] ) / (1024 * 1024);
					usedNAS = Long.parseLong( fields[2] ) / (1024 * 1024);
				}
			}
		}
	}
	
	protected Component getUsageChart( String title, long size, long used ) 
	{
        final Chart chart = new Chart();
        chart.setWidth("100%");

        final Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.GAUGE);
        configuration.getChart().setPlotBackgroundColor(null);
        configuration.getChart().setPlotBackgroundImage(null);
        configuration.getChart().setPlotBorderWidth(0);
        configuration.getChart().setPlotShadow(false);
        configuration.setTitle( title + " (" + size + " GB)");

        GradientColor gradient1 = GradientColor.createLinear(0, 0, 0, 1);
        gradient1.addColorStop(0, new SolidColor("#FFF"));
        gradient1.addColorStop(1, new SolidColor("#333"));

        GradientColor gradient2 = GradientColor.createLinear(0, 0, 0, 1);
        gradient2.addColorStop(0, new SolidColor("#333"));
        gradient2.addColorStop(1, new SolidColor("#FFF"));

        Background[] background = new Background[3];
        background[0] = new Background();
        background[0].setBackgroundColor(gradient1);
        background[0].setBorderWidth(0);
        background[0].setOuterRadius("109%");

        background[1] = new Background();
        background[1].setBackgroundColor(gradient2);
        background[1].setBorderWidth(1);
        background[1].setOuterRadius("107%");

        background[2] = new Background();
        background[2].setBackgroundColor(new SolidColor("#DDD"));
        background[2].setBorderWidth(0);
        background[2].setInnerRadius("103%");
        background[2].setOuterRadius("105%");

        configuration.getPane().setStartAngle(-150);
        configuration.getPane().setEndAngle(150);
        configuration.getPane().setBackground(background);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new Title("GB"));
        yAxis.setMin(0);
        yAxis.setMax(size);
        yAxis.setMinorTickInterval("auto");
        yAxis.setMinorTickWidth(1);
        yAxis.setMinorTickLength(10);
        yAxis.setMinorTickPosition(TickPosition.INSIDE);
        yAxis.setMinorTickColor(new SolidColor("#000"));
        yAxis.setGridLineWidth(0);
        yAxis.setTickPixelInterval(30);
        yAxis.setTickWidth(2);
        yAxis.setTickPosition(TickPosition.INSIDE);
        yAxis.setTickLength(10);
        yAxis.setTickColor(new SolidColor("#000"));

        Labels labels = new Labels();
        labels.setStep(2);
        labels.setRotationPerpendicular();
        yAxis.setLabels(labels);

        Double green = size * 0.8;
        Double yellow = size * 0.9;

        PlotBand[] plotBands = new PlotBand[3];
        plotBands[0] = new PlotBand(0, green, new SolidColor("#55BF3B"));
        plotBands[1] = new PlotBand(green, yellow, new SolidColor("#DDDF0D"));
        plotBands[2] = new PlotBand(yellow, size, new SolidColor("#DF5353"));
        yAxis.setPlotBands(plotBands);

        final ListSeries series = new ListSeries("Usados", used);
        PlotOptionsGauge plotOptions = new PlotOptionsGauge();
        plotOptions.setTooltip(new Tooltip());
        plotOptions.getTooltip().setValueSuffix(" GB");
        series.setPlotOptions(plotOptions);
        configuration.setSeries(series);

        chart.drawChart(configuration);
        return chart;
    }
	
	public Component getQueryFecha()
	{
		ComboBox combo = new ComboBox();
		combo.setWidth( "180px" );
		combo.setNullSelectionAllowed( false );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "queryDate" ) );
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
		queryDate = 4;
		
		bi = new BeanItem<StorageStatusDlg>( this );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing( true );
		row1.setMargin( false );
		row1.addComponent( getQueryFecha() );
		
		return row1;
	}

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 6543981729356198246L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent();
			}
		} );
	}

	public void setDateFilter( InformeQuery queryObj )
	{
		switch ( queryDate.intValue() )
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
	
	public void setDateFilter( StudyQuery queryObj )
	{
		switch ( queryDate.intValue() )
		{
			case 1:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 2:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 00:00:00" )  );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 3:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastWeekDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 4:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastMonthDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 5:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastYearDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			default:
				queryObj.setFrom_date( null );
				queryObj.setTo_date( null );
			break;
		}
	}
	
	private String getNumberReportsDone()
	{
		InformeQuery queryObj = new InformeQuery();

		setDateFilter( queryObj );
		queryObj.setEstado( Informe.STATUS_FINISHED );
		
		try
		{
			return Integer.toString( informesManager.getNumberOfRows( getContext(), queryObj ) );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return "error";
	}

	private int getNumberStudiesDone()
	{
		StudyQuery queryObj = new StudyQuery();

		setDateFilter( queryObj );
		
		try
		{
			return studiesManager.getNumberOfRows( getContext(), queryObj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return 0;
	}

	private int getNumberImagesDone()
	{
		StudyQuery queryObj = new StudyQuery();

		setDateFilter( queryObj );
		
		try
		{
			return studiesManager.getTotalImages( getContext(), queryObj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return 0;
	}

	public void refreshVisibleContent()
	{
		labelNumberReports.setValue( getNumberReportsDone() );
		
		int studies = getNumberStudiesDone();
		labelNumberStudies.setValue( Integer.toString( studies ) );
		
		int images = getNumberImagesDone();
		labelNumberImages.setValue( Integer.toString( images ) );
		
		double average = studies != 0 ? images / (double)studies : 0;
		labelAverageImagePerStudy.setValue( Double.toString( Utils.roundDouble( average, 1 ) ) );
	}
}
