package es.pryades.imedig.wado.resources;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.restlet.resource.ServerResource;
import org.apache.log4j.Logger;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public abstract class ImedigResource extends ServerResource implements Serializable
{
	private static final long serialVersionUID = -902475493112818070L;
	
	private static final Logger LOG = Logger.getLogger( ImedigResource.class );

	public ImedigResource() 
	{
		super();
	}

    public void getParameters( HashMap<String,String> parameters )
    {
		String query = getRequest().getResourceRef().getQuery();
		
		if ( query != null && query != "" )
		{
			try 
			{
				String queries[] = URLDecoder.decode( query, "UTF-8" ).split( "&" );

				for ( int i = 0; i < queries.length; i++ )
				{
					String attrs[] = queries[i].split( "=" );
					
					if ( attrs.length == 2 )
						parameters.put( attrs[0], attrs[1] );
				}
			} 
			catch ( UnsupportedEncodingException e ) 
			{
				LOG.error( "Ivalid request parameters {}", e );
			} 
		}
    }
    
	public boolean isStructuredReport( DicomObject obj )
	{
		String scuid = obj.getString( Tag.SOPClassUID );
		
		if ( UID.BasicTextSRStorage.equals( scuid ) )
			return true;
			
		if ( UID.EnhancedSRStorage.equals( scuid ) )
			return true;
				
		return false;		
	}
	
	public boolean isGrayscaleSoftcopyPresentation( DicomObject obj )
	{
		String scuid = obj.getString( Tag.SOPClassUID );
		
		if ( UID.GrayscaleSoftcopyPresentationStateStorageSOPClass.equals( scuid ) )
			return true;
			
		return false;		
	}
}
