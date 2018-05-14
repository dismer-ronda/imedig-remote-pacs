package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface ParametrosManager extends ImedigManager
{
    public void loadParametros( ImedigContext ctx ) throws Throwable;
}
