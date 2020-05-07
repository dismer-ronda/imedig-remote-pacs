package es.pryades.imedig.core.common;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;

public class Rockey2License extends License
{
	private static final long serialVersionUID = -720073413179770574L;
	
	private static final Logger LOG = Logger.getLogger( Rockey2License.class );

	private static int present;
	private static int[] iHid = new int[10];
	
	@Override
	public boolean isPresent()
	{
		try
		{
			byte[] cBuffer = new byte[513];

			present = Rockey2Imedig.INSTANCE.isPresent( iHid, cBuffer );
			
			if ( present != 0 )
			{
				String code = new String( cBuffer, 0, BufLen( cBuffer, 512 ) );
				
				LOG.info( "code=" + code );
				
				if ( !code.isEmpty() )
					setLicenseCode( code );
				
				return true;
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			Utils.logException( e, LOG );
		}

		return false;
	}

	@Override
	public long getLicenseId()
	{
		if ( present != 0 )
			return iHid[0];
		
		return 0;
	}
	
	@Override
	public boolean checkLicenseId( long id )
	{
		return id == getLicenseId();
	}
	
	public int BufLen( byte[] buffer, int len )
	{
		int i = 0;
		while ( i < len && buffer[i] != 0 )
			i++;
		
		return i;
	}
	
	@Override
	public void readLicense()
	{
	}

	@Override
	public boolean writeLicense()
	{
		byte[] code = getLicenseCode().getBytes();
		
		byte[] cBuffer = new byte[513];
		for ( int i = 0; i < code.length; i++ )
			cBuffer[i] = code[i];
		cBuffer[code.length] = 0;
		
		int iRetcode = Rockey2Imedig.INSTANCE.writeCode( cBuffer );
		if (iRetcode < 0)
		{
			LOG.error("ERROR: " + Integer.toHexString(iRetcode));
			return false;
		}

		return true;
	}
}
