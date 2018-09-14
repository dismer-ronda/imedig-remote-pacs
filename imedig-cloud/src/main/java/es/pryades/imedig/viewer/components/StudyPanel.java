package es.pryades.imedig.viewer.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyPanel extends CssLayout {

	private static final Logger LOG = LoggerFactory.getLogger(StudyPanel.class);

	private Button btnClose;
	private Button btnFooter;
	private Label labelFooter;
	private VerticalLayout studyInfo;
	private VerticalLayout thumnails;
	private ImedigContext context;
	private StudyTree study;
	private StudyPanel instance;
	private List<ImageData> datas;
	private Integer index;
	private int mode = MODE_ALL;
	
	private static final int LIMIT_TO_SHOW_ALL = 2;
	private static final int MODE_ALL = 1;
	private static final int MODE_ONE = 2;
	
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
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		content.addComponents( studyInfo, thumnails );
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
	
	public void setStudy(StudyTree study, List<ImageData> datas){
		this.study = study;
		this.datas = datas;
		addStudyInfo( );
		
		addStudyImages( );
	}
	private void addStudyInfo( ) {
		Label label = new Label(getPatientInitials( study.getStudyData().getPatientName() )+"<br/>"+study.getStudyData().getStudyDate()+"<br/>"+study.getStudyData().getPatientID());
		label.setContentMode(ContentMode.HTML);
		studyInfo.addComponent(label);
	}

	private void addStudyImages( ) {
		if (datas.size() > LIMIT_TO_SHOW_ALL){
			showOneImage();
		}else{
			showAllImages();
		}
		
	}

	private void showOneImage()	{
		index = datas.size()/2;
		
		addToThumnails( datas.get( index ) );
		
		addThumnailsFooter();
		
		mode = MODE_ONE;
	}

	private void addThumnailsFooter(){
		if (labelFooter != null) return;
		
		labelFooter = new Label( String.format( "%d/%d", index+1, datas.size() ) );
		labelFooter.addStyleName( ImedigTheme.TO_LEFT);
		btnFooter = new Button( FontAwesome.PLUS );
		btnFooter.setDescription( context.getString( "words.expand" ) );
		btnFooter.addStyleName( ValoTheme.BUTTON_ICON_ONLY);
		btnFooter.addStyleName( ValoTheme.BUTTON_TINY);
		btnFooter.addStyleName( ImedigTheme.TO_RIGHT);
		
		btnFooter.addClickListener( new Button.ClickListener()
		{
			@Override
			public void buttonClick( com.vaadin.ui.Button.ClickEvent event )
			{
				if (mode == MODE_ONE){
					mode = MODE_ALL;
					labelFooter.setVisible( false );
					btnFooter.setIcon( FontAwesome.MINUS );
					thumnails.removeAllComponents();
					btnFooter.setDescription( context.getString( "words.contract" ) );
					showAllImages();
				}else{
					thumnails.removeAllComponents();
					mode = MODE_ONE;
					labelFooter.setVisible( true );
					btnFooter.setIcon( FontAwesome.PLUS );
					btnFooter.setDescription( context.getString( "words.expand" ) );
					showOneImage();
				}
			}
		});


		
		addComponents( labelFooter, btnFooter );
	}

	private void showAllImages(){
		for (ImageData data : datas){
			addToThumnails( data );
		}
	}
	
	private void addToThumnails(ImageData data){
		com.vaadin.ui.Image img = getImage(data.getSeries(), data.getImage());
		img.addStyleName(ImedigTheme.CURSOR_POINTER);
		img.setData(data);
			
		img.addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				context.sendAction( new OpenImage(this, ((com.vaadin.ui.Image)event.getSource()).getData()));
			}
		});
		thumnails.addComponent(img);
		thumnails.setComponentAlignment(img, Alignment.MIDDLE_CENTER);
	}
	
	private com.vaadin.ui.Image getImage(SeriesTree series, Image image) {

		String imageUrl = Utils.getEnviroment( "CLOUD_URL" ) + image.getWadoUrl()
				+ "&contentType=image/jpeg&columns=64&rows=64";
		
		LOG.info("imageUrl " + imageUrl);

		String modality = series.getSeriesData().getModality();
		String hint = " ";
		if (!modality.isEmpty())
			hint += modality + " ";

		com.vaadin.ui.Image result = new com.vaadin.ui.Image(null, new ExternalResource(imageUrl));
		result.setDescription(hint);
		result.setWidth("64px");

		return result;
	}

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
}
