package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.AccesoMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Acceso;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class AccesosManagerImpl extends ImedigManagerImpl implements AccesosManager
{
	private static final long serialVersionUID = -2096425479868799335L;
	
	private static final Logger LOG = Logger.getLogger( AccesosManagerImpl.class );

	public static ImedigManager build()
	{
		return new AccesosManagerImpl();
	}

	public AccesosManagerImpl()
	{
		super( AccesoMapper.class, Acceso.class, LOG );
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
