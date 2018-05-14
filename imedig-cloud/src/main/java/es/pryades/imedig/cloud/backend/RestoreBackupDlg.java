package es.pryades.imedig.cloud.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.StorageConfiguration;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.BackupFile;
import es.pryades.imedig.core.common.ModalParent;

public class RestoreBackupDlg extends Window implements ModalParent
{
	private static final long serialVersionUID = -3015958813689928788L;

	private static final Logger LOG = Logger.getLogger( RestoreBackupDlg.class );
	
	private VerticalLayout mainLayout;
	private ListSelect listBackups;

	@Getter
	private ImedigContext context;
	
	private StorageConfiguration storageConfiguration;
	private List<BackupFile> backupFiles;
	
	public RestoreBackupDlg( ImedigContext context ) throws Exception
	{
		this.context = context;
		
		setCaption( context.getString( "RestoreBackupDlg.title" ) );
		
		setResizable( false );
		setModal( true );
		setClosable( true );
		addCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "800px" );
		setHeight( "-1px" );
		
		center();

		mainLayout = new VerticalLayout();
		mainLayout.setWidth( "100%" );
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		setContent( mainLayout );
		
		readStorageConfiguration();
		readBackupFiles();
		
		buildMainLayout();
	}

	public void buildMainLayout()
	{
		addStyleName( "mainwnd" );

		VerticalLayout layoutSettings = new VerticalLayout();
		layoutSettings.setWidth( "100%" );
		layoutSettings.setSpacing( true );

		/*chartDiskUsage = (Chart)getUsageChart( context.getString( "StorageStatusDlg.main" ), sizeMain, usedMain );
		chartNasUsage = (Chart)getUsageChart( context.getString( "StorageStatusDlg.nas" ), sizeNAS, usedNAS );
		chartNasUsage.setVisible( storageConfiguration.getEnableExternalStorage() );*/
		
		listBackups = new ListSelect();
		listBackups.setWidth( "100%" );
		listBackups.setImmediate( true );
		listBackups.setNullSelectionAllowed( false );
		/*listCylindersDevices.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6999023069968232887L;

			@Override
	        public void valueChange( ValueChangeEvent event ) 
	        {
	            Object value = listCylindersDevices.getValue();
	        }
	    });*/
		fillBackupFiles();

		HorizontalLayout rowButtons = new HorizontalLayout();
		rowButtons.setSpacing( true );
		
		Button btn;

		btn = new Button( getContext().getString( "words.restore" ) );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				onRestore();
			}
		} );
		rowButtons.addComponent( btn );

		btn = new Button( getContext().getString( "words.cancel" ) );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				onCancel();
			}
		} );
		rowButtons.addComponent( btn );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( listBackups );

		layoutSettings.addComponent( row1 );
		layoutSettings.addComponent( rowButtons );
		layoutSettings.setComponentAlignment( rowButtons, Alignment.MIDDLE_RIGHT );

		mainLayout.addComponent( layoutSettings );
	}

	private String getOnlineStorageMinimunFreeDiskSpace() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileSystemMgt,group=ONLINE_STORAGE\" MinimumFreeDiskSpace" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
		{
			Notification.show( context.getString( "StorageSetupDlg.error.connection" ), Notification.Type.ERROR_MESSAGE );
		
			throw new Exception( "Cannot communicate with PACS" );
		}

		return parts[1];
	}

	private String getMoveStudyIfNotAccessedFor() throws Exception
	{
		String result = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin get \"dcm4chee.archive:service=FileMove\" MoveStudyIfNotAccessedFor" ); 
		
		String parts[] = result.split( "=" );
		
		if ( parts.length != 2 )
		{
			Notification.show( context.getString( "StorageSetupDlg.error.connection" ), Notification.Type.ERROR_MESSAGE );
		
			throw new Exception( "Cannot communicate with PACS" );
		}

		return parts[1];
	}

	@Override
	public void refreshVisibleContent()
	{
	}
	
	private void readStorageConfiguration() throws Exception
	{
		storageConfiguration = (StorageConfiguration)Utils.readObject( Settings.HOME_dir + "/conf/storage.conf" );
		
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
	}
	
	private void readBackupFiles()
	{
		String mountName = Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() );

		String result = Utils.cmdExec( "stat --printf=%n,%s,%Y: /home/imedig/" + mountName + "/imedig-backup-*.zip" );

		String files[] = result.split( ":" );
		
		backupFiles = new ArrayList<BackupFile>();
		
		for ( String file : files )
		{
			String parts[] = file.split( "," );
			if ( parts.length == 3 )
				backupFiles.add( new BackupFile( parts[0], Long.parseLong( parts[1] ), Long.parseLong( parts[2] ) ) );
		}
		
		Comparator<BackupFile> comparator = new Comparator<BackupFile>()
		{
			@Override
			public int compare( BackupFile o1, BackupFile o2 )
			{
		 		return (int) (o2.getModified() - o1.getModified());
			}
		};
		            
		Collections.sort( backupFiles, comparator );
	}

	private void fillBackupFiles()
	{
		listBackups.removeAllItems();
		
		try
		{
			for ( BackupFile backupFile : backupFiles )
			{
				listBackups.addItem( backupFile );

				Calendar now = Utils.getCalendarNow();
				LOG.info( " now = " + now.getTimeInMillis()/1000 + " then = " + backupFile.getModified() );
				
				String line = getContext().getString( "RestoreBackupDlg.format" ).
						replace( "%date%", Utils.getDuration( now.getTimeInMillis()/1000 - backupFile.getModified() ) ).
						replace( "%size%", "" + Utils.roundDouble( (double)backupFile.getSize() / (1024*1024), 1 ) );
				
				listBackups.setItemCaption( backupFile, line );
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}
	
	private void onRestore()
	{
		try 
		{
			BackupFile backupFile = (BackupFile)listBackups.getValue();
			
			String restoreCommands = "#!/bin/bash\n\n";
			
			restoreCommands += "/etc/init.d/dcm4chee stop\n";
			restoreCommands += "/etc/init.d/tomcat7 stop\n";
			restoreCommands += "unzip -o " + backupFile.getFileName() + " -d /tmp\n";

			restoreCommands += "sleep 30\n";

			restoreCommands += "su - postgres -c \"dropdb imedig-cloud\"\n";
			restoreCommands += "su - postgres -c \"createdb --owner imedig imedig-cloud\"\n";
			restoreCommands += "su - postgres -c \"psql imedig-cloud -f /tmp/imedig-cloud.sql\"\n";
			
			restoreCommands += "su - postgres -c \"dropdb pacsdb\"\n";
			restoreCommands += "su - postgres -c \"createdb pacsdb\"\n";
			restoreCommands += "su - postgres -c \"psql pacsdb -f /tmp/pacsdb.sql\"\n";
            
			restoreCommands += "rm /tmp/imedig-cloud.sql\n";
			restoreCommands += "rm /tmp/pacsdb.sql\n";
            
			restoreCommands += "/etc/init.d/dcm4chee start\n";
			restoreCommands += "/etc/init.d/tomcat7 start\n";
			
			Utils.writeFile( "/tmp/imedig-restore.sh", restoreCommands );
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "chmod a+x /tmp/imedig-restore.sh" );

			Utils.cmdExecNoWait( "/tmp/imedig-restore.sh" );
			
			Notification.show( getContext().getString( "RestoreBackupDlg.wait.restore" ), Notification.Type.TRAY_NOTIFICATION);
			
		} 
		catch ( Throwable e) 
		{
			e.printStackTrace();
		}

		((UI)getContext().getData( "Application" )).removeWindow( this );
	}
	
	private void onCancel()
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}
}
