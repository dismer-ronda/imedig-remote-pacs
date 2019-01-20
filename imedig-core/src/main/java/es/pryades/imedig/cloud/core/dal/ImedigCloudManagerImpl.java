package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.ImedigManagerImpl;


/**
*
* @author hector.licea 
* 
*/
public abstract class ImedigCloudManagerImpl extends ImedigManagerImpl implements AccesosManager
{
	private static final long serialVersionUID = 4736705092542624933L;

	public ImedigCloudManagerImpl( Class mapperClass, Class dtoClass, Logger logger )
	{
		super( mapperClass, dtoClass, logger );
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
