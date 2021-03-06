package es.pryades.imedig.viewer.components.image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.fabricjs.ChainOfCommand;
import es.pryades.fabricjs.FabricJs;
import es.pryades.fabricjs.config.FigureConfiguration;
import es.pryades.fabricjs.config.LoaderConfiguration;
import es.pryades.fabricjs.config.NotesConfiguration;
import es.pryades.fabricjs.config.RulerConfiguration;
import es.pryades.fabricjs.data.Note;
import es.pryades.fabricjs.data.Point;
import es.pryades.fabricjs.enums.CanvasAction;
import es.pryades.fabricjs.enums.FigureAlignment;
import es.pryades.fabricjs.enums.FontWeight;
import es.pryades.fabricjs.enums.RulerPosition;
import es.pryades.fabricjs.enums.SpinnerPosition;
import es.pryades.fabricjs.enums.SpinnerSpeed;
import es.pryades.fabricjs.enums.StrokeLineCap;
import es.pryades.fabricjs.enums.TextAlign;
import es.pryades.fabricjs.geometry.Figure;
import es.pryades.fabricjs.geometry.Ruler;
import es.pryades.fabricjs.listeners.DrawFigureListener;
import es.pryades.fabricjs.listeners.MouseWheelListener;
import es.pryades.fabricjs.listeners.ResizeListener;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.actions.AddFigure;
import es.pryades.imedig.viewer.actions.AddToUndoAction;
import es.pryades.imedig.viewer.actions.ChangeImageFrame;
import es.pryades.imedig.viewer.actions.DisableDistanceAction;
import es.pryades.imedig.viewer.actions.EnumActions;
import es.pryades.imedig.viewer.actions.NotFigures;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.viewer.exceptions.OperationException;
import es.pryades.imedig.wado.retrieve.RetrieveManager;
import lombok.Getter;

public class ImageCanvas extends CssLayout {
	private static final long serialVersionUID = 1646462855404317204L;

	private static final Logger LOG = Logger.getLogger(ImageCanvas.class);

	private User user;

	private FabricJs canvas;
	private FigureConfiguration defaultConfiguration;
	private List<Figure> imagenFigures;
	@Getter
	private ImageData imageData = null;
	@Getter
	private EnumActions currentAction = EnumActions.NONE; 
	private Rectangle imageRect;
	private Rectangle viewRect;
	private Double currentCenter; 
	private Double currentWidth;
	private Integer numberOfFrames = 1;
	private Integer currentFrame = 0;
	private String currentSerie = "";
	private ImageHeader imageHeader;
	private Stack<ImageStatus> back;
	
	private final ImedigContext context;
	
	private final ListenerAction listenerAction;
	
	private final ImageSerieNavigator imageDataNavigator;
	
	private static Map<EnumActions, FigureConfiguration> configurations;
	private static LoaderConfiguration loadingConfiguration;
	
	private static final Integer REFERENCE_IN_mm = 50;
	
	private Map<ImageData, List<Figure>> imageDataFigures = new HashMap<>();
	private Map<String, Stack<ImageStatus>> serieStatus = new HashMap<>();
	
	private CssLayout changeFrame;
	private Button btnFrameFirst;
	private Button btnFramePrior;
	private Button btnFrameNext;
	private Button btnFrameLast;
	
	private final boolean showFullScreen;

	@Getter
	private ReportInfo reportInfo;

	public ImageCanvas( ImedigContext context, User user, ListenerAction listenerAction, ImageSerieNavigator navidator, boolean showFullScreen) {
		
		this.context = context;
		this.listenerAction = listenerAction;
		this.imageDataNavigator = navidator;
		this.showFullScreen = showFullScreen;
		
		setSizeFull();
		
		this.user = user;

		init();

		buildImageFrameChangeButtons();
		settingCanvas();
	}

	private void init() {
		imagenFigures = new ArrayList<Figure>();
		back = new Stack<>();
	}
	
