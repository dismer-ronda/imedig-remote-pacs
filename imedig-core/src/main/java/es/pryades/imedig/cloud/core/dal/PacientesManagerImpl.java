package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.PacienteMapper;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class PacientesManagerImpl extends ImedigCloudManagerImpl implements PacientesManager
{
	private static final long serialVersionUID = 1616814174321878135L;

	private static final Logger LOG = Logger.getLogger( PacientesManagerImpl.class );

	public static ImedigManager build()
	{
		return new PacientesManagerImpl();
	}

	public PacientesManagerImpl()
	{
		super( PacienteMapper.class, Paciente.class, LOG );
	}

}
