package es.pryades.imedig.cloud.services;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ibatis.ClientDalManager;
import es.pryades.imedig.pacs.dal.ibatis.PacsDalManager;

public class BootLoader extends Thread
{
    private static final Logger LOG = Logger.getLogger( BootLoader.class );

    static BootLoader instance;
    
	public static void bootup()
	{
		instance = new BootLoader();
		
		instance.start();
	}
 
	public BootLoader()
	{
	}
	
	@Override
	public void run()
	{
		while ( true )
		{
			try
			{
	    		ClientDalManager.Init( Settings.DB_engine, Settings.DB_driver, Settings.DB_url, Settings.DB_user, Settings.DB_password );
				PacsDalManager.Init( Settings.PACS_DB_driver, Settings.PACS_DB_url, Settings.PACS_DB_user, Settings.PACS_DB_password );
	    	
	    		RemoveStudiesProcessor.startProcessor();
	    		BackupProcessor.startProcessor();
	    		
		    	break;
			}
			catch ( Throwable e )
			{
			}
			
			LOG.info( "not ready, waiting to retry ..." );
			Utils.Sleep( 10 * Utils.ONE_SECOND );
		}

		LOG.info( "completed" );
	}
}