	private void buildImageFrameChangeButtons(){
		btnFrameFirst = new Button( );
		btnFrameFirst.setIcon( FontAwesome.ANGLE_DOUBLE_UP  );
		btnFrameFirst.setImmediate( true );
		btnFrameFirst.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnFrameFirst.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btnFrameFirst.setDescription( context.getString( "ViewerWnd.first.image.serie" ) );
		btnFrameFirst.addClickListener( new Button.ClickListener(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 7515427205675270359L;

			@Override
			public void buttonClick( ClickEvent event ){
				btnFrameFirst.setEnabled( false );
				btnFramePrior.setEnabled( false );
				btnFrameNext.setEnabled( true );
				btnFrameLast.setEnabled( true );
				openFirstImage();
			}
		} );
		
		btnFramePrior = new Button( );
		btnFramePrior.setIcon( FontAwesome.ANGLE_UP  );
		btnFramePrior.setImmediate( true );
		btnFramePrior.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnFramePrior.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btnFramePrior.setDescription( context.getString( "ViewerWnd.prior.image.serie" ) );
		btnFramePrior.addClickListener( new Button.ClickListener(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -8368839172588393747L;

			@Override
			public void buttonClick( ClickEvent event ){
				openPreviousImage();
				if (!imageDataNavigator.hasPriorImageSerie()){
					btnFrameFirst.setEnabled( false );
					btnFramePrior.setEnabled( false );
				}
				btnFrameNext.setEnabled( true );
				btnFrameLast.setEnabled( true );
			}
		} );

		btnFrameNext = new Button( );
		btnFrameNext.setIcon( FontAwesome.ANGLE_DOWN  );
		btnFrameNext.setImmediate( true );
		btnFrameNext.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnFrameNext.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btnFrameNext.setDescription( context.getString( "ViewerWnd.next.image.serie" ) );
		btnFrameNext.addClickListener( new Button.ClickListener(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -951616351416582941L;

			@Override
			public void buttonClick( ClickEvent event ){
				openNextImage();
				if (!imageDataNavigator.hasNextImageSerie()){
					btnFrameNext.setEnabled( false );
					btnFrameLast.setEnabled( false );
				}
				btnFrameFirst.setEnabled( true );
				btnFramePrior.setEnabled( true );
			}
		} );
		
		btnFrameLast = new Button( );
		btnFrameLast.setIcon( FontAwesome.ANGLE_DOUBLE_DOWN  );
		btnFrameLast.setImmediate( true );
		btnFrameLast.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnFrameLast.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btnFrameLast.setDescription( context.getString( "ViewerWnd.last.image.serie" ) );
		btnFrameLast.addClickListener( new Button.ClickListener(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -211457028026544883L;

			@Override
			public void buttonClick( ClickEvent event ){
				btnFrameFirst.setEnabled( true );
				btnFramePrior.setEnabled( true );
				btnFrameNext.setEnabled( false );
				btnFrameLast.setEnabled( false );
				openLastImage();
			}
		} );

		changeFrame = new CssLayout( btnFrameFirst, btnFramePrior, btnFrameNext, btnFrameLast );
		changeFrame.addStyleName( ImedigTheme.CHANGE_FRAME );
		changeFrame.setHeight( "-1px" );
		changeFrame.setWidth( "0px" );
		changeFrame.setVisible( false );
		addComponent( changeFrame );
	}


	private void settingCanvas() {
		buildCanvasConfiguration();
		defaultConfiguration = configurations.get(EnumActions.NONE);
		canvas = new FabricJs(defaultConfiguration);
		canvas.setLoaderConfiguration( loadingConfiguration );
		canvas.setSizeFull();
		addComponent(canvas);
		
		//Inicializar listeners
		canvas.setResizeListener(new ResizeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2705147359658817842L;

			@Override
			public void onResize(double width, double height) {
				resizeAction();
			}
		});
		
		canvas.setDrawFigureListener(new DrawFigureListener() {
			@Override
			public void onDrawFigure(Figure figure) {
				calculateValue(figure);
			}

			private void calculateValue(Figure figure) {
				switch (figure.getFigureType()) {
				case ANGLE:
					calculateAngle(figure);
					break;
				case FREE_ANGLE:
					calculateAngle(figure);
					break;	
				case LINE:
					calculateDistance(figure);
					break;
				case RECT:
					if (currentAction == EnumActions.ZOOM){
						zoomOperation(figure);
					}else if (currentAction == EnumActions.CONTRAST){
						contrastOperation(figure);
					}
					
					break;	
				default:
					break;
				}
				
			}
		});
		
