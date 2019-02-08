package es.pryades.imedig.cloud.core.dal.ibatis;

import es.pryades.imedig.cloud.dto.query.EstudioQuery;
import es.pryades.imedig.core.common.ImedigMapper;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/

public interface EstudioMapper extends ImedigMapper
{
	long getLastDate(EstudioQuery query);
}
