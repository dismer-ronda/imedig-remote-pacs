package es.pryades.imedig.cloud.core.dal;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ibatis.PacienteMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.query.PacienteQuery;
import es.pryades.imedig.core.common.ImedigManager;


/**
*
* @author hector.licea 
* 
*/
public class PacientesManagerImpl extends ImedigCloudManagerImpl implements PacientesManager
{
	private static final long serialVersionUID = 1616814174321878135L;

	private static final Logger LOG = Logger.getLogger( PacientesManagerImpl.class );

	public static ImedigManager build()
	{
		return new PacientesManagerImpl();
	}

	public PacientesManagerImpl()
	{
		super( PacienteMapper.class, Paciente.class, LOG );
	}

	@Override
	public List getPageLazy( ImedigContext ctx, PacienteQuery query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		ArrayList<ImedigDto> rows = null;
		
		try 
		{
			PacienteMapper mapper = (PacienteMapper)session.getMapper( getMapperClass() );
			
			rows = mapper.getPageLazy( query );
			
			for ( ImedigDto dto : rows )
				Utils.nullToEmpty( dto, dto.getClass() );
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

		if ( rows == null )
			throw new Exception( "Null return" );
		
		return rows;
	}

	@Override
	public Paciente getPaciente( ImedigContext ctx, String uid ) throws Throwable
	{
		PacienteQuery query = new PacienteQuery();
		query.setUid( uid );
		
		List<Paciente> pacientes = getRows( ctx, query );
		
		if (pacientes.isEmpty()) return null;
		
		return pacientes.get( 0 );
	}

}
