package es.pryades.imedig.viewer.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.StudyUtils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyPanel extends CssLayout{

	private static final Logger LOG = LoggerFactory.getLogger(StudyPanel.class);

	private Button btnClose;
	private VerticalLayout studyInfo;
	private VerticalLayout seriesPanelsContent;
	private ImedigContext context;
	private StudyTree study;
	private StudyPanel instance;
	private Map<String, ImageSerie> seriesPanels;
	
	//private Map<String, List<ImageData>> datas;
//	private Map<String, Integer> indexBySeries;
	//private Integer index;
//	private int mode = MODE_ALL;
	
//	private static final int LIMIT_TO_SHOW_ALL = 1;
//	private static final int MODE_ALL = 1;
//	private static final int MODE_ONE = 2;
//	private static final List<String> MODALITIES_SERIES2 = Arrays.asList( "CT", "MR" ); 

	public StudyPanel(ImedigContext context) {
		addStyleName( "study-thumnails-panel" );
		instance = this;
		this.context = context;

		setWidth("100%");
		buildTop();
		VerticalLayout content = new VerticalLayout();
		content.setWidth( "100%" );
		studyInfo = new VerticalLayout();
		studyInfo.setWidth("100%");
		seriesPanelsContent = new VerticalLayout();
		seriesPanelsContent.setWidth("100%");
		content.addComponents( studyInfo, seriesPanelsContent );
		addComponent(content);
	}

	private void buildTop(){
		btnClose = new Button( FontAwesome.CLOSE );
		btnClose.setDescription( context.getString( "words.close" ) );
		btnClose.addStyleName( ValoTheme.BUTTON_ICON_ONLY);
		btnClose.addStyleName( ValoTheme.BUTTON_TINY);
		btnClose.addStyleName( ImedigTheme.TO_RIGHT);
		btnClose.addClickListener( new Button.ClickListener()
		{
			@Override
			public void buttonClick( com.vaadin.ui.Button.ClickEvent event )
			{
				context.sendAction( new CloseStudies( instance, study ) );
				
			}
		});
		
		addComponent( btnClose );
	}
	
	public void setStudy(StudyTree study, Map<String, List<ImageData>> datas){
		this.study = study;
		seriesPanels = new HashMap<>();
		addStudyInfo( );
		
		for ( String serieUID : StudyUtils.getSeriesUID( study ) ){
			
			if (datas.get( serieUID ) == null || datas.get( serieUID ).isEmpty()) continue;
			
			SeriesTree serie = StudyUtils.getSerie( serieUID, study );
			if (serie == null) continue;
			
			ImageSerie panel = new ImageSerie( context, serie, datas.get( serieUID ) );
			panel.showSerieImage();
			
			seriesPanels.put( serieUID, panel );
			
			seriesPanelsContent.addComponent( panel );
		}
		
		
//		index = 0;
		
//		if (MODALITIES_SERIES2.contains( study.getSeriesList().get( 0 ).getSeriesData().getModality() )){
//			index = datas.size()/2;
//		}

//		addStudyInfo( );
//		addStudySeriesImages( );
		//addStudyImages( );
	}
	
	private void addStudyInfo( ) {
		Label label = new Label(getPatientInitials( study.getStudyData().getPatientName() )+"<br/>"+study.getStudyData().getStudyDate()+"<br/>"+study.getStudyData().getPatientID());
		label.setContentMode(ContentMode.HTML);
		studyInfo.addComponent(label);
	}
	
//	private void addStudySeriesImages( ) {
//		for ( String serieUID : StudyUtils.getSeriesUID( study ) ){
//			
//			SeriesTree serie = StudyUtils.getSerie( serieUID, study );
//			if (serie == null) continue;
//			
//			addSerieImages(serie);
//		}
//	}
//
//	private void addSerieImages( SeriesTree serie ){
//		String serieUID = serie.getSeriesData().getSeriesInstanceUID();
//
//		Integer index = 0;
//		if (MODALITIES_SERIES2.contains( serie.getSeriesData().getModality() )){
//			index = datas.size()/2;
//		}
//		indexBySeries.put( serieUID, index );
//		
//		if (datas.get( serieUID ) == null) return;
//		
//		if (datas.get( serieUID ).size() > LIMIT_TO_SHOW_ALL){
//			showOneImage(serieUID);
//		}else{
//			showAllImages(serieUID);
//		}
//	}

//	private void addStudyImages( ) {
//		if (datas.size() > LIMIT_TO_SHOW_ALL){
//			showOneImage();
//		}else{
//			showAllImages();
//		}
//		
//	}
	
//	private void showOneImage(String serieUID)	{
//		
//		addToThumnails( datas.get( serieUID ).get( indexBySeries.get( serieUID ) ) );
//		
//		addThumnailsFooter();
//		
//		mode = MODE_ONE;
//	}

//	private void showOneImage()	{
//		addToThumnails( datas.get( index ) );
//		
//		addThumnailsFooter();
//		
//		mode = MODE_ONE;
//	}

//	private void addThumnailsFooter(){
//		if (labelFooter != null) return;
//		
//		labelFooter = new Label( String.format( "%d/%d", index+1, datas.size() ) );
//		labelFooter.addStyleName( ImedigTheme.TO_LEFT);
//		btnFooter = new Button( FontAwesome.PLUS );
//		btnFooter.setDescription( context.getString( "words.expand" ) );
//		btnFooter.addStyleName( ValoTheme.BUTTON_ICON_ONLY);
//		btnFooter.addStyleName( ValoTheme.BUTTON_TINY);
//		btnFooter.addStyleName( ImedigTheme.TO_RIGHT);
//		
//		btnFooter.addClickListener( new Button.ClickListener()
//		{
//			@Override
//			public void buttonClick( com.vaadin.ui.Button.ClickEvent event )
//			{
//				if (mode == MODE_ONE){
//					mode = MODE_ALL;
//					labelFooter.setVisible( false );
//					btnFooter.setIcon( FontAwesome.MINUS );
//					thumnails.removeAllComponents();
//					btnFooter.setDescription( context.getString( "words.contract" ) );
//					showAllImages();
//				}else{
//					thumnails.removeAllComponents();
//					mode = MODE_ONE;
//					labelFooter.setVisible( true );
//					btnFooter.setIcon( FontAwesome.PLUS );
//					btnFooter.setDescription( context.getString( "words.expand" ) );
//					showOneImage();
//				}
//			}
//		});
//		
//		addComponents( labelFooter, btnFooter );
//	}
//
//	private void showAllImages(){
//		for (ImageData data : datas){
//			addToThumnails( data );
//		}
//	}
	
//	private void addToThumnails(ImageData data){
//		com.vaadin.ui.Image img = getImage(data.getSeries(), data.getImage());
//		img.addStyleName(ImedigTheme.CURSOR_POINTER);
//		img.setData(data);
//			
//		img.addClickListener(new ClickListener() {
//			@Override
//			public void click(ClickEvent event) {
//				ImageData idata = (ImageData)((com.vaadin.ui.Image)event.getSource()).getData();
//				index = datas.indexOf( idata );
//				updateLabelValue();
//				context.sendAction( new OpenImage(this, idata));
//			}
//		});
//		VerticalLayout inside = new VerticalLayout( img );
//		inside.setWidth( "64px" );
//		inside.setHeight( "64px" );
//		inside.setMargin( false );
//		inside.setSpacing( false );
//		inside.setComponentAlignment(img, Alignment.MIDDLE_CENTER);
//		thumnails.addComponent(inside);
//		thumnails.setComponentAlignment(inside, Alignment.MIDDLE_CENTER);
//	}
//	
//	private com.vaadin.ui.Image getImage(SeriesTree series, Image image) {
//
//		String imageUrl = imageUrl( image );
//		
//		String modality = series.getSeriesData().getModality();
//		String hint = " ";
//		if (!modality.isEmpty())
//			hint += modality + " ";
//
//		com.vaadin.ui.Image result = new com.vaadin.ui.Image(null, new ExternalResource(imageUrl));
//		result.setDescription(hint);
//		//result.setHeight( "64px");
//
//		return result;
//	}
//
	private String getPatientInitials(String name) {
		String ret = "";

		if (name != null && !name.isEmpty()) {
			String splitChar = " ";

			if (name.contains("^")) {
				splitChar = "\\^";
			} else if (name.contains("/")) {
				splitChar = "/";
			} else if (name.contains(",")) {
				splitChar = ",";
			}

			String parts[] = name.split(splitChar);

			for (int i = 0; i < parts.length; i++) {
				if (parts[i] != null && !parts[i].isEmpty())
					ret += parts[i].toUpperCase().charAt(0) + ".";
			}
		}

		return ret;
	}

	public void changeImageFrame( ImageData data ){
		
		String serieUID = data.getSeries().getSeriesData().getSeriesInstanceUID();
		
		if ( seriesPanels.get( serieUID ) == null ) return;
		
		seriesPanels.get( serieUID ).changeImageFrame( data );
//		index = datas.indexOf( data );
//		
//		updateLabelValue();
//		
//		if (mode == MODE_ALL) return;
//		
//		index = datas.indexOf( data );
//		updateImage( datas.get( index ) );
	}
	
//	private void updateImage( ImageData data ){
//		
//		String imageUrl = imageUrl( data.getImage() );
//		
//		com.vaadin.ui.Image image = (com.vaadin.ui.Image)((AbstractOrderedLayout)thumnails.getComponent( 0 )).getComponent( 0 );
//		image.setData( data );
//		image.setSource( new ExternalResource(imageUrl) );
//	}
//	
//	private String imageUrl(Image image){
//		String imageUrl = Utils.getEnviroment( "CLOUD_URL" ) + image.getWadoUrl() + "&contentType=image/jpeg&columns=64&rows=64";
//
//		LOG.info("imageUrl " + imageUrl);
//		
//		return imageUrl;
//	}
	
//	private void updateLabelValue(){
//		if (labelFooter == null) return;
//		
//		labelFooter.setValue( String.format( "%d/%d", index+1, datas.size() ) );
//	}
}
