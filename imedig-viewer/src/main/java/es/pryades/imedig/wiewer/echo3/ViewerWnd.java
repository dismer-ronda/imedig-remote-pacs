package es.pryades.imedig.wiewer.echo3;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Border;
import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.Grid;
import nextapp.echo.app.HttpImageReference;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.echo3.components.Annotation;
import es.pryades.imedig.viewer.echo3.components.ContentPaneEx;
import es.pryades.imedig.viewer.echo3.components.DistanceAnnotation;
import es.pryades.imedig.viewer.echo3.components.ImedigCanvas;
import es.pryades.imedig.wado.query.QueryManager;
import es.pryades.imedig.wado.retrieve.RetrieveManager;

public class ViewerWnd extends ContentPane implements GenericWnd, ModalContainer
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger( ViewerWnd.class );
	
	public static HashMap<String,ReportInfo> imagesInfo = new HashMap<String, ReportInfo>();

	private class Status
	{
		int ix1;
		int iy1;
		int ix2;
		int iy2;
		
		double windowWidth;
		double windowCenter;
		
		int frame;
		
		public Status( int ix1, int iy1, int ix2, int iy2, double windowCenter, double windowWidth, int frame )
		{
			this.ix1 = ix1;
			this.iy1 = iy1;
			this.ix2 = ix2;
			this.iy2 = iy2;
			
			this.windowCenter = windowCenter;
			this.windowWidth = windowWidth;
			
			this.frame = frame;
		}
	}
	
	public static final int OPERATION_NONE = 0;
	public static final int OPERATION_ZOOM = 1;
	public static final int OPERATION_PAN = 2;
	public static final int OPERATION_MEASURE_LINE = 3;
	public static final int OPERATION_MEASURE_ANGLE = 4;
	public static final int OPERATION_MEASURE_AREA = 5;
	public static final int OPERATION_WINDOW = 6;
	
	protected ResourceBundle resourceBundle;
	
	private int buttonSize;
	
	private QueryDlg queryDlg = null;

    ArrayList<String> selectedStudies = null;
	private ArrayList<StudyTree> studies;
	
	private String currentSelMode;
	private boolean currentShowLine;
	private int currentOperation;
	private int currentCursorOffsetX;
	private int currentCursorOffsetY;
	
//	private ResourceImageReference iconLink;
//	private ResourceImageReference iconLinkBreak;
	
//	private ResourceImageReference cursorPointer;
//	private ResourceImageReference cursorZoom;
//	private ResourceImageReference cursorPan;
//	private ResourceImageReference cursorWindow;
//	private ResourceImageReference cursorDistance;
//	private ResourceImageReference cursorAngle;
//	private ResourceImageReference currentCursor;

	private String currentCursor;

//	private Button buttonLink;
//	private Button buttonPrevImage = null;
//	private Button buttonNextImage = null;
//	private Button buttonPrevFrame = null;
//	private Button buttonNextFrame = null;
	private Button buttonDistance = null;
	private Button buttonAngle = null;
	private Button buttonZoom = null;
//	private Button buttonPan = null;
	private Button buttonContrast = null;
	private Button buttonUndo = null;
//	private Button buttonClear = null;
	private Button buttonOpen = null;
	private Button buttonClose = null;
	private Button buttonNone = null;
	
	boolean replicate = false;
	ImedigCanvas activeCanvas = null;
	private ArrayList<ImedigCanvas> canvasImages;
	
	boolean firstLine = true;
	
	private double aix1;
	private double aiy1;
	private double aix2;
	private double aiy2;
	
	private ImedigApplication app;
	
	private User user;
	
	private Column imagesThumbnail;
	private ContentPane contentPaneGrid;
	private Grid opGrid;
	
	private boolean firstTime = true;

	private ActionListener actionOpen = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onOpenStudies();
		}
	};
	
	private ActionListener actionClose = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onCloseStudies( e );
		}
	};
	
	private ActionListener actionPreviousImage = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onPreviousImage( e );
		}
	};
	
	private ActionListener actionNextImage = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onNextImage( e );
		}
	};
	
	private ActionListener actionNone = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onNone( e );
		}
	};
	
	private ActionListener actionZoom = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onZoom( e );
		}
	};
	
	private ActionListener actionPan = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onPan( e );
		}
	};
	
	private ActionListener actionBrightContrast = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onBrightContrast( e );
		}
	};
	
	private ActionListener actionPreviousFrame = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onPreviousFrame( e );
		}
	};
	
	private ActionListener actionNextFrame = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onNextFrame( e );
		}
	};
	
	private ActionListener actionZoomBack = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onZoomBack( e );
		}
	};
	
	private ActionListener actionReplicate = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onReplicate( e );
		}
	};
	
	private ActionListener actionGrid1x1 = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onGrid1x1( e );
		}
	};
	
	private ActionListener actionGrid2x1 = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onGrid2x1( e );
		}
	};
	
	private ActionListener actionGrid2x2 = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onGrid2x2( e );
		}
	};
	
	private ActionListener actionGrid3x2 = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onGrid3x2( e );
		}
	};
	
	private ActionListener actionClear = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onClear( e );
		}
	};
	
	private ActionListener actionDistance = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onDistance( e );
		}
	};
	
	private ActionListener actionAngle = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onAngle( e );
		}
	};
	
	private ActionListener actionClearAnnotations = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onClearAnnotations( e );
		}
	};
	
	private ActionListener actionOpenImage = new ActionListener()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed( ActionEvent e )
		{
			onOpenImage( e );
		}
	};
	
//	private SplitPane splitPaneTools;
	
