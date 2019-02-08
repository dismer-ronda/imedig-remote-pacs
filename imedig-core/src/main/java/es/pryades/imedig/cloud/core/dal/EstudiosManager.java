package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.query.EstudioQuery;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/
public interface EstudiosManager extends ImedigManager
{
	Long getLastDate(ImedigContext ctx, EstudioQuery query) throws Throwable;
}
