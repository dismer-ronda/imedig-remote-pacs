package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.StudyControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;

/**
 * 
 * @author Dismer Ronda
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class StudyVto extends GenericVto
{
	private String study_datetime;
	private String study_iuid;
	private String study_desc;
	private Integer num_instances;
	private String pat_name;

	public StudyVto()
	{
	}
	
	public GenericControlerVto getControlerVto( ImedigContext ctx )
	{
		return new StudyControlerVto( ctx );
	}
}
