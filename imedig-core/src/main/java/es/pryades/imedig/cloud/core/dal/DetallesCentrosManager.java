package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface DetallesCentrosManager extends ImedigManager
{
	boolean echoCenter( ImedigContext ctx, DetalleCentro centro );
	String getCentroUrl( ImedigContext ctx, DetalleCentro centro, String uid );
}
