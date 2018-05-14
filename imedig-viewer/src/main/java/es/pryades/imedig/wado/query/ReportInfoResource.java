package es.pryades.imedig.wado.query;

import java.util.HashMap;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.wado.resources.ImedigResource;
import es.pryades.imedig.wiewer.echo3.ViewerWnd;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class ReportInfoResource extends ImedigResource 
{
    private static final Logger LOG = Logger.getLogger( ReportInfoResource.class );

    String user;
    
    public ReportInfoResource() 
	{
		super();
	}
    
    @Override
    protected void doInit() throws ResourceException 
    {
		user = (String)getRequest().getAttributes().get( "user" );

        setExisting( user != null );
    }
 
	static public Representation getRepresentation( ReportInfo image ) throws Throwable
	{
		return new StringRepresentation( Utils.toJson( image ), MediaType.APPLICATION_JSON );
	}
	
	/**
	 * GET
	 */
	@Get("json")
    public Representation toJson() throws Throwable 
    {
		Representation rep;
		
		Return ret = new Return();
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		getParameters( params );
		
		try 
		{
			ReportInfo info = ViewerWnd.imagesInfo.get( user );
			
			if ( info == null )
			{
				ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
				ret.setDesc( "No image found" );
				
				rep = ReturnFactory.getRepresentation( ret );
			}
			else
			{
				rep = getRepresentation( info );
				
				ViewerWnd.imagesInfo.put( user, null );
			}

			getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		}
		catch ( Throwable e ) 
		{
			ret.setCode( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR );
			ret.setDesc( e.getMessage() );
			
			rep = ReturnFactory.getRepresentation( ret );

			Utils.logException( e, LOG );

			getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		}

		return rep;
    }
}
