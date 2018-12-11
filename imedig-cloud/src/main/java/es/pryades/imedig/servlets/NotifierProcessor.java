package es.pryades.imedig.servlets;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.WebServiceRequest;
import es.pryades.imedig.core.common.Settings;

public class NotifierProcessor extends Thread implements Serializable
{
	private static final long serialVersionUID = -1061258604726776654L;

	private static final Logger LOG = Logger.getLogger( NotifierProcessor.class );
    
	Integer waiting;
    volatile boolean ended;

	public static NotifierProcessor instance = null;
	
	public NotifierProcessor()
	{
    	waiting = new Integer(0);
    	ended = false;
	}

	public static void startInstance()
	{
		instance = new NotifierProcessor();
		
		instance.start();
	}
	
    public static void stopInstance() 
    {
    	LOG.info( "stop command received" );
    	
    	if ( instance != null )
    	{
    		instance.stopProcessor();

    		try 
			{
				instance.join();
			} 
			catch ( Throwable e ) 
			{
			}
    	}
    }

	public static NotifierProcessor getInstance()
	{
		return instance;
	}
	
	public void waitIddle( long time )
	{
		synchronized ( waiting )
		{
			try 
			{
				waiting.wait( time );
			} 
			catch ( InterruptedException e ) 
			{
			}
		}
	}
	
	public synchronized void stopProcessor()
	{
		ended = true;

		synchronized( waiting )
		{
			waiting.notifyAll();
		}
	}
	
	public synchronized boolean isStopped()
	{
		return ended;
	}	
	
	@Override
	public void run()
	{
		LOG.info( "processor started" );
		
		while ( true )
		{
    		long ts = new Date().getTime();
    		
    		String extra = "ts=" + ts + "&port=" + Settings.IMEDIG_Port;
    		String token = "token=" + Utils.getTokenString( "IMEDIG" + ts, Settings.TrustKey );
    		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ); 
    		
			//String url = Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-services/echo/" + "IMEDIG" + "?" + token + "&" + code;
    		String url = Utils.getProperty( "CLOUD_URL" ) + "/imedig-services/echo/" + "IMEDIG" + "?" + token + "&" + code;

			LOG.info( "CLOUD_URL " + url );
			
			WebServiceRequest req = new WebServiceRequest( url, 200, "GET", "", "", new HashMap<String, String>() );

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			try
			{
				req.Execute( null, os, null );
				
				LOG.debug( "response " + os.toString() );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}

			waitIddle( Utils.ONE_MINUTE );
			
			if ( isStopped() )
				break;
		}
		
		LOG.info( "processor finished" );
	}
}
