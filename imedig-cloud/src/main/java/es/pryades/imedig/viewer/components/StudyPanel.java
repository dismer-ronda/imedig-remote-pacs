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
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyPanel extends VerticalLayout {

	private static final Logger LOG = LoggerFactory.getLogger(StudyPanel.class);

	private VerticalLayout studyInfo;
	private VerticalLayout thumnails;

	private ListenerAction listenerAction;
	
	public StudyPanel(ListenerAction listenerAction) {
		addStyleName(ImedigTheme.STUDY_PANEL);

		this.listenerAction = listenerAction;

		setWidth("100%");
		studyInfo = new VerticalLayout();
		studyInfo.setWidth("100%");
		addComponent(studyInfo);
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		thumnails.setSpacing(true);
		addComponent(thumnails);
	}

	public void setStudy(StudyTree study, List<ImageData> datas){
		addStudyInfo(study);
		addStudyImages( datas );
	}
	private void addStudyInfo(StudyTree study) {
		Label label = new Label(getPatientInitials( study.getStudyData().getPatientName() )+"<br/>"+study.getStudyData().getStudyDate()+"<br/>"+study.getStudyData().getPatientID());
		label.setContentMode(ContentMode.HTML);
		studyInfo.addComponent(label);
	}

	private void addStudyImages(List<ImageData> datas) {
		for (ImageData data : datas){
			com.vaadin.ui.Image img = getImage(data.getSeries(), data.getImage());
			img.addStyleName(ImedigTheme.CURSOR_POINTER);
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

	private com.vaadin.ui.Image getImage(SeriesTree series, Image image) {

		String imageUrl = ((BackendApplication) UI.getCurrent()).getServerUrl() + image.getWadoUrl()
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
