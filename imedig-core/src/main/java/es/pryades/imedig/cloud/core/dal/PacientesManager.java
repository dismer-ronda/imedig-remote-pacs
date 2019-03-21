package es.pryades.imedig.cloud.core.dal;

import java.util.List;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.query.PacienteQuery;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/
public interface PacientesManager extends ImedigManager
{
	List getPageLazy(ImedigContext ctx, PacienteQuery query) throws Throwable;
	
	Paciente getPaciente(ImedigContext ctx, String uid)  throws Throwable;
}
