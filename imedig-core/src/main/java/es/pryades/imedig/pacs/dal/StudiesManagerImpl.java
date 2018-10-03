package es.pryades.imedig.pacs.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;
import es.pryades.imedig.pacs.dal.ibatis.StudyMapper;
import es.pryades.imedig.pacs.dto.Study;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class StudiesManagerImpl extends ImedigManagerImpl implements StudiesManager
{
	private static final long serialVersionUID = 4264000490880357403L;
	
	private static final Logger LOG = Logger.getLogger( StudiesManagerImpl.class );

	public static ImedigManager build()
	{
		return new StudiesManagerImpl();
	}

	public StudiesManagerImpl()
	{
		super( StudyMapper.class, Study.class, LOG );
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

	@Override
	public Integer getTotalImages( ImedigContext ctx, Query query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		int count = 0;
		
		try 
		{
			StudyMapper mapper = (StudyMapper)session.getMapper( getMapperClass() );
			
			count = mapper.getTotalImages( query ); 
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
		
		return count;
	}
}
