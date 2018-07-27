package es.pryades.imedig.viewer.components.image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.fabricjs.FabricJs;
import es.pryades.fabricjs.config.FigureConfiguration;
import es.pryades.fabricjs.config.NotesConfiguration;
import es.pryades.fabricjs.data.Point;
import es.pryades.fabricjs.enums.CanvasAction;
import es.pryades.fabricjs.enums.NotesAlignment;
import es.pryades.fabricjs.enums.TextAlign;
import es.pryades.fabricjs.geometry.Figure;
import es.pryades.fabricjs.listeners.DrawFigureListener;
import es.pryades.fabricjs.listeners.ResizeListener;
import es.pryades.imedig.cloud.backend.BackendApplication;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.ListenerAction;
import es.pryades.imedig.viewer.actions.AddToUndoAction;
import es.pryades.imedig.viewer.actions.EnumActions;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.retrieve.RetrieveManager;

public class ImageCanvas extends VerticalLayout {
	private static final Logger LOG = Logger.getLogger(ImageCanvas.class);

	private User user;

	private FabricJs canvas;
	private FigureConfiguration canvasConfiguration;
	private List<Figure> imagenFigures;
	private ImageData imageData = null;
	private EnumActions currentAction = EnumActions.NONE; 
	private Rectangle imageRect;
	private Rectangle viewRect;
	private Double currentCenter; 
	private Double currentWidth;
	private Integer currentFrame;
	private ImageHeader imageHeader;
	private Stack<ImageStatus> back;
	
	private final ImedigContext context;
	
	private final ListenerAction listenerAction;

	public ImageCanvas( ImedigContext context, User user, ListenerAction listenerAction) {
		
		this.context = context;
		this.listenerAction = listenerAction;
		
		setSizeFull();
		setMargin(false);
		setSpacing(false);
		
		this.user = user;

		init();

		settingCanvas();
	}
	
	

	private void init() {
		//normalizeFigures = new ArrayList<Figure>();
		imagenFigures = new ArrayList<Figure>();
		back = new Stack<>();
	}

	private void settingCanvas() {
		buildCanvasConfiguration();
		
		canvas = new FabricJs(canvasConfiguration);
		canvas.setSizeFull();
		canvas.setCursor("default");
		addComponent(canvas);
		
		//Inicializar listeners
		canvas.setResizeListener(new ResizeListener() {
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
		
		String spacing = imageHeader.getPixelSpacing();
		
		if (spacing == null) return;
		
		String sp[] = spacing.split( "\\\\" );
		
		if ( sp.length != 2 ){
			Notification.show("Error", context.getString( "ViewerWnd.NoPixelSizeMsg"), Notification.Type.ERROR_MESSAGE);
			return;
		}
		
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
		addNormalizeFigure(figure);
		addImagenFigure(figure);
		canvas.setText(figure, text);
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
		addNormalizeFigure(figure);
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

			addToUndo( new ImageStatus((Rectangle) imageRect.clone(), currentCenter, currentWidth, currentFrame) );
			
			currentCenter = getDouble(imageHeader.getWindowCenter());
			currentWidth = getDouble(imageHeader.getWindowWidth());
			currentFrame = 0;
				
			imageRect = new Rectangle( nix1, niy1, nix2 - nix1 + 1, niy2 - niy1 + 1 );
						
			viewRect = getCanvasImage();
			
			canvas.clear();
			
			openImage();
			showImagenFigures();
		}
	}
	
	private void addToUndo(ImageStatus status){
		back.push(status);
		listenerAction.doAction( new AddToUndoAction( this, null ) );
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
	
	private void addNormalizeFigure(Figure figure){
		
		List<Point> npoints = new ArrayList<>();
		
		double dx = viewRect.getWidth()-1;
		double dy = viewRect.getHeight()-1;
		
		for (Point point : figure.getPoints()) {
			double x = point.getX()/dx;
			double y = point.getY()/dy;
			
			npoints.add(new Point(x, y));
		}
		
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
		
		imagenFigures.add(new Figure(figure.getFigureType(), newpoints, figure.getText()));
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
				
				canvas.clear();
				
				openImage();
				showImagenFigures();
			}
		}
	}


	private void buildCanvasConfiguration(){
		canvasConfiguration = new FigureConfiguration()
				.withStrokeWidth(1.0)
				.withFillColor("#FFFF00")
				.withStrokeColor("#FFFF00")
				.withBackgroundColor( "transparent" )
				.withTextFontFamily("Roboto")
				.withTextFontSize(14)
				.withTextFillColor("#FFFF00");
	}
	
