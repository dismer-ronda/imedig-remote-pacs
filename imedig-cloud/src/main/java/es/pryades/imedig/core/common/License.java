package es.pryades.imedig.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Cryptor;
import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;

@Data
public abstract class License implements Serializable
{
	private static final long serialVersionUID = 1883048926670430029L;
	
	private static final Logger LOG = Logger.getLogger( License.class );

	public static final long ONE_SECOND 			= 1000; 
	public static final long ONE_MINUTE 			= 60 * ONE_SECOND; 
	public static final long ONE_HOUR 				= 60 * ONE_MINUTE; 
	public static final long ONE_DAY 				= 24 * ONE_HOUR; 

	public static final long RIGHTS_BASIC			= 0x01; 
	public static final long RIGHTS_WORKLIST		= 0x02; 
	public static final long RIGHTS_CITATIONS		= 0x04; 

	private static final String PASSWORD 			= "58877416"; 

	public static final int LICENSE_SIZE 			= 6; 
	
	protected long id;
	protected long rights;
	protected long expiration;
	
	private static Random random = new Random();

	public abstract boolean isPresent();
	public abstract long getLicenseId();
	
	public abstract boolean checkLicenseId( long id );
	
	public abstract void readLicense();
	public abstract boolean writeLicense();
	
	public License()
	{
		setId( getLicenseId() );
		setRights( 0 );
		setExpiration( new Date().getTime() );
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
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
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
			Utils.logException( e, LOG );
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
			Utils.logException( e, LOG );
		}
		
		return false;
    }
    
    public boolean importLicenseCode( String code ) 
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
			
			setExpiration( buffer[0] );
	    	setId( buffer[2] );
	    	setRights( buffer[(int)buffer[1]] );
	    	
	    	LOG.info( "License code imported successfully" );
			
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

}
