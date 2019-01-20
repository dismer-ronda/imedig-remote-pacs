package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.EquipoMapper;
import es.pryades.imedig.cloud.dto.Equipo;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class EquiposManagerImpl extends ImedigCloudManagerImpl implements EquiposManager
{
	private static final long serialVersionUID = 8161752430745244011L;
	
	private static final Logger LOG = Logger.getLogger( EquiposManagerImpl.class );

	public static ImedigManager build()
	{
		return new EquiposManagerImpl();
	}

	public EquiposManagerImpl()
	{
		super( EquipoMapper.class, Equipo.class, LOG );
	}

}