		canvas.setMouseWheelListener( new MouseWheelListener(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 828514638200548092L;

			@Override
			public void onMouseWheel( double weelDelta ){
				if (imageData == null) return;

				if (weelDelta < 0 ){
					openPreviousImage();
				}else{
					openNextImage();
				}
			}
		} );
	}
	
	private void openFirstImage(){
		if (numberOfFrames >1){
			Integer frame = getFirstFrame();
			
			if (frame.equals( currentFrame )) return;
			
			currentFrame = frame;
			openImage();
		}else{
			ImageData data = imageDataNavigator.getFirstImageSerie();
			if (data != null){
				openImage( data );
				context.sendAction( new ChangeImageFrame( this, data ) );
			}
		}
	}

	private void openPreviousImage(){
		if (numberOfFrames >1){
			Integer frame = getPreviousFrame();
			
			if (frame.equals( currentFrame )) return;
			
			currentFrame = frame;
			openImage();
		}else{
			ImageData data = imageDataNavigator.getPreviousImageSerie();
			if (data != null){
				openImage( data );
				context.sendAction( new ChangeImageFrame( this, data ) );
			}
		}
	}

	private Integer getFirstFrame(){
		return 0;
	}

	private Integer getPreviousFrame(){
		if (currentFrame.equals( 0 )) return currentFrame;
		
		return currentFrame - 1;
	}

	private void openNextImage(){
		
		if (numberOfFrames >1){
			Integer frame = getNextFrame();
			
			if (frame.equals( currentFrame )) return;
			
			currentFrame = frame;
			
			openImage();
		}else{
			ImageData data = imageDataNavigator.getNextImageSerie();
			if (data != null){
				openImage( data );
				context.sendAction( new ChangeImageFrame( this, data ) );
			}
		}
	}

	private void openLastImage(){
		if (numberOfFrames >1){
			Integer frame = getLastFrame();
			
			if (frame.equals( currentFrame )) return;
			
			currentFrame = frame;
			
			openImage();
		}else{
			ImageData data = imageDataNavigator.getLastImageSerie();
			if (data != null){
				openImage( data );
				context.sendAction( new ChangeImageFrame( this, data ) );
			}
		}
	}

	private Integer getNextFrame(){
		if (currentFrame.equals( numberOfFrames - 1 )) return currentFrame;
		
		return currentFrame + 1;

	}

	private Integer getLastFrame(){
		return numberOfFrames - 1;
	}


	private void calculateDistance(Figure figure) {

		double ix1 = imageRect.getX();
		double iy1 = imageRect.getY();
		double ix2 = ix1 + imageRect.getWidth() - 1;
		double iy2 = iy1 + imageRect.getHeight() - 1;
		
		double vx1 = viewRect.getX();
		double vy1 = viewRect.getY();
		double vx2 = vx1 + viewRect.getWidth() - 1;
		double vy2 = vy1 + viewRect.getHeight() - 1;
		
		if (!verifyPixelSpacing()){
			Notification.show("Error", context.getString( "ViewerWnd.NoPixelSizeMsg"), Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		String sp[] = imageHeader.getPixelSpacing().split( "\\\\" );
		
		Point p1 = figure.getPoints().get(0);
		Point p2 = figure.getPoints().get(1);
		
		double mx = ( ix2 - ix1 ) / ( vx2 - vx1 );
		double nx = ix1 - mx * vx1;
		
		double my = ( iy2 - iy1 ) / ( vy2 - vy1 );
		double ny = iy1 - my * vy1;
		
		double ipx1 = mx * p1.getX() + nx;
		double ipx2 = mx * p2.getX() + nx;
		
		double ipy1 = my * p1.getY() + ny;
		double ipy2 = my * p2.getY() + ny;
		
		double dx = ( ipx2 - ipx1 ) * Double.parseDouble( sp[0] );
		double dy = ( ipy2 - ipy1 ) * Double.parseDouble( sp[1] );
		
		double distance = Math.sqrt( dx * dx + dy * dy );
		
		String text = Double.toString( Utils.roundDouble( distance, 2 ) ) + " mm";
		figure.setText(text);
		addImagenFigure(figure);
		//figure.setConfiguration( configurations.get( currentAction ) );
		canvas.setText(figure, text);
	}
	
	private boolean verifyPixelSpacing(){
		if (imageHeader == null) return false;
		
		String spacing = imageHeader.getPixelSpacing();
		
		if (spacing == null) return false;
		
		String sp[] = spacing.split( "\\\\" );
		
		if ( sp.length != 2 ){
			return false;
		}
		
		return true;
	}
	
	private void calculateAngle(Figure figure) {
		Point p1 = figure.getPoints().get(0);
		Point p2 = figure.getPoints().get(1);
		Point p3 = figure.getPoints().get(2);
		Point p4 = figure.getPoints().get(3);
		
		double angle = Utils.roundDouble( getAngle( p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY() ), 2 );
		double other = 180-angle;
		
		String text = Double.toString( Utils.roundDouble( angle, 2 ) ) + " / " +  Double.toString( Utils.roundDouble( other, 2 ) ) + " grados";
		figure.setText(text);
		addImagenFigure(figure);
		canvas.setText(figure, text);
	}
	
	private void zoomOperation(Figure figure) {
		
		double ix1 = imageRect.getX();
		double iy1 = imageRect.getY();
		double ix2 = ix1 + imageRect.getWidth() - 1;
		double iy2 = iy1 + imageRect.getHeight() - 1;
		
		double vx1 = viewRect.getX();
		double vy1 = viewRect.getY();
		double vx2 = vx1 + viewRect.getWidth() - 1;
		double vy2 = vy1 + viewRect.getHeight() - 1;

		double vpx1 = figure.getPoints().get(0).getX();
		double vpy1 = figure.getPoints().get(0).getY();
		double vpx2 = figure.getPoints().get(1).getX();
		double vpy2 = figure.getPoints().get(1).getY();
		
		if ( vpx1 < 0 ) vpx1 = 0;
		if ( vpx1 > vx2 ) vpx1 = vx2;
		if ( vpy1 < 0 ) vpy1 = 0;
		if ( vpy1 >= vy2 ) 	vpy1 = vy2;
		if ( vpx2 < 0 ) vpx2 = 0;
		if ( vpx2 >= vx2 ) 	vpx2 = vx2;
		if ( vpy2 < 0 )	vpy2 = 0;
		if ( vpy2 >= vy2 )	vpy2 = vy2;
		
		if ( vpx1 > vpx2 ){
			double temp = vpx1;
			
			vpx1 = vpx2;
			vpx2 = temp;
		}
		
		if ( vpy1 > vpy2 ) {
			double temp = vpy1;
			
			vpy1 = vpy2;
			vpy2 = temp;
		}
		
		if ( vpx2 - vpx1 > 10 && vpy2 - vpy1 > 10 ){
			double mx = (double) ( ix2 - ix1 ) / ( vx2 - vx1 );
			double nx = ix1 - mx * vx1;
			
			double my = (double) ( iy2 - iy1 ) / ( vy2 - vy1 );
			double ny = iy1 - my * vy1;
			
			int nix1 = (int) ( mx * vpx1 + nx );
			int nix2 = (int) ( mx * vpx2 + nx );
			
			int niy1 = (int) ( my * vpy1 + ny );
			int niy2 = (int) ( my * vpy2 + ny );
			
			try
			{
				verifyImagenOperation( new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 ) );
			}
			catch ( OperationException e )
			{
				Notification.show( context.getString( "ViewerWnd.error.zoom.operation" ), Notification.Type.WARNING_MESSAGE);
				return;
			}

			addToUndo( new ImageStatus((Rectangle) imageRect.clone(), currentCenter, currentWidth, currentFrame) );
				
			imageRect = new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 );
			
			settingViewRect();
			
			//canvas.clearDraw();
			
			openImage();
			
			List<Figure> figures = getImagenFiguresToShow();
			canvas.chainOfCommand(
	                new ChainOfCommand()
	                .withClearDraw( true )
	                .withFigures( figures ));
			showRuleReference();
		}
	}
	
	private void addToUndo(ImageStatus status){
		back.push(status);
		listenerAction.doAction( new AddToUndoAction( this) );
	}

	private double getAngle( double x1, double y1, double x2, double y2, double xp1, double yp1, double xp2, double yp2 ){
		double angle = Math.toDegrees( Math.abs( getAngle( x1, y1, x2, y2 ) - getAngle( xp1, yp1, xp2, yp2 ) ) );
		
		return angle > 90 ? 180 - angle : angle;
	}
	
	private double getAngle( double x1, double y1, double x2, double y2 ){
		double angle = 0;
		
		if ( x2 - x1 == 0 )
			angle = Math.PI / 2;
		else
		{
			angle = Math.atan( ( y2 - y1 ) / ( x2 - x1 ) );
		}
		
		return angle;
	}
	
	private void addImagenFigure(Figure figure){
		
		double ix1 = imageRect.getX();
		double iy1 = imageRect.getY();
		double ix2 = ix1 + imageRect.getWidth() - 1;
		double iy2 = iy1 + imageRect.getHeight() - 1;
		
		double vx1 = viewRect.getX();
		double vy1 = viewRect.getY();
		double vx2 = vx1 + viewRect.getWidth() - 1;
		double vy2 = vy1 + viewRect.getHeight() - 1;
		
		double mx = ( ix2 - ix1 ) / ( vx2 - vx1 );
		double nx = ix1 - mx * vx1;
		
		double my = ( iy2 - iy1 ) / ( vy2 - vy1 );
		double ny = iy1 - my * vy1;
		
		List<Point> newpoints = new ArrayList<>();
		
		for (Point point : figure.getPoints()) {
			double x = mx * point.getX() + nx;
			double y = my * point.getY() + ny;
			
			newpoints.add(new Point(x, y));
		}
		
		Figure fig = new Figure(figure.getKey(), figure.getFigureType(), newpoints, figure.getText());
		fig.setConfiguration( figure.getConfiguration() );
		
		imagenFigures.add(fig);
		listenerAction.doAction( new AddFigure( this ) );
	}
	
	private void contrastOperation(Figure figure) {
		double ix1 = imageRect.getX();
		double iy1 = imageRect.getY();
		double ix2 = ix1 + imageRect.getWidth() - 1;
		double iy2 = iy1 + imageRect.getHeight() - 1;
		
		double vx1 = viewRect.getX();
		double vy1 = viewRect.getY();
		double vx2 = vx1 + viewRect.getWidth() - 1;
		double vy2 = vy1 + viewRect.getHeight() - 1;
		
		if ( currentWidth > 0 )
		{
			double center = getDouble(imageHeader.getWindowCenter());
			double width = getDouble( imageHeader.getWindowWidth() );
			
			double mw = (double) ( width ) / ( vx2 - vx1 );
			double mc = (double) ( center ) / ( vy2 - vy1 );
			
			double nw = currentWidth + ( mw * ( figure.getPoints().get(1).getX() - figure.getPoints().get(0).getX() ) );
			double nc = currentCenter - ( mc * ( figure.getPoints().get(1).getY() - figure.getPoints().get(0).getY() ) );
			
			if ( nc > 0 ){
				addToUndo( new ImageStatus((Rectangle) imageRect.clone(), currentCenter, currentWidth, currentFrame));
				
				currentCenter = nc;
				currentWidth = nw;
				
				//canvas.clearDraw();
				
				openImage();
				
				List<Figure> figures = getImagenFiguresToShow();
				canvas.chainOfCommand(
		                new ChainOfCommand()
		                .withClearDraw( true )
		                .withFigures( figures ));
			}
		}
	}


	private void buildCanvasConfiguration(){
		
		if (configurations != null) return;
		configurations = new HashMap<>();
		
		defaultConfiguration = new FigureConfiguration()
				.withStrokeWidth(2.0)
				.withFillColor("#F0BE20")
				.withHoverColor( "#E02525" )
				.withStrokeColor("#F0BE20")
				.withBackgroundColor( "transparent" )
				.withTextFontFamily("Roboto,'Open Sans',sans-serif")
				.withTextFontSize(15)
				.withTextFillColor("#F0BE20")
				.withTextFontWeight( FontWeight.FW700 )
				.withAction( CanvasAction.NONE )
				.withCursor( "default" )
				.withCancelDrawKeyCode( 46 );
		
		configurations.put( EnumActions.NONE, defaultConfiguration );
		configurations.put( EnumActions.ANGLE, defaultConfiguration.clone().withAction( CanvasAction.DRAW_FREE_ANGLE ).withCursor( "crosshair" ).withTextShadow( "#020202 -1px 1px 1px" ));
		configurations.put( EnumActions.DISTANCE, defaultConfiguration.clone().withAction( CanvasAction.DRAW_LINE ).withCursor( "crosshair" ).withTextShadow( "#020202 -1px 1px 1px" ) );
		
		configurations.put( EnumActions.CONTRAST, defaultConfiguration.clone().withVisible( false ).withAction( CanvasAction.SHOW_RECT ).withCursor( "move" ) );
		
		FigureConfiguration temp = defaultConfiguration.clone()
				.withVisible( true )
				.withStrokeWidth( 1.0 )
				.withStrokeLineCap( StrokeLineCap.BUTT )
				.withStrokeDashArray( new ArrayList<>( Arrays.asList( 3.0, 3.0 )) )
				.withAction( CanvasAction.SHOW_RECT )
				.withCursor( "crosshair" );
		
		configurations.put( EnumActions.ZOOM, temp );
		
		loadingConfiguration = new LoaderConfiguration()
                .withFillColor("transparent")
                .withStrokeColor("transparent")
                .withSpinnerColor("#bb910b")                
                .withLoaderText(context.getString( "ViewerWnd.loading.image" ))
                .withTextFontSize( 16 )
                .withTextFontFamily("Roboto,'Open Sans',sans-serif")
                .withTextFillColor("#F0BE20")
                .withTextFontWeight( FontWeight.BOLDER )
                .withSpinnerPosition(SpinnerPosition.LEFT)
                .withShadow("")
                .withSpinnerRadio(15)
                .withLoaderAlignment(FigureAlignment.MIDDLE_CENTER)
                .withSpinnerSpeed( SpinnerSpeed.SLOW );
	}
	
	private void resizeAction(){
		if (imageData == null) return;

		openImage();
		settingViewRect();
		
		List<Figure> figures = getImagenFiguresToShow();
		List<Note> notes = informationNote(imageHeader);
		
		canvas.chainOfCommand(
                new ChainOfCommand()
                .withClearDraw( true )
                .withFigures( figures )
                .withClearNotes( true )
                .withNotes(notes));
		showRuleReference();
	}
	
	private void showImagenFigures() {
		
		Rectangle vrect = viewRect;
		Rectangle irect = imageRect;
		
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
		
		for (Figure fig : imagenFigures) {
			
			List<Point> npoints = new ArrayList<>();
			for (Point point : fig.getPoints()) {
				double x = mx * point.getX() + nx;
				double y = my * point.getY() + ny;
				
				npoints.add(new Point(x, y));
			}
			Figure figure = new Figure(fig.getFigureType(), npoints, fig.getText());
			figure.setConfiguration( fig.getConfiguration() );
			canvas.draw(figure);
		}
	}
	
	private List<Figure> getImagenFiguresToShow() {
		
		Rectangle vrect = viewRect;
		Rectangle irect = imageRect;
		
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
		
		List<Figure> result = new ArrayList<>();
		for (Figure fig : imagenFigures) {
			
			List<Point> npoints = new ArrayList<>();
			for (Point point : fig.getPoints()) {
				double x = mx * point.getX() + nx;
				double y = my * point.getY() + ny;
				
				npoints.add(new Point(x, y));
			}
			Figure figure = new Figure(fig.getFigureType(), npoints, fig.getText());
			figure.setConfiguration( fig.getConfiguration() );
			result.add( figure );
		}
		
		return result;
	}
	
	
	private void verifyImagenOperation(Rectangle irect) throws OperationException{
		int ix1 = (int) irect.getX();
		int iy1 = (int) irect.getY();
		int ix2 = (int) ( ix1 + irect.getWidth() - 1 );
		int iy2 = (int) ( iy1 + irect.getHeight() - 1 );
		
		if (( ix2 - ix1 ) == 0 || ( iy2 - iy1 )==0){
			throw new OperationException();
		}
	}



	public void openImage(ImageData imageData) {
		
		if (imageData == null) {
			return;
		}
		
		if (this.imageData != null && this.imageData.equals( imageData )) {
			if (!verifyPixelSpacing()){
				listenerAction.doAction( new DisableDistanceAction( this) );
			}
			return;
		}
		
		if (this.imageData != null) {
			
			imageDataFigures.put( this.imageData, new ArrayList<>( imagenFigures ) );
			this.imageData = null;
			imagenFigures.clear();
			canvas.clearDraw();
			//canvas.clear();
		}
		
		this.imageData = imageData;

		HashMap<String,String> params = new HashMap<String,String>();
		
		params.put( "StudyInstanceUID", imageData.getStudy().getStudyData().getStudyInstanceUID() );
		params.put( "SeriesInstanceUID", imageData.getSeries().getSeriesData().getSeriesInstanceUID() );
		params.put( "SOPInstanceUID", imageData.getImage().getSOPInstanceUID() );

		try{
			imageHeader = RetrieveManager.getInstance().getMetaData( Settings.PACS_AETitle, Settings.PACS_Host, Settings.PACS_Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );
			numberOfFrames = imageHeader.getNumberOfFrames();
			
			if (imageDataFigures.get( this.imageData ) != null){
				imagenFigures = imageDataFigures.get( this.imageData );
			}
			
			if (currentSerie.isEmpty()){
				currentSerie = imageData.serieId();
				back = new Stack<>();
				newImageData();
			}else if (!currentSerie.equals( imageData.serieId())){
				//Guardar el estado actual
				addToUndo( new ImageStatus((Rectangle) imageRect.clone(), currentCenter, currentWidth, currentFrame) );
				serieStatus.put( currentSerie, back );
				
				//Obtener la nueva serie
				currentSerie = imageData.serieId();
				//Verificar si ya se tiene el estado almacenado
				if (serieStatus.get( currentSerie ) != null){
					back = serieStatus.get( currentSerie );
					newImageDataFromStatus( back.pop() );
				}else{
					back = new Stack<>();
					newImageData();
				}
			}/*else{
				if (imageDataFigures.get( this.imageData ) != null){
					imagenFigures = imageDataFigures.get( this.imageData );
				}
			}*/
			
			if (!verifyPixelSpacing()){
				listenerAction.doAction( new DisableDistanceAction( this ) );
			}
			
			if (!imagenFigures.isEmpty()){
				listenerAction.doAction( new AddFigure( this ) );
			}else{
				listenerAction.doAction( new NotFigures( this ) );
			}
			
			openImage();
			List<Figure> figures = getImagenFiguresToShow();
			List<Note> notes = informationNote(imageHeader);
			canvas.chainOfCommand(
	                new ChainOfCommand()
	                .withClearDraw( true )
	                .withFigures( figures )
	                .withClearNotes( true )
	                .withNotes(notes));
			showRuleReference();
			
			if (imageDataNavigator.containImagesSeries()){
				changeFrame.setVisible( true );
				
				btnFrameFirst.setEnabled( imageDataNavigator.hasPriorImageSerie() );
				btnFramePrior.setEnabled( imageDataNavigator.hasPriorImageSerie() );
				btnFrameNext.setEnabled( imageDataNavigator.hasNextImageSerie() );
				btnFrameLast.setEnabled( imageDataNavigator.hasNextImageSerie() );
			}else{
				changeFrame.setVisible( false );
			}
		}catch ( Throwable ex )	{
			reportInfo = null;
		}
	}
	
	private void newImageData(){
		imageRect = new Rectangle(0,  0, imageHeader.getColumns(), imageHeader.getRows());
		currentCenter = getDouble(imageHeader.getWindowCenter()); 
		currentWidth = getDouble(imageHeader.getWindowWidth());
		currentFrame = 0;
		viewRect = getCanvasImage();
	}
	
	private void newImageDataFromStatus(ImageStatus status){
		imageRect = status.getIrect();
		currentCenter = status.getWindowCenter(); 
		currentWidth = status.getWindowWidth();
		currentFrame = status.getFrame();
		viewRect = getCanvasImage();
	}

	private void openImage(){
		try{
			double ix1 = imageRect.getX();
			double iy1 = imageRect.getY();
			double ix2 = ix1 + imageRect.getWidth() - 1;
			double iy2 = iy1 + imageRect.getHeight() - 1;
			
			int vcols = (int)canvas.getvWidth();
			int vrows = (int)canvas.getvHeight();
			
			String dx1 = Double.toString( (double) ix1 / ( imageHeader.getColumns() - 1 ) );
			String dy1 = Double.toString( (double) iy1 / ( imageHeader.getRows() - 1 ) );
			String dx2 = Double.toString( (double) ix2 / ( imageHeader.getColumns() - 1 ) );
			String dy2 = Double.toString( (double) iy2 / ( imageHeader.getRows() - 1 ) );
			
			String region = "region=" + dx1 + "," + dy1 + "," + dx2 + "," + dy2;
			String zoom = "columns=" + vcols + "&rows=" + vrows;
			String zoomIcon = "columns=" + 64 + "&rows=" + 64;
			String content = "contentType=" + user.getCompression();
			String contentIcon = "contentType=image/jpeg";
			String bright = currentWidth > 0 ? "windowCenter=" + currentCenter + "&windowWidth=" + currentWidth : "";
			String frame = "frameNumber=" + currentFrame;
			
			String url = imageData.getImage().getWadoUrl() + "&" + zoom + "&" + region + "&" + content + "&" + bright + "&" + frame;
			String urlIcon = imageData.getImage().getWadoUrl() + "&" + zoomIcon + "&" + region + "&" + contentIcon + "&" + bright + "&" + frame;
			
			String urlimage = context.getData( "Url") + url;
			LOG.info(urlimage);
			canvas.setImageUrl(new ExternalResource(urlimage));
			
			ReportInfo info = new ReportInfo();
			
			info.setHeader( imageHeader );
			info.setUrl( url );
			info.setIcon( urlIcon );
			
			reportInfo = info;
		}catch ( Throwable ex )	{
			Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private static double getDouble(String value) {
		double temp = 0;

		try {
			temp = Double.parseDouble(value);
		} catch (Exception e) {
		}

		return temp;
	}
	
	private static String string( String source ){
		return source != null ? source : "";
	}

	private Rectangle getCanvasImage() {

		int vcols =  (int)canvas.getvWidth();
		int vrows =  (int)canvas.getvHeight();

		int icols = (int)imageRect.getWidth();
		int irows = (int)imageRect.getHeight();

		double ar = (double) icols / irows;

		int cols = 0;
		int rows = 0;

		if (vrows * ar > vcols) {
			cols = vcols;
			rows = (int) (vcols / ar + .5f);
		} else {
			cols = (int) (vrows * ar + .5f);
			rows = vrows;
		}

		return new Rectangle(0, 0, cols, rows);
	}
	
	private void settingViewRect() {

		int vcols =  (int)canvas.getvWidth();
		int vrows =  (int)canvas.getvHeight();

		int icols = (int)imageRect.getWidth();
		int irows = (int)imageRect.getHeight();

		double ar = (double) icols / irows;

		int cols = 0;
		int rows = 0;

		if (vrows * ar > vcols) {
			cols = vcols;
			rows = (int) (vcols / ar + .5f);
		} else {
			cols = (int) (vrows * ar + .5f);
			rows = vrows;
		}

		viewRect = new Rectangle(0, 0, cols, rows);
	}
	
	private void showInformation(ImageHeader metadata){
        NotesConfiguration configuration = new NotesConfiguration()
        		.withTextFontSize( 17 )
        		.withTextFillColor( "#ecedee" )
        		.withTextBackgroundColor( "transparent" )
        		.withNotesAlignment( FigureAlignment.TOP_LEFT)
        		.withTextAlign(TextAlign.LEFT)
        		.withTextFontFamily("Roboto,'Open Sans',sans-serif");
        
        
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(string( metadata.getPatientName())).append(" ").append(string( metadata.getPatientSex())).append("\n").
                 append(string( metadata.getPatientID())).append("\n").
                 append(string( metadata.getStudyDate())).append("\n").
                 append(string( metadata.getInstanceNumber() )).append("\n");
		canvas.clearNotes();
        canvas.addNotes(sbuilder.toString(), configuration);
	}
	
	private void showRuleReference( ){
		
		if (!verifyPixelSpacing()){
			return;
		}
		
		double iy1 = imageRect.getY();
		double iy2 = iy1 + imageRect.getHeight() - 1;
		double vy1 = viewRect.getY();
		double vy2 = vy1 + viewRect.getHeight() - 1;
		
		String sp[] = imageHeader.getPixelSpacing().split( "\\\\" );
		
		double my = ( iy2 - iy1 ) / ( vy2 - vy1 );
		
		Double pixels = REFERENCE_IN_mm/( my * Double.parseDouble( sp[1] ) );
		
		Ruler ruler = new Ruler(String.format( "%d mm", REFERENCE_IN_mm ), new RulerConfiguration()
                .withPixels(pixels.intValue())
                .withSplit(5)
                .withStrokeWidth(2.0)
                .withStrokeColor("#F0BE20")
                .withPosition(RulerPosition.RIGHT)
                .withFigureShadow("-1 1 1 #020202")
                .withTextFontFamily("Roboto,'Open Sans',sans-serif")
				.withTextFontSize(15)
				.withTextFillColor("#F0BE20")
				.withTextFontWeight( FontWeight.FW700 )
        );
		
		canvas.draw(ruler);
	}
	
	private List<Note> informationNote(ImageHeader metadata){
        NotesConfiguration configuration = new NotesConfiguration()
        		.withTextFontSize( 17 )
        		.withTextFillColor( "#ecedee" )
        		.withTextBackgroundColor( "transparent" )
        		.withNotesAlignment( FigureAlignment.TOP_LEFT)
        		.withTextAlign(TextAlign.LEFT)
        		.withTextFontFamily("Roboto,'Open Sans',sans-serif");
        
        
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(string( metadata.getPatientName())).append(" ").append(string( metadata.getPatientSex())).append("\n").
                 append(string( metadata.getPatientID())).append("\n").
                 append(string( metadata.getStudyDate())).append("\n").
                 append(string( metadata.getInstanceNumber() )).append("\n");
        
        return Arrays.asList( new Note( sbuilder.toString(), configuration ) );
	}

	public void clear() {
		settingAction( EnumActions.NONE );
		imageData = null;
		imagenFigures = new ArrayList<>();
		canvas.clear();
		back = new Stack<>();
		currentSerie = "";
		serieStatus = new HashMap<>();
		imageDataFigures = new HashMap<>();
		changeFrame.setVisible( false );
	}
	
	public void clearFigures() {
		imagenFigures.clear();
		canvas.clearDraw();
	}
	
	public void settingAction(EnumActions action){
		if (imageData == null) return;
		
		currentAction = action;
		canvas.setFigureConfiguration( configurations.get( currentAction ));
	}
	
	public boolean undoAction() {
		if (back.isEmpty()) return false;
		
		ImageStatus status = back.pop();
		
		currentCenter = status.getWindowCenter();
		currentWidth = status.getWindowWidth();
		currentFrame = status.getFrame();
		imageRect = status.getIrect();

		openImage();

		settingViewRect();
		List<Figure> figures = getImagenFiguresToShow();
		List<Note> notes = informationNote(imageHeader);
		canvas.chainOfCommand(
                new ChainOfCommand()
                .withClearDraw( true )
                .withFigures( figures )
                .withClearNotes( true )
                .withNotes(notes));
		showRuleReference();
		
		if (back.isEmpty()) return false;
		
		return true;
	}

	public void restoreAction()	{
		if (back.isEmpty()) return;

		ImageStatus status = back.get( 0 );
		currentCenter = status.getWindowCenter();
		currentWidth = status.getWindowWidth();
		currentFrame = status.getFrame();
		imageRect = status.getIrect();
		
		viewRect = getCanvasImage();

		back = new Stack<>();
		serieStatus.remove( currentSerie );

		openImage();
		settingViewRect();
		List<Figure> figures = getImagenFiguresToShow();
		List<Note> notes = informationNote(imageHeader);
		canvas.chainOfCommand(
                new ChainOfCommand()
                .withClearDraw( true )
                .withFigures( figures )
                .withClearNotes( true )
                .withNotes(notes));
		showRuleReference();
	}

}
