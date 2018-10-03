package es.pryades.imedig.cloud.services;

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
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class EchoResource extends ImedigResource 
{
	private static final long serialVersionUID = -3763881630167296713L;

	private static final Logger LOG = Logger.getLogger( EchoResource.class );

    String serial;

	CentrosManager centrosManager;
	DetallesCentrosManager dcentrosManager;

	public EchoResource() 
	{
		super();
    	
		centrosManager = (CentrosManager) IOCManager.getInstanceOf( CentrosManager.class );
    	dcentrosManager = (DetallesCentrosManager) IOCManager.getInstanceOf( DetallesCentrosManager.class );
	}

    @Override
    protected void doInit() throws ResourceException 
    {
    	serial = (String)getRequest().getAttributes().get( "serial" );

        setExisting( serial != null );
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
		
		String ip = getRequest().getClientInfo().getAddress();
		
		String token = params.get( "token" );
		String code = Utils.decrypt( params.get( "code" ), Settings.TrustKey );
		
		params.clear();
		
        Utils.getParameters( code, params );
        
        String ts = params.get( "ts" );
        String port = params.get( "port" );

        LOG.info( "ts = " + ts );
        LOG.info( "port = " + port );
        LOG.info( "serial = " + serial );
        
		Return ret = new Return();
	    
		try
		{
	    	ImedigContext ctx = new ImedigContext();
	    	
	    	CentroQuery query = new CentroQuery();
	    	query.setSerie( serial );
	    	
	    	DetalleCentro centro = (DetalleCentro)dcentrosManager.getRow( ctx, query  );
	    	
			if ( Utils.isValidRequest( token, serial + ts, ts, Settings.TrustKey, 0 ) ) 
			{
		    	centrosManager.updateIpPort( ctx, centro, ip, port );
		    	
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
