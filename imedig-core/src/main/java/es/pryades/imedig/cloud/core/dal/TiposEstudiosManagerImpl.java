package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.TipoEstudioMapper;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class TiposEstudiosManagerImpl extends ImedigCloudManagerImpl implements TiposEstudiosManager
{
	private static final long serialVersionUID = 4009345172145961484L;

	private static final Logger LOG = Logger.getLogger( TiposEstudiosManagerImpl.class );

	public static ImedigManager build()
	{
		return new TiposEstudiosManagerImpl();
	}

	public TiposEstudiosManagerImpl()
	{
		super( TipoEstudioMapper.class, TipoEstudio.class, LOG );
	}

}
