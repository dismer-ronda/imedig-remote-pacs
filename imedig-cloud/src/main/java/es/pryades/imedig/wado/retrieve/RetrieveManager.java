package es.pryades.imedig.wado.retrieve;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.net.ConfigurationException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.dto.viewer.ImageHeader;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.wado.resources.CacheManager;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class RetrieveManager 
{
    private static final Logger LOG = Logger.getLogger( RetrieveManager.class );

    static RetrieveManager instance = null;

    static final int connectTimeout = 60000; 
    static final int acceptTimeout = 60000; 
    static final int retrieveTimeout = 60000;
    
	public RetrieveManager()
	{
		super();
	}
	
	static public void Init()
	{
		instance = new RetrieveManager();
	}

	static public RetrieveManager getInstance()
	{
		return instance;
	}

	public HashMap<String,String> retrieve( StudyRootRetrieve dcmqr, HashMap<String,String> parameters ) throws IOException, ConfigurationException, InterruptedException
	{
		HashMap<String,String> result = null;

        long t1 = System.currentTimeMillis();
        
        dcmqr.open();
        
        long t2 = System.currentTimeMillis();
        
        LOG.info( "Connected to " + dcmqr.getCalledAET() + " in " + Float.valueOf((t2 - t1) / 1000f) + " s" );
        LOG.info( "Making a retrieve with " + parameters );
        
        result = dcmqr.retrieve();
        
        long t3 = System.currentTimeMillis();
        
        LOG.info( "Retrieved " + Integer.valueOf( result.size() ) + " images in " + Float.valueOf((t3 - t2) / 1000f) + " seconds" );
        
        t2 = t3;

        dcmqr.close();
        
        LOG.info( "Released connection to " + dcmqr.getCalledAET() );

        return result;
	}
	
	public HashMap<String,String> retrieveImage( String remoteAE, String remoteHost, int remotePort, String localAE, String storeDest, HashMap<String,String> parameters ) throws IOException, ConfigurationException, InterruptedException
	{
		StudyRootRetrieve dcmqr = new StudyRootRetrieve( localAE );
        
        dcmqr.setCalledAET( remoteAE, false ); 
        
        dcmqr.setRemoteHost( remoteHost );
        dcmqr.setRemotePort( remotePort );
            
        dcmqr.setPackPDV( true );
        dcmqr.setTcpNoDelay( true );
        dcmqr.setMaxOpsInvoked( 1 );
        dcmqr.setMaxOpsPerformed( 0 );
        dcmqr.setConnectTimeout( connectTimeout );
        dcmqr.setAcceptTimeout( acceptTimeout );
        dcmqr.setRetrieveRspTimeout( retrieveTimeout );
        
        dcmqr.setRetrieveLevel( "IMAGE" );

        dcmqr.setStoreDestination( storeDest );

        dcmqr.addMatchingKey( Tag.SOPInstanceUID, parameters.get( "SOPInstanceUID" ) ); 
        dcmqr.addMatchingKey( Tag.StudyInstanceUID, parameters.get( "StudyInstanceUID" ) ); 
        dcmqr.addMatchingKey( Tag.SeriesInstanceUID, parameters.get( "SeriesInstanceUID" ) ); 
        
        return retrieve( dcmqr, parameters );
	}
	
	@SuppressWarnings("rawtypes")
	public static DicomObject getImageHeader( File file ) throws Exception, IOException 
    {
        Iterator it = ImageIO.getImageReadersByFormatName( "DICOM" );
        
        if ( !it.hasNext() ) 
        {
        	LOG.error( "DICOM ImageReader not found. Possible cause is missing appContextProtection=false flag in Tomcat server.xml configuration file" );
        	
        	throw new Exception( "DICOM ImageReader not found. Check appContextProtection flag in Tomcat server.xml configuration file" );
        }
        
        ImageReader reader = (ImageReader) it.next();
        
        ImageInputStream in = new FileImageInputStream( file );
        
        try 
        {
            reader.setInput( in, false );
            
			DicomObject data = ((DicomStreamMetaData) reader.getStreamMetadata()).getDicomObject();
			
            return data;
        } 
        finally 
        {
            // !!!! without this, we get "too many open files" when generating
            // icons in a tight loop
            in.close();
        }
    }

    public ImageHeader getMetaData( String remoteAE, String remoteHost, int remotePort, String localAE, String storeDest, HashMap<String,String> params ) throws Exception 
    {
		String StudyInstanceUID = params.get( "StudyInstanceUID" );
		String SeriesInstanceUID = params.get( "SeriesInstanceUID" );
		String SOPInstanceUID = params.get( "SOPInstanceUID" );

		CacheManager.CacheElement lck = CacheManager.getInstance().addElement( SOPInstanceUID );

		synchronized ( lck )
		{
			String fileName = null;
			
			String cache = params.get( "cache" );
			
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

                luids = retrieveImage( remoteAE, remoteHost, remotePort, localAE, Settings.Cache_Dir, params );

                fileName = luids.get( SOPInstanceUID );

                lck.filename( fileName );
	    	}
	    	
			if ( fileName == null )
    			return null;

			DicomObject dh = getImageHeader( new File( fileName ) );

    		if ( dh != null )
    		{
    			ImageHeader ih = new ImageHeader();
    			
    			String spacing[] = dh.getStrings( Tag.PixelSpacing );

    			ih.setStudyInstanceUID( StudyInstanceUID );
    			ih.setSeriesInstanceUID( SeriesInstanceUID ); 
    			ih.setSOPInstanceUID( SOPInstanceUID );

    			ih.setStudyDate( dh.getString( Tag.StudyDate ) );
    			ih.setStudyTime( dh.getString( Tag.StudyTime ) );
    			ih.setAccessionNumber( dh.getString( Tag.AccessionNumber ) );
    			ih.setStudyID( dh.getString( Tag.StudyID ) );
    			ih.setReferringPhysicianName( dh.getString( Tag.ReferringPhysicianName ) );
    			ih.setPatientName( dh.getString( Tag.PatientName ) );
    			ih.setPatientID( dh.getString( Tag.PatientID ) );
    			ih.setIssuerOfPatientID( dh.getString( Tag.IssuerOfPatientID ) );
    			ih.setPatientBirthDate( dh.getString( Tag.PatientBirthDate ) );
    			ih.setPatientSex( dh.getString( Tag.PatientSex ) );
    			ih.setModalitiesInStudy( dh.getString( Tag.ModalitiesInStudy ) );
    			
    			ih.setModality( dh.getString( Tag.Modality ) );
    			ih.setSeriesNumber( dh.getString( Tag.SeriesNumber ) );

    			ih.setSOPClassUID( dh.getString( Tag.SOPClassUID ) );
    			ih.setInstanceNumber( dh.getString( Tag.InstanceNumber ) );

    			ih.setColumns( dh.getInt( Tag.Columns ) );
    			ih.setRows( dh.getInt( Tag.Rows ) );
    			ih.setBitsAllocated(dh.getInt( Tag.BitsAllocated ) );
    			ih.setBitsStored( dh.getInt( Tag.BitsStored ) );
    			ih.setHighBit( dh.getInt( Tag.HighBit ) );
    			ih.setPhotometricInterpretation( dh.getString( Tag.PhotometricInterpretation ) );
    			ih.setPixelRepresentation( dh.getInt( Tag.PixelRepresentation ) );
    			ih.setPixelSpacing( (spacing != null && spacing.length == 2 ) ? spacing[0] + "\\" + spacing[1] : "" ) ;
    			ih.setPlanarConfiguration( dh.getInt( Tag.PlanarConfiguration ) );
    			ih.setRescaleIntercept( dh.getDouble( Tag.RescaleIntercept ) );
    			ih.setRescaleSlope( dh.getDouble( Tag.RescaleSlope ) );
    			ih.setSamplesPerPixel( dh.getInt( Tag.SamplesPerPixel ) );
    			ih.setSliceThickness( dh.getDouble( Tag.SliceThickness ) );
    			ih.setWindowCenter( dh.getString( Tag.WindowCenter ) );
    			ih.setWindowWidth( dh.getString( Tag.WindowWidth ) );
    			ih.setNumberOfFrames( dh.getInt( Tag.NumberOfFrames ) );
    			
    			return ih; 
    		}
		}

		return null;
    }
}
