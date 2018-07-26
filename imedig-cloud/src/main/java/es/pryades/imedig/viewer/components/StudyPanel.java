package es.pryades.imedig.viewer.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.backend.BackendApplication;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.ListenerAction;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyPanel extends VerticalLayout {

	private static final Logger LOG = LoggerFactory.getLogger(StudyPanel.class);

	private VerticalLayout studyInfo;
	private VerticalLayout thumnails;

	private final StudyTree study;
	private ListenerAction listenerAction;
	
	public StudyPanel(StudyTree study, ListenerAction listenerAction) {
		addStyleName(ViewerTheme.STUDY_PANEL);

		this.study = study;
		this.listenerAction = listenerAction;

		setWidth("100%");
		studyInfo = new VerticalLayout();
		studyInfo.setWidth("100%");
		addComponent(studyInfo);
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		thumnails.setSpacing(true);
		addComponent(thumnails);

		buildComponents();
	}

	private void buildComponents() {
		addStudyInfo();
		addStudyImages();
	}

	private void addStudyInfo() {
		Label label = new Label(getPatientInitials( study.getStudyData().getPatientName() )+"<br/>"+study.getStudyData().getStudyDate()+"<br/>"+study.getStudyData().getPatientID());
		label.setContentMode(ContentMode.HTML);
		studyInfo.addComponent(label);
		//studyInfo.addComponent(new Label( getPatientInitials( study.getStudyData().getPatientName() )));
		//studyInfo.addComponent(new Label( study.getStudyData().getStudyDate()));
		//studyInfo.addComponent(new Label( study.getStudyData().getPatientID()));
	}

	private void addStudyImages() {
		for (SeriesTree series : study.getSeriesList()) {

			List<Image> imageList = series.getImageList();

			for (Image image : imageList) {
				com.vaadin.ui.Image img = getImage(study, series, image, false);
				img.addStyleName(ViewerTheme.CURSOR_POINTER);
				
				ImageData data = new ImageData(); 
				data.setImage(image);
				data.setSeries(series);
				data.setStudy(study);
				img.setData(data);
				
				img.addClickListener(new ClickListener() {
					@Override
					public void click(ClickEvent event) {
						listenerAction.doAction(new OpenImage(this, ((com.vaadin.ui.Image)event.getSource()).getData()));
					}
				});
				thumnails.addComponent(img);
				thumnails.setComponentAlignment(img, Alignment.MIDDLE_CENTER);
			}
		}

	}

	private com.vaadin.ui.Image getImage(StudyTree study, SeriesTree series, Image image, boolean group) {

		String imageUrl = ((BackendApplication) UI.getCurrent()).getServerUrl() + image.getWadoUrl()
				+ "&contentType=image/jpeg&columns=64&rows=64";
		
		LOG.info("imageUrl " + imageUrl);

		String modality = series.getSeriesData().getModality();
		String hint = " ";
		if (!modality.isEmpty())
			hint += modality + " ";

		com.vaadin.ui.Image result = new com.vaadin.ui.Image(null, new ExternalResource(imageUrl));
		result.setDescription(hint);
		result.setData(study);
		result.setWidth("64px");

		return result;
	}

	/*
	 * private Button getImage( StudyTree study, SeriesTree series, Image image,
	 * boolean group ){
	 * 
	 * String imageUrl = ((ViewerApplicationUI)UI.getCurrent()).getServerUrl() +
	 * image.getWadoUrl() + "&contentType=image/jpeg&columns=64&rows=64";
	 * LOG.info( "imageUrl " + imageUrl );
	 * 
	 * String modality = series.getSeriesData().getModality(); String hint =
	 * " "; if ( !modality.isEmpty() ) hint += modality + " ";
	 * 
	 * Button result = new Button(new ExternalResource(imageUrl));
	 * result.setWidth("64px"); result.setDescription(hint);
	 * result.setData(study); result.addStyleName(ValoTheme.BUTTON_BORDERLESS);
	 * result.addStyleName(ValoTheme.BUTTON_ICON_ONLY); return result; }
	 */

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
