package es.pryades.imedig.cloud.backend;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.StorageConfiguration;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.ModalParent;

public class StorageSetupDlg extends Window implements ModalParent
{
	private static final Logger LOG = Logger.getLogger( StorageSetupDlg.class );
	
	private static final long serialVersionUID = 5722841685959892036L;
	
	private VerticalLayout mainLayout;

	/*
	 * 
-- Definir el mínimo espacio disponible en el almacenamiento principal ( <min_free> )

	twiddle.sh -u admin -p admin setattrs "dcm4chee.archive:service=FileSystemMgt,group=ONLINE_STORAGE" MinimumFreeDiskSpace <min_free>
		el texto de salida debe contener:
			MinimumFreeDiskSpace=<min_free>

-- Crear un NEARLINE_STORAGE ( <address> <directory> <move_not_accessed> <keep_studies_during_days> )

	mkdir /home/imedig/<directory>
	echo '<address>:/volume1/<directory> /home/imedig/<directory> nfs rw 0 0' >> /etc/fstab
	mount /home/imedig/<directory>
	
	twiddle.sh -u admin -p admin invoke "dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE" addRWFileSystem /home/imedig/<name>
	twiddle.sh -u admin -p admin invoke "dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE" listFileSystems
		el texto de salida debe contener:
			/home/imedig<name>

	twiddle.sh -u admin -p admin setattrs "dcm4chee.archive:service=FileMove" \
		SourceFileSystemGroupID ONLINE_STORAGE \
		DestinationFileSystemGroupID NEARLINE_STORAGE \
		MoveStudyIfNotAccessedFor <move_not_accessed> \
		MoveStudyOnlyIfNotAccessedFor 30d \
		ScheduleStudiesForMoveInterval 1h
		el texto de salida debe contener:
			SourceFileSystemGroupID=ONLINE_STORAGE
			SourceFileSystemGroupID=NEARLINE_STORAGE
			MoveStudyIfNotAccessedFor=<move_not_accessed>
			
	crear una tarea que busque los estudios anteriores a fecha actual - <keep_studies_during_days> y ejecute para cada estudio

		twiddle.sh -u admin -p admin invoke "dcm4chee.archive:service=ContentEditService" purgeStudy <study uid>
			el texto de salida debe contener:
				'null'
				
--- Mostrar el espacio disponible en el NEARLINE_STORAGE

	twiddle.sh -u admin -p admin get "dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE" UsableDiskSpaceOnCurrentStorageFileSystem
		el texto de salida debe contener:
			UsableDiskSpaceOnCurrentStorageFileSystem=<value in bytes>

	 */
	private TextField editMinimumFreeDiskSpace;
	private CheckBox checkEnableExternalStorage;

	private TextField editExternalStorageDirectory;
	private TextField editExternalStorageFilesystem;
	private TextField editMoveStudyIfNotAccessedFor;
	private TextField editKeepStudyMax;
	
	private HorizontalLayout row3;
	private HorizontalLayout row5;
	
	@Getter
	private ImedigContext context;
	
	private StorageConfiguration storageConfiguration;
	private BeanItem<StorageConfiguration> bi;

	private boolean wasNASActive;
	
	public StorageSetupDlg( ImedigContext context ) throws Exception
	{
		this.context = context;
		
		setCaption( context.getString( "StorageSetupDlg.title" ) );
		
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
		bi = new BeanItem<StorageConfiguration>( storageConfiguration );

		buildMainLayout();
	}

