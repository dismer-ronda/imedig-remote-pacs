package es.pryades.imedig.cloud.core.dal;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.WebServiceRequest;
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
	
	public boolean echoCenter( ImedigContext ctx, DetalleCentro centro )
	{
		SqlSession session = null;
		
		try
		{
			session = ctx.openSessionCloud();
			
	    	DetalleCentro temp = (DetalleCentro)getRow( ctx, centro.getId()  );
	    	
    		long ts = new Date().getTime();
    		
    		String extra = "ts=" + ts;
    		String token = "token=" + Utils.getTokenString( "IMEDIG" + ts, Settings.TrustKey );
    		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ); 
    		
			//String url = "http://" + temp.getIp() + ":" + temp.getPuerto() + "/imedig-viewer/services/echo" + "?" + token + "&" + code;;

			String url =  Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer/services/report/" + ctx.getUsuario().getLogin() + "?" + token + "&" + code;

			LOG.debug( "url " + url  );
			
			WebServiceRequest req = new WebServiceRequest( url, 200, "GET", "", "", new HashMap<String, String>() );

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			try
			{
				boolean ret = req.Execute( null, os, null );
				
				LOG.debug( "response " + os.toString() );
				
				if ( !ret )
				{
					// Cuando falle la conexion, hacemos null la IP 
					// para no chequearla hasta que entre un echo de ese servidor
					//
			    	DetalleCentro clone = (DetalleCentro)Utils.clone( temp );
			    	
			    	clone.setIp( null );
			    	
			    	centrosManager.setRow( ctx, temp, clone );
					
					session.commit();
				}
				
				return ret;
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		finally
		{
			if ( session != null )
			{
				try
				{
					ctx.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
		
		return false;
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
    		
    		String url = Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer/viewer" + "?" + token + "&" + code + "&debug";
    		
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
