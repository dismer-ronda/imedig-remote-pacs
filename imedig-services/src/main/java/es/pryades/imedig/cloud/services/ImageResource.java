package es.pryades.imedig.cloud.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import es.pryades.imedig.cloud.common.ImedigResource;
import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class ImageResource extends ImedigResource 
{
    private static final Logger LOG = Logger.getLogger( ImageResource.class );

    String id;

	private ImagenesManager imagenesManager;

	public ImageResource() 
	{
		super();
    	
		imagenesManager = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );
	}

    @Override
    protected void doInit() throws ResourceException 
    {
    	id = (String)getRequest().getAttributes().get( "id" );

        setExisting( id != null );
    }
	
	/**
	 * GET
	 */
	@Get("png")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
		
		String token = params.get( "token" );
		String code = Utils.decrypt( params.get( "code" ), Settings.TrustKey );
		
		params.clear();
		
        Utils.getParameters( code, params );
        
        String ts = params.get( "ts" );
        
		Return ret = new Return();
	    
		try
		{
			if ( Utils.isValidRequest( token, id + ts, ts, Settings.TrustKey, 0 ) ) 
			{
				rep = new OutputRepresentation(MediaType.IMAGE_PNG) 
				{
					@Override
					public void write( OutputStream arg0 ) throws IOException
					{
						try
						{
					    	ImedigContext ctx = new ImedigContext();
					    	
							Imagen imagen = (Imagen)imagenesManager.getRow( ctx, Integer.parseInt( id ) );
							
							arg0.write( imagen.getDatos() );
						}
						catch ( Throwable e )
						{
							Utils.logException( e, LOG );
						}
					}
				};				
			}
			else
			{
				ret.setCode( ReturnFactory.STATUS_4XX_FORBIDDEN );
				ret.setDesc( "Access denied" );
	
				rep = new StringRepresentation( ret.getDesc() );
			}
		}
		catch( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
			ret.setDesc( "Resource not found" );

			rep = new StringRepresentation( ret.getDesc() ); 
			
			Utils.logException( e, LOG );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
