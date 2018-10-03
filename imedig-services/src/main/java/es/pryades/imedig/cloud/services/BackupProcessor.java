package es.pryades.imedig.cloud.services;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.StorageConfiguration;
import es.pryades.imedig.cloud.common.Utils;

/**
 * 
 * @author dismer.ronda
 * @version 
 * @since Jul, 2010
 */

public class BackupProcessor extends TimerTask implements Serializable
{
	private static final long serialVersionUID = -544381427911316970L;

	private static final Logger LOG = Logger.getLogger( BackupProcessor.class );
    
    private static BackupProcessor processor = null;

    private Timer timer; 
    
	public static void startProcessor() throws Exception
	{
		processor = new BackupProcessor();

		processor.init();
	}

	public static void stopProcessor()
	{
		synchronized ( processor )
		{
			processor.stopRequest();
		}		
	}
	
	private BackupProcessor()
	{
		super();
	}

	private void init() throws Exception
	{
		timer = new Timer();

    	long wait = Utils.getHowMuchToWaitForFirstSecondOfNextHour(); 
		long repeat = Utils.ONE_HOUR; 

		timer.schedule( this, wait, repeat );

		LOG.info( "----- started. Waiting " + wait/1000 + " seconds" );
	}
	
	private void stopRequest()
	{
		synchronized ( this )
		{
			timer.cancel();

			LOG.info( "----- finished" );
		}
	}
	
	private String getOnlineStorageMinimunFreeDiskSpace() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileSystemMgt,group=ONLINE_STORAGE\" MinimumFreeDiskSpace" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
			throw new Exception( "Cannot communicate with PACS" );
		
		return parts[1];
	}

	private String getMoveStudyIfNotAccessedFor() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileMove\" MoveStudyIfNotAccessedFor" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
			throw new Exception( "Cannot communicate with PACS" );

		if ( parts.length == 2 )
			return parts[1];
		
		return ""; 
	}

	
	private StorageConfiguration readStorageConfiguration() throws Exception
	{
		StorageConfiguration storageConfiguration = (StorageConfiguration)Utils.readObject( Settings.HOME_dir + "/conf/storage.conf" );
		
		if ( storageConfiguration == null )
		{
			storageConfiguration = new StorageConfiguration();
			
			storageConfiguration.setEnableExternalStorage( false );
			storageConfiguration.setExternalStorageDirectory( "192.168.254.254:/Public" ); 
			storageConfiguration.setExternalStorageFilesystem( "nfs" ); 
			storageConfiguration.setKeepStudyMax( 60 ); 
		}
		
		storageConfiguration.setMinimunFreeDiskSpace( getOnlineStorageMinimunFreeDiskSpace() );
		storageConfiguration.setMoveStudyIfNotAccessedFor( getMoveStudyIfNotAccessedFor() ); 

		LOG.info( "storageConfiguration " + storageConfiguration );
		
		return storageConfiguration;
	}

	@Override
	public void run()
	{
		try 
		{
			StorageConfiguration storageConfiguration = readStorageConfiguration();
			
			Calendar now = Utils.getCalendarNow();
			
			if ( storageConfiguration.getEnableExternalStorage().booleanValue() && now.get( Calendar.HOUR_OF_DAY ) == 23 )
			{
				int dayOfWeek = now.get( Calendar.DAY_OF_WEEK );
				
				LOG.info( "--------- back up application " + dayOfWeek );
			
				String mountName = Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() );
				
	            Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "su - postgres -c \"pg_dump imedig-cloud\" > /tmp/imedig-cloud.sql" );
	            Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "su - postgres -c \"pg_dump pacsdb\" > /tmp/pacsdb.sql" );
	            
	            Utils.cmdExec( "zip -Dj /home/imedig/" + mountName + "/imedig-backup-" + dayOfWeek + ".zip /tmp/imedig-cloud.sql /tmp/pacsdb.sql"  );
	            
	            Utils.DeleteFile( "/tmp/imedig-cloud.sql" );
	            Utils.DeleteFile( "/tmp/pacsdb.sql" );
			}
		} 
		catch ( Throwable e) 
		{
			e.printStackTrace();
		}
		
		LOG.info( "timer finished" );
	}
}
