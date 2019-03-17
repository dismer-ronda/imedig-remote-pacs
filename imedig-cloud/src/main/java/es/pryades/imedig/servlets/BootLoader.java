package es.pryades.imedig.servlets;

import java.io.Serializable;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.wado.query.QueryManager;
import es.pryades.imedig.wado.resources.CacheManager;
import es.pryades.imedig.wado.retrieve.RetrieveManager;

public class BootLoader extends Thread implements Serializable
{
	private static final long serialVersionUID = 9174562826193056615L;

	private static final Logger LOG = Logger.getLogger( BootLoader.class );

    static BootLoader instance;
    
    Boolean ended;
    
	public static void bootup()
	{
		instance = new BootLoader();
		
		instance.start();
	}
 
	public BootLoader()
	{
    	ended = Boolean.FALSE;
	}
	
	public static void stopBoot()
	{
		if ( instance != null )
		{
			synchronized ( instance )
			{
				instance.ended = true;
			}
		}
		
		try 
		{
			instance.join();
		} 
		catch ( Throwable e ) 
		{
			if ( !(e instanceof ImedigException) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}
	
	public synchronized boolean isStopped()
	{
		return ended;
	}

	@Override
	public void run()
	{
		while ( true )
		{
			try 
			{
				Settings.Init();

	        	CacheManager.Init();
				QueryManager.Init();
				RetrieveManager.Init();
				
				LOG.info( "init successfull" );

				break;
			} 
			catch ( Throwable e ) 
			{
			}
			
			LOG.info( "imedig not initialized, waiting to retry ..." );
			
			Utils.Sleep( Utils.ONE_MINUTE );
		}
	}
}
