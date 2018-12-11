package es.pryades.imedig.cloud.core.dal;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.ibatis.DetalleCentroMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class DetallesCentrosManagerImpl extends ImedigManagerImpl implements DetallesCentrosManager
{
	private static final long serialVersionUID = 6056863621045915636L;

	private static final Logger LOG = Logger.getLogger( DetallesCentrosManagerImpl.class );

    @Inject
    CentrosManager centrosManager;
    
    @Inject
	UsuariosManager usuariosManager;
	
	public static ImedigManager build()
	{
		return new DetallesCentrosManagerImpl();
	}

	public DetallesCentrosManagerImpl()
	{
		super( DetalleCentroMapper.class, DetalleCentro.class, LOG );
	}
	
	private Usuario getUsuario( ImedigContext ctx )
	{
    	try
    	{
    		return (Usuario)usuariosManager.getRow( ctx, ctx.getUsuario().getId() );
    	}
		catch ( Throwable e )
		{
			return ctx.getUsuario();
		}
	}
	
	public String getCentroUrl( ImedigContext ctx, DetalleCentro centro, String uid )
	{
    	if ( centro != null  )
    	{
    		long ts = new Date().getTime();
    		
    		Usuario usuario = getUsuario( ctx );
    		
    		String extra = "ts=" + ts + 
    						"&login=" + usuario.getLogin() + 
    						"&filter=" + usuario.getFiltro() +
    						"&query=" + usuario.getQuery() +
    						"&compression=" + usuario.getCompresion() +
							"&uid=" + uid;
    		String token = "token=" + Utils.getTokenString( "IMEDIG" + ts, Settings.TrustKey );
    		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ) ;
    		
    		String url = ctx.getData( "Url") + "/imedig-viewer/viewer" + "?" + token + "&" + code + "&debug";
    		
    		return url;
    	}
    	
    	return null;
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
