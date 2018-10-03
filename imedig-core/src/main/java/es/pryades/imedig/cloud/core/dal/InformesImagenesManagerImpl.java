package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.InformeImagenMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InformesImagenesManagerImpl extends ImedigManagerImpl implements InformesImagenesManager
{
	private static final long serialVersionUID = 6229246994970492334L;
	
	private static final Logger LOG = Logger.getLogger( InformesImagenesManagerImpl.class );

	public static ImedigManager build()
	{
		return new InformesImagenesManagerImpl();
	}

	public InformesImagenesManagerImpl()
	{
		super( InformeImagenMapper.class, InformeImagen.class, LOG );
	}

	@Override
	public SqlSession getDatabaseSession( ImedigContext ctx )
	{
		return ctx.getSessionCloud();
	}

	@Override
	public SqlSession openDatabaseSession( ImedigContext ctx ) throws ImedigException
	{
		return ctx.openSessionCloud();
	}

	@Override
	public void closeDatabaseSession( ImedigContext ctx ) throws ImedigException
	{
		ctx.closeSessionCloud();
	}
}
