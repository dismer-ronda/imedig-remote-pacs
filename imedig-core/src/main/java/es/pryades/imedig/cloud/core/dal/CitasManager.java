package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/
public interface CitasManager extends ImedigManager
{
	Long getLastDate(ImedigContext ctx, CitaQuery query) throws Throwable;
}
