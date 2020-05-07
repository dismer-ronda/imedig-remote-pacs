package es.pryades.imedig.cloud.backend;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.LicenseManager;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

public class SystemSettingsDlg extends Window implements ModalParent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( SystemSettingsDlg.class );
	
	private static final long serialVersionUID = 5722841685959892036L;
	
	private VerticalLayout mainLayout;
	private HorizontalLayout globalBar;
	private HorizontalLayout toolBarTop;
	private VerticalLayout workSpace;

	private TextArea settings;
	private TextField license;
	
	@Getter
	private ImedigContext context;
	
	/*
	 *  Crear un filesystem para almacenamiento externo de las imágenes
	 *  
	 *  En configuracion del nuevo filesystem se debe obtener:
	 *  
	 *  	<address>: 	direccion de red donde se tiene el recurso compartido
	 *  	<share>: 	nombre del recurso compartido
	 *  	<user>: 	nombre de usuario con acceso RW al recurso compartido
	 *  	<password>: contraseña de usuario con acceso RW al recurso compartido
	 *  
	 *  Con esta información debería hacerse lo siguiente:
	 *  
	 *  	mkdir /home/imedig/<share>
	 *  
	 *      newId = select pk from filesystem where rext_fk is null;
	 *      
	 *      insert into filesystem(pk, dirpath, fs_group_id, retrieve_aet, availability, fs_status ) 
	 *      values (<newId>, '/home/imedig/pryades', 'ONLINE_STORAGE', 'IMEDIG', 0, 1 );
	 *  
	 *      update filesystem set next_fk=<newId> where next_fk is null;
	 *  
	 *  Al iniciar IMEDIG Cloud habría que agregar al montar todos los filesystems con algo así:
	 *   
	 *  	mount -t cifs //<address>/<share> /home/imedig/<share> -o user=<user>,pass=<password>,rw,uid=root,gid=root
	 *  
	 *  Al terminar IMEDIG Cloud habría que desmontar todos los filesystems:
	 *  
	 *  	umount /home/imedig/<share>
	 *   
	 *  El campo fs_status debe ponerse a 1 cuando se crea y se pondrá a cero automaticamente cuando dcm4chee cambie a ese filesystem.
	 *   
	 *   
	 */
	
	public SystemSettingsDlg( ImedigContext context )
	{
		this.context = context;
		
		setCaption( context.getString( "SystemSettingsDlg.title" ) );
		
		setResizable( false );
		setModal( true );
		setClosable( true );
		addCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "1024px" );
		setHeight( "600px" );
		center();

		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		setContent( mainLayout );
		
		buildMainLayout();
		settingClose();
	}

	public void buildMainLayout()
	{
		addStyleName( "mainwnd" );

		mainLayout.addComponent( buildTop() );
		mainLayout.addComponent( buildWorkspace() );

		VerticalLayout layoutSettings = new VerticalLayout();
		layoutSettings.setSizeFull();
		layoutSettings.setSpacing( true );

		settings = new TextArea();
        settings.setValue( Utils.readFile( "/opt/network/interfaces" ) );
        settings.setSizeFull();

        license = new TextField();
        license.setValue( LicenseManager.getInstance().getLicense().getLicenseCode() );
        license.setSizeFull();

        HorizontalLayout rowButtons = new HorizontalLayout();
        rowButtons.setSpacing( true );
        
		Button bttn = new Button( getContext().getString( "SystemSettingsDlg.loadSettings" ) );
		bttn.addClickListener(new Button.ClickListener()
		{
			private static final long serialVersionUID = 9181772180916945342L;

			public void buttonClick( ClickEvent event )
			{
				try 
				{
					loadSettings();
				} 
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		} );
		rowButtons.addComponent( bttn );
		
		bttn = new Button( getContext().getString( "SystemSettingsDlg.saveSettings" ) );
		bttn.addClickListener(new Button.ClickListener()
		{
			private static final long serialVersionUID = 9181772180916945342L;

			public void buttonClick( ClickEvent event )
			{
				try 
				{
					saveSettings();
				} 
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		} );
		rowButtons.addComponent( bttn );

        HorizontalLayout rowLicense = new HorizontalLayout();
        rowLicense.setSpacing( true );
        rowLicense.setWidth( "100%" );
        
        bttn = new Button( getContext().getString( "SystemSettingsDlg.setLicense" ) );
		bttn.addClickListener(new Button.ClickListener()
		{
			private static final long serialVersionUID = 507605970879641156L;

			public void buttonClick( ClickEvent event )
			{
				try 
				{
					if ( LicenseManager.getInstance().getLicense().setLicenseCode( license.getValue() ) )
					{
						if ( !LicenseManager.getInstance().getLicense().writeLicense() )
							Notification.show( getContext().getString( "SystemSettingsDlg.licenseError" ), Notification.Type.ERROR_MESSAGE );
					}
					else
						Notification.show( getContext().getString( "SystemSettingsDlg.licenseError" ), Notification.Type.ERROR_MESSAGE );
				} 
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		} );
        rowLicense.addComponent( license );
		rowLicense.addComponent( bttn );
        rowLicense.setExpandRatio( license, 1.0f );
        
		layoutSettings.addComponent( new Label( getContext().getString( "SystemSettingsDlg.config.label" ) ) );
		layoutSettings.addComponent( rowButtons );
		layoutSettings.addComponent( settings );
		layoutSettings.addComponent( rowLicense );
		layoutSettings.setExpandRatio( settings, 1.0f );

		workSpace.addComponent( layoutSettings );
		workSpace.setExpandRatio( layoutSettings, 1.0f );
		
		mainLayout.setExpandRatio( workSpace, 1.0f );
	}

	private Component buildWorkspace()
	{
		workSpace = new VerticalLayout();
		//workSpace.setMargin( true );
		workSpace.setSizeFull();

		return workSpace;
	}

	private void buildGlobalBar()
	{
		globalBar = new HorizontalLayout();
		globalBar.setSpacing( true );
		globalBar.setSizeUndefined();

		showGlobalButtons();

		toolBarTop.addComponent( globalBar );
		toolBarTop.setComponentAlignment( globalBar, Alignment.MIDDLE_CENTER );
	}

	private Component buildTop()
	{
		toolBarTop = new HorizontalLayout();

		toolBarTop.setWidth( "100%" );
		toolBarTop.setHeight( "-1px" );
		toolBarTop.setStyleName( "toolbar" );

		buildGlobalBar();
		
		toolBarTop.setExpandRatio( globalBar, 1.0f );

		return toolBarTop;
	}

	private void showGlobalButtons()
	{
		globalBar.removeAllComponents();

		Button btn;

		btn = new Button( getContext().getString( "SystemSettingsDlg.storage" ), FontAwesome.DATABASE );
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 507605970879641156L;

			public void buttonClick( ClickEvent event )
			{
				setupStorage();
			}
		} );
		globalBar.addComponent( btn );
		
		btn = new Button( context.getString( "words.status" ), FontAwesome.TACHOMETER );
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		btn.setDescription( context.getString( "StorageStatusDlg.title" ) );

		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8633533166630222974L;

			public void buttonClick( ClickEvent event )
			{
				statusStorage();
			}
		} );
		globalBar.addComponent( btn );
		

		btn = new Button( getContext().getString( "SystemSettingsDlg.update" ), FontAwesome.UPLOAD );
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				updateApplication();
			}
		} );
		globalBar.addComponent( btn );

		btn = new Button( getContext().getString( "SystemSettingsDlg.restore" ) , FontAwesome.HISTORY);
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				restoreApplication();
			}
		} );
		globalBar.addComponent( btn );

		btn = new Button( getContext().getString( "SystemSettingsDlg.reboot" ), FontAwesome.POWER_OFF );
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -2811321741260832007L;

			public void buttonClick( ClickEvent event )
			{
				rebootServer();
			}
		} );
		globalBar.addComponent( btn );
	}

	private void saveSettings() throws Throwable
	{
		Utils.writeFile( "/opt/network/interfaces", settings.getValue() );
		
		Notification.show( getContext().getString( "SystemSettingsDlg.savedSettings" ), Notification.Type.TRAY_NOTIFICATION );
	}
	
	private void loadSettings() throws Throwable
	{
		settings.setValue( Utils.readFile( "/opt/network/interfaces" ) );

		Notification.show( getContext().getString( "SystemSettingsDlg.loadedSettings" ), Notification.Type.TRAY_NOTIFICATION );
	}

	private void updateApplication()
	{
		new ModalUpdateApplication( getContext(), this ).showModalWindow();
	}

	private void rebootServer()
	{
		ConfirmDialog.show( UI.getCurrent(), 
				context.getString( "words.confirm" ), 
				getContext().getString( "SystemSettingsDlg.reboot.confirm" ), 
				context.getString( "Generic.Yes" ), 
				context.getString( "Generic.No" ), 
				new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = -7633483104056821026L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
		                	Utils.cmdExec( "reboot" );
		            }
		        });
	}

	private void setupStorage()
	{
		try
		{
			StorageSetupDlg dlg = new StorageSetupDlg( getContext() );
			getUI().addWindow( dlg );
			dlg.focus();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}

	private void statusStorage()
	{
		try
		{
			StorageStatusDlg dlg = new StorageStatusDlg( getContext() );
			getUI().addWindow( dlg );
			dlg.focus();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}

	private void restoreApplication()
	{
		try
		{
			RestoreBackupDlg dlg = new RestoreBackupDlg( getContext() );
			getUI().addWindow( dlg );
			dlg.focus();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void refreshVisibleContent()
	{
	}
	
	private void settingClose(){
		Button bttnClose = new Button( getContext().getString( "words.close" ) );

		HorizontalLayout closeContainer = new HorizontalLayout(bttnClose);
		closeContainer.setComponentAlignment( bttnClose, Alignment.BOTTOM_RIGHT );
		mainLayout.addComponent( closeContainer );
		mainLayout.addComponent( closeContainer );
		mainLayout.setComponentAlignment( closeContainer, Alignment.BOTTOM_RIGHT );
		bttnClose.addClickListener( new ClickListener(){
			private static final long serialVersionUID = 4214003674325511602L;

			@Override
			public void buttonClick( ClickEvent event ){
				SystemSettingsDlg.this.close();
				
			}
		} );
	}
}
