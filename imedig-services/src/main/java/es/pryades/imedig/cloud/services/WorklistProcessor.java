package es.pryades.imedig.cloud.services;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CitasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * 
 * @author dismer.ronda
 * @version 
 * @since Jul, 2010
 */

public class WorklistProcessor extends TimerTask implements Serializable
{
	private static final long serialVersionUID = -3835554590129825511L;

	private static final Logger LOG = Logger.getLogger( WorklistProcessor.class );
    
    private static WorklistProcessor processor = null;

    private Timer timer; 
    
	public static void startProcessor() throws Exception
	{
		processor = new WorklistProcessor();

		processor.init();
	}

	public static void stopProcessor()
	{
		synchronized ( processor )
		{
			processor.stopRequest();
		}		
	}
	
	private WorklistProcessor()
	{
		super();
	}

	private void init() throws Exception
	{
		timer = new Timer();

    	long wait = Utils.getHowMuchToWaitForFirstSecondOfNextHour(); 
		long repeat = Utils.ONE_HOUR; 

		timer.schedule( this, wait, repeat );

		LOG.info( "----- started. Waiting " + wait/60000 + " minutes" );
	}
	
	private void stopRequest()
	{
		synchronized ( this )
		{
			timer.cancel();

			LOG.info( "----- finished" );
		}
	}
	
	@Override
	public void run()
	{
		LOG.info( "timer started" );
		
		try 
		{
			ImedigContext ctx = new ImedigContext();

			CitasManager citasManager = (CitasManager)IOCManager.getInstanceOf( CitasManager.class );
			
			citasManager.transferNextHourWorklist( ctx );
		} 
		catch ( Throwable e) 
		{
			e.printStackTrace();
		}
		
		LOG.info( "timer finished" );
	}
}
