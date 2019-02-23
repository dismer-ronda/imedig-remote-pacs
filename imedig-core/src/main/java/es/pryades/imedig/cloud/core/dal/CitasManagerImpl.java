package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ibatis.CitaMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class CitasManagerImpl extends ImedigCloudManagerImpl implements CitasManager
{
	private static final long serialVersionUID = -6213712981966757378L;

	private static final Logger LOG = Logger.getLogger( CitasManagerImpl.class );

	public static ImedigManager build()
	{
		return new CitasManagerImpl();
	}

	public CitasManagerImpl()
	{
		super( CitaMapper.class, Cita.class, LOG );
	}

	@Override
	public Long getLastDate( ImedigContext ctx, CitaQuery query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		try 
		{
			CitaMapper mapper = (CitaMapper)session.getMapper( getMapperClass() );
			
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
