package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface CentrosManager extends ImedigManager
{
	void updateIpPort( ImedigContext ctx, Centro centro, String ip, String port ) throws Throwable;
}
