package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
@EqualsAndHashCode(of={"StudyInstanceUID"})
public class Study implements Serializable 
{
	private static final long serialVersionUID = 3975923599826446923L;
	
	private String StudyDate;
	private String StudyTime;
	private String AccessionNumber;
	private String StudyID;
	private String StudyInstanceUID;
	private String ReferringPhysicianName;
	private String PatientName;
	private String PatientID;
	private String IssuerOfPatientID;
	private String PatientBirthDate;
	private String PatientSex;
	private String ModalitiesInStudy;

	static public Study getStudy( String text ) throws Throwable
	{
		return (Study)Utils.toPojo( text, Study.class, true );
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public List<Study> getStudies( String text ) throws Exception
	{
		return (List) Utils.toArrayList( text, new TypeToken<ArrayList<Study>>() {}.getType() );
	}
}
