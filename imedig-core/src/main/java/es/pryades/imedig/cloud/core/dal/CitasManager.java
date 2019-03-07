package es.pryades.imedig.cloud.core.dal;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.Recurso;
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
	
	void transferTodayWorklist( ImedigContext ctx ) throws Throwable;
	void transferNextHourWorklist( ImedigContext ctx ) throws Throwable;

	void transferAppointmentWorklist( ImedigContext ctx, Cita cita, Recurso recurso ) throws Throwable;
}
