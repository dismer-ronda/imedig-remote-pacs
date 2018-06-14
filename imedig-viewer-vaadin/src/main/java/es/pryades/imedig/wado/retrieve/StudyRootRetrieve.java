package es.pryades.imedig.wado.retrieve;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;

import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSPHandler;
import org.dcm4che2.net.ExtRetrieveTransferCapability;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.net.service.DicomService;
import org.dcm4che2.net.service.StorageService;
import org.dcm4che2.net.PDVInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class StudyRootRetrieve
{
    private static final Logger LOG = LoggerFactory.getLogger( StudyRootRetrieve.class );

    private static final String[] NATIVE_LE_TS = 
    {
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian
    };

    private final Executor executor;
    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();
    private final NetworkConnection remoteConn = new NetworkConnection();
    private final Device device;
    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();
    private final NetworkConnection conn = new NetworkConnection();
    private Association assoc;
    private final DicomObject keys = new BasicDicomObject();
    private final List<TransferCapability> storeTransferCapability = new ArrayList<TransferCapability>(8);

    private int priority = 0;

    private int completed;
    private int warning;
    private int failed;
    
    private HashMap<String,String> retrieved;

    private File storeDest;
    private final static int fileBufferSize = 256;

    private static final String[] DEF_TS = 
    {
        UID.JPEGLossless,
        UID.JPEGLosslessNonHierarchical14,
        UID.JPEGLSLossless,
        UID.JPEGLSLossyNearLossless,
        UID.JPEG2000LosslessOnly,
        UID.JPEG2000,
        UID.JPEGBaseline1,
        UID.JPEGExtended24,
        UID.MPEG2,
        UID.DeflatedExplicitVRLittleEndian,
        UID.ExplicitVRBigEndian,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian
    };

    private static final String[] CUID = 
    {
        UID.HardcopyGrayscaleImageStorageSOPClassRetired,
        UID.HardcopyColorImageStorageSOPClassRetired,
        UID.ComputedRadiographyImageStorage,
        UID.DigitalXRayImageStorageForPresentation,
        UID.DigitalXRayImageStorageForProcessing,
        UID.DigitalMammographyXRayImageStorageForPresentation,
        UID.DigitalMammographyXRayImageStorageForProcessing,
        UID.XRayAngiographicImageStorage,
        UID.EnhancedXAImageStorage,
        UID.XRayRadiofluoroscopicImageStorage,
        UID.EnhancedXRFImageStorage,
        UID.XRayAngiographicBiPlaneImageStorageRetired,
        UID.PositronEmissionTomographyImageStorage,
        UID.XRay3DAngiographicImageStorage,
        UID.XRay3DCraniofacialImageStorage,
        UID.BreastTomosynthesisImageStorage,
        UID.EnhancedPETImageStorage,
        UID.CTImageStorage,
        UID.EnhancedCTImageStorage,
        UID.NuclearMedicineImageStorage,
        UID.MRImageStorage,
        UID.EnhancedMRImageStorage,
        UID.EnhancedMRColorImageStorage,
        UID.RTImageStorage,
        UID.NuclearMedicineImageStorageRetired,
        UID.UltrasoundImageStorageRetired,
        UID.UltrasoundImageStorage,
        UID.SecondaryCaptureImageStorage,
        UID.VLImageStorageTrialRetired,
        UID.VLEndoscopicImageStorage,
        UID.VideoEndoscopicImageStorage,
        UID.VLMicroscopicImageStorage,
        UID.VideoMicroscopicImageStorage,
        UID.VLSlideCoordinatesMicroscopicImageStorage,
        UID.VLPhotographicImageStorage,
        UID.VideoPhotographicImageStorage,
        UID.OphthalmicPhotography8BitImageStorage,
        UID.OphthalmicPhotography16BitImageStorage,
        UID.OphthalmicTomographyImageStorage,
        UID.SiemensCSANonImageStorage,
        UID.BasicTextSRStorage,
        UID.ChestCADSRStorage,
        UID.ColonCADSRStorage,
        UID.ComprehensiveSRStorage
    };

    public StudyRootRetrieve( String name ) 
    {
        device = new Device(name);
        
        executor = new NewThreadExecutor(name);
        
        remoteAE.setInstalled(true);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setNetworkConnection(new NetworkConnection[] { remoteConn });

        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(conn);
        
        ae.setNetworkConnection(conn);
        ae.setAssociationInitiator(true);
        ae.setAssociationAcceptor(false);
        ae.setAETitle(name);
    }

    public final void setLocalHost( String hostname ) 
    {
        conn.setHostname( hostname );
    }

    public final void setLocalPort( int port ) 
    {
        conn.setPort( port );
    }

    public final void setRemoteHost( String hostname ) 
    {
        remoteConn.setHostname( hostname );
    }

    public final void setRemotePort( int port ) 
    {
        remoteConn.setPort( port );
    }

    public final void setCalledAET( String called, boolean reuse ) 
    {
        remoteAE.setAETitle( called );
    
        if ( reuse )
            ae.setReuseAssocationToAETitle( new String[] { called } );
    }

    public String getCalledAET() 
    {
        return remoteAE.getAETitle();
    }

    public final void setCalling( String calling ) 
    {
        ae.setAETitle( calling );
    }

    public final void setUserIdentity( UserIdentity userIdentity ) 
    {
        ae.setUserIdentity( userIdentity );
    }

    public final void setPriority( int priority ) 
    {
        this.priority = priority;
    }

    public final void setConnectTimeout( int connectTimeout ) 
    {
        conn.setConnectTimeout( connectTimeout );
    }

    public final void setMaxPDULengthReceive( int maxPDULength ) 
    {
        ae.setMaxPDULengthReceive( maxPDULength );
    }

    public final void setMaxOpsInvoked( int maxOpsInvoked ) 
    {
        ae.setMaxOpsInvoked( maxOpsInvoked );
    }

    public final void setMaxOpsPerformed( int maxOps ) 
    {
        ae.setMaxOpsPerformed( maxOps );
    }

    public final void setPackPDV( boolean packPDV ) 
    {
        ae.setPackPDV( packPDV );
    }

    public final void setAssociationReaperPeriod( int period ) 
    {
        device.setAssociationReaperPeriod( period );
    }

    public final void setDimseRspTimeout( int timeout ) 
    {
        ae.setDimseRspTimeout( timeout );
    }

    public final void setRetrieveRspTimeout( int timeout ) 
    {
        ae.setRetrieveRspTimeout( timeout );
    }

    public final void setTcpNoDelay( boolean tcpNoDelay ) 
    {
        conn.setTcpNoDelay( tcpNoDelay );
    }

    public final void setAcceptTimeout( int timeout ) 
    {
        conn.setAcceptTimeout( timeout );
    }

    public final void setReleaseTimeout( int timeout ) 
    {
        conn.setReleaseTimeout( timeout );
    }

    public final void setSocketCloseDelay( int timeout ) 
    {
        conn.setSocketCloseDelay( timeout );
    }

    public final void setMaxPDULengthSend( int maxPDULength ) 
    {
        ae.setMaxPDULengthSend( maxPDULength );
    }

    public final void setReceiveBufferSize( int bufferSize ) 
    {
        conn.setReceiveBufferSize( bufferSize );
    }

    public final void setSendBufferSize( int bufferSize ) 
    {
        conn.setSendBufferSize( bufferSize );
    }

    public void addMatchingKey( int[] tagPath, String value ) 
    {
        keys.putString( tagPath, null, value );
    }

    public void addMatchingKey( int tagPath, String value ) 
    {
        keys.putString( tagPath, null, value );
    }

    public void addReturnKey( int[] tagPath ) 
    {
        keys.putNull( tagPath, null );
    }

    public void setRetrieveLevel( String level ) 
    {
        keys.putString( Tag.QueryRetrieveLevel, VR.CS, level );
        
    	for ( String cuid : CUID )
    		storeTransferCapability.add( new TransferCapability( cuid, DEF_TS, TransferCapability.SCP ) );
        
		TransferCapability [] tcs = new TransferCapability[1 + storeTransferCapability.size()];
		
		int i = 0;
		
		tcs[i++] = mkRetrieveTC( UID.StudyRootQueryRetrieveInformationModelGET, NATIVE_LE_TS );

		for ( TransferCapability tc : storeTransferCapability ) 
            tcs[i++] = tc;

		ae.setTransferCapability( tcs );
		
        if ( !storeTransferCapability.isEmpty() ) 
        {
            ae.register( createStorageService() );
        }
    	
    }

    public void open() throws IOException, ConfigurationException, InterruptedException 
    {
        assoc = ae.connect( remoteAE, executor );
    }

    public DicomObject getKeys() 
    {
    	return keys;
    }

    public void setStoreDestination( String filePath ) 
    {
        this.storeDest = new File( filePath );
    }

    private TransferCapability mkRetrieveTC( String cuid, String[] ts ) 
    {
		ExtRetrieveTransferCapability tc = new ExtRetrieveTransferCapability( cuid, ts, TransferCapability.SCU );

		tc.setExtInfoBoolean( ExtRetrieveTransferCapability.RELATIONAL_RETRIEVAL, false );
        tc.setExtInfo(null);

        return tc;
    }

    private DicomService createStorageService() 
    {
        String[] cuids = new String[storeTransferCapability.size()];
        
        int i = 0;
        
        for ( TransferCapability tc : storeTransferCapability ) 
            cuids[i++] = tc.getSopClass();

        return new StorageService( cuids ) 
        {
            protected void onCStoreRQ( Association as, int pcid, DicomObject rq,
                    PDVInputStream dataStream, String tsuid, DicomObject rsp ) throws IOException, DicomServiceException 
            {
                if ( storeDest.exists() ) 
                {
                	Exception exception = null;
                	
                    try 
                    {
                        String cuid = rq.getString( Tag.AffectedSOPClassUID );
                        String iuid = rq.getString( Tag.AffectedSOPInstanceUID );
                        
        				String fileUID = iuid + "-" + java.util.UUID.randomUUID().toString().replaceAll( "-", "" );
                        
                        BasicDicomObject fmi = new BasicDicomObject();
                        
                        fmi.initFileMetaInformation( cuid, iuid, tsuid );
                        
                		File file = new File( storeDest, fileUID );
                    
                        FileOutputStream fos = new FileOutputStream( file );
                        
                        BufferedOutputStream bos = new BufferedOutputStream( fos, fileBufferSize );

                        DicomOutputStream dos = new DicomOutputStream( bos );
                        
                        dos.writeFileMetaInformation( fmi );
                        
                        dataStream.copyTo( dos );
                        
                        dos.close();
                        
                        retrieved.put( iuid, storeDest + "/" + fileUID ); 
                    } 
                    catch ( IOException e ) 
                    {
                		LOG.error( "IOException storing image received {}", e.getMessage() );
                		
                        exception = e; 
                    }
                    
                    if ( exception != null  )
                    {
                		LOG.error( "Dispatching DicomServiceException {}", exception.getMessage() );

                		throw new DicomServiceException( rq, Status.ProcessingFailure, exception.getMessage() );
                    }
                }
                else
            		LOG.error( "Directory {} where the images should be stored does not exist.", storeDest );
            }
        };
    }
    
    private final DimseRSPHandler rspHandler = new DimseRSPHandler() 
    {
        @Override
        public void onDimseRSP( Association as, DicomObject cmd, DicomObject data ) 
        {
        	StudyRootRetrieve.this.onMoveRSP( as, cmd, data );
        }
    };

    protected void onMoveRSP( Association as, DicomObject cmd, DicomObject data ) 
    {
        if ( !CommandUtils.isPending( cmd ) )  
        {
            completed += cmd.getInt(Tag.NumberOfCompletedSuboperations);
            warning += cmd.getInt(Tag.NumberOfWarningSuboperations);
            failed += cmd.getInt(Tag.NumberOfFailedSuboperations);
        }
    }    

    public HashMap<String,String> retrieve() throws IOException, InterruptedException 
    {
        TransferCapability tc = assoc.getTransferCapabilityAsSCU( UID.StudyRootQueryRetrieveInformationModelGET );
    	
    	String cuid = tc.getSopClass();
    	String tsuid = selectTransferSyntax( tc );

		LOG.info( "Send Retrieve Request using {}:\n{}", UIDDictionary.getDictionary().prompt( cuid ), keys );
		
		retrieved = new HashMap<String,String>();
		
		assoc.cget( cuid, priority, keys, tsuid, rspHandler);
    	
    	assoc.waitForDimseRSP();
    	
    	return retrieved;
    }

    public String selectTransferSyntax( TransferCapability tc ) 
    {
        String[] tcuids = tc.getTransferSyntax();
        
        if ( Arrays.asList( tcuids ).indexOf( UID.DeflatedExplicitVRLittleEndian) != -1 )
            return UID.DeflatedExplicitVRLittleEndian;
        
        return tcuids[0];
    }

    public void close() throws InterruptedException 
    {
        assoc.release( true );
    }

}
