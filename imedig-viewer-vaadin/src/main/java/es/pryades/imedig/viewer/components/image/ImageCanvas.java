package es.pryades.imedig.viewer.components.image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.fabricjs.FabricJs;
import es.pryades.fabricjs.config.FabricCanvasConfiguration;
import es.pryades.fabricjs.config.NotesConfiguration;
import es.pryades.fabricjs.data.Point;
import es.pryades.fabricjs.enums.CanvasAction;
import es.pryades.fabricjs.enums.NotesAlignment;
import es.pryades.fabricjs.enums.TextAlign;
import es.pryades.fabricjs.geometry.Figure;
import es.pryades.fabricjs.listeners.DrawFigureListener;
import es.pryades.fabricjs.listeners.ResizeListener;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.application.ViewerApplicationUI;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.retrieve.RetrieveManager;

public class ImageCanvas extends VerticalLayout {
	private static final Logger LOG = Logger.getLogger(ImageCanvas.class);

	private User user;

	private FabricJs canvas;
	private FabricCanvasConfiguration canvasConfiguration;
	private List<Figure> figures;
	private ImageData imageData = null;
	private CanvasAction currentAction = CanvasAction.NONE; 
	private Rectangle imageRect;
	private Rectangle viewRect;
	private ImageHeader imageHeader;
	

	public ImageCanvas( User user) {
		setSizeFull();
		setMargin(false);
		setSpacing(false);
		
		this.user = user;

		init();

		settingCanvas();
	}
	
	

	private void init() {
		figures = new ArrayList<Figure>();
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
					calculateGrade(figure);
					break;
				case LINE:
					calculateDistance(figure);
					break;
				default:
					break;
				}// TODO Auto-generated method stub
				
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
			//TODO Mostrar error
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
		canvas.setText(text);
	}
	
	private void calculateGrade(Figure figure) {
		Point p1 = figure.getPoints().get(0);
		Point p2 = figure.getPoints().get(1);
		Point p3 = figure.getPoints().get(2);
		
		double angle = Utils.roundDouble( getAngle( p1.getX(), p1.getY(), p2.getX(), p2.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY() ), 2 );
		double other = 180-angle;
		
		String text = Double.toString( Utils.roundDouble( angle, 2 ) ) + " / " +  other + " grados";
		figure.setText(text);
		canvas.setText(text);
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

	private void buildCanvasConfiguration(){
		canvasConfiguration = new FabricCanvasConfiguration();
		canvasConfiguration.setStrokeWidth(1.0);
		canvasConfiguration.setFillColor("#FFFF00");
		canvasConfiguration.setStrokeColor("#FFFF00");
		canvasConfiguration.setTextFontFamily("Open Sans");
		canvasConfiguration.setTextFontSize(14);
		canvasConfiguration.setTextFillColor("#FFFF00");
	}
	
	private void resizeAction(){
		canvas.clear();
		if (imageData == null) return;
		openImage();
	}

	public void openImage(ImageData imageData) {
		if (this.imageData != null) {
			clear();
		}

		this.imageData = imageData;
		openImage();
	}

	private void openImage(){
		HashMap<String,String> params = new HashMap<String,String>();
		
		params.put( "StudyInstanceUID", imageData.getStudy().getStudyData().getStudyInstanceUID() );
		params.put( "SeriesInstanceUID", imageData.getSeries().getSeriesData().getSeriesInstanceUID() );
		params.put( "SOPInstanceUID", imageData.getImage().getSOPInstanceUID() );

		try{
			imageHeader = RetrieveManager.getInstance().getMetaData( Settings.PACS_AETitle, Settings.PACS_Host, Settings.PACS_Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );
			
			viewRect = getCanvasImage(imageHeader);
			imageRect = new Rectangle(0,  0, imageHeader.getColumns(), imageHeader.getRows());
			Double currentCenter = getDouble(imageHeader.getWindowCenter()); 
			Double currentWidth = getDouble(imageHeader.getWindowWidth());
			Integer currentFrame = 0;
			
			double ix1 = imageRect.getX();
			double iy1 = imageRect.getY();
			double ix2 = ix1 + imageRect.getWidth() - 1;
			double iy2 = iy1 + imageRect.getHeight() - 1;
			
			int vcols = canvas.getvWidth().intValue();
			int vrows = canvas.getvHeight().intValue();
			
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
			
			String urlimage = ((ViewerApplicationUI) UI.getCurrent()).getServerUrl() + url;
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

	private Rectangle getCanvasImage(ImageHeader metadata) {

		int vcols = canvas.getvWidth().intValue();
		int vrows = canvas.getvHeight().intValue();

		int icols = metadata.getColumns();
		int irows = metadata.getRows();

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
        NotesConfiguration configuration = new NotesConfiguration();
        configuration.setTextFontSize(16);
        configuration.setTextFillColor("#FFFF00");
        configuration.setTextBackgroundColor("transparent");
        configuration.setNotesAlignment(NotesAlignment.TOP_LEFT);
        configuration.setTextAlign(TextAlign.LEFT);
        configuration.setTextFontFamily("Open Sans");
        
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
		figures.clear();
		canvas.clear();
	}

	public void noneAction(){
		currentAction = CanvasAction.NONE;
		canvas.setAction(currentAction);
		canvas.setCursor("default");
	}

	public void distanceAction(){
		if (imageData == null) return;
		
		currentAction = CanvasAction.DRAW_LINE;
		canvas.setAction(currentAction);
		canvas.setCursor("crosshair");
	}

	public void angleAction(){
		if (imageData == null) return;

		currentAction = CanvasAction.DRAW_ANGLE;
		canvas.setAction(currentAction);
		canvas.setCursor("crosshair");
	}

}