//	private Column columnStudy;
	//	private Column columnOperations;
	//	private Column columnLayout;
	//	private Column columnMeasure;
	//	private Column columnExit;
	
	/**
	 * Creates a new <code>ViewerWnd</code>.
	 */
	public ViewerWnd( ImedigApplication app, int height, User user )
	{
		super();
	
		this.app = app;
		
		if ( user.getUid() == null )
			this.selectedStudies = null;
		else
		{
			selectedStudies = new ArrayList<String>();
			
			selectedStudies.add( user.getUid() );
		}
		
		setUser( user );
		
		/*if ( height <= 768 )
			setButtonSize( 16 );
		else if ( height < 1024 )
			setButtonSize( 24 );
		else*/
		setButtonSize( 32 );
		
		// Add design-time configured components.
		
		initComponents();
		
		initButtons();
		
		currentOperation = OPERATION_NONE;
		
		currentSelMode = "none";
		currentShowLine = false;
		
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		canvasImages = new ArrayList<ImedigCanvas>();
		
		studies = new ArrayList<StudyTree>();
		
/*		iconLink = AppUtils.getIcon( "link", getButtonSize() );
		iconLinkBreak = AppUtils.getIcon( "link-break", getButtonSize() );
		
		cursorPointer = getCursor( "cursor-green" );
		cursorZoom = getCursor( "magnifier" );
		cursorPan = getCursor( "pan" );
		cursorDistance = getCursor( "distance" );
		cursorAngle = getCursor( "angle" );
		cursorWindow = getCursor( "contrast" );
		
		currentCursor = cursorPointer;*/
		
		currentCursor = "default";
		
		createImageGrid();
	}
	
	/**
	 * Returns the user's application instance, cast to its specific type.
	 * 
	 * @return The user's application instance.
	 */
	protected ImedigApplication getApplication()
	{
		return app;
	}

	private void setInsets( SplitPane splitPane, int insets )
	{
		SplitPaneLayoutData splitPaneLayoutData = new SplitPaneLayoutData();
		splitPaneLayoutData.setOverflow( SplitPaneLayoutData.OVERFLOW_HIDDEN );
		splitPaneLayoutData.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );

		splitPane.setLayoutData( splitPaneLayoutData );
	}

	private SplitPane getMainSplitPane()
	{
		SplitPane splitPane = new SplitPane();
		
		splitPane.setStyleName( "Default" );
		splitPane.setSeparatorPosition( new Extent( 160, Extent.PX ) );
		splitPane.setOrientation( SplitPane.ORIENTATION_HORIZONTAL_LEFT_RIGHT );
		splitPane.setBackground( Color.LIGHTGRAY );
		splitPane.setSeparatorWidth( new Extent( 2, Extent.PX ) );
		splitPane.setResizable( false );
		splitPane.setRenderId( "splitPaneViewer" );
		splitPane.setAutoPositioned( false );
		splitPane.setSeparatorVisible( true );
		
		setInsets( splitPane, 5 );
		
		return splitPane;
	}	

	private SplitPane getLeftSplitPane()
	{
		SplitPane splitPane = new SplitPane();
		
		splitPane.setStyleName( "Default" );
		splitPane.setOrientation( SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM );
		splitPane.setSeparatorPosition( new Extent( 160, Extent.PX ) );
		splitPane.setBackground( Color.LIGHTGRAY );
		//splitPane.setSeparatorHeight( new Extent( 2, Extent.PX ) );
		splitPane.setResizable( false );
		splitPane.setAutoPositioned( false );
		splitPane.setSeparatorVisible( false );

		setInsets( splitPane, 5 );
		
		return splitPane;
	}	

	private void initComponents()
	{
		resourceBundle = app.getResourceBundle(); //ResourceBundle.getBundle( "es.pryades.imedig.wiewer.resource.localization.Messages", ApplicationInstance.getActive().getLocale() );
		this.setOverflow( ContentPane.OVERFLOW_HIDDEN );
		
		SplitPane splitPaneMain = getMainSplitPane();

		add( splitPaneMain );
		
		SplitPane splitPaneLeft = getLeftSplitPane();
		splitPaneMain.add( splitPaneLeft );

		contentPaneGrid = new ContentPane();
		contentPaneGrid.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		
		splitPaneMain.add( contentPaneGrid );

		Column col1 = new Column();
		col1.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		col1.setCellSpacing( new Extent( 5, Extent.PX ) );
		
		/*Label label1 = new Label();
		label1.setIcon( new ResourceImageReference( "/es/pryades/imedig/wiewer/resource/image/icon/id/imedig.png" ) );
		col1.add( label1 );*/

		opGrid = new Grid( 3 );
		opGrid.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		opGrid.setBackground( new Color( Colors.INT_METRO_TEAL ) );
		col1.add( opGrid );
		splitPaneLeft.add( col1 );

		imagesThumbnail = new Column();
		imagesThumbnail.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		imagesThumbnail.setCellSpacing( new Extent( 5, Extent.PX ) );
		splitPaneLeft.add( imagesThumbnail );
	}

	String getResString( String name )
	{
		return resourceBundle.getString( "ViewerWnd." +  name );
	}
	
	Button getButton( String name, String toolTip, ActionListener action )
	{
		Button button = new Button();
		
		button.setStyleName( "Default" );
		
		button.setIcon( AppUtils.getIcon( name, getButtonSize() ) );
		button.setDisabledIcon( AppUtils.getGrayIcon( name, getButtonSize() ) ); 
		
		button.setInsets( new Insets( new Extent( 0, Extent.PX ) ) );
		
		button.setBackground( new Color( Colors.INT_METRO_TEAL ) );
		button.setRolloverBackground( Color.LIGHTGRAY );
		button.setDisabledBackground( new Color( Colors.INT_METRO_TEAL ) );
		
		button.setForeground( new Color( 0x33ccff ) );
		
		button.setBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );
		button.setDisabledBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );
		button.setFocusedBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );
		button.setPressedBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );
		button.setRolloverBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_ORANGE ), Border.STYLE_SOLID ) );
		
		button.setToolTipText( toolTip );
		button.addActionListener( action );
		
		return button;
	}
	
	void setButtonState( Button button, boolean enabled )
	{
		button.setEnabled( enabled );
	}
	
	private void initButtons()
	{
		//splitPaneTools.setSeparatorPosition( new Extent( getButtonSize() + 8, Extent.PX ) );
		
//		columnStudy.removeAll();
		//		columnOperations.removeAll();
		//columnLayout.removeAll();
		//columnMeasure.removeAll();
		//columnExit.removeAll();

		opGrid.add( buttonOpen = getButton( "open", getResString( "Open" ), actionOpen ) );
		opGrid.add( buttonClose = getButton( "close", getResString( "Close" ), actionClose ) );
		
//		columnStudy.add( buttonPrevImage = getButton( "go-previous", getResString( "GoPrevious" ), actionPreviousImage ) );
//		columnStudy.add( buttonNextImage = getButton( "go-next", getResString( "GoNext" ), actionNextImage ) );
		
		opGrid.add( buttonNone = getButton( "cursor-green", getResString( "NoOperation" ), actionNone ) );
		opGrid.add( buttonZoom = getButton( "magnifier", getResString( "Zoom" ), actionZoom ) );
//		columnOperations.add( buttonPan = getButton( "pan", getResString( "Pan" ), actionPan ) );
		opGrid.add( buttonContrast = getButton( "contrast", getResString( "Contrast" ), actionBrightContrast ) );
//		columnOperations.add( buttonPrevFrame = getButton( "previous-frame", getResString( "PreviousFrame" ), actionPreviousFrame ) );
//		columnOperations.add( buttonNextFrame = getButton( "next-frame", getResString( "NextFrame" ), actionNextFrame ) );
		opGrid.add( buttonUndo = getButton( "undo", getResString( "Undo" ), actionZoomBack ) );
//		columnOperations.add( buttonLink = getButton( "link-break", getResString( "LinkBreak" ), actionReplicate ) );
		
//		columnLayout.add( getButton( "layout-1", getResString( "Layout1" ), actionGrid1x1 ) );
//		columnLayout.add( getButton( "layout-2", getResString( "Layout2" ), actionGrid2x1 ) );
//		columnLayout.add( getButton( "layout-4", getResString( "Layout4" ), actionGrid2x2 ) );
//		columnLayout.add( getButton( "layout-6", getResString( "Layout6" ), actionGrid3x2 ) );
//		columnLayout.add( getButton( "clear", getResString( "Clear" ), actionClear ) );
		
		opGrid.add( buttonDistance = getButton( "distance", getResString( "Distance" ), actionDistance ) );
		opGrid.add( buttonAngle = getButton( "angle", getResString( "Angle" ), actionAngle ) );
//		columnMeasure.add( buttonClear = getButton( "eraser", getResString( "ClearAnnotations" ), actionClearAnnotations ) );
	}
	
	public void takeControl( ApplicationInstance app )
	{
		/*if ( app != null )
			onOpenStudies();*/
	}
	
	public void fillThumbnail( int level )
	{
		switch ( level )
		{
			case 0:
				// fillStudiesThumnail();
			break;
			
			case 1:
				// fillStudiesThumnail();
			break;
			
		}
	}
	
	String getPatientInitials( String name )
	{
		String ret = "";
		
		if ( name != null && !name.isEmpty() )
		{
			String splitChar = " ";
			
			if ( name.contains( "^" ) )
			{
				splitChar = "\\^";
			}
			else if ( name.contains( "/" ) )
			{
				splitChar = "/";
			}
			else if ( name.contains( "," ) )
			{
				splitChar = ",";
			}
			
			String parts[] = name.split( splitChar );
			
			for ( int i = 0; i < parts.length; i++ )
			{
				if ( parts[i] != null && !parts[i].isEmpty() )
					ret += parts[i].toUpperCase().charAt( 0 ) + ".";
			}
		}
		
		return ret;
	}
	
	Button getImageButton( StudyTree study, SeriesTree series, Image image, boolean group )
	{
		String imageUrl = app.getServerUrl() + image.getWadoUrl() + "&contentType=image/jpeg&columns=64&rows=64";

		ImageReference ref = new HttpImageReference( imageUrl );

		LOG.info( "imageUrl " + imageUrl );
		
		Button button1 = new Button();
		
		String modality = series.getSeriesData().getModality();
		
		String hint = " ";
		
		if ( !modality.isEmpty() )
			hint += modality + " ";
		
		button1.set( "ImageBean", image );
		button1.set( "SeriesBean", series );
		button1.set( "StudyBean", study );
		
		button1.setStyleName( "Default" );
		button1.setText( "" );
		button1.setWidth( new Extent( 64, Extent.PX ) );
		button1.setInsets( new Insets( new Extent( 0, Extent.PX ) ) );
		button1.setBackground( Color.LIGHTGRAY );
		button1.setToolTipText( hint );

		int border = 1;
		Color color = Color.LIGHTGRAY;
		int style = Border.STYLE_SOLID;
		
		if ( group )
		{
			border = 5;
			color = Color.WHITE;
			style = Border.STYLE_DOUBLE;
		}
		
		button1.setBorder( new Border( new Extent( border, Extent.PX ), color, style ) );
		button1.setPressedBorder( new Border( new Extent( border, Extent.PX ), color, style ) );
		button1.setRolloverBorder( new Border( new Extent( border, Extent.PX ), Color.ORANGE, style ) );
		button1.setDisabledBorder( new Border( new Extent( border, Extent.PX ), color, style ) );
		button1.setFocusedBorder( new Border( new Extent( border, Extent.PX ), color, style ) );
		
		button1.setRolloverBackground( Color.BLACK );
		button1.setPressedBackground( Color.BLACK );
		button1.setBackground( Color.BLACK );
		button1.setFocusedBackground( Color.BLACK );
		button1.setDisabledBackground( Color.BLACK );
		
		RowLayoutData button1LayoutData = new RowLayoutData();
		button1LayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		button1.setLayoutData( button1LayoutData );
		button1.setIcon( ref );
		button1.addActionListener( actionOpenImage );
		
		return button1;
	}
	
	Panel getStudyPanel( StudyTree study, int index )
	{
		if ( study.getSeriesList().size() == 0 )
			return null;
		
		Panel panel1 = new Panel();
		
		Column column1 = new Column();
		Column column2 = new Column();
		Column column3 = new Column();
		
		Row rowId = new Row();
		Row rowSeries = new Row();
		Row row1 = new Row();
		Row row2 = new Row();
		Row row3 = new Row();
		
		Label labelPatientName = new Label();
		Label labelPatientId = new Label();
		Label labelStudyDate = new Label();
		
		int i = index % 5;
		
		panel1.setBackground( new Color( Colors.INT_METRO_COLORS[i] ) );
		
		RowLayoutData column2LayoutData = new RowLayoutData();
		column2LayoutData.setWidth( new Extent( 100, Extent.PERCENT ) );
		column2.setLayoutData( column2LayoutData );
		column2.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		
		RowLayoutData column3LayoutData = new RowLayoutData();
		column3LayoutData.setWidth( new Extent( 100, Extent.PERCENT ) );
		column3.setLayoutData( column3LayoutData );
		column3.setInsets( new Insets( new Extent( 5, Extent.PX ) ) );
		column3.setCellSpacing( new Extent( 5, Extent.PX ) );
		
		labelPatientName.setText( getPatientInitials( study.getStudyData().getPatientName() ) );
		labelPatientName.setFont( new Font( new Font.Typeface( "sans-serif" ), Font.BOLD, new Extent( 8, Extent.PT ) ) );
		labelPatientName.setToolTipText( study.getStudyData().getPatientName() );
		
		labelStudyDate.setText( study.getStudyData().getStudyDate() );
		labelStudyDate.setFont( new Font( new Font.Typeface( "sans-serif" ), Font.BOLD, new Extent( 8, Extent.PT ) ) );
		
		labelPatientId.setText( study.getStudyData().getPatientID() );
		labelPatientId.setFont( new Font( new Font.Typeface( "sans-serif" ), Font.BOLD, new Extent( 8, Extent.PT ) ) );

		panel1.set( "StudyUID", study.getStudyData().getStudyInstanceUID() );

		List<SeriesTree> seriesList = study.getSeriesList();
		
		for ( SeriesTree series : seriesList )
		{
			List<Image> imageList = series.getImageList();
			
			Column col = new Column();
			
			for ( Image image : imageList )
			{
				col.add( getImageButton( study, series, image, false ) );
			}
			
			column3.add( col );
		}

		row1.add( labelPatientName );
		row2.add( labelStudyDate );
		row3.add( labelPatientId );
		
		column2.add( row1 );
		column2.add( row2 );
		column2.add( row3 );
		
		rowId.add( column2 );
		rowSeries.add( column3 );
		
		column1.add( rowId );
		column1.add( rowSeries );
		
		panel1.add( column1 );
		
		return panel1;
	}
	
	public void setStudyThumbnails()
	{
		imagesThumbnail.removeAll();
		
		int i = 0;

		for ( StudyTree study : studies )
		{
			Panel panel = getStudyPanel( study, i++ );
			
			if ( panel != null )
				imagesThumbnail.add( panel );
		}
	}
	
	public void openStudies( ArrayList<String> selectedStudies )
	{
		StudyTree firstStudy = null;
		
		for ( String uid : selectedStudies )
		{
			LOG.info( "selectedStudies uid " + uid );
			
			String AETitle = Settings.PACS_AETitle;
			String Host = Settings.PACS_Host;
			int Port = Settings.PACS_Port;
				
			StudyTree study = QueryManager.getInstance().getStudyTree( AETitle, Host, Port, Settings.IMEDIG_AETitle, uid, new HashMap<String,String>() );

			if ( study != null )
			{
				studies.add( study );
					
				if ( firstStudy == null )
					firstStudy = study;
			}
		}
		
		setStudyThumbnails();
	}
	
	void setViewRect( ImedigCanvas canvasImage )
	{
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		Point vsize = (Point) canvasImage.get( "vsize" );
		
		if ( irect != null && vsize != null )
		{
			int vcols = (int) vsize.getX();
			int vrows = (int) vsize.getY();
			
			int icols = (int) irect.getWidth();
			int irows = (int) irect.getHeight();
			
			double ar = (double) icols / irows;
			
			int cols = 0;
			int rows = 0;
			
			if ( vrows * ar > vcols )
			{
				cols = vcols;
				rows = (int) ( vcols / ar + .5f );
			}
			else
			{
				cols = (int) ( vrows * ar + .5f );
				rows = vrows;
			}
			
			canvasImage.set( "vrect", new Rectangle( 0, 0, cols, rows ) );
		}
	}
	
	private void openCurrentImage( ImedigCanvas canvasImage )
	{
		Image image = (Image) canvasImage.get( "ImageBean" );
		ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
		
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		Point vsize = (Point) canvasImage.get( "vsize" );
		
		if ( image != null && metadata != null && irect != null && vsize != null )
		{
			double currentCenter = (Double) canvasImage.get( "currentCenter" );
			double currentWidth = (Double) canvasImage.get( "currentWidth" );
			
			int currentFrame = (Integer) canvasImage.get( "currentFrame" );
			
			double ix1 = irect.getX();
			double iy1 = irect.getY();
			double ix2 = ix1 + irect.getWidth() - 1;
			double iy2 = iy1 + irect.getHeight() - 1;
			
			int vcols = (int) vsize.getX();
			int vrows = (int) vsize.getY();
			
			String dx1 = Double.toString( (double) ix1 / ( metadata.getColumns() - 1 ) );
			String dy1 = Double.toString( (double) iy1 / ( metadata.getRows() - 1 ) );
			String dx2 = Double.toString( (double) ix2 / ( metadata.getColumns() - 1 ) );
			String dy2 = Double.toString( (double) iy2 / ( metadata.getRows() - 1 ) );
			
			/*
			 * if ( ix1 == 0 && iy1 == 0 && ix2 == metadata.getColumns() - 1 &&
			 * iy2 == metadata.getRows() - 1 ) { if ( metadata.getColumns() <
			 * vcols ) vcols = metadata.getColumns(); if ( metadata.getRows() <
			 * vrows ) vrows = metadata.getRows();
			 * 
			 * canvasImage.set( "vsize", new Point( vcols, vrows ) );
			 * setViewRect( canvasImage ); }
			 */

			String region = "region=" + dx1 + "," + dy1 + "," + dx2 + "," + dy2;
			String zoom = "columns=" + vcols + "&rows=" + vrows;
			String zoomIcon = "columns=" + 64 + "&rows=" + 64;
			String content = "contentType=" + user.getCompression();
			String contentIcon = "contentType=image/jpeg";
			String bright = currentWidth > 0 ? "windowCenter=" + currentCenter + "&windowWidth=" + currentWidth : "";
			String frame = "frameNumber=" + currentFrame;
			
			String url = image.getWadoUrl() + "&" + zoom + "&" + region + "&" + content + "&" + bright + "&" + frame;
			String urlIcon = image.getWadoUrl() + "&" + zoomIcon + "&" + region + "&" + contentIcon + "&" + bright + "&" + frame;
			
			HttpImageReference imgRef = new HttpImageReference( app.getServerUrl() + url );
			
			ReportInfo info = new ReportInfo();
			
			info.setHeader( metadata );
			info.setUrl( url );
			info.setIcon( urlIcon );
			
			imagesInfo.put( user.getLogin(), info );

			canvasImage.setOverlays( getViewSpaceOverlays( canvasImage ) );
			canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
			canvasImage.setImage( imgRef );
			canvasImage.setRefresh( true );
		}
	}
	
	double getDouble( String value )
	{
		double temp = 0;
		
		try
		{
			temp = Double.parseDouble( value );
		}
		catch ( Exception e )
		{
		}
		
		return temp;
	}
	
	private void openImage( ImedigCanvas canvasImage, Image image, SeriesTree series, StudyTree study )
	{
		canvasImage.set( "ImageBean", image );
		canvasImage.set( "SeriesBean", series );
		canvasImage.set( "StudyBean", study );
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		params.put( "StudyInstanceUID", study.getStudyData().getStudyInstanceUID() );
		params.put( "SeriesInstanceUID", series.getSeriesData().getSeriesInstanceUID() );
		params.put( "SOPInstanceUID", image.getSOPInstanceUID() );

		try
		{
			ImageHeader metadata = RetrieveManager.getInstance().getMetaData( Settings.PACS_AETitle, Settings.PACS_Host, Settings.PACS_Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );
			
			canvasImage.set( "irect", new Rectangle( 0, 0, metadata.getColumns(), metadata.getRows() ) );
			canvasImage.set( "currentAnnotations", new ArrayList<Annotation>() );
			canvasImage.set( "backStatus", new ArrayList<Status>() );
			canvasImage.set( "metadata", metadata );
			canvasImage.set( "currentCenter", new Double( getDouble( metadata.getWindowCenter() ) ) );
			canvasImage.set( "currentWidth", new Double( getDouble( metadata.getWindowWidth() ) ) );
			canvasImage.set( "currentFrame", new Integer( 0 ) );
			
			setViewRect( canvasImage );
			
			openCurrentImage( canvasImage );
		}
		catch ( Throwable ex )
		{
			MessageDialog messageDialog = new MessageDialog( "Error", ex.getMessage(), MessageDialog.CONTROLS_OK, resourceBundle );
			getApplication().getMainWindow().getContent().add( messageDialog );
		}
	}
	
	ImedigCanvas getFreeCanvas()
	{
		for ( ImedigCanvas canvasImage : canvasImages )
		{
			Image image = (Image) canvasImage.get( "ImageBean" );
			
			if ( image == null )
				return canvasImage;
		}
		
		return null;
	}
	
	private void onOpenImage( ActionEvent e )
	{
		Button b = (Button) e.getSource();
		
		Image image = (Image) b.get( "ImageBean" );
		SeriesTree series = (SeriesTree) b.get( "SeriesBean" );
		StudyTree study = (StudyTree) b.get( "StudyBean" );
		
		ImedigCanvas canvasImage = getActiveCanvas();
		
		if ( canvasImage == null )
			canvasImage = getFreeCanvas();
			
		if ( canvasImage != null )
		{
			openImage( canvasImage, image, series, study );
			
			setActiveCanvas( canvasImage );

			canvasImage = getFreeCanvas();
		
			if ( canvasImage != null )
				setActiveCanvas( canvasImage );
		}
	
		setButtonsState();
	}
	
	@SuppressWarnings( "unchecked" )
	private void zoomOperation( ImedigCanvas canvasImage, int vpx1, int vpy1, int vpx2, int vpy2 )
	{
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		if ( vpx1 < 0 )
			vpx1 = 0;
		if ( vpx1 > vx2 )
			vpx1 = vx2;
		if ( vpy1 < 0 )
			vpy1 = 0;
		if ( vpy1 >= vy2 )
			vpy1 = vy2;
		if ( vpx2 < 0 )
			vpx2 = 0;
		if ( vpx2 >= vx2 )
			vpx2 = vx2;
		if ( vpy2 < 0 )
			vpy2 = 0;
		if ( vpy2 >= vy2 )
			vpy2 = vy2;
		
		if ( vpx1 > vpx2 )
		{
			int temp = vpx1;
			
			vpx1 = vpx2;
			vpx2 = temp;
		}
		
		if ( vpy1 > vpy2 )
		{
			int temp = vpy1;
			
			vpy1 = vpy2;
			vpy2 = temp;
		}
		
		if ( vpx2 - vpx1 > 10 && vpy2 - vpy1 > 10 )
		{
			double mx = (double) ( ix2 - ix1 ) / ( vx2 - vx1 );
			double nx = ix1 - mx * vx1;
			
			double my = (double) ( iy2 - iy1 ) / ( vy2 - vy1 );
			double ny = iy1 - my * vy1;
			
			int nix1 = (int) ( mx * vpx1 + nx );
			int nix2 = (int) ( mx * vpx2 + nx );
			
			int niy1 = (int) ( my * vpy1 + ny );
			int niy2 = (int) ( my * vpy2 + ny );
			
			if ( !replicate )
			{
				ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
				
				double currentCenter = (Double) canvasImage.get( "currentCenter" );
				double currentWidth = (Double) canvasImage.get( "currentWidth" );
				
				int currentFrame = (Integer) canvasImage.get( "currentFrame" );
				
				backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
				
				canvasImage.set( "irect", new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 ) );
				
				setViewRect( canvasImage );
				
				openCurrentImage( canvasImage );
			}
			else
			{
				ImageHeader meta1 = (ImageHeader) canvasImage.get( "metadata" );
				
				for ( ImedigCanvas canvas : canvasImages )
				{
					ImageHeader meta2 = (ImageHeader) canvas.get( "metadata" );
					
					if ( meta2 != null )
					{
						if ( meta1.getColumns() == meta2.getColumns() && meta1.getRows() == meta2.getRows() )
						{
							ArrayList<Status> backStatus = (ArrayList<Status>) canvas.get( "backStatus" );
							
							double currentCenter = (Double) canvas.get( "currentCenter" );
							double currentWidth = (Double) canvas.get( "currentWidth" );
							
							int currentFrame = (Integer) canvas.get( "currentFrame" );
							
							backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
							
							canvas.set( "irect", new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 ) );
							
							setViewRect( canvas );
							
							openCurrentImage( canvas );
						}
					}
				}
			}
		}
	}
	
	Rectangle getRectInViewSpace( ImedigCanvas canvasImage, int ipx1, int ipy1, int ipx2, int ipy2 )
	{
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		double mx = (double) ( vx2 - vx1 ) / ( ix2 - ix1 );
		double nx = vx1 - mx * ix1;
		
		double my = (double) ( vy2 - vy1 ) / ( iy2 - iy1 );
		double ny = vy1 - my * iy1;
		
		double vpx1 = (int) ( mx * ipx1 + nx );
		double vpx2 = (int) ( mx * ipx2 + nx );
		
		double vpy1 = (int) ( my * ipy1 + ny );
		double vpy2 = (int) ( my * ipy2 + ny );
		
		return new Rectangle( (int) vpx1, (int) vpy1, (int) ( vpx2 - vpx1 + 1 ), (int) ( vpy2 - vpy1 + 1 ) );
	}
	
	@SuppressWarnings( "unchecked" )
	String getViewSpaceAnnotations( ImedigCanvas canvasImage )
	{
		ArrayList<Annotation> annotations = (ArrayList<Annotation>) canvasImage.get( "currentAnnotations" );
		
		String ret = "";
		
		for ( Annotation a : annotations )
		{
			Rectangle r = getRectInViewSpace( canvasImage, a.getX1(), a.getY1(), a.getX2(), a.getY2() );
			
			ret += a.getTitle() + "," + r.getX() + "," + r.getY() + "," + ( r.getX() + r.getWidth() ) + "," + ( r.getY() + r.getHeight() ) + "," + a.getText() + "|";
		}
		
		return ret;
	}
	
	String getString( String source )
	{
		return source != null ? source : "";
	}
	
	String getViewSpaceOverlays( ImedigCanvas canvasImage )
	{
		ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
		Image image = (Image) canvasImage.get( "ImageBean" );
		SeriesTree series = (SeriesTree) canvasImage.get( "SeriesBean" );

		int index = getImageIndex( series, image ) + 1;
		int total = series.getImageList().size();
		
		String ret = "";
		
		int size = 16;
		int left = 4;
		int top = 4;
		String color = Colors.STR_METRO_YELLOW;
		String font = "sans-serif";
		
		ret += color + "," + size + "," + font + "," + left + "," + top + "," + getString( metadata.getPatientName() ) + " " + getString( metadata.getPatientSex() ) + "|";
		top += size + 2;
		
		ret += color + "," + size + "," + font + "," + left + "," + top + "," + getString( metadata.getPatientID() ) + "|";
		top += size + 2;
		
		ret += color + "," + size + "," + font + "," + left + "," + top + "," + getString( metadata.getStudyDate() ) + "|";
		top += size + 2;
		
//		ret += color + "," + size + "," + font + "," + left + "," + top + "," + getString( metadata.getInstanceNumber() ) + "|";
//		top += size + 2;
		
		ret += color + "," + size + "," + font + "," + left + "," + top + "," + index + "/" + total + "|";
		top += size + 2;

		return ret;
	}
	
	double getAngle( double x1, double y1, double x2, double y2 )
	{
		double angle = 0;
		
		if ( x2 - x1 == 0 )
			angle = Math.PI / 2;
		else
		{
			angle = Math.atan( ( y2 - y1 ) / ( x2 - x1 ) );
		}
		
		return angle;
	}
	
	double getAngle( double x1, double y1, double x2, double y2, double xp1, double yp1, double xp2, double yp2 )
	{
		double angle = Math.toDegrees( Math.abs( getAngle( x1, y1, x2, y2 ) - getAngle( xp1, yp1, xp2, yp2 ) ) );
		
		return angle > 90 ? 180 - angle : angle;
	}
	
	@SuppressWarnings( "unchecked" )
	private void measureAngleOperation( ImedigCanvas canvasImage, int vpx1, int vpy1, int vpx2, int vpy2 )
	{
		ArrayList<Annotation> annotations = (ArrayList<Annotation>) canvasImage.get( "currentAnnotations" );
		
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		double mx = (double) ( ix2 - ix1 ) / ( vx2 - vx1 );
		double nx = ix1 - mx * vx1;
		
		double my = (double) ( iy2 - iy1 ) / ( vy2 - vy1 );
		double ny = iy1 - my * vy1;
		
		double ipx1 = mx * vpx1 + nx;
		double ipx2 = mx * vpx2 + nx;
		
		double ipy1 = my * vpy1 + ny;
		double ipy2 = my * vpy2 + ny;
		
		if ( firstLine )
		{
			aix1 = ipx1;
			aix2 = ipx2;
			aiy1 = ipy1;
			aiy2 = ipy2;
			
			firstLine = false;

			annotations.add( new DistanceAnnotation( (int) aix1, (int) aiy1, (int) aix2, (int) aiy2, "" ) );

			canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
			canvasImage.setRefresh( false );
		}
		else
		{
			double angle = Utils.roundDouble( getAngle( aix1, aiy1, aix2, aiy2, ipx1, ipy1, ipx2, ipy2 ), 2 );
			double other = 180-angle;
			
			String text = Double.toString( Utils.roundDouble( angle, 2 ) ) + " / " +  other + " grados";
			
			annotations.add( new DistanceAnnotation( (int) ipx1, (int) ipy1, (int) ipx2, (int) ipy2, text ) );
			
			canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
			canvasImage.setRefresh( false );
			
			firstLine = true;
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void measureLineOperation( ImedigCanvas canvasImage, int vpx1, int vpy1, int vpx2, int vpy2 )
	{
		ArrayList<Annotation> annotations = (ArrayList<Annotation>) canvasImage.get( "currentAnnotations" );
		
		ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
		
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		String spacing = metadata.getPixelSpacing();
		
		if ( spacing != null )
		{
			String sp[] = spacing.split( "\\\\" );
			
			if ( sp.length == 2 )
			{
				double mx = (double) ( ix2 - ix1 ) / ( vx2 - vx1 );
				double nx = ix1 - mx * vx1;
				
				double my = (double) ( iy2 - iy1 ) / ( vy2 - vy1 );
				double ny = iy1 - my * vy1;
				
				double ipx1 = mx * vpx1 + nx;
				double ipx2 = mx * vpx2 + nx;
				
				double ipy1 = my * vpy1 + ny;
				double ipy2 = my * vpy2 + ny;
				
				double dx = ( ipx2 - ipx1 ) * Double.parseDouble( sp[0] );
				double dy = ( ipy2 - ipy1 ) * Double.parseDouble( sp[1] );
				
				double distance = Math.sqrt( dx * dx + dy * dy );
				
				String text = Double.toString( Utils.roundDouble( distance, 2 ) ) + " mm";
				
				annotations.add( new DistanceAnnotation( (int) ipx1, (int) ipy1, (int) ipx2, (int) ipy2, text ) );
				
				canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
				canvasImage.setRefresh( false );
			}
			else
			{
				MessageDialog messageDialog = new MessageDialog( "Error", getResString( "NoPixelSizeMsg" ), MessageDialog.CONTROLS_OK, resourceBundle );
				getApplication().getMainWindow().getContent().add( messageDialog );
			}
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void windowOperation( ImedigCanvas canvasImage, int vpx1, int vpy1, int vpx2, int vpy2 )
	{
		ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
		
		double currentCenter = (Double) canvasImage.get( "currentCenter" );
		double currentWidth = (Double) canvasImage.get( "currentWidth" );
		
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		if ( currentWidth > 0 )
		{
			double center = getDouble( metadata.getWindowCenter() );
			double width = getDouble( metadata.getWindowWidth() );
			
			double mw = (double) ( width ) / ( vx2 - vx1 );
			double mc = (double) ( center ) / ( vy2 - vy1 );
			
			double nw = currentWidth + ( mw * ( vpx2 - vpx1 ) );
			double nc = currentCenter - ( mc * ( vpy2 - vpy1 ) );
			
			if ( nc > 0 )
			{
				if ( !replicate )
				{
					int currentFrame = (Integer) canvasImage.get( "currentFrame" );
					
					ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
					
					backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
					
					canvasImage.set( "currentCenter", new Double( nc ) );
					canvasImage.set( "currentWidth", new Double( nw ) );
					
					openCurrentImage( canvasImage );
				}
				else
				{
					ImageHeader meta1 = (ImageHeader) canvasImage.get( "metadata" );
					
					for ( ImedigCanvas canvas : canvasImages )
					{
						ImageHeader meta2 = (ImageHeader) canvas.get( "metadata" );
						
						if ( meta2 != null )
						{
							if ( meta1.getBitsAllocated() == meta2.getBitsAllocated() && meta1.getBitsStored() == meta2.getBitsStored() && meta1.getHighBit() == meta2.getHighBit() && meta1.getPixelRepresentation() == meta2.getPixelRepresentation() && meta1.getSamplesPerPixel() == meta2.getSamplesPerPixel() )
							{
								ArrayList<Status> backStatus = (ArrayList<Status>) canvas.get( "backStatus" );
								
								double bc = (Double) canvas.get( "currentCenter" );
								double bw = (Double) canvas.get( "currentWidth" );
								
								int currentFrame = (Integer) canvas.get( "currentFrame" );
								
								backStatus.add( new Status( ix1, iy1, ix2, iy2, bc, bw, currentFrame ) );
								
								canvas.set( "currentCenter", new Double( nc ) );
								canvas.set( "currentWidth", new Double( nw ) );
								
								openCurrentImage( canvas );
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void panOperation( ImedigCanvas canvasImage, int vpx1, int vpy1, int vpx2, int vpy2 )
	{
		ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
		
		Rectangle vrect = (Rectangle) canvasImage.get( "vrect" );
		Rectangle irect = (Rectangle) canvasImage.get( "irect" );
		
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		int vx1 = (int) vrect.getX();
		int vy1 = (int) vrect.getY();
		int vx2 = (int) ( vx1 + vrect.getWidth() - 1 );
		int vy2 = (int) ( vy1 + vrect.getHeight() - 1 );
		
		if ( vpx1 < 0 )
			vpx1 = 0;
		if ( vpx1 > vx2 )
			vpx1 = vx2;
		if ( vpy1 < 0 )
			vpy1 = 0;
		if ( vpy1 >= vy2 )
			vpy1 = vy2;
		if ( vpx2 < 0 )
			vpx2 = 0;
		if ( vpx2 >= vx2 )
			vpx2 = vx2;
		if ( vpy2 < 0 )
			vpy2 = 0;
		if ( vpy2 >= vy2 )
			vpy2 = vy2;
		
		double mx = (double) ( ix2 - ix1 ) / ( vx2 - vx1 );
		double nx = ix1 - mx * vx1;
		
		double my = (double) ( iy2 - iy1 ) / ( vy2 - vy1 );
		double ny = iy1 - my * vy1;
		
		int ipx1 = (int) ( mx * vpx1 + nx );
		int ipx2 = (int) ( mx * vpx2 + nx );
		int idx = ipx2 - ipx1;
		
		int ipy1 = (int) ( my * vpy1 + ny );
		int ipy2 = (int) ( my * vpy2 + ny );
		int idy = ipy2 - ipy1;
		
		LOG.info( "idx = " + idx + " idy = " + idy );
		
		if ( ix1 - idx > 0 && ix2 - idx < metadata.getColumns() - 1 && iy1 - idy > 0 && iy2 - idy < metadata.getRows() - 1 )
		{
			int nix1 = ix1 - idx;
			int nix2 = ix2 - idx;
			
			int niy1 = iy1 - idy;
			int niy2 = iy2 - idy;
			
			if ( !replicate )
			{
				ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
				
				double currentCenter = (Double) canvasImage.get( "currentCenter" );
				double currentWidth = (Double) canvasImage.get( "currentWidth" );
				
				int currentFrame = (Integer) canvasImage.get( "currentFrame" );
				
				backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
				
				canvasImage.set( "irect", new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 ) );
				
				setViewRect( canvasImage );
				
				openCurrentImage( canvasImage );
			}
			else
			{
				ImageHeader meta1 = (ImageHeader) canvasImage.get( "metadata" );
				
				for ( ImedigCanvas canvas : canvasImages )
				{
					ImageHeader meta2 = (ImageHeader) canvas.get( "metadata" );
					
					if ( meta2 != null )
					{
						if ( meta1.getColumns() == meta2.getColumns() && meta1.getRows() == meta2.getRows() )
						{
							ArrayList<Status> backStatus = (ArrayList<Status>) canvas.get( "backStatus" );
							
							double currentCenter = (Double) canvas.get( "currentCenter" );
							double currentWidth = (Double) canvas.get( "currentWidth" );
							
							int currentFrame = (Integer) canvas.get( "currentFrame" );
							
							backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
							
							canvas.set( "irect", new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 ) );
							
							setViewRect( canvas );
							
							openCurrentImage( canvas );
						}
					}
				}
			}
		}
	}
	
	int getCanvasIndex( ImedigCanvas canvasImage )
	{
		int i = 0;
		
		for ( ImedigCanvas canvas : canvasImages )
		{
			if ( canvas == canvasImage )
				return i;
			
			i++;
		}
		
		return -1;
	}
	
	private void onCanvasAction( ActionEvent e )
	{
		ImedigCanvas canvasImage = (ImedigCanvas) e.getSource();
		
		setActiveCanvas( canvasImage );
		
		if ( canvasImage.get( "ImageBean" ) != null )
		{
			if ( canvasImage.getActionEvent() == 1 )
			{
				int vpx1 = canvasImage.getRoiX1();
				int vpy1 = canvasImage.getRoiY1();
				int vpx2 = canvasImage.getRoiX2();
				int vpy2 = canvasImage.getRoiY2();
				
				if ( vpx1 != vpx2 || vpy1 != vpy2 )
				{
					switch ( currentOperation )
					{
						case OPERATION_ZOOM:
							zoomOperation( canvasImage, vpx1, vpy1, vpx2, vpy2 );
						break;
						
						case OPERATION_PAN:
							panOperation( canvasImage, vpx1, vpy1, vpx2, vpy2 );
						break;
						
						case OPERATION_WINDOW:
							windowOperation( canvasImage, vpx1, vpy1, vpx2, vpy2 );
						break;
						
						case OPERATION_MEASURE_LINE:
							measureLineOperation( canvasImage, vpx1, vpy1, vpx2, vpy2 );
						break;
						
						case OPERATION_MEASURE_ANGLE:
							measureAngleOperation( canvasImage, vpx1, vpy1, vpx2, vpy2 );
						break;
					}
				}
			}
			else if ( canvasImage.getActionEvent() == 2 )
			{
				if ( canvasImage.getWheel() > 0 )
					onNextImage( e );
				else
					onPreviousImage( e );
			}
		}
		
		setButtonsState();
	}
	
	void setCurrentOperationOnCanvas()
	{
		for ( ImedigCanvas canvas : canvasImages )
		{
			canvas.setSelectionMode( currentSelMode );
			canvas.setShowLine( currentShowLine );
			canvas.setCursor( currentCursor );
			canvas.setCursorOffsetX( currentCursorOffsetX );
			canvas.setCursorOffsetY( currentCursorOffsetY );
			canvas.setRefresh( false );
		}

		setButtonsState();
	}
	
	private void onZoom( ActionEvent e )
	{
		currentOperation = OPERATION_ZOOM;
		currentSelMode = ImedigCanvas.MODE_RECT;
		currentShowLine = true;
		currentCursor = "crosshair";
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		setCurrentOperationOnCanvas();
	}
	
	private void onPan( ActionEvent e )
	{
		currentOperation = OPERATION_PAN;
		currentSelMode = ImedigCanvas.MODE_LINE;
		currentShowLine = false;
//		currentCursor = cursorPan;
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		setCurrentOperationOnCanvas();
	}
	
	@SuppressWarnings( "unchecked" )
	private void onZoomBack( ActionEvent e )
	{
		if ( replicate )
		{
			for ( ImedigCanvas canvas : canvasImages )
			{
				ArrayList<Status> backStatus = (ArrayList<Status>) canvas.get( "backStatus" );
				
				if ( backStatus != null && backStatus.size() > 0 )
				{
					Status status = backStatus.get( backStatus.size() - 1 );
					
					backStatus.remove( backStatus.size() - 1 );
					
					canvas.set( "currentCenter", new Double( status.windowCenter ) );
					canvas.set( "currentWidth", new Double( status.windowWidth ) );
					canvas.set( "currentFrame", new Integer( status.frame ) );
					canvas.set( "irect", new Rectangle( status.ix1, status.iy1, status.ix2 - status.ix1 + 1, status.iy2 - status.iy1 + 1 ) );
					
					setViewRect( canvas );
					
					openCurrentImage( canvas );
				}
			}
		}
		else
		{
			ImedigCanvas canvasImage = getActiveCanvas();
			
			if ( canvasImage != null )
			{
				ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
				
				if ( backStatus != null && backStatus.size() > 0 )
				{
					Status status = backStatus.get( backStatus.size() - 1 );
					
					backStatus.remove( backStatus.size() - 1 );
					
					canvasImage.set( "currentCenter", new Double( status.windowCenter ) );
					canvasImage.set( "currentWidth", new Double( status.windowWidth ) );
					canvasImage.set( "currentFrame", new Integer( status.frame ) );
					canvasImage.set( "irect", new Rectangle( status.ix1, status.iy1, status.ix2 - status.ix1 + 1, status.iy2 - status.iy1 + 1 ) );
					
					setViewRect( canvasImage );
					
					openCurrentImage( canvasImage );
				}
			}
		}

		setButtonsState();
	}
	
	private void onBrightContrast( ActionEvent e )
	{
		currentOperation = OPERATION_WINDOW;
		currentSelMode = ImedigCanvas.MODE_RECT;
		currentShowLine = false;
		currentCursor = "move";
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		setCurrentOperationOnCanvas();
	}
	
	ImedigCanvas getActiveCanvas()
	{
		return activeCanvas;
	}
	
	@SuppressWarnings( "unchecked" )
	void setButtonsState()
	{
//		setButtonState( buttonPrevImage, false );
//		setButtonState( buttonNextImage, false );
		setButtonState( buttonDistance, false );
//		setButtonState( buttonPrevFrame, false );
//		setButtonState( buttonNextFrame, false );
		setButtonState( buttonUndo, false );
//		setButtonState( buttonClear, false );

		setButtonState( buttonAngle, false );
		setButtonState( buttonZoom, false );
//		setButtonState( buttonPan, false );
		setButtonState( buttonContrast, false );

		setButtonState( buttonNone, currentOperation != OPERATION_NONE );
//		setButtonState( buttonLink, studies.size() > 0 );
		setButtonState( buttonOpen, user.getUid() == null );
		setButtonState( buttonClose, user.getUid() == null && studies.size() > 0 );
		
		if ( activeCanvas != null )
		{
			Image image = (Image)activeCanvas.get( "ImageBean" );
			
			if ( image != null )
			{
				SeriesTree series = (SeriesTree)activeCanvas.get( "SeriesBean" );
				ImageHeader metadata = (ImageHeader)activeCanvas.get( "metadata" );
				ArrayList<Status> backStatus = (ArrayList<Status>) activeCanvas.get( "backStatus" );
				ArrayList<Annotation> annotations = (ArrayList<Annotation>) activeCanvas.get( "currentAnnotations" );
				int frame = (Integer) activeCanvas.get( "currentFrame" );
				int index = getImageIndex( series, image );
				
				boolean prevImage = index > 0;
				boolean nextImage = index < series.getImageList().size() - 1;
				
				boolean distance = !metadata.getPixelSpacing().isEmpty();
				
				boolean prevFrame = frame > 0;
				boolean nextFrame = frame < metadata.getNumberOfFrames() - 1;
				
				boolean back = backStatus.size() > 0; 
				boolean clear = annotations.size() > 0; 
				
//				setButtonState( buttonPrevImage, prevImage );
//				setButtonState( buttonNextImage, nextImage );
				
				setButtonState( buttonDistance, distance );
				
//				setButtonState( buttonPrevFrame, prevFrame );
//				setButtonState( buttonNextFrame, nextFrame );
	
				setButtonState( buttonUndo, back );
//				setButtonState( buttonClear, clear );
				
				setButtonState( buttonAngle, true );
				setButtonState( buttonZoom, true );
//				setButtonState( buttonPan, true );
				setButtonState( buttonContrast, true );
			}
			else
			{
//				setButtonState( buttonPrevImage, false );
//				setButtonState( buttonNextImage, false );
				setButtonState( buttonDistance, false );
//				setButtonState( buttonPrevFrame, false );
//				setButtonState( buttonNextFrame, false );
				setButtonState( buttonUndo, false );
//				setButtonState( buttonClear, false );

				setButtonState( buttonAngle, false );
				setButtonState( buttonZoom, false );
				//setButtonState( buttonPan, false );
				setButtonState( buttonContrast, false );
			}
		}
	}
	
	void setActiveCanvas( ImedigCanvas canvasImage )
	{
		ImedigCanvas previous = activeCanvas;
		
		if ( previous != null && canvasImage != null )
			previous.setActive( false );
		
		if ( canvasImage != null )
		{
			activeCanvas = canvasImage;
		
			activeCanvas.setActive( true );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void onPreviousFrame( ActionEvent e )
	{
		ImedigCanvas canvasImage = getActiveCanvas();
		
		if ( canvasImage != null && canvasImage.get( "ImageBean" ) != null )
		{
			double currentCenter = (Double) canvasImage.get( "currentCenter" );
			double currentWidth = (Double) canvasImage.get( "currentWidth" );
			
			Rectangle irect = (Rectangle) canvasImage.get( "irect" );
			
			ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
			
			int currentFrame = (Integer) canvasImage.get( "currentFrame" );
			
			int ix1 = (int) irect.getX();
			int iy1 = (int) irect.getY();
			int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
			int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
			
			if ( currentFrame > 0 )
			{
				backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
				
				canvasImage.set( "currentFrame", new Integer( currentFrame - 1 ) );
				
				openCurrentImage( canvasImage );
			}
		}

		setButtonsState();
	}
	
	@SuppressWarnings( "unchecked" )
	private void onNextFrame( ActionEvent e )
	{
		ImedigCanvas canvasImage = getActiveCanvas();
		
		if ( canvasImage != null && canvasImage.get( "ImageBean" ) != null )
		{
			ImageHeader metadata = (ImageHeader) canvasImage.get( "metadata" );
			
			double currentCenter = (Double) canvasImage.get( "currentCenter" );
			double currentWidth = (Double) canvasImage.get( "currentWidth" );
			
			Rectangle irect = (Rectangle) canvasImage.get( "irect" );
			
			ArrayList<Status> backStatus = (ArrayList<Status>) canvasImage.get( "backStatus" );
			
			int currentFrame = (Integer) canvasImage.get( "currentFrame" );
			
			int ix1 = (int) irect.getX();
			int iy1 = (int) irect.getY();
			int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
			int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
			
			if ( currentFrame < metadata.getNumberOfFrames() )
			{
				backStatus.add( new Status( ix1, iy1, ix2, iy2, currentCenter, currentWidth, currentFrame ) );
				
				canvasImage.set( "currentFrame", new Integer( currentFrame + 1 ) );
				
				openCurrentImage( canvasImage );
			}
		}

		setButtonsState();
	}
	
	private void onDistance( ActionEvent e )
	{
		currentOperation = OPERATION_MEASURE_LINE;
		currentSelMode = ImedigCanvas.MODE_LINE;
		currentShowLine = true;
		currentCursor = "crosshair";
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 6;
		
		setCurrentOperationOnCanvas();
	}
	
	private void onAngle( ActionEvent e )
	{
		currentOperation = OPERATION_MEASURE_ANGLE;
		currentSelMode = ImedigCanvas.MODE_LINE;
		currentShowLine = true;
		currentCursor = "crosshair";
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		setCurrentOperationOnCanvas();
		
		firstLine = true;
	}
	
	@SuppressWarnings( "unchecked" )
	private void onClearAnnotations( ActionEvent e )
	{
		ImedigCanvas canvasImage = getActiveCanvas();
		
		if ( canvasImage != null )
		{
			ArrayList<Annotation> annotations = (ArrayList<Annotation>) canvasImage.get( "currentAnnotations" );
			
			if ( annotations != null )
			{
				annotations.clear();
				
				canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
			}
		}

		setButtonsState();
	}
	
	private void onNone( ActionEvent e )
	{
		currentOperation = OPERATION_NONE;
		currentSelMode = ImedigCanvas.MODE_NONE;
		currentShowLine = false;
		currentCursor = "default";
		currentCursorOffsetX = 0;
		currentCursorOffsetY = 0;
		
		setCurrentOperationOnCanvas();
	}
	
	private void onOpenStudies()
	{
		if ( queryDlg == null )
			queryDlg = new QueryDlg( this, app, user );
		
		queryDlg.clearSelected();
		
		getApplication().getMainWindow().getContent().add( queryDlg );
		
		if ( firstTime )
		{
			firstTime = false;
			queryDlg.onQuery();
		}
	}
	
	public void notifyCommand( String command, ModalContainer child )
	{
		if ( child == queryDlg && "open".equals( command ) )
			openStudies( queryDlg.getSelectedStudies() );
	
		setButtonsState();
	}
	
/*	public void setQueryUrl( String queryUrl )
	{
		this.queryUrl = queryUrl;
	}
	
	public String getQueryUrl()
	{
		return queryUrl;
	}*/
	
	private void onPaneResize( ActionEvent e )
	{
		for ( ImedigCanvas canvasImage : canvasImages )
		{
			ContentPaneEx contentPaneEx1 = (ContentPaneEx) canvasImage.getParent();
			
			int rows = contentPaneEx1.getHeight();
			int cols = contentPaneEx1.getWidth();
			
			canvasImage.setWidth( cols );
			canvasImage.setHeight( rows );
			
			canvasImage.set( "vsize", new Point( cols, rows ) );
			
			canvasImage.setActive( canvasImage == activeCanvas );
			
			setViewRect( canvasImage );
			
			openCurrentImage( canvasImage );
		}
		
		if ( selectedStudies != null && selectedStudies.size() != 0 )
		{
			openStudies( selectedStudies );
			
			selectedStudies = null;

			setButtonsState();
		}
		else if ( firstTime )
			onOpenStudies();
	}
	
	ImedigCanvas createCanvas()
	{
		ImedigCanvas canvasImage = new ImedigCanvas();
		canvasImage.addActionListener( new ActionListener()
		{
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed( ActionEvent e )
			{
				onCanvasAction( e );
			}
		} );
		
		canvasImage.set( "ImageBean", null );
		canvasImage.set( "irect", null );
		canvasImage.set( "vrect", null );
		canvasImage.set( "vsize", null );
		canvasImage.set( "currentAnnotations", null );
		canvasImage.set( "backStatus", null );
		canvasImage.set( "metadata", null );
		canvasImage.set( "currentCenter", null );
		canvasImage.set( "currentWidth", null );
		canvasImage.set( "currentFrame", null );
		
		canvasImage.setWidth( 0 );
		canvasImage.setHeight( 0 );
		canvasImage.setForeground( Color.YELLOW );
		canvasImage.setBackground( Color.LIGHTGRAY );
		canvasImage.setSelectionMode( currentSelMode );
		canvasImage.setShowLine( currentShowLine );
		canvasImage.setActive( false );
		canvasImage.setInactiveColor( "#" + Integer.toHexString( Color.LIGHTGRAY.getRgb() ) );
		canvasImage.setActiveColor( Colors.STR_METRO_LIME );
		
		canvasImage.setImage( null );
		canvasImage.setCursor( currentCursor );

		//canvasImage.setCursorOffsetX( currentCursorOffsetX );
		//canvasImage.setCursorOffsetY( currentCursorOffsetY );
		
		return canvasImage;
	}
	
	ContentPaneEx createContentPaneEx()
	{
		ContentPaneEx contentPaneEx1 = new ContentPaneEx();
		contentPaneEx1.setOverflow( ContentPane.OVERFLOW_HIDDEN );
		contentPaneEx1.addActionListener( new ActionListener()
		{
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed( ActionEvent e )
			{
				onPaneResize( e );
			}
		} );
		
		return contentPaneEx1;
	}
	
	SplitPane createSplitPane( boolean horizontal, int percent )
	{
		SplitPane splitPane1 = new SplitPane();
		
		splitPane1.setStyleName( "Default" );
		splitPane1.setOrientation( horizontal ? SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM : SplitPane.ORIENTATION_HORIZONTAL_LEFT_RIGHT );
		splitPane1.setResizable( true );
		splitPane1.setSeparatorWidth( new Extent( 4, Extent.PX ) );
		splitPane1.setSeparatorHeight( new Extent( 4, Extent.PX ) );
		splitPane1.setSeparatorPosition( new Extent( percent, Extent.PERCENT ) );
		
		return splitPane1;
	}
	
	void createImageGrid1x1()
	{
		setActiveCanvas( null );
		canvasImages.clear();
		contentPaneGrid.removeAll();
		
		ImedigCanvas canvasImage1 = createCanvas();
		ContentPaneEx contentPaneEx1 = createContentPaneEx();
		
		contentPaneEx1.add( canvasImage1 );
		contentPaneGrid.add( contentPaneEx1 );
		
		canvasImages.add( canvasImage1 );
	}
	
	void createImageGrid2x1()
	{
		setActiveCanvas( null );
		contentPaneGrid.removeAll();
		canvasImages.clear();
		
		ImedigCanvas canvasImage1 = createCanvas();
		ImedigCanvas canvasImage2 = createCanvas();
		
		ContentPaneEx contentPaneEx1 = createContentPaneEx();
		ContentPaneEx contentPaneEx2 = createContentPaneEx();
		
		SplitPane splitPane1 = createSplitPane( false, 50 );
		
		contentPaneEx1.add( canvasImage1 );
		contentPaneEx2.add( canvasImage2 );
		
		splitPane1.add( contentPaneEx1 );
		splitPane1.add( contentPaneEx2 );
		
		contentPaneGrid.add( splitPane1 );
		
		canvasImages.add( canvasImage1 );
		canvasImages.add( canvasImage2 );
	}
	
	void createImageGrid2x2()
	{
		setActiveCanvas( null );
		contentPaneGrid.removeAll();
		canvasImages.clear();
		
		ImedigCanvas canvasImage1 = createCanvas();
		ImedigCanvas canvasImage2 = createCanvas();
		ImedigCanvas canvasImage3 = createCanvas();
		ImedigCanvas canvasImage4 = createCanvas();
		
		ContentPaneEx contentPaneEx1 = createContentPaneEx();
		ContentPaneEx contentPaneEx2 = createContentPaneEx();
		ContentPaneEx contentPaneEx3 = createContentPaneEx();
		ContentPaneEx contentPaneEx4 = createContentPaneEx();
		
		SplitPane splitPane1 = createSplitPane( true, 50 );
		SplitPane splitPane2 = createSplitPane( false, 50 );
		SplitPane splitPane3 = createSplitPane( false, 50 );
		
		contentPaneEx1.add( canvasImage1 );
		contentPaneEx2.add( canvasImage2 );
		contentPaneEx3.add( canvasImage3 );
		contentPaneEx4.add( canvasImage4 );
		
		splitPane1.add( splitPane2 );
		splitPane1.add( splitPane3 );
		
		splitPane2.add( contentPaneEx1 );
		splitPane2.add( contentPaneEx2 );
		splitPane3.add( contentPaneEx3 );
		splitPane3.add( contentPaneEx4 );
		
		contentPaneGrid.add( splitPane1 );
		
		canvasImages.add( canvasImage1 );
		canvasImages.add( canvasImage2 );
		canvasImages.add( canvasImage3 );
		canvasImages.add( canvasImage4 );
	}
	
	void createImageGrid3x2()
	{
		setActiveCanvas( null );
		contentPaneGrid.removeAll();
		canvasImages.clear();
		
		ImedigCanvas canvasImage1 = createCanvas();
		ImedigCanvas canvasImage2 = createCanvas();
		ImedigCanvas canvasImage3 = createCanvas();
		ImedigCanvas canvasImage4 = createCanvas();
		ImedigCanvas canvasImage5 = createCanvas();
		ImedigCanvas canvasImage6 = createCanvas();
		
		ContentPaneEx contentPaneEx1 = createContentPaneEx();
		ContentPaneEx contentPaneEx2 = createContentPaneEx();
		ContentPaneEx contentPaneEx3 = createContentPaneEx();
		ContentPaneEx contentPaneEx4 = createContentPaneEx();
		ContentPaneEx contentPaneEx5 = createContentPaneEx();
		ContentPaneEx contentPaneEx6 = createContentPaneEx();
		
		contentPaneEx1.add( canvasImage1 );
		contentPaneEx2.add( canvasImage2 );
		contentPaneEx3.add( canvasImage3 );
		contentPaneEx4.add( canvasImage4 );
		contentPaneEx5.add( canvasImage5 );
		contentPaneEx6.add( canvasImage6 );
		
		SplitPane splitPane1 = createSplitPane( true, 50 );
		
		SplitPane splitPane2 = createSplitPane( false, 33 );
		SplitPane splitPane3 = createSplitPane( false, 33 );
		
		SplitPane splitPane4 = createSplitPane( false, 50 );
		SplitPane splitPane5 = createSplitPane( false, 50 );
		
		splitPane1.add( splitPane2 );
		splitPane1.add( splitPane3 );
		
		splitPane2.add( contentPaneEx1 );
		splitPane2.add( splitPane4 );
		splitPane4.add( contentPaneEx2 );
		splitPane4.add( contentPaneEx3 );
		
		splitPane3.add( contentPaneEx4 );
		splitPane3.add( splitPane5 );
		splitPane5.add( contentPaneEx5 );
		splitPane5.add( contentPaneEx6 );
		
		contentPaneGrid.add( splitPane1 );
		
		canvasImages.add( canvasImage1 );
		canvasImages.add( canvasImage2 );
		canvasImages.add( canvasImage3 );
		canvasImages.add( canvasImage4 );
		canvasImages.add( canvasImage5 );
		canvasImages.add( canvasImage6 );
	}
	
	public void createImageGrid()
	{
		createImageGrid1x1();
		
		ImedigCanvas freeCanvas = getFreeCanvas();
		
		if ( freeCanvas != null )
			setActiveCanvas( freeCanvas );
		else
			setActiveCanvas( canvasImages.get( 0 ) );
		
		setButtonsState();
	}
	
	private void onGrid1x1( ActionEvent e )
	{
		ImedigCanvas canvas = canvasImages.get( 0 );
		
		Image image = (Image) canvas.get( "ImageBean" );
		SeriesTree series = (SeriesTree) canvas.get( "SeriesBean" );
		StudyTree study = (StudyTree) canvas.get( "StudyBean" );
		
		createImageGrid1x1();
		
		if ( image != null )
			openImage( canvasImages.get( 0 ), image, series, study );
		
		ImedigCanvas freeCanvas = getFreeCanvas();
		
		if ( freeCanvas != null )
			setActiveCanvas( freeCanvas );
		else
			setActiveCanvas( canvasImages.get( 0 ) );

		setButtonsState();
	}
	
	private void onGrid2x1( ActionEvent e )
	{
		ImedigCanvas canvas0 = canvasImages.get( 0 );
		ImedigCanvas canvas1 = canvasImages.size() > 1 ? canvasImages.get( 1 ) : null;
		
		Image image0 = (Image) canvas0.get( "ImageBean" );
		SeriesTree series0 = (SeriesTree) canvas0.get( "SeriesBean" );
		StudyTree study0 = (StudyTree) canvas0.get( "StudyBean" );
		
		Image image1 = canvas1 != null ? (Image) canvas1.get( "ImageBean" ) : null;
		SeriesTree series1 = canvas1 != null ? (SeriesTree) canvas1.get( "SeriesBean" ) : null;
		StudyTree study1 = canvas1 != null ? (StudyTree) canvas1.get( "StudyBean" ) : null;
		
		createImageGrid2x1();
		
		if ( image0 != null )
			openImage( canvasImages.get( 0 ), image0, series0, study0 );
		if ( image1 != null )
			openImage( canvasImages.get( 1 ), image1, series1, study1 );
		
		ImedigCanvas freeCanvas = getFreeCanvas();
		
		if ( freeCanvas != null )
			setActiveCanvas( freeCanvas );
		else
			setActiveCanvas( canvasImages.get( 0 ) );

		setButtonsState();
	}
	
	private void onGrid2x2( ActionEvent e )
	{
		ImedigCanvas canvas0 = canvasImages.get( 0 );
		ImedigCanvas canvas1 = canvasImages.size() > 1 ? canvasImages.get( 1 ) : null;
		ImedigCanvas canvas2 = canvasImages.size() > 2 ? canvasImages.get( 2 ) : null;
		ImedigCanvas canvas3 = canvasImages.size() > 3 ? canvasImages.get( 3 ) : null;
		
		Image image0 = (Image) canvas0.get( "ImageBean" );
		SeriesTree series0 = (SeriesTree) canvas0.get( "SeriesBean" );
		StudyTree study0 = (StudyTree) canvas0.get( "StudyBean" );

		Image image1 = canvas1 != null ? (Image) canvas1.get( "ImageBean" ) : null;
		SeriesTree series1 = canvas1 != null ? (SeriesTree) canvas1.get( "SeriesBean" ) : null;
		StudyTree study1 = canvas1 != null ? (StudyTree) canvas1.get( "StudyBean" ) : null;

		Image image2 = canvas2 != null ? (Image) canvas2.get( "ImageBean" ) : null;
		SeriesTree series2 = canvas2 != null ? (SeriesTree) canvas2.get( "SeriesBean" ) : null;
		StudyTree study2 = canvas2 != null ? (StudyTree) canvas2.get( "StudyBean" ) : null;

		Image image3 = canvas3 != null ? (Image) canvas3.get( "ImageBean" ) : null;
		SeriesTree series3 = canvas3 != null ? (SeriesTree) canvas3.get( "SeriesBean" ) : null;
		StudyTree study3 = canvas3 != null ? (StudyTree) canvas3.get( "StudyBean" ) : null;
		
		createImageGrid2x2();
		
		if ( image0 != null )
			openImage( canvasImages.get( 0 ), image0, series0, study0 );
		if ( image1 != null )
			openImage( canvasImages.get( 1 ), image1, series1, study1 );
		if ( image2 != null )
			openImage( canvasImages.get( 2 ), image2, series2, study2 );
		if ( image3 != null )
			openImage( canvasImages.get( 3 ), image3, series3, study3 );

		ImedigCanvas freeCanvas = getFreeCanvas();
		
		if ( freeCanvas != null )
			setActiveCanvas( freeCanvas );
		else
			setActiveCanvas( canvasImages.get( 0 ) );

		setButtonsState();
	}
	
	private void onGrid3x2( ActionEvent e )
	{
		ImedigCanvas canvas0 = canvasImages.get( 0 );
		ImedigCanvas canvas1 = canvasImages.size() > 1 ? canvasImages.get( 1 ) : null;
		ImedigCanvas canvas2 = canvasImages.size() > 2 ? canvasImages.get( 2 ) : null;
		ImedigCanvas canvas3 = canvasImages.size() > 3 ? canvasImages.get( 3 ) : null;
		ImedigCanvas canvas4 = canvasImages.size() > 4 ? canvasImages.get( 4 ) : null;
		ImedigCanvas canvas5 = canvasImages.size() > 5 ? canvasImages.get( 5 ) : null;
		
		Image image0 = (Image) canvas0.get( "ImageBean" );
		SeriesTree series0 = (SeriesTree) canvas0.get( "SeriesBean" );
		StudyTree study0 = (StudyTree) canvas0.get( "StudyBean" );

		Image image1 = canvas1 != null ? (Image) canvas1.get( "ImageBean" ) : null;
		SeriesTree series1 = canvas1 != null ? (SeriesTree) canvas1.get( "SeriesBean" ) : null;
		StudyTree study1 = canvas1 != null ? (StudyTree) canvas1.get( "StudyBean" ) : null;

		Image image2 = canvas2 != null ? (Image) canvas2.get( "ImageBean" ) : null;
		SeriesTree series2 = canvas2 != null ? (SeriesTree) canvas2.get( "SeriesBean" ) : null;
		StudyTree study2 = canvas2 != null ? (StudyTree) canvas2.get( "StudyBean" ) : null;

		Image image3 = canvas3 != null ? (Image) canvas3.get( "ImageBean" ) : null;
		SeriesTree series3 = canvas3 != null ? (SeriesTree) canvas3.get( "SeriesBean" ) : null;
		StudyTree study3 = canvas3 != null ? (StudyTree) canvas3.get( "StudyBean" ) : null;
		
		Image image4 = canvas4 != null ? (Image) canvas4.get( "ImageBean" ) : null;
		SeriesTree series4 = canvas4 != null ? (SeriesTree) canvas4.get( "SeriesBean" ) : null;
		StudyTree study4 = canvas4 != null ? (StudyTree) canvas4.get( "StudyBean" ) : null;

		Image image5 = canvas5 != null ? (Image) canvas5.get( "ImageBean" ) : null;
		SeriesTree series5 = canvas5 != null ? (SeriesTree) canvas5.get( "SeriesBean" ) : null;
		StudyTree study5 = canvas5 != null ? (StudyTree) canvas5.get( "StudyBean" ) : null;
		
		createImageGrid3x2();
		
		if ( image0 != null )
			openImage( canvasImages.get( 0 ), image0, series0, study0 );
		if ( image1 != null )
			openImage( canvasImages.get( 1 ), image1, series1, study1 );
		if ( image2 != null )
			openImage( canvasImages.get( 2 ), image2, series2, study2 );
		if ( image3 != null )
			openImage( canvasImages.get( 3 ), image3, series3, study3 );
		if ( image4 != null )
			openImage( canvasImages.get( 4 ), image4, series4, study4 );
		if ( image5 != null )
			openImage( canvasImages.get( 5 ), image5, series5, study5 );

		ImedigCanvas freeCanvas = getFreeCanvas();
		
		if ( freeCanvas != null )
			setActiveCanvas( freeCanvas );
		else
			setActiveCanvas( canvasImages.get( 0 ) );

		setButtonsState();
	}
	
	private void onReplicate( ActionEvent e )
	{
		replicate = !replicate;
		
//		buttonLink.setIcon( replicate ? iconLink : iconLinkBreak );
//		buttonLink.setToolTipText( getResString( replicate ? "LinkBreak" : "LinkUnbreak" ) );
	}
	
	private void clearArea()
	{
		for ( ImedigCanvas canvasImage : canvasImages )
		{
			canvasImage.set( "ImageBean", null );
			canvasImage.setImage( null );
			canvasImage.setRefresh( true );
		}
		
		setActiveCanvas( getFreeCanvas() );
	}
	
	private void onClear( ActionEvent e )
	{
		clearArea();
	}
	
	private void closeStudies()
	{
		clearArea();
		
		imagesThumbnail.removeAll();
		
		studies.clear();

		setButtonsState();
	}
	
	private void onCloseStudies( ActionEvent e )
	{
		closeStudies();
	}
	
	StudyTree getFirstStudy()
	{
		if ( studies.size() != 0 )
			return studies.get( 0 );
		
		return null;
	}
	
	StudyTree getPreviousStudy( StudyTree study )
	{
		for ( int i = studies.size() - 1; i > 0; i-- )
		{
			if ( studies.get( i ) == study )
				return studies.get( i - 1 );
		}
		
		return null;
	}
	
	StudyTree getNextStudy( StudyTree study )
	{
		for ( int i = 0; i < studies.size() - 1; i++ )
		{
			if ( studies.get( i ) == study )
				return studies.get( i + 1 );
		}
		
		return null;
	}
	
	SeriesTree getFirstSeries( StudyTree study )
	{
		List<SeriesTree> seriesList = study.getSeriesList();
		
		if ( seriesList.size() != 0 )
			return seriesList.get( 0 );
		
		return null;
	}
	
	SeriesTree getLastSeries( StudyTree study )
	{
		List<SeriesTree> seriesList = study.getSeriesList();
		
		if ( seriesList.size() != 0 )
			return seriesList.get( seriesList.size() - 1 );
		
		return null;
	}
	
	SeriesTree getPreviousSeries( StudyTree study, SeriesTree series )
	{
		List<SeriesTree> seriesList = study.getSeriesList();
		
		for ( int i = seriesList.size() - 1; i > 0; i-- )
		{
			if ( seriesList.get( i ) == series )
				return seriesList.get( i - 1 );
		}
		
		return null;
	}
	
	SeriesTree getNextSeries( StudyTree study, SeriesTree series )
	{
		List<SeriesTree> seriesList = study.getSeriesList();
		
		for ( int i = 0; i < seriesList.size() - 1; i++ )
		{
			if ( seriesList.get( i ) == series )
				return seriesList.get( i + 1 );
		}
		
		return null;
	}
	
	Image getFirstImage( SeriesTree series )
	{
		List<Image> imageList = series.getImageList();
		
		if ( imageList.size() != 0 )
			return imageList.get( 0 );
		
		return null;
	}
	
	Image getLastImage( SeriesTree series )
	{
		List<Image> imageList = series.getImageList();
		
		if ( imageList.size() != 0 )
			return imageList.get( imageList.size() - 1 );
		
		return null;
	}
	
	Image getPreviousImage( SeriesTree series, Image image )
	{
		if ( series != null && image != null )
		{
			List<Image> imageList = series.getImageList();
			
			for ( int i = imageList.size() - 1; i > 0; i-- )
			{
				if ( imageList.get( i ) == image )
					return imageList.get( i - 1 );
			}
		}
		
		return null;
	}
	
	Image getNextImage( SeriesTree series, Image image )
	{
		if ( series != null && image != null )
		{
			List<Image> imageList = series.getImageList();
			
			for ( int i = 0; i < imageList.size() - 1; i++ )
			{
				if ( imageList.get( i ) == image )
					return imageList.get( i + 1 );
			}
		}
		
		return null;
	}
	
	int getImageIndex( SeriesTree series, Image image )
	{
		if ( series != null && image != null )
			return series.getImageList().indexOf( image );
		
		return 0;
	}
	
	private void onPreviousImage( ActionEvent e )
	{
		if ( activeCanvas != null )
		{
			Image image = (Image) activeCanvas.get( "ImageBean" );
			SeriesTree series = (SeriesTree) activeCanvas.get( "SeriesBean" );
			StudyTree study = (StudyTree) activeCanvas.get( "StudyBean" );
			
			Image next = getPreviousImage( series, image );
			
			if ( next != null )
				openImage( activeCanvas, next, series, study );
		}

		setButtonsState();
	}
	
	private void onNextImage( ActionEvent e )
	{
		if ( activeCanvas != null )
		{
			Image image = (Image) activeCanvas.get( "ImageBean" );
			SeriesTree series = (SeriesTree) activeCanvas.get( "SeriesBean" );
			StudyTree study = (StudyTree) activeCanvas.get( "StudyBean" );
			
			Image next = getNextImage( series, image );
			
			if ( next != null )
				openImage( activeCanvas, next, series, study );
		}

		setButtonsState();
	}
	
	public void setButtonSize( int buttonSize )
	{
		this.buttonSize = buttonSize;
	}
	
	public int getButtonSize()
	{
		return buttonSize;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser( User user )
	{
		this.user = user;
	}
}
