package es.pryades.imedig.pacs.dal.ibatis;

import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.ImedigMapper;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface StudyMapper extends ImedigMapper
{
	public Integer getTotalImages( Query query );
}
