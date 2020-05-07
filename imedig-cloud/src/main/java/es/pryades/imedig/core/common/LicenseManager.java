package es.pryades.imedig.core.common;

import java.io.Serializable;

import org.apache.log4j.Logger;

import lombok.Data;

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

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( LicenseManager.class );
	
	private License license;
		
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
		initLicense();
	}
	
	public void initLicense()
	{
		Rockey2License temp = new Rockey2License();
		
		if ( temp.isPresent() )
			license = temp;
		else
			license = new EthernetLicense();
		
		license.readLicense();
	}

    public static void main( String[] args )
	{
		try
		{
			LicenseManager.Init();
			
			/*if ( LicenseManager.getInstance().getLicense().importLicenseCode( "9D8325657FCCAE17111940D684EC5B2EA21BCAFEA77F9D86F9A58B66BCD3218F543373822C65F718EB9852D40258A0008C949EE82DA51047" ) )
			{
				System.out.println( "Expiration = " + LicenseManager.getInstance().getLicense().getExpiration() );
				System.out.println( "Rights = " + Long.toHexString( LicenseManager.getInstance().getLicense().getRights() ) );
				System.out.println( "Id = " + Long.toHexString( LicenseManager.getInstance().getLicense().getId() ) );
				System.out.println( LicenseManager.getInstance().getLicense().getLicenseCode() );
				System.out.println();

				LicenseManager.getInstance().getLicense().setBasicRights();
				LicenseManager.getInstance().getLicense().setWorklistRights();
				LicenseManager.getInstance().getLicense().setCitationsRights();
				LicenseManager.getInstance().getLicense().addExpiration( 5 * 365 );
				
				System.out.println( "Expiration = " + LicenseManager.getInstance().getLicense().getExpiration() );
				System.out.println( "Rights = " + Long.toHexString( LicenseManager.getInstance().getLicense().getRights() ) );
				System.out.println( "Id = " + Long.toHexString( LicenseManager.getInstance().getLicense().getId() ) );
				System.out.println( LicenseManager.getInstance().getLicense().getLicenseCode() );
				System.out.println();
			}*/
			
			/*System.load( "/usr/local/lib/libJRockey2.so");
			
			LicenseManager.Init();
			
			Rockey2License lic = new Rockey2License();
			
			lic.createUID();*/
			
			//System.out.println( LicenseManager.getInstance().getLicense().getLicenseCode() );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}
}
