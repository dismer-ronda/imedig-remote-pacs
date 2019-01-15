package es.pryades.imedig.pacs.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author hector.licea 
* @since 2.0.0
*/
public abstract class ImedigPacsManagerImpl extends ImedigManagerImpl
{
	public ImedigPacsManagerImpl( Class mapperClass, Class dtoClass, Logger logger )
	{
		super( mapperClass, dtoClass, logger );
	}

	@Override
	public SqlSession getDatabaseSession( ImedigContext ctx )
	{
		return ctx.getSessionPacs();
	}

	@Override
	public SqlSession openDatabaseSession( ImedigContext ctx ) throws Throwable
	{
		return ctx.openSessionPacs();
	}

	@Override
	public void closeDatabaseSession( ImedigContext ctx ) throws Throwable
	{
		ctx.closeSessionPacs();
	}
}
