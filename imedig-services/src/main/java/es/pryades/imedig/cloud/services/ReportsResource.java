package es.pryades.imedig.cloud.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class ReportsResource extends ImedigResource 
{
    private static final Logger LOG = Logger.getLogger( ReportsResource.class );

	public ReportsResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
	public List<DetalleInforme> getReports( InformeQuery query ) 
	{
    	ImedigContext ctx = new ImedigContext();
		
		try
		{
			DetallesInformesManager informesManager = (DetallesInformesManager)IOCManager.getInstanceOf( DetallesInformesManager.class );
			
			return informesManager.getRows( ctx, query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			return new ArrayList<DetalleInforme>();
		}
	}
    
    private void processRequest( Representation entity ) throws IOException
    {
		String text = entity != null ? entity.getText() : "";
		
		LOG.info( "text "+ text );
		
		if ( entity == null )
		{
			LOG.info( "empty message received" );

			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
			getResponse().setEntity( new StringRepresentation( "failure" ) );
		}
		else
		{
			try
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
			        List<DetalleInforme> informes = getReports( (InformeQuery)Utils.toPojo( text, InformeQuery.class, false ) );
	
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
					getResponse().setEntity( new StringRepresentation( Utils.toJson( informes ), MediaType.APPLICATION_JSON ) );
				}
				else
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_FORBIDDEN ) );
					
			}
			catch ( Throwable e )
			{
				e.printStackTrace();
				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
			}
		}
    }
    
    @Post("application/json")
    public void doPost( Representation entity ) throws Exception 
    {
    	processRequest( entity );
    }
}
