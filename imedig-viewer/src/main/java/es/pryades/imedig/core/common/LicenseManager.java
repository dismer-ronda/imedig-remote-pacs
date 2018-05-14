package es.pryades.imedig.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Cryptor;
import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.dto.viewer.License;
import es.pryades.imedig.cloud.dto.viewer.LicenseDetails;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class LicenseManager
{
    private static final Logger LOG = Logger.getLogger( LicenseManager.class );
	
	public static final boolean LICENSE_ENABLED = false;

	public static final long ONE_SECOND = 1000; 
	public static final long ONE_MINUTE = 60 * ONE_SECOND; 
	public static final long ONE_HOUR = 60 * ONE_MINUTE; 
	public static final long ONE_DAY = 24 * ONE_HOUR; 

	private static final String PASSWORD = "58877416"; 

	public static final int LICENSE_SIZE = 8; 

	private static Random random = new Random();
	
	private LicenseDetails details; 
	
	static LicenseManager instance = null;

	static public void Init() throws Exception
	{
		instance = new LicenseManager();
	}

	static public LicenseManager getInstance()
	{
		return instance;
	}
	
	public LicenseManager() throws FileNotFoundException
	{
		details = new LicenseDetails();
		
		details.setId( getLicenseId() );
		details.setRights( 0 );
		details.setExpiration( new Date().getTime() );
		
		setLicenseCode( Settings.License );
	}

	public void setRights( long rights )
	{
		details.setRights( rights );
	}
	
	public long getRights()
	{
		return details.getRights();
	}

	public long getId()
	{
		return details.getId();
	}
	
	public long getExpiration()
	{
		return details.getExpiration();
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
		catch ( SocketException e )
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

	public void setExpiration( long expiration )
	{
		if ( expiration == -1 )
			details.setExpiration( -1 );
		else
			details.setExpiration( new java.util.Date().getTime() + (long)expiration * ONE_DAY );
	}
	
	private String convertToHex( byte value )
	{
        String table = "0123456789ABCDEF";

        byte lo = (byte)(value & 0x0F);
		byte hi = (byte)((value & 0xF0) >> 4);
		
        return "" + table.charAt( hi ) + table.charAt( lo );
	}
	
	private String convertToHex( byte[] data ) 
    { 
        StringBuffer buf = new StringBuffer();
    
        for ( int i = 0; i < data.length; i++ ) 
        	buf.append( convertToHex( data[i] ) );

        return buf.toString();
    } 
	
	private byte convertFromHex( byte hi, byte lo )
	{
        String table = "0123456789ABCDEF";

        return (byte)(table.indexOf( lo ) | (table.indexOf( hi ) << 4));
	}
    
	private byte[] convertFromHex( byte[] data ) 
    {
        byte buffer[] = new byte[data.length/2];

        for ( int i = 0; i < data.length; i += 2 ) 
            buffer[i/2] = convertFromHex( data[i], data[i+1] );
        
        return buffer;
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
		    	addresses.add( Long.parseLong( convertToHex( maca ), 16 ) );
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
    
    public Return getLicenseCode( License license ) 
    {
    	Return ret = new Return();
    	
		long buffer[] = new long[LICENSE_SIZE];
		
		for ( int i = 0; i < LICENSE_SIZE; i++ )
			buffer[i] = random.nextLong();
		
		int pos = random.nextInt( LICENSE_SIZE - 4 ) + 3;
		
		buffer[0] = details.getExpiration();
		buffer[1] = pos;
		buffer[2] = details.getId();
		buffer[pos] = details.getRights();

		long sum = 0;
		for ( int i = 0; i < LICENSE_SIZE-1; i++ )
			sum += buffer[i];
		buffer[LICENSE_SIZE-1] = sum; 
		
		try
		{
			license.setCode( convertToHex( Cryptor.encrypt( bufferToBytes( buffer ), PASSWORD ) ) );
		}
		catch ( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR );
			ret.setDesc( e.toString() );
		}
		
		return ret;
	}
    
    public boolean setLicenseCode( String code ) 
    {
		try
		{
			long buffer[] = bytesToBuffer( Cryptor.decrypt( convertFromHex( code.getBytes() ), PASSWORD ) );
	    	
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

			details.setExpiration( buffer[0] );
	    	details.setId( buffer[2] );
	    	details.setRights( buffer[(int)buffer[1]] );
	    	
			Settings.License = code;
			
			LOG.info( "License code set successfully" );
			
			return true;
		}
		catch ( Throwable e )
		{
		}
		
		return false;
    }
    
    public Return exportLicenseCode( LicenseDetails details, License license ) 
    {
    	Return ret = new Return();
    	
		long buffer[] = new long[LICENSE_SIZE];
		
		for ( int i = 0; i < LICENSE_SIZE; i++ )
			buffer[i] = random.nextLong();
		
		int pos = random.nextInt( LICENSE_SIZE - 4 ) + 3;
		
		buffer[0] = details.getExpiration();
		buffer[1] = pos;
		buffer[2] = details.getId();
		buffer[pos] = details.getRights();

		long sum = 0;
		for ( int i = 0; i < LICENSE_SIZE-1; i++ )
			sum += buffer[i];
		buffer[LICENSE_SIZE-1] = sum; 
		
		try
		{
			license.setCode( convertToHex( Cryptor.encrypt( bufferToBytes( buffer ), PASSWORD ) ) );
		}
		catch ( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR );
			ret.setDesc( e.toString() );
		}
		
		return ret;
	}

    public boolean importLicenseCode( String code, LicenseDetails license ) 
    {
    	Return ret = new Return();
    	
		LOG.info( "Decoding license " + code );

		try
		{
			long buffer[] = bytesToBuffer( Cryptor.decrypt( convertFromHex( code.getBytes() ), PASSWORD ) );
	    	
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
			
			if ( buffer[2] == 0 )
			{
				LOG.error( "License error: invalid id" );
				return false;
			}

			license.setExpiration( buffer[0] );
	    	license.setId( buffer[2] );
	    	license.setRights( buffer[(int)buffer[1]] );
	    	
			ret.setCode( ReturnFactory.STATUS_2XX_OK );
			
			LOG.info( "License imported successfully" );

			return true;
		}
		catch ( Throwable e )
		{
			LOG.error( "ERROR: " + e.toString() );
		}
		
		return false;
    }

    public boolean isExpired()
    {
    	if ( details.getExpiration() != -1 )
    		return new java.util.Date().getTime() > details.getExpiration(); 
    		
    	return false;
    }
    
    public boolean isValid()
    {
    	return details.getId() != 0 && checkLicenseId( details.getId() ) && !isExpired();
    }
}
