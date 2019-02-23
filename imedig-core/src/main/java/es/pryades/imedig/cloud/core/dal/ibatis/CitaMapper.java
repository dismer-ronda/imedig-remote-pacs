package es.pryades.imedig.cloud.core.dal.ibatis;

import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.core.common.ImedigMapper;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/

public interface CitaMapper extends ImedigMapper
{
	long getLastDate(CitaQuery query);
}
