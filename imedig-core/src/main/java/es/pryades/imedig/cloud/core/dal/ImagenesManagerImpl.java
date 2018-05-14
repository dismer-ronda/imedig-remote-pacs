package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.ImagenMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ImagenesManagerImpl extends ImedigManagerImpl implements ImagenesManager
{
    private static final Logger LOG = Logger.getLogger( ImagenesManagerImpl.class );

	public static ImedigManager build()
	{
		return new ImagenesManagerImpl();
	}

	public ImagenesManagerImpl()
	{
		super( ImagenMapper.class, Imagen.class, LOG );
	}

	@Override
	public boolean hasBlob() 
	{
		return true;
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
