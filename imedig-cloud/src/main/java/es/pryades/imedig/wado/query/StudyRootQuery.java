package es.pryades.imedig.wado.query;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;

import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.ExtQueryTransferCapability;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;

import org.apache.log4j.Logger;

/**
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class StudyRootQuery 
{
    private static final Logger LOG = Logger.getLogger( StudyRootQuery.class );

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

    private int priority = 0;

    public StudyRootQuery( String name ) 
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

    private TransferCapability mkFindTC( String cuid, String[] ts ) 
    {
        ExtQueryTransferCapability tc = new ExtQueryTransferCapability( cuid, ts, TransferCapability.SCU );
        
        tc.setExtInfoBoolean( ExtQueryTransferCapability.RELATIONAL_QUERIES, false );
        tc.setExtInfoBoolean( ExtQueryTransferCapability.DATE_TIME_MATCHING, false );
        tc.setExtInfoBoolean( ExtQueryTransferCapability.FUZZY_SEMANTIC_PN_MATCHING, false );
        tc.setExtInfo( null );
        
        return tc;
    }

    public void setQueryLevel( String level ) 
    {
        keys.putString( Tag.QueryRetrieveLevel, VR.CS, level );

        TransferCapability [] tcs = new TransferCapability[1];
		
		tcs[0] = mkFindTC( UID.StudyRootQueryRetrieveInformationModelFIND, NATIVE_LE_TS);
		
		ae.setTransferCapability( tcs );
    }

    public void open() throws IOException, ConfigurationException, InterruptedException 
    {
        assoc = ae.connect( remoteAE, executor );
    }

    public DicomObject getKeys() 
    {
    	return keys;
    }
    
    public List<DicomObject> query() throws IOException, InterruptedException 
    {
        TransferCapability tc = assoc.getTransferCapabilityAsSCU( UID.StudyRootQueryRetrieveInformationModelFIND );
        
        String cuid = tc.getSopClass();
        String tsuid = selectTransferSyntax( tc );
        
        List<DicomObject> result = new ArrayList<DicomObject>();

        LOG.info( "Send Query Request using " + UIDDictionary.getDictionary().prompt( cuid ) + ":\n" + keys );
        
        DimseRSP rsp = assoc.cfind( cuid, priority, keys, tsuid, Integer.MAX_VALUE );
        
        for ( int i = 0; rsp.next(); ++i ) 
        {
            DicomObject cmd = rsp.getCommand();
        
            if ( CommandUtils.isPending( cmd ) ) 
            {
                DicomObject data = rsp.getDataset();
                
                DicomObject suidKey = new BasicDicomObject();
                
                data.copyTo( suidKey );
                
                result.add( suidKey );
            }
        }

        return result;
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
