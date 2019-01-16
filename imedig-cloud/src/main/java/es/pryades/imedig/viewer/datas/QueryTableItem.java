package es.pryades.imedig.viewer.datas;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.viewer.components.query.SelectedStudyListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of={"study"})
public class QueryTableItem implements Serializable {

	private static final long serialVersionUID = 2449295980450125857L;
	
	private CheckBox selected;
	private String studyDate;
	private Component studyReport;
	private String modality;
	private String patientId;
	private String patientName;
	private String patientAge;
	private String referringPhysicianName;
	private Study study;
	private SelectedStudyListener listener;
	private boolean report;
	
	private final ImedigContext context;

	public QueryTableItem(ImedigContext context, Study study) {
		
		this.study = study;
		this.context = context;
		selected = new CheckBox();
		selected.addValueChangeListener( new ValueChangeListener(){
			private static final long serialVersionUID = 6142729965661373474L;

			public void valueChange( ValueChangeEvent event ){
				if (listener == null) return;
				
				listener.selectStudy();
			}
		} );
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

		if (StringUtils.isBlank( birthDate )) return age;
		
		Date dob = getBirthDate( birthDate );
		
		if (dob != null)
			age = Utils.getAge(dob, 
					context.getString("Generic.Month"),
					context.getString("Generic.Year"));
		
		return age;
	}
	
	private Date getBirthDate(String sdate){
		Date result = ddMMyyyy( sdate );
		
		if (result == null) result = yyyyMMdd( sdate );
		
		return result;
	}
	
	private static Date ddMMyyyy(String sdate){
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		
		Date dob = null;

		try {
			dob = f.parse(sdate);
		} catch (ParseException e) {
		}
		
		return dob;
	}
	
	private static Date yyyyMMdd(String sdate){
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		
		Date dob = null;

		try {
			dob = f.parse(sdate);
		} catch (ParseException e) {
		}
		
		return dob;
	}

}
