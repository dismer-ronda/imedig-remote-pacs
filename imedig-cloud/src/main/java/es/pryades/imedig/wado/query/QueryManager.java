package es.pryades.imedig.wado.query;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.Series;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class QueryManager implements Serializable 
{
	private static final long serialVersionUID = 6078039563734637138L;

	private static final Logger LOG = Logger.getLogger( QueryManager.class );

    static QueryManager instance = null;
    
    static final int connectTimeout = 60000; 
    static final int acceptTimeout = 60000; 
	
	public QueryManager()
	{
		super();
	}
	
	static public void Init()
	{
		instance = new QueryManager();
	}

	static public QueryManager getInstance()
	{
		return instance;
	}

	private boolean isStructuredReport( DicomObject obj )
	{
		String scuid = obj.getString( Tag.SOPClassUID );
		
		if ( UID.BasicTextSRStorage.equals( scuid ) )
			return true;
			
		if ( UID.EnhancedSRStorage.equals( scuid ) )
			return true;
				
		return false;		
	}
	
	private boolean isGrayscaleSoftcopyPresentation( DicomObject obj )
	{
		String scuid = obj.getString( Tag.SOPClassUID );
		
		if ( UID.GrayscaleSoftcopyPresentationStateStorageSOPClass.equals( scuid ) )
			return true;
			
		return false;		
	}

	private List<DicomObject> query( StudyRootQuery dcmqr, HashMap<String,String> parameters ) throws ImedigException
	{
		try
		{
			List<DicomObject> objs = null;
		
	        long t1 = System.currentTimeMillis();
	        
	       	dcmqr.open();
	        
	        long t2 = System.currentTimeMillis();
	        
	        LOG.info( "Connected to " + dcmqr.getCalledAET() + " in " + Float.valueOf((t2 - t1) / 1000f) + " s" );
	        LOG.info( "Making a query with " + parameters );
	        
	        objs = dcmqr.query();
	        
	        long t3 = System.currentTimeMillis();
	        
	        LOG.info( "Received "+ Integer.valueOf( objs.size() ) + " matching entries in " + Float.valueOf((t3 - t2) / 1000f) + " seconds" );
	        
	        t2 = t3;
	
	       	dcmqr.close();
	        
	        LOG.info( "Released connection to " + dcmqr.getCalledAET() );
	
	        return objs;
		}
		catch ( Throwable e )
		{
			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}

	private List<DicomObject> queryStudies( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> parameters ) throws ImedigException
	{
		try
		{
	        StudyRootQuery dcmqr = new StudyRootQuery( localAE );
	        
	        dcmqr.setCalledAET( remoteAE, false ); 
	        
	        dcmqr.setRemoteHost( remoteHost );
	        dcmqr.setRemotePort( remotePort );
	            
	        dcmqr.setPackPDV( true );
	        dcmqr.setTcpNoDelay( true );
	        dcmqr.setMaxOpsInvoked( 1 );
	        dcmqr.setMaxOpsPerformed( 0 );
	        dcmqr.setConnectTimeout( connectTimeout );
	        dcmqr.setAcceptTimeout( acceptTimeout );
	
	        dcmqr.setQueryLevel( "STUDY" );
	
	        dcmqr.addMatchingKey( Tag.StudyInstanceUID, parameters.get( "StudyInstanceUID" ) ); 
	        dcmqr.addMatchingKey( Tag.StudyDate, parameters.get( "StudyDate" ) ); 
	        dcmqr.addMatchingKey( Tag.AccessionNumber, parameters.get( "AccessionNumber" ) ); 
	        dcmqr.addMatchingKey( Tag.StudyID, parameters.get( "StudyID" ) ); 
	        dcmqr.addMatchingKey( Tag.ReferringPhysicianName, parameters.get( "ReferringPhysicianName" ) ); 
	        dcmqr.addMatchingKey( Tag.PatientName, parameters.get( "PatientName" ) ); 
	        dcmqr.addMatchingKey( Tag.PatientID, parameters.get( "PatientID" ) ); 
	        dcmqr.addMatchingKey( Tag.IssuerOfPatientID, parameters.get( "IssuerOfPatientID" ) ); 
	        dcmqr.addMatchingKey( Tag.PatientSex, parameters.get( "PatientSex" ) ); 
	        dcmqr.addMatchingKey( Tag.ModalitiesInStudy, parameters.get( "ModalitiesInStudy" ) );
	        dcmqr.addMatchingKey( Tag.StudyTime, null ); 
	        dcmqr.addMatchingKey( Tag.PatientBirthDate, null ); 
	        
	        return query( dcmqr, parameters );
		}
		catch ( Throwable e )
		{
			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}
	
	private List<DicomObject> querySeries( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> parameters ) throws ImedigException
	{
		try
		{
			StudyRootQuery dcmqr = new StudyRootQuery( localAE );
	        
	        dcmqr.setCalledAET( remoteAE, false ); 
	        
	        dcmqr.setRemoteHost( remoteHost );
	        dcmqr.setRemotePort( remotePort );
	            
	        dcmqr.setPackPDV( true );
	        dcmqr.setTcpNoDelay( true );
	        dcmqr.setMaxOpsInvoked( 1 );
	        dcmqr.setMaxOpsPerformed( 0 );
	        dcmqr.setConnectTimeout( connectTimeout );
	        dcmqr.setAcceptTimeout( acceptTimeout );
	
	        dcmqr.setQueryLevel( "SERIES" );
	
	        dcmqr.addMatchingKey( Tag.SeriesInstanceUID, null ); 
	
	        dcmqr.addMatchingKey( Tag.StudyInstanceUID, parameters.get( "StudyInstanceUID" ) ); 
	        dcmqr.addMatchingKey( Tag.Modality, parameters.get( "Modality" ) ); 
	        dcmqr.addMatchingKey( Tag.SeriesNumber, parameters.get( "SeriesNumber" ) ); 
	
	        return query( dcmqr, parameters );
		}
		catch ( Throwable e )
		{
			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}

	public List<DicomObject> queryImages( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> parameters ) throws ImedigException
	{
		try
		{
			StudyRootQuery dcmqr = new StudyRootQuery( localAE );
	        
	        dcmqr.setCalledAET( remoteAE, false ); 
	        
	        dcmqr.setRemoteHost( remoteHost );
	        dcmqr.setRemotePort( remotePort );
	            
	        dcmqr.setPackPDV( true );
	        dcmqr.setTcpNoDelay( true );
	        dcmqr.setMaxOpsInvoked( 1 );
	        dcmqr.setMaxOpsPerformed( 0 );
	        dcmqr.setConnectTimeout( connectTimeout );
	        dcmqr.setAcceptTimeout( acceptTimeout );
	
	        dcmqr.setQueryLevel( "IMAGE" );
	
	        dcmqr.addMatchingKey( Tag.SOPInstanceUID, null ); 
	        
	        dcmqr.addMatchingKey( Tag.StudyInstanceUID, parameters.get( "StudyInstanceUID" ) ); 
	        dcmqr.addMatchingKey( Tag.SeriesInstanceUID, parameters.get( "SeriesInstanceUID" ) ); 
	        dcmqr.addMatchingKey( Tag.InstanceNumber, parameters.get( "InstanceNumber" ) ); 
	        dcmqr.addMatchingKey( Tag.SOPClassUID, parameters.get( "SOPClassUID" ) ); 
	
	        return query( dcmqr, parameters );
		}
		catch ( Throwable e )
		{
			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}
	
	public List<Study> findStudies( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> parameters )
	{
    	List<Study> studies = new ArrayList<Study>();
    	
    	try
    	{
			List<DicomObject> objs = queryStudies( remoteAE, remoteHost, remotePort, localAE, parameters );
			
            for ( DicomObject obj : objs ) 
            {
            	Study study = new Study();
            	
            	Date studyDate = obj.getDate( Tag.StudyDate );
            	Date birthDate = obj.getDate( Tag.PatientBirthDate );
            	
        		SimpleDateFormat f = new SimpleDateFormat( "dd/MM/yyyy" );

        		String StudyInstanceUID = obj.getString( Tag.StudyInstanceUID );
        		
            	study.setStudyInstanceUID( StudyInstanceUID );

            	study.setStudyDate( studyDate == null ? null : f.format( studyDate ) );
            	study.setStudyTime( obj.getString( Tag.StudyTime ) );
            	study.setAccessionNumber( obj.getString( Tag.AccessionNumber ) );
            	study.setStudyID( obj.getString( Tag.StudyID ) );
            	study.setReferringPhysicianName( obj.getString( Tag.ReferringPhysicianName) );
            	study.setPatientName( obj.getString( Tag.PatientName ) );
            	study.setPatientID( obj.getString( Tag.PatientID ) );
            	study.setIssuerOfPatientID( obj.getString( Tag.IssuerOfPatientID ) );
            	study.setPatientBirthDate( birthDate == null ? null : f.format( birthDate ) );
            	study.setPatientSex( obj.getString( Tag.PatientSex ) );
            	study.setModalitiesInStudy( obj.getString( Tag.ModalitiesInStudy ) );
            	
            	studies.add( study );
            }
            
            Comparator<Study> studyComparator = new Comparator<Study>()
            {
				@Override
				public int compare( Study o1, Study o2 )
				{
			 		return -Utils.compateDates( o1.getStudyDate(), o2.getStudyDate() );
				}
            };
            
    		String order = parameters.get( "order" );
    		
    		Boolean desc = true;
    		
    		if ( order != null && "asc".equalsIgnoreCase( order ) )
    			desc = false;
    		
            if ( !desc )
            	studyComparator = Collections.reverseOrder( studyComparator );
            
            Collections.sort( studies, studyComparator );
		}
		catch ( Throwable e )
		{
		}
		
		return studies;
	}
	
	private Study getStudy( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> params ) throws ImedigException 
	{
		List<Study> studies = findStudies( remoteAE, remoteHost, remotePort, localAE, params );
		
		if ( !studies.isEmpty() )
			return studies.get( 0 );

		return null;
	}
	
	private List<Series> getSeriesList( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> params ) throws ImedigException 
	{
		List<Series> seriesList = null;
		
		String StudyInstanceUID = params.get( "StudyInstanceUID" );
		
		List<DicomObject> ser_objs = querySeries( remoteAE, remoteHost, remotePort, localAE, params );

		if ( !ser_objs.isEmpty() )
		{
			seriesList = new ArrayList<Series>();
			
            for ( DicomObject ser_obj : ser_objs ) 
            {
            	Series series = new Series();
            	
				String SeriesInstanceUID = ser_obj.getString( Tag.SeriesInstanceUID );

            	series.setStudyInstanceUID( StudyInstanceUID );
				series.setSeriesInstanceUID( SeriesInstanceUID );
				
				series.setModality( ser_obj.getString( Tag.Modality ) );
				series.setSeriesNumber( ser_obj.getString( Tag.SeriesNumber ) );

				seriesList.add( series );
            }
		}
		
		return seriesList;
	}

	private List<Image> getImageList( String remoteAE, String remoteHost, int remotePort, String localAE, HashMap<String,String> params, String modality ) throws ImedigException 
	{
		List<Image> imageList = null;

		String StudyInstanceUID = params.get( "StudyInstanceUID" );
        String seriesInstanceUID = params.get( "SeriesInstanceUID" );

        List<DicomObject> img_objs = queryImages( remoteAE, remoteHost, remotePort, localAE, params );

		if ( !img_objs.isEmpty() )
		{
			imageList = new ArrayList<Image>();
			
            for ( DicomObject img_obj : img_objs ) 
            {
            	Image image = new Image();
            	
            	String sopInstanceUID = img_obj.getString( Tag.SOPInstanceUID );
    			
            	image.setStudyInstanceUID( StudyInstanceUID );
            	image.setSeriesInstanceUID( seriesInstanceUID );
            	image.setSOPInstanceUID( sopInstanceUID );

            	image.setInstanceNumber( img_obj.getString( Tag.InstanceNumber ) );
            	image.setSOPClassUID( img_obj.getString( Tag.SOPClassUID ) );
            	
        		image.setWadoUrl( "/services/wado?requestType=WADO&studyUID=" + StudyInstanceUID + "&seriesUID=" + seriesInstanceUID + "&objectUID=" + sopInstanceUID );
        		
            	if ( !isGrayscaleSoftcopyPresentation( img_obj ) && !isStructuredReport( img_obj ) )
            		imageList.add( image );	
            }
            
            Comparator<Image> imageComparator = new Comparator<Image>()
            {
				@Override
				public int compare( Image o1, Image o2 )
				{
					return Utils.compareInstances( o1.getInstanceNumber(), o2.getInstanceNumber() );
				}
            };
            
            Collections.sort( imageList, imageComparator );
        }
		
		return imageList;
	}

    public StudyTree getStudyTree( String remoteAE, String remoteHost, int remotePort, String localAE, String StudyInstanceUID, HashMap<String,String> params ) 
    {
		StudyTree studyTree = new StudyTree();
		
		try 
		{
			params.put( "StudyInstanceUID", StudyInstanceUID );

			Study study = getStudy( remoteAE, remoteHost, remotePort, localAE, params );
			
			if ( study == null )
				return null;
			
			List<Series> seriesList = getSeriesList( remoteAE, remoteHost, remotePort, localAE, params );
			
			if ( seriesList == null )
				return null;

			List<SeriesTree> seriesTreeList = new ArrayList<SeriesTree>();
			
            for ( Series series : seriesList ) 
            {
        		HashMap<String,String> img_params = new HashMap<String,String>();
        		
        		img_params.put( "StudyInstanceUID", series.getStudyInstanceUID() );
        		img_params.put( "SeriesInstanceUID", series.getSeriesInstanceUID() );
        		
        		List<Image> imageList = getImageList( remoteAE, remoteHost, remotePort, localAE, img_params, series.getModality() );

        		if ( imageList.size() > 0 )
        		{
					SeriesTree seriesTree = new SeriesTree();
					
            		seriesTree.setSeriesData( series );
					seriesTree.setImageList( imageList );	

            		seriesTreeList.add( seriesTree );
        		}
            }

            studyTree.setStudyData( study );
            studyTree.setSeriesList( seriesTreeList );
		}
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
		}

		return studyTree;
    }

}
