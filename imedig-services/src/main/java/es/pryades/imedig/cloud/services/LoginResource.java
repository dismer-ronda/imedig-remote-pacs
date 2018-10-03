package es.pryades.imedig.cloud.services;

import java.io.IOException;
import java.util.HashMap;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigResource;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class LoginResource extends ImedigResource 
{
	private static final long serialVersionUID = 6049285688332966529L;
	
	private static final Logger LOG = Logger.getLogger( LoginResource.class );

	public LoginResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
	public Usuario remoteLogin( Usuario usuario ) throws Throwable
	{
    	ImedigContext ctx = new ImedigContext();
		
		synchronized ( ctx )
		{
			UsuariosManager usuariosManager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );
			
			usuariosManager.remoteLogin( ctx, usuario.getLogin(), usuario.getPwd() );
			
			return ctx.getUsuario();
		}
	}
    
    private void processRequest( Representation entity ) throws IOException
    {
		try
		{
			String text = entity != null ? entity.getText() : "";
			
			if ( text.isEmpty() )
			{
				LOG.info( "empty message received" );

				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
				getResponse().setEntity( new StringRepresentation( "failure" ) );
			}
			else
			{
				HashMap<String,String> params = new HashMap<String,String>();
				
				Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
				
				String token = params.get( "token" );
				String code = Utils.decrypt( params.get( "code" ), Settings.TrustKey );
				
				params.clear();
				
		        Utils.getParameters( code, params );
		        
		        String ts = params.get( "ts" );
				
				if ( Utils.isValidRequest( token, ts, ts, Settings.TrustKey, 0 ) )
				{
					Usuario usuario = remoteLogin( (Usuario)Utils.toPojo( text, Usuario.class, false ) );
	
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
					getResponse().setEntity( new StringRepresentation( Utils.toJson( usuario ), MediaType.APPLICATION_JSON ) );
				}
				else
				{
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
				}
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
		}
    }
    
    @Post("application/json")
    public void doPost( Representation entity ) throws Exception 
    {
    	processRequest( entity );
    }
}
