package es.pryades.imedig.core.common;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;

public class EthernetLicense extends License
{
	private static final long serialVersionUID = -720073413179770574L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( EthernetLicense.class );
	
	public EthernetLicense()
	{
	}

	private ArrayList<Long> getMACAddresses() throws SocketException
    {
	    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	    
	    ArrayList<Long> addresses = new ArrayList<Long>();
	 
	    while ( interfaces.hasMoreElements() ) 
	    {
		    NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
		    
		    byte[] maca = ni.getHardwareAddress();
	
		    if ( maca != null && !ni.getDisplayName().contains( "docker" ) )
		    {
		    	String hex = Utils.convertToHex( maca );
		    	Long l = Long.parseLong( hex, 16 );

		    	addresses.add( l );
		    	
			    // LOG.info( "NIC " + ni.getDisplayName() + " MAC address hex " + hex );
		    }
	    }
	    
	    return addresses;
    }

	@Override
	public boolean isPresent()
	{
		return true;
	}

	@Override
	public  long getLicenseId()
	{
    	ArrayList<Long> addresses;
 
    	try
		{
			addresses = getMACAddresses();
 
			if ( addresses.size() != 0 )
	    		return addresses.get( 0 );
		}
		catch ( Throwable e )
		{
		}
 
		return 0;
	}
	
	@Override
	public boolean checkLicenseId( long id )
	{
    	ArrayList<Long> addresses;
 
    	try
		{
			addresses = getMACAddresses();
 
			for ( Long id1 : addresses ) 
	    		if ( id == id1 )
	    			return true;
		}
		catch ( SocketException e )
		{
		}
 
		return false;
	}

	@Override
	public void readLicense()
	{
		String code = Utils.readFile( "/opt/imedig/conf/license.txt" ).replaceAll( "\n", "" );
		
		if ( !code.isEmpty() )
			setLicenseCode( code );
	}

	@Override
	public boolean writeLicense()
	{
		Utils.writeFile( "/opt/imedig/conf/license.txt", getLicenseCode() );
		
		return true;
	}
}
