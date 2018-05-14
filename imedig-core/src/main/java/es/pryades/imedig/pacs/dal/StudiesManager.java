package es.pryades.imedig.pacs.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface StudiesManager extends ImedigManager
{
	public Integer getTotalImages( ImedigContext ctx, Query query ) throws Throwable;
}
