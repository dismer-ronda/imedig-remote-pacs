package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ibatis.EstudioMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.query.EstudioQuery;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class EstudiosManagerImpl extends ImedigCloudManagerImpl implements EstudiosManager
{
	private static final long serialVersionUID = -6213712981966757378L;

	private static final Logger LOG = Logger.getLogger( EstudiosManagerImpl.class );

	public static ImedigManager build()
	{
		return new EstudiosManagerImpl();
	}

	public EstudiosManagerImpl()
	{
		super( EstudioMapper.class, Estudio.class, LOG );
	}

	@Override
	public Long getLastDate( ImedigContext ctx, EstudioQuery query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		try 
		{
			EstudioMapper mapper = (EstudioMapper)session.getMapper( getMapperClass() );
			
			return mapper.getLastDate( query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, getLogger() );
			
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
	}
}
