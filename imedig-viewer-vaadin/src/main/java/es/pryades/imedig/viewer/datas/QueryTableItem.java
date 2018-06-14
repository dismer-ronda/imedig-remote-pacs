package es.pryades.imedig.viewer.datas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.CheckBox;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.viewer.application.ViewerApplicationUI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTableItem {
	private CheckBox selected;
	private String studyDate;
	private String modality;
	private String patientId;
	private String patientName;
	private String patientAge;
	private String referringPhysicianName;
	private Study study;

	public QueryTableItem(Study study) {
		this.study = study;
		selected = new CheckBox();
		studyDate = study.getStudyDate();
		modality = study.getModalitiesInStudy();
		patientId = study.getPatientID();
		patientName = study.getPatientName();
		patientAge = calcAge();
		referringPhysicianName = study.getReferringPhysicianName();
	}

	private String calcAge() {
		String birthDate = study.getPatientBirthDate();
		String age = "";

		if (birthDate != null) {
			SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

			Date dob;

			try {
				dob = f.parse(birthDate);

				if (dob != null)
					age = Utils.getAge(dob, 
							ViewerApplicationUI.getText("Generic.Month"),
							ViewerApplicationUI.getText("Generic.Year"));
			} catch (ParseException e) {
			}
		}
		return age;
	}
}
