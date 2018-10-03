package es.pryades.imedig.pacs.dto;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.ImedigDto;

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
	String study_iuid;
	String study_desc;
	Integer num_instances;
	
	String pat_name;
}
