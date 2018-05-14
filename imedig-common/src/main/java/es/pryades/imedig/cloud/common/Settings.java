package es.pryades.imedig.cloud.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class Settings 
{
    private static final Logger LOG = Logger.getLogger( Settings.class );

	//public static String CLOUD_Url;
	
	public static final String TrustKey = "im3d1gcl0ud25887";

	public static String DB_engine;
	public static String DB_driver;
	public static String DB_url;
	public static String DB_user;
	public static String DB_password;

	public static String PACS_DB_driver;
	public static String PACS_DB_url;
	public static String PACS_DB_user;
	public static String PACS_DB_password;

	public static String HOME_dir;

	public static String MAIL_host;
	public static String MAIL_from;
	public static String MAIL_user;
	public static String MAIL_password;

	public static Integer PWD_min_size;
	public static Integer PWD_expiration;
	public static Integer PWD_fails_change;
	public static Integer PWD_fails_block;

	public static String LANGUAGES;

	public static Settings instance = null;
	
    Boolean lock = Boolean.FALSE;
    Properties settings;

    public static Boolean EXEC_sudo;
    public static Boolean LOCAL_server;
	public static String IMAGE_directory;
	public static String TRUST_KEY;

	private Settings()
	{
		super();
	}
	
	static public void Init() throws Exception
	{
		if ( instance == null )
			instance = new Settings();
		
		instance.loadSettingsFromFile( "imedig-cloud.properties" );
	}
	
	static public Settings getInstance() throws ImedigException
	{
		if ( instance == null )
			throw new ImedigException( new Exception( "Settings not initialized " ), LOG, ImedigException.SETTINGS_NOT_INITIALIZED );

		return instance;
	}
	
	private synchronized void loadSettingsFromFile( String fileName ) throws ImedigException  
	{
		settings = new Properties();

		URL url = Thread.currentThread().getContextClassLoader().getResource( fileName );
		
        if ( url == null )
        	throw new ImedigException( new Exception( "Settings file " + fileName + " was not found in the classpath" ), LOG, ImedigException.SETTINGS_NOT_FOUND );

        try 
        {
			settings.load( new FileInputStream( url.getPath() ) );
		} 
        catch ( FileNotFoundException e ) 
        {
        	throw new ImedigException( e, LOG, ImedigException.SETTINGS_NOT_FOUND );
		} 
        catch ( IOException e ) 
        {
        	throw new ImedigException( e, LOG, ImedigException.SETTINGS_READ_ERROR );
		}

        initSettings();
        
        HOME_dir =  new File( url.getPath().replace( "imedig-cloud.properties", "" ) ).getParent();
        
        LOG.info( "Settings read successfully from " + url.getPath() );
        LOG.info( "HOME_dir set to " + HOME_dir );
	}
	
	private void initSettings() throws ImedigException
	{
		//CLOUD_Url = getSetting( "CLOUD_Url", "http://www.imedig.com/imedig-cloud/desktop" );

		DB_engine = getSetting( "DB_engine", "postgresql" );
		DB_driver = getSetting( DB_engine + ".DB_driver", "org.postgresql.Driver" );
		DB_url = getSetting( DB_engine + ".DB_url", "jdbc:postgresql://localhost/imedig-cloud" );
		DB_user = getSetting( DB_engine + ".DB_user", "imedig" );
		DB_password = getSetting( DB_engine + ".DB_password", "secreto" );
		
		PACS_DB_driver = getSetting( "PACS_DB_driver", "org.postgresql.Driver" );
		PACS_DB_url = getSetting( "PACS_DB_url", "jdbc:postgresql://localhost/pacsdb" );
		PACS_DB_user = getSetting( "PACS_DB_user", "postgres" );
		PACS_DB_password = getSetting( "PACS_DB_password", "" );
		
		MAIL_host = getSetting( "MAIL_host", "" );
		MAIL_from = getSetting( "MAIL_from", "" );
		MAIL_user = getSetting( "MAIL_user", "" );
		MAIL_password = getSetting( "MAIL_password", "" );

		PWD_min_size = Utils.getInt( getSetting( "PWD_min_size", "8" ), 8 );
		PWD_expiration = Utils.getInt( getSetting( "PWD_expiration", "365" ), 365 );
		PWD_fails_change = Utils.getInt( getSetting( "PWD_fails_change", "5" ), 5 );
		PWD_fails_block = Utils.getInt( getSetting( "PWD_fails_block", "20" ), 20 );
		
		LANGUAGES = getSetting( "LANGUAGES", "es,en" );

		EXEC_sudo = "true".equals( getSetting( "EXEC_sudo", "false" ) );
		LOCAL_server = "true".equals( getSetting( "LOCAL_server", "false" ) );
		IMAGE_directory = getSetting( "IMAGE_directory", "/" );
		TRUST_KEY = getSetting( "trust_key", "s3cr3t0" );
	}

	public static String getSetting( String name, String defValue ) 
	{
		try
		{
			if ( getInstance().settings == null )
				return defValue;
			
			String value = getInstance().settings.getProperty( name, defValue );
				
			if ( value == null )
				return defValue;
		
			return value.trim();
		}
		catch ( Throwable e )
		{
			return name;
		}
	}
	
	public static String getEnviroment( String name, String defValue )
	{
		String value = System.getenv( name );
		
		if ( value == null || value.isEmpty() )
			return defValue;
		
		return value;
	}
}
