package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.DetalleEstadisticaInformeMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleEstadisticaInforme;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class DetallesEstadisticasInformesManagerImpl extends ImedigManagerImpl implements DetallesEstadisticasInformesManager
{
    private static final Logger LOG = Logger.getLogger( DetallesEstadisticasInformesManagerImpl.class );

    @Inject
    InformesManager InformesManager;
    
	public static ImedigManager build()
	{
		return new DetallesEstadisticasInformesManagerImpl();
	}

	public DetallesEstadisticasInformesManagerImpl()
	{
		super( DetalleEstadisticaInformeMapper.class, DetalleEstadisticaInforme.class, LOG );
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