	public void buildMainLayout()
	{
		addStyleName( "mainwnd" );

		VerticalLayout layoutSettings = new VerticalLayout();
		layoutSettings.setWidth( "100%" );
		layoutSettings.setSpacing( true );

		editMinimumFreeDiskSpace = new TextField( getContext().getString( "StorageSetupDlg.labelMinimumFreeDiskSpace" ), bi.getItemProperty( "minimunFreeDiskSpace" ) );
		editMinimumFreeDiskSpace.setWidth( "100%" );
		editMinimumFreeDiskSpace.setDescription( getContext().getString( "StorageSetupDlg.labelMinimumFreeDiskSpace.description" ) );
		editMinimumFreeDiskSpace.setNullRepresentation( "" );

		editKeepStudyMax = new TextField( getContext().getString( "StorageSetupDlg.labelKeepStudyMax" ), bi.getItemProperty( "keepStudyMax" ) );
		editKeepStudyMax.setWidth( "100%" );
		editKeepStudyMax.setDescription( getContext().getString( "StorageSetupDlg.labelKeepStudyMax.description" ) );
		editKeepStudyMax.setNullRepresentation( "" );
		
		boolean visible = storageConfiguration.getEnableExternalStorage();
		
		checkEnableExternalStorage = new CheckBox( getContext().getString( "StorageSetupDlg.checkExternalStorage" ), bi.getItemProperty( "enableExternalStorage" ) );
		checkEnableExternalStorage.setWidth( "100%" );
		checkEnableExternalStorage.setValue( visible );
		checkEnableExternalStorage.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -973998514686068197L;

			public void valueChange( ValueChangeEvent event) 
			{
				boolean visible = (Boolean) event.getProperty().getValue();
				
				editExternalStorageDirectory.setVisible( visible );
				editExternalStorageFilesystem.setVisible( visible );
				editMoveStudyIfNotAccessedFor.setVisible( visible );
				
				row3.setVisible( visible );
				row5.setVisible( visible );
            }
        } );

		editExternalStorageDirectory = new TextField( getContext().getString( "StorageSetupDlg.labelExternalStorageDirectory" ), bi.getItemProperty( "externalStorageDirectory" ) );
		editExternalStorageDirectory.setWidth( "100%" );
		editExternalStorageDirectory.setDescription( getContext().getString( "StorageSetupDlg.labelExternalStorageDirectory.description" ) );
		editExternalStorageDirectory.setNullRepresentation( "" );
		//editExternalStorageDirectory.setValue( storageConfiguration.getExternalStorageDirectory() );
		editExternalStorageDirectory.setVisible( visible );
		editExternalStorageDirectory.setEnabled( !wasNASActive );

		editExternalStorageFilesystem = new TextField( getContext().getString( "StorageSetupDlg.labelExternalStorageFilesystem" ), bi.getItemProperty( "externalStorageFilesystem" ) );
		editExternalStorageFilesystem.setWidth( "100%" );
		editExternalStorageFilesystem.setDescription( getContext().getString( "StorageSetupDlg.labelExternalStorageFilesystem.description" ) );
		editExternalStorageFilesystem.setNullRepresentation( "" );
		//editExternalStorageFilesystem.setValue( storageConfiguration.getExternalStorageFilesystem() );
		editExternalStorageFilesystem.setVisible( visible );
		editExternalStorageFilesystem.setEnabled( !wasNASActive );

		editMoveStudyIfNotAccessedFor = new TextField( getContext().getString( "StorageSetupDlg.labelMoveStudyIfNotAccessedFor" ), bi.getItemProperty( "moveStudyIfNotAccessedFor" ) );
		editMoveStudyIfNotAccessedFor.setWidth( "100%" );
		editMoveStudyIfNotAccessedFor.setDescription( getContext().getString( "StorageSetupDlg.labelMoveStudyIfNotAccessedFor.description" ) );
		editMoveStudyIfNotAccessedFor.setNullRepresentation( "" );
		//editMoveStudyIfNotAccessedFor.setValue( storageConfiguration.getMoveStudyIfNotAccessedFor() );
		editMoveStudyIfNotAccessedFor.setVisible( visible );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editMinimumFreeDiskSpace );
		row1.addComponent( editKeepStudyMax );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( checkEnableExternalStorage );

		row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editExternalStorageDirectory );
		row3.addComponent( editExternalStorageFilesystem );
		row3.setVisible( visible );
		
		row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.addComponent( editMoveStudyIfNotAccessedFor );
		row5.addComponent( new HorizontalLayout() );
		row5.setVisible( visible );
		
		HorizontalLayout rowButtons = new HorizontalLayout();
		rowButtons.setSpacing( true );
		
		Button btn;

		btn = new Button( getContext().getString( "words.ok" ) );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				onOk();
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

		layoutSettings.addComponent( row1 );
		layoutSettings.addComponent( row2 );
		layoutSettings.addComponent( row3 );
		layoutSettings.addComponent( row5 );

		//mainLayout.setExpandRatio( workSpace, 1.0f );
		mainLayout.addComponent( layoutSettings );
		mainLayout.addComponent( rowButtons );
		mainLayout.setComponentAlignment( rowButtons, Alignment.BOTTOM_RIGHT );
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

		if ( parts.length == 2 )
			return parts[1];
		
		return ""; 
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

		wasNASActive = storageConfiguration.getEnableExternalStorage().booleanValue();
		
		LOG.info( "storageConfiguration " + storageConfiguration );
	}

	private void writeStorageConfiguration()
	{
		Utils.writeObject( Settings.HOME_dir + "/conf/storage.conf", storageConfiguration );
	}
	
	private boolean createNASStorage()
	{
		String mountName = Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() );

		Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE\" addRWFileSystem /home/imedig/" + mountName );
		
		String response = Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE\" listFileSystems" );

		boolean created = response.contains( "/home/imedig/" + mountName );
		
		if ( created )
		{
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "mkdir /home/imedig/" + mountName );
			
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "chmod o+w /etc/fstab" );
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "echo '" + storageConfiguration.getExternalStorageDirectory() + " " + "/home/imedig/" + mountName + " " + storageConfiguration.getExternalStorageFilesystem() + " rw 0 0' >> /etc/fstab" );
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "chmod o-w /etc/fstab" );
			
			Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "mount /home/imedig/" + mountName );
		}		
		
		return created; 
	}
	
	private void removeNasStorage()
	{
		String mountName = Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() );

		Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=FileSystemMgt,group=NEARLINE_STORAGE\" removeFileSystem /home/imedig/" + mountName );

		String replacement = mountName + " " + storageConfiguration.getExternalStorageFilesystem();
		
		Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "umount /home/imedig/" + mountName );
		Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "sed -n '/" + replacement + "/!p' /etc/fstab > /tmp/fstab &&" + (Settings.EXEC_sudo ? " sudo " : "") + " mv /tmp/fstab /etc/fstab" );
		Utils.cmdExec( (Settings.EXEC_sudo ? "sudo " : "") + "rmdir /home/imedig/" + mountName );
	}
	
	private void onOk()
	{
		boolean created = true;
		
		if ( !wasNASActive && storageConfiguration.getEnableExternalStorage().booleanValue() )
			created = createNASStorage();
		else if ( wasNASActive && !storageConfiguration.getEnableExternalStorage().booleanValue() )
			removeNasStorage();
		
		if ( !created )
			Notification.show( context.getString( "StorageSetupDlg.error.create" ), Notification.Type.ERROR_MESSAGE );
		else
		{
			Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin setattrs \"dcm4chee.archive:service=FileSystemMgt,group=ONLINE_STORAGE\" MinimumFreeDiskSpace " + storageConfiguration.getMinimunFreeDiskSpace() );

			if ( storageConfiguration.getEnableExternalStorage().booleanValue() )
				Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin setattrs \"dcm4chee.archive:service=FileMove\" SourceFileSystemGroupID ONLINE_STORAGE DestinationFileSystemGroupID NEARLINE_STORAGE MoveStudyIfNotAccessedFor " + storageConfiguration.getMoveStudyIfNotAccessedFor() );
			else
				Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin setattrs \"dcm4chee.archive:service=FileMove\" SourceFileSystemGroupID NONE DestinationFileSystemGroupID NONE" );
		}

		if ( created )
		{
			writeStorageConfiguration();
			
			((UI)getContext().getData( "Application" )).removeWindow( this );
		}
	}
	
	private void onCancel()
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}
}
