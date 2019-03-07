package es.pryades.imedig.cloud.services;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class RemoveStudiesProcessor extends TimerTask implements Serializable
{
	private static final long serialVersionUID = -2941701131908630979L;

	private static final Logger LOG = Logger.getLogger( RemoveStudiesProcessor.class );
    
    private static RemoveStudiesProcessor processor = null;

    private Timer timer; 
    
	public static void startProcessor() throws Exception
	{
		processor = new RemoveStudiesProcessor();

		processor.init();
	}

	public static void stopProcessor()
	{
		synchronized ( processor )
		{
			processor.stopRequest();
		}		
	}
	
	private RemoveStudiesProcessor()
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
		LOG.info( "timer started" );

		try 
		{
			StorageConfiguration storageConfiguration = readStorageConfiguration();
			
			Class.forName( "org.postgresql.Driver" );
			Connection conn = DriverManager.getConnection( "jdbc:postgresql://localhost/pacsdb", "postgres", "" ); 

			Statement statement = conn.createStatement();
    		
			Calendar tooOld = Utils.getCalendarBefore( Utils.getCalendarNow(), Calendar.MONTH, storageConfiguration.getKeepStudyMax() );
			String deletionDate = Utils.getCalendarAsString( tooOld, "yyyy/MM/dd HH:mm:ss" );
			
			LOG.info( "--------- deleting studies previous to " + deletionDate );
			
			ResultSet set = statement.executeQuery( "select study_datetime, study_iuid from study where study_datetime < '" + deletionDate + "'" );
			
			while ( set.next() ) 
			{
	            String studyUID = set.getString( "study_iuid" );
	            String studyDate = set.getString( "study_datetime" );
	            
	            LOG.info( "deleting study " + studyUID + " performed at " + studyDate );
	            
	            Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=ContentEditService\" moveStudyToTrash " + studyUID );
	        }
			
			LOG.info( "--------- deleting patients without studies and appointments" );

			set = statement.executeQuery( "select patient.pk, patient.pat_id, patient.pat_name from patient where not exists (select 1 from study where study.patient_fk = patient.pk) and not exists (select 1 from mwl_item where mwl_item.patient_fk = patient.pk)" );
			
			while ( set.next() ) 
			{
	            String pk = set.getString( "pk" );
	            String patientName = set.getString( "pat_name" );
	            String patientId = set.getString( "pat_id" );
	            
	            LOG.info( "deleting patient " + patientName + " with id " + patientId );
	            
	            Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=ContentEditService\" movePatientToTrash " + pk );
	        }

            Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=ContentEditService\" emptyTrash" );

            conn.close();
		} 
		catch ( Throwable e) 
		{
			e.printStackTrace();
		}
		
		LOG.info( "timer finished" );
	}
}
