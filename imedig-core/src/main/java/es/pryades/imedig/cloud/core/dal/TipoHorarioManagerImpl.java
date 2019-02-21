package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.TipoHorarioMapper;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class TipoHorarioManagerImpl extends ImedigCloudManagerImpl implements TipoHorarioManager
{
	private static final long serialVersionUID = -361954764834995534L;
	
	private static final Logger LOG = Logger.getLogger( TipoHorarioManagerImpl.class );

	public static ImedigManager build()
	{
		return new TipoHorarioManagerImpl();
	}

	public TipoHorarioManagerImpl()
	{
		super( TipoHorarioMapper.class, TipoHorario.class, LOG );
	}

}
