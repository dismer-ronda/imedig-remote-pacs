package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.HorarioMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Horario;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class HorariosManagerImpl extends ImedigManagerImpl implements HorariosManager
{
	private static final long serialVersionUID = 8336417013940177972L;
	
	private static final Logger LOG = Logger.getLogger( HorariosManagerImpl.class );

	public static ImedigManager build()
	{
		return new HorariosManagerImpl();
	}

	public HorariosManagerImpl()
	{
		super( HorarioMapper.class, Horario.class, LOG );
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
