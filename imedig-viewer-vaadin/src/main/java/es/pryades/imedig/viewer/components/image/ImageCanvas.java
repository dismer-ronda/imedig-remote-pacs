package es.pryades.imedig.viewer.components.image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.fabricjs.FabricJs;
import es.pryades.fabricjs.geometry.Figure;
import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.application.ViewerApplicationUI;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.retrieve.RetrieveManager;

public class ImageCanvas extends VerticalLayout {
	private static final Logger LOG = Logger.getLogger(ImageCanvas.class);

	private FabricJs canvas = new FabricJs();

	private List<Figure> figures;
	private ImageData imageData = null;
	private User user;

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
		canvas.setSizeFull();
		canvas.setCursor("pointer");
		addComponent(canvas);
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
			ImageHeader metadata = RetrieveManager.getInstance().getMetaData( Settings.PACS_AETitle, Settings.PACS_Host, Settings.PACS_Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );
			
			Rectangle vrect = getRectangleImage(metadata);
			Rectangle irect = new Rectangle(0,  0, metadata.getColumns(), metadata.getRows());
			Double currentCenter = getDouble(metadata.getWindowCenter()); 
			Double currentWidth = getDouble( metadata.getWindowWidth());
			Integer currentFrame = 0;
			
			/**********************************************/
			double ix1 = irect.getX();
			double iy1 = irect.getY();
			double ix2 = ix1 + irect.getWidth() - 1;
			double iy2 = iy1 + irect.getHeight() - 1;
			
			int vcols = canvas.getvWidth().intValue();
			int vrows = canvas.getvHeight().intValue();
			
			String dx1 = Double.toString( (double) ix1 / ( metadata.getColumns() - 1 ) );
			String dy1 = Double.toString( (double) iy1 / ( metadata.getRows() - 1 ) );
			String dx2 = Double.toString( (double) ix2 / ( metadata.getColumns() - 1 ) );
			String dy2 = Double.toString( (double) iy2 / ( metadata.getRows() - 1 ) );
			
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
			
			
			//HttpImageReference imgRef = new HttpImageReference( app.getServerUrl() + url );
			
			ReportInfo info = new ReportInfo();
			
			info.setHeader( metadata );
			info.setUrl( url );
			info.setIcon( urlIcon );
			
			//imagesInfo.put( user.getLogin(), info );

			//canvasImage.setOverlays( getViewSpaceOverlays( canvasImage ) );
			//canvasImage.setAnnotations( getViewSpaceAnnotations( canvasImage ) );
			//canvasImage.setImage( imgRef );
			//canvasImage.setRefresh( true );
			/***********************************************************************/
		}catch ( Throwable ex )	{
			//MessageDialog messageDialog = new MessageDialog( "Error", ex.getMessage(), MessageDialog.CONTROLS_OK, resourceBundle );
			//getApplication().getMainWindow().getContent().add( messageDialog );
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

	private Rectangle getRectangleImage(ImageHeader metadata) {

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

	public void clear() {
		imageData = null;
		figures.clear();
		canvas.clear();
	}

}
