package es.pryades.imedig.pacs.dto;

import java.sql.Timestamp;

import es.pryades.imedig.cloud.dto.ImedigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Study extends ImedigDto 
{
	private static final long serialVersionUID = -4895937618873092201L;
	
	Timestamp study_datetime;
	String study_id;
	String study_iuid;
	String study_desc;
	Integer num_instances;
	String accession_no;
	String ref_physician;
	String mods_in_study;

	String pat_name;
	String pat_id; 
	String pat_id_issuer; 
	String pat_birthdate; 
	String pat_sex;
}
