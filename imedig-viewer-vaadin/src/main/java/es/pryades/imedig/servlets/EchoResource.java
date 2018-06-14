package es.pryades.imedig.servlets;

import java.util.HashMap;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.ImedigResource;
import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.core.common.Settings;

public class EchoResource extends ImedigResource 
{
    private static final Logger LOG = Logger.getLogger( EchoResource.class );

	public EchoResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
	/**
	 * GET
	 */
	@Get("text")
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
			if ( Utils.isValidRequest( token, "IMEDIG" + ts, ts, Settings.TrustKey, Utils.ONE_SECOND * 30 ) ) 
			{
		    	rep = new StringRepresentation( Integer.toString( ret.getCode() ) ); 
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
			
			if ( !(e instanceof ImedigException) )
				new ImedigException( e, LOG, 0 );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
