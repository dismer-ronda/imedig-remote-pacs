package es.pryades.imedig.cloud.core.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.ibatis.RecursoMapper;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class RecursosManagerImpl extends ImedigCloudManagerImpl implements RecursosManager
{
	private static final long serialVersionUID = 8161752430745244011L;
	
	private static final Logger LOG = Logger.getLogger( RecursosManagerImpl.class );

	public static ImedigManager build()
	{
		return new RecursosManagerImpl();
	}

	public RecursosManagerImpl()
	{
		super( RecursoMapper.class, Recurso.class, LOG );
	}

}
