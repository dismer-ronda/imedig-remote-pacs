package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ibatis.CentroMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class CentrosManagerImpl extends ImedigManagerImpl implements CentrosManager
{
	private static final long serialVersionUID = -4960454412875591797L;
	
	private static final Logger LOG = Logger.getLogger( CentrosManagerImpl.class );

	public static ImedigManager build()
	{
		return new CentrosManagerImpl();
	}

	public CentrosManagerImpl()
	{
		super( CentroMapper.class, Centro.class, LOG );
	}
	
	public void updateIpPort( ImedigContext ctx, Centro centro, String ip, String port ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		try
		{
			Centro clone = (Centro)Utils.clone( centro );
			
			clone.setIp( ip );
			clone.setPuerto( Integer.parseInt( port ) );
			
			setRow( ctx, centro, clone );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
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
