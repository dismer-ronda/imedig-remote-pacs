package es.pryades.imedig.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import lombok.Data;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Cryptor;
import es.pryades.imedig.cloud.common.Utils;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

@Data 
public class LicenseManager implements Serializable
{
	private static final long serialVersionUID = -4381813041140479179L;

	private static final Logger LOG = Logger.getLogger( LicenseManager.class );
	
	public static final boolean LICENSE_ENABLED 	= true;

	public static final long ONE_SECOND 			= 1000; 
	public static final long ONE_MINUTE 			= 60 * ONE_SECOND; 
	public static final long ONE_HOUR 				= 60 * ONE_MINUTE; 
	public static final long ONE_DAY 				= 24 * ONE_HOUR; 

	public static final long RIGHTS_BASIC			= 0x01; 
	public static final long RIGHTS_WORKLIST		= 0x02; 
	public static final long RIGHTS_CITATIONS		= 0x04; 

	private static final String PASSWORD 			= "58877416"; 

	public static final int LICENSE_SIZE 			= 6; 

	private static Random random = new Random();
	
	private long id;
	private long rights;
	private long expiration;
	
	static LicenseManager instance = null;

	static public void Init() throws Exception
	{
		instance = new LicenseManager();
	}

	static public LicenseManager getInstance()
	{
		return instance;
	}
	
	public LicenseManager()
	{
		setId( getLicenseId() );
		setRights( 0 );
		setExpiration( new Date().getTime() );
	}

	private long getLicenseId()
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
	
	private boolean checkLicenseId( long id )
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

	private ArrayList<Long> getMACAddresses() throws SocketException
    {
	    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	    
	    ArrayList<Long> addresses = new ArrayList<Long>();
	 
	    while ( interfaces.hasMoreElements() ) 
	    {
		    NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
	
		    byte[] maca = ni.getHardwareAddress();
	
		    if ( maca != null )
		    	addresses.add( Long.parseLong( Utils.convertToHex( maca ), 16 ) );
	    }
	    
	    return addresses;
    }
    
    private byte [] bufferToBytes( long [] buffer )
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();

    	DataOutputStream dos = new DataOutputStream(baos);
    	
    	try
		{
    		for ( int i = 0; i < LICENSE_SIZE; i++ )
    			dos.writeLong( buffer[i] );
    		
	    	dos.flush();
		}
		catch ( IOException e )
		{
		}
    	
    	return baos.toByteArray();
    }

    private long [] bytesToBuffer( byte [] values ) throws IOException
    {
    	ByteArrayInputStream bais = new ByteArrayInputStream( values );

    	DataInputStream dis = new DataInputStream( bais );
    	
    	long buffer[] = new long[LICENSE_SIZE];
    	
    	for ( int i = 0; i < LICENSE_SIZE; i++ )
    		buffer[i] = dis.readLong();
    	
		return buffer;
    }
 
    public String getLicenseCode() 
    {
		long buffer[] = new long[LICENSE_SIZE];
		
		for ( int i = 0; i < LICENSE_SIZE; i++ )
			buffer[i] = random.nextLong();
		
		int pos = random.nextInt( LICENSE_SIZE - 4 ) + 3;
		
		buffer[0] = getExpiration();
		buffer[1] = pos;
		buffer[2] = getId();
		buffer[pos] = getRights();

		long sum = 0;
		for ( int i = 0; i < LICENSE_SIZE-1; i++ )
			sum += buffer[i];
		buffer[LICENSE_SIZE-1] = sum; 
		
		try
		{
			return Utils.convertToHex( Cryptor.encrypt( bufferToBytes( buffer ), PASSWORD ) );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		
		return null;
	}

    public boolean setLicenseCode( String code ) 
    {
		try
		{
			long buffer[] = bytesToBuffer( Cryptor.decrypt( Utils.convertFromHex( code.getBytes() ), PASSWORD ) );
	    	
			long sum = 0;
			for ( int i = 0; i < LICENSE_SIZE-1; i++ )
				sum += buffer[i];

			if ( buffer[LICENSE_SIZE-1] != sum )
			{
				LOG.error( "License error: invalid checksum" );
				return false;
			}

			if ( buffer[1] < 3 || buffer[1] > LICENSE_SIZE - 2 )
			{
				LOG.error( "License error: invalid format" );
				return false;
			}
			
			if ( buffer[2] == 0 || !checkLicenseId( buffer[2] ) )
			{
				LOG.error( "License error: invalid id" );
				return false;
			}

			setExpiration( buffer[0] );
	    	setId( buffer[2] );
	    	setRights( buffer[(int)buffer[1]] );
	    	
	    	LOG.info( "License code set successfully" );
			
			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		
		return false;
    }
    
    public boolean isExpired()
    {
    	if ( getExpiration() != -1 )
    		return new java.util.Date().getTime() > getExpiration(); 
    		
    	return false;
    }
    
    public boolean isValid()
    {
    	return getId() != 0 && checkLicenseId( getId() ) && !isExpired() && getRights() != 0;
    }
    
    public boolean hasBasicRights()
    {
    	return isValid() && !isExpired() && (getRights() & RIGHTS_BASIC) == RIGHTS_BASIC;
    }

    public boolean hasWorklistRights()
    {
    	return isValid() && !isExpired() && (getRights() & RIGHTS_WORKLIST) == RIGHTS_WORKLIST;
    }

    public boolean hasCitationsRights()
    {
    	return isValid() && !isExpired() && (getRights() & RIGHTS_CITATIONS) == RIGHTS_CITATIONS;
    }
    
	public void addExpiration( long expiration )
	{
		if ( expiration == -1 )
			setExpiration( -1 );
		else
			this.expiration = new java.util.Date().getTime() + (long)expiration * ONE_DAY;
	}
	
    public void setBasicRights()
    {
    	setRights( getRights() | RIGHTS_BASIC );
    }

    public void setWorklistRights()
    {
    	setRights( getRights() | RIGHTS_WORKLIST );
    }
    
    public void setCitationsRights()
    {
    	setRights( getRights() | RIGHTS_CITATIONS );
    }

    public static void main( String[] args )
	{
		try
		{
			LicenseManager.Init();
			
			LicenseManager.getInstance().setLicenseCode( "CFCD9B2D32E52EFF111940D684EC5B2EA21BCAFEA77F9D86F9A58B66BCD3218FE768C9FD5E777B3D347D584BAF235A278C949EE82DA51047" );
			
			LicenseManager.getInstance().setBasicRights();
			LicenseManager.getInstance().setWorklistRights();
			LicenseManager.getInstance().setCitationsRights();
			LicenseManager.getInstance().addExpiration( 365 );
			
			System.out.println( "Expiration = " + LicenseManager.getInstance().getExpiration() );
			System.out.println( "Rights = " + Long.toHexString( LicenseManager.getInstance().getRights() ) );
			System.out.println( "Id = " + Long.toHexString( LicenseManager.getInstance().getId() ) );
			System.out.println( LicenseManager.getInstance().getLicenseCode() );
			System.out.println();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}
}
