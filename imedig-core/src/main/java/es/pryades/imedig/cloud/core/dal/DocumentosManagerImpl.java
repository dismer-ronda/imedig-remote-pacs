package es.pryades.imedig.cloud.core.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.ibatis.DocumentoMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Documento;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class DocumentosManagerImpl extends ImedigManagerImpl implements DocumentosManager
{
	private static final long serialVersionUID = -4475911105694937987L;
	
	private static final Logger LOG = Logger.getLogger( DocumentosManagerImpl.class );

	public static ImedigManager build()
	{
		return new DocumentosManagerImpl();
	}

	public DocumentosManagerImpl()
	{
		super( DocumentoMapper.class, Documento.class, LOG );
	}

	@Override
	public boolean hasBlob() 
	{
		return true;
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
