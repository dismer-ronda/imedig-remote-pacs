package es.pryades.imedig.cloud.core.dal.ibatis;

import java.util.ArrayList;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.ImedigMapper;

/**
*
* @author hector.licea
* @since 2.2.6.0
*/

public interface PacienteMapper extends ImedigMapper
{
	ArrayList<ImedigDto> getPageLazy(Query query);
}
