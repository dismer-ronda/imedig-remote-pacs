package es.pryades.imedig.cloud.core.dal;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.ParametroMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Parametro;
import es.pryades.imedig.cloud.dto.Parametros;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings("rawtypes")
public class ParametrosManagerImpl extends ImedigManagerImpl implements ParametrosManager
{
    private static final Logger LOG = Logger.getLogger( ParametrosManagerImpl.class );

	public static ImedigManager build()
	{
		return new ParametrosManagerImpl();
	}

	public ParametrosManagerImpl()
	{
		super( ParametroMapper.class, Parametro.class, LOG );
	}
	
	@SuppressWarnings("unchecked")
	public void loadParametros( ImedigContext ctx ) throws Throwable
	{
		Parametro query = new Parametro();

		List rows = super.getRows( ctx, query );

		HashMap map = new HashMap();
		
		for ( Object obj : rows )
		{
			Integer centro = ((Parametro)obj).getCentro();
			String codigo = ((Parametro)obj).getCodigo();
			
			String key = (centro == null) ? codigo : centro + codigo; 
			
			map.put( key, obj );
		}

		Parametros parametros = new Parametros();
		
		parametros.setParametros( map );
		
		ctx.setParametros( parametros );
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
