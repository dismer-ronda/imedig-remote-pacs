package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.UsuarioCentrosMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class UsuarioCentrosManagerImpl extends ImedigManagerImpl implements UsuarioCentrosManager
{
	private static final long serialVersionUID = -2799432844991492724L;
	
	private static final Logger LOG = Logger.getLogger( UsuarioCentrosManagerImpl.class );

	public static ImedigManager build()
	{
		return new UsuarioCentrosManagerImpl();
	}
	
	public UsuarioCentrosManagerImpl()
	{
		super( UsuarioCentrosMapper.class, Usuario.class, LOG );
	}

	@Override
	public boolean hasUniqueId( ImedigContext ctx ) 
	{
		return false;
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