	private void resizeAction(){
		canvas.clear();
		
		if (imageData == null) return;
		
		openImage();
		
		viewRect = getCanvasImage();
		showImagenFigures();
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
			
			canvas.draw(new Figure(fig.getFigureType(), npoints, fig.getText()));
		}
	}



	public void openImage(ImageData imageData) {
		if (this.imageData != null) {
			clear();
		}

		this.imageData = imageData;

		HashMap<String,String> params = new HashMap<String,String>();
		
		params.put( "StudyInstanceUID", imageData.getStudy().getStudyData().getStudyInstanceUID() );
		params.put( "SeriesInstanceUID", imageData.getSeries().getSeriesData().getSeriesInstanceUID() );
		params.put( "SOPInstanceUID", imageData.getImage().getSOPInstanceUID() );

		try{
			imageHeader = RetrieveManager.getInstance().getMetaData( Settings.PACS_AETitle, Settings.PACS_Host, Settings.PACS_Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );
			
			imageRect = new Rectangle(0,  0, imageHeader.getColumns(), imageHeader.getRows());
			currentCenter = getDouble(imageHeader.getWindowCenter()); 
			currentWidth = getDouble(imageHeader.getWindowWidth());
			currentFrame = new Integer(0);
			viewRect = getCanvasImage();
			
			openImage();
			
		}catch ( Throwable ex )	{
			//TODO Mostar error
		}
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
			
			String urlimage = ((BackendApplication) UI.getCurrent()).getServerUrl() + url;
			LOG.info(urlimage);
			canvas.setImageUrl(new ExternalResource(urlimage));
			
			showInformation(imageHeader);
			
			ReportInfo info = new ReportInfo();
			
			info.setHeader( imageHeader );
			info.setUrl( url );
			info.setIcon( urlIcon );
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
	
	private void showInformation(ImageHeader metadata){
        NotesConfiguration configuration = new NotesConfiguration()
        		.withTextFontSize( 16 )
        		.withTextFillColor( "#FFFF00" )
        		.withTextBackgroundColor( "transparent" )
        		.withNotesAlignment(NotesAlignment.TOP_LEFT)
        		.withTextAlign(TextAlign.LEFT)
        		.withTextFontFamily("Roboto");
        
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(string( metadata.getPatientName())).append(" ").append(string( metadata.getPatientSex())).append("\n").
                 append(string( metadata.getPatientID())).append("\n").
                 append(string( metadata.getStudyDate())).append("\n").
                 append(string( metadata.getInstanceNumber() )).append("\n");
        
        canvas.addNotes(sbuilder.toString(), configuration);
	}

	public void clear() {
		noneAction();
		imageData = null;
		imagenFigures.clear();
		canvas.clear();
		back = new Stack<>();
	}
	
	public void noneAction(){
		currentAction = EnumActions.NONE;
		canvas.setAction(CanvasAction.NONE);
		canvas.setCursor("default");
	}

	public void distanceAction(){
		if (imageData == null) return;
		
		currentAction = EnumActions.DISTANCE;
		FigureConfiguration config = canvas.getFigureConfiguration();
		config.setVisible( true );
		canvas.setAction(CanvasAction.DRAW_LINE, config);
		canvas.setCursor("crosshair");
	}

	public void angleAction(){
		if (imageData == null) return;

		currentAction = EnumActions.ANGLE;
		FigureConfiguration config = canvas.getFigureConfiguration();
		config.setVisible( true );
		canvas.setAction(CanvasAction.DRAW_FREE_ANGLE, config);
		canvas.setCursor("crosshair");
	}
	
	public void zoomAction(){
		if (imageData == null) return;

		currentAction = EnumActions.ZOOM;
		FigureConfiguration config = canvas.getFigureConfiguration();
		config.setVisible( true );
		canvas.setAction(CanvasAction.SHOW_RECT, config);
		canvas.setCursor("crosshair");
	}

	public void contrastAction() {
		if (imageData == null) return;

		currentAction = EnumActions.CONTRAST;
		FigureConfiguration config = canvas.getFigureConfiguration();
		config.setVisible( false );
		canvas.setAction(CanvasAction.SHOW_RECT, config);
		canvas.setCursor("crosshair");
	}



	public boolean undoAction() {
		if (back.isEmpty()) return false;
		
		ImageStatus status = back.pop();
		
		currentCenter = status.getWindowCenter();
		currentWidth = status.getWindowWidth();
		currentFrame = status.getFrame();
		imageRect = status.getIrect();
					
		viewRect = getCanvasImage();
		
		canvas.clear();
		
		openImage();
		showImagenFigures();
		
		if (back.isEmpty()) return false;
		
		return true;
	}

}
