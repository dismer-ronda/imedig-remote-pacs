package es.pryades.imedig.wado.retrieve;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.wado.resources.CacheManager;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class ImageStandardResource extends ImageResource 
{
    private static final Logger LOG = Logger.getLogger( ImageStandardResource.class );
    
    public ImageStandardResource() 
	{
		super();
	}
    
    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }

    boolean checkParameters( HashMap<String,String> params, Return ret )
	{
		if ( !("WADO".equals( params.get( "requestType" ) )) )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_BAD_REQUEST );
			ret.setDesc( "requestType parameter must be present and set to WADO" );

			return false;
		}

		if ( params.get( "objectUID" ) == null )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_BAD_REQUEST );
			ret.setDesc( "objectUID parameter must be present" );

			return false;
		}
		
		return true;
	}
	
	/**
	 * GET
	 */
	@Get("json")
    public Representation toJson() throws Throwable 
    {
		Return ret = new Return();
		
		Representation rep = null;

		HashMap<String,String> params = new HashMap<String,String>();
		
		getParameters( params );
		
		if ( !checkParameters( params, ret ) )
		{
			rep = ReturnFactory.getRepresentation( ret );

        	LOG.error( "Request parameters are not valid: " + ret.toString() );

			getResponse().setStatus( Status.valueOf( ret.getCode() ) );
			
			return rep;
		}

		String SOPInstanceUID = params.get( "objectUID" );
		
		params.put( "StudyInstanceUID", params.get( "studyUID" ) );
		params.put( "SeriesInstanceUID", params.get( "seriesUID" ) );
		params.put( "SOPInstanceUID", SOPInstanceUID );

		CacheManager.CacheElement lck = CacheManager.getInstance().addElement( SOPInstanceUID );

		synchronized ( lck )
		{
			String fileName = null;

			try 
			{
				String content = params.get( "contentType" ); 
				String cache = params.get( "cache" );
				
				MediaType mt = MediaType.IMAGE_PNG;
				
				if ( content != null )
				{
					if ( "image/jpeg".equals( content ) )
						mt = MediaType.IMAGE_JPEG;
					else if ( "application/dicom".equals( content ) )
						mt = MediaType.APPLICATION_OCTET_STREAM;
				}
				
	            boolean exists = lck.exists();
		    	
		    	if ( "no".equals( cache ) )
		    		exists = false;
		    	
		    	HashMap<String,String> luids = null;
		    	
		    	if ( exists )
	            {
	                fileName = lck.filename();

	                LOG.info( "Taking the image "+ SOPInstanceUID + " from cache" );
	            }
		    	else
		    	{
	                LOG.info( "Retrieving the image " + SOPInstanceUID + " from PACS" );
	
	    			String AETitle = Settings.PACS_AETitle;
	    			String Host = Settings.PACS_Host;
	    			int Port = Settings.PACS_Port;

	                luids = RetrieveManager.getInstance().retrieveImage( AETitle, Host, Port, Settings.IMEDIG_AETitle, Settings.Cache_Dir, params );

	                fileName = luids.get( SOPInstanceUID );
	            	
	                lck.filename( fileName );
		    	}
		    	
				if ( fileName == null )
				{
	    			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
	    			ret.setDesc( "Image not found in PACS neither the cache" );
	    			
	    			rep = ReturnFactory.getRepresentation( ret );
				}
				else
				{
					BufferedImage bi = null;
					
					if ( mt != MediaType.APPLICATION_OCTET_STREAM )
					{
						String rows = params.get( "rows" );
						String cols = params.get( "columns" );
						String frame = params.get( "frameNumber" );
						String roi = params.get( "region" );
						String center = params.get( "windowCenter" );
						String width = params.get( "windowWidth" );
						
						bi = getImage( new File( fileName ), frame == null ? 0 : Integer.parseInt( frame ), rows, cols, roi, width, center );
	
						LOG.info( "Retrieved image " + "width=" + bi.getWidth() + " height=" + bi.getHeight() );
					}
	                
		    		if ( bi != null || mt == MediaType.APPLICATION_OCTET_STREAM )
		    		{
		    			FileInputStream is;
		    			
		    			if ( mt == MediaType.IMAGE_JPEG )
		    			{
			    			ImageIO.write( bi, "jpg", new File( fileName + ".jpeg" ) );
			    			
				    		is = new FileInputStream( new File( fileName + ".jpeg" ) );
		    			} 
		    			else if ( mt == MediaType.APPLICATION_OCTET_STREAM )
		    			{
				    		is = new FileInputStream( new File( fileName ) );
		    			} 
		    			else 
		    			{
		    				ImageIO.write( bi, "png", new File( fileName + ".png" ) );
	
		    				is = new FileInputStream( new File( fileName + ".png" ) );
		    			}
	
		    			rep = new InputRepresentation( is, mt );
		    			
		    			if ( mt == MediaType.IMAGE_JPEG )
		    				new File( fileName + ".jpeg" ).delete();
		    			else if ( mt == MediaType.IMAGE_PNG )
		    				new File( fileName + ".png" ).delete();
		    		}
		    		else
		    		{
		    			ret.setCode( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR );
		    			ret.setDesc( "Invalid request parameters" );
		    			
		    			rep = ReturnFactory.getRepresentation( ret );
		    		}
				}
			}
			catch ( Exception e ) 
			{
				ret.setCode( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR );
				ret.setDesc( e.toString() );
				
				rep = ReturnFactory.getRepresentation( ret );
	
				Utils.logException( e, LOG );
			}
		}

		getResponse().setStatus( Status.valueOf( ret.getCode() ) );

		return rep;
    }
}
