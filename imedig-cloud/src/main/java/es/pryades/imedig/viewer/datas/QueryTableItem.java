package es.pryades.imedig.viewer.datas;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.CheckBox;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Study;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTableItem implements Serializable {

	private static final long serialVersionUID = 2449295980450125857L;
	
	private CheckBox selected;
	private String studyDate;
	private String modality;
	private String patientId;
	private String patientName;
	private String patientAge;
	private String referringPhysicianName;
	private Study study;
	
	private final ImedigContext context;

	public QueryTableItem(ImedigContext context, Study study) {
		
		this.study = study;
		this.context = context;
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
							context.getString("Generic.Month"),
							context.getString("Generic.Year"));
			} catch (ParseException e) {
			}
		}
		return age;
	}
}
