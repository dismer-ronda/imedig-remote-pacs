package es.pryades.imedig.pacs.dto.query;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.pacs.dto.Study;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class StudyQuery extends Study 
{
	private Timestamp from_date;
	private Timestamp to_date;
}
