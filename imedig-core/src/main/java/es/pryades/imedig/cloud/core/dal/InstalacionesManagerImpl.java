package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.InstalacionMapper;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class InstalacionesManagerImpl extends ImedigCloudManagerImpl implements InstalacionesManager
{
	private static final long serialVersionUID = 8161752430745244011L;
	
	private static final Logger LOG = Logger.getLogger( InstalacionesManagerImpl.class );

	public static ImedigManager build()
	{
		return new InstalacionesManagerImpl();
	}

	public InstalacionesManagerImpl()
	{
		super( InstalacionMapper.class, Instalacion.class, LOG );
	}

}
