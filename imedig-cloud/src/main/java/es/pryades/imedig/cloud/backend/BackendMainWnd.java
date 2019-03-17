package es.pryades.imedig.cloud.backend;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.fullscreen.FullScreenExtension;
import es.pryades.fullscreen.listeners.FullScreenChangeListener;
import es.pryades.imedig.cloud.common.FontIcoMoon;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.MessageDlg;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.Action;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.AccesosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dal.ParametrosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Acceso;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Perfil;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Administration.AdministrationDlg;
import es.pryades.imedig.cloud.modules.Configuration.ConfigurationDlg;
import es.pryades.imedig.cloud.modules.Reports.ReportsViewer;
import es.pryades.imedig.cloud.modules.Reports.ShowExternalUrlDlg;
import es.pryades.imedig.core.common.LicenseManager;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.actions.ExitFullScreen;
import es.pryades.imedig.viewer.actions.FullScreen;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.ShowReportsListAction;
import es.pryades.imedig.viewer.components.ViewerWnd;
import es.pryades.imedig.viewer.components.appointments.AppointmentsViewer;
import es.pryades.imedig.viewer.components.patients.PatientsViewer;
import es.pryades.imedig.viewer.components.query.QueryViewer;
import lombok.Getter;

public class BackendMainWnd extends VerticalLayout implements ModalParent,ListenerAction
{
	private static final Logger LOG = Logger.getLogger( BackendMainWnd.class );

	private static final long serialVersionUID = 5722841685959892036L;

	private static final String LOGO_WIDTH = "120px";

	private static final String AUTH_CONFIGURACION = "configuracion";
	private static final String AUTH_ADMINISTRACION = "administracion";
	
	private LoginPanel loginPanel;

	private VerticalLayout mainLayout;

	private HorizontalLayout topBar;
	private HorizontalLayout logoBar;
	private HorizontalLayout selectButtonsBar;
	private HorizontalLayout buttonsBar;

	private CssLayout contents;
	
	private Button btnSelected;
	private Button buttonPatients;
	private Button buttonAppointments;
	private Button buttonImages;
	private Button buttonStudies;
	private Button buttonReports;
	private Button buttonManual;

	private DetallesCentrosManager centrosManager;
	private AccesosManager accesosManager;
	private UsuariosManager usuariosManager;
	//private InformesManager informesManager;

	@Getter
	private ImedigContext context;

	private PatientsViewer patientViewer;
	private AppointmentsViewer appointmentsViewer;
	private ViewerWnd imageViewer;
	private ReportsViewer reportsViewer;
	private QueryViewer queryViewer;
	private DetalleCentro detalleCentro;
	@Getter 
	private InformeImagen imagen;
	
	private boolean fullScreen = false;
	
	private boolean hasImage = false;

	public BackendMainWnd( ImedigContext ctx )
	{
		this.context = ctx;
		
		centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );
		accesosManager = (AccesosManager)IOCManager.getInstanceOf( AccesosManager.class );
		usuariosManager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );
		
		try
		{
			detalleCentro = (DetalleCentro)centrosManager.getRow( getContext(), 1 );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}

	public void showLogin()
	{
		setMargin( false );
		setSpacing( false );
		setSizeFull();
		setStyleName( "mainlogin" );

		HorizontalLayout banner = new HorizontalLayout();
		banner.setStyleName( "banner" );
		banner.setWidth( "100%" );

		addComponent( banner );
		setComponentAlignment( banner, Alignment.TOP_CENTER );

		VerticalLayout center = new VerticalLayout();
		center.setSizeFull();

		loginPanel = new LoginPanel( getContext(), this );
		center.addComponent( loginPanel );
		center.setComponentAlignment( loginPanel, Alignment.MIDDLE_CENTER );

		addComponent( center );
		setExpandRatio( center, 1.0f );
	}
	
	@Override
	public void attach(){
		super.attach();
		context.addListener( this );
	}
	
	@Override
	public void detach(){
		super.detach();
		context.removeListener( this );
	}

	private void registerAccess()
	{
		if ( getContext().getUsuario().isLocal() )
		{
			try
			{
				Acceso acceso1 = new Acceso();
	
				acceso1.setCuando( Utils.getTodayAsLong( "Europe/Madrid" ) );
				acceso1.setUsuario( getContext().getUsuario().getId() );
	
				accesosManager.setRow( getContext(), null, acceso1 );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
	}
	
	public void Logout() {
		getUI().getSession().close();
		getUI().getPage().setLocation( getContext().getData( "Url" ).toString() );
	}

	public void messageAndExit( String title, ResourceBundle resources, String message )
	{
		MessageDlg dlg = new MessageDlg( title, resources, message );

		dlg.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = -5303587015039065226L;

			@Override
			public void windowClose( CloseEvent e )
			{
				Logout();
			}
		} );

		getUI().addWindow( dlg );
	}


	@SuppressWarnings("unchecked")
	public void showMainWindow()
	{
		removeAllComponents();
		
		buildMainLayout();

		addComponent( mainLayout );

		try
		{
			((ParametrosManager)IOCManager.getInstanceOf( ParametrosManager.class )).loadParametros( getContext() );

			CentroQuery query = new CentroQuery();
			query.setUsuario( getContext().getUsuario().getId() );

			getContext().setCentros( centrosManager.getRows( getContext(), query ) );

			registerAccess();

			startMainWindow();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			messageAndExit( context.getString( "words.error" ), context.getResources(), context.getString( "error.unknown" ) );
		}
	}

	private VerticalLayout buildMainLayout()
	{
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate( false );
		mainLayout.setSizeFull();
		mainLayout.setMargin( false );
		mainLayout.setStyleName( "mainapp" );

		setSizeFull();

		mainLayout.addComponent( buildTop() );

		contents = new CssLayout();
		contents.setSizeFull();
		CssLayout virtualContent = new CssLayout();
		virtualContent.setSizeFull();
		buildFullScreenButtons(virtualContent);
		virtualContent.addComponent( contents );

		mainLayout.addComponent( virtualContent );
		mainLayout.setExpandRatio( virtualContent, 1.0f );

		
		return mainLayout;
	}
	
	protected Button btnFullScreen;
	protected Button btnPatients;
	protected Button btnAppointments;
	protected Button btnStudies;
	protected Button btnImages;
	protected Button btnReports;
	protected Button floatSelected;
	private static final String ID_FULLSCREEN = "btn.fullscreen";
	
	private void buildFullScreenButtons(ComponentContainer layout){
		btnFullScreen = buildBtnFloat( ID_FULLSCREEN, context.getString( "words.fullscreen" ), false );
		btnFullScreen.setIcon( FontIcoMoon.WINDOW_MAXIMIZE  );
		
		FullScreenExtension extension = new FullScreenExtension();
        extension.trigger(btnFullScreen);
        extension.setFullScreenChangeListener(new FullScreenChangeListener() {

            @Override
            public void onChange(boolean fullscreen) {
            	BackendMainWnd.this.fullScreen = fullscreen;
                if (fullscreen) {
                	context.sendAction( new FullScreen( this ) );
					btnFullScreen.setIcon( FontIcoMoon.WINDOW_RESTORE );
					btnFullScreen.setDescription( context.getString( "words.restore.fullscreen" ) );
					btnStudies.setVisible( true );
					if (hasImage) btnImages.setVisible( true );
					btnReports.setVisible( true );
					btnPatients.setVisible( true );
					btnAppointments.setVisible( true );
                } else {
                	context.sendAction( new ExitFullScreen( this ) );
					btnFullScreen.setIcon( FontIcoMoon.WINDOW_MAXIMIZE );
					btnFullScreen.setDescription( context.getString( "words.fullscreen" ) );
					btnStudies.setVisible( false );
					btnImages.setVisible( false );
					btnReports.setVisible( false );
					btnPatients.setVisible( false );
					btnAppointments.setVisible( false );
                }
            }
        });

        btnStudies = buildBtnFloat( "btn.studies.float", context.getString( "words.studies" ), true );
        btnStudies.setIcon( FontAwesome.SEARCH );
        btnStudies.setVisible( false );
        btnStudies.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 7515427205675270359L;

			@Override
			public void buttonClick( ClickEvent event ){
				buttonStudies.click();
			}
		} );
        btnImages = buildBtnFloat( "btn.images.float", context.getString( "words.images" ), true );
        btnImages.setIcon( FontIcoMoon.ROOT_CATEGORY );
        btnImages.setVisible( false );
        btnImages.addClickListener( new ClickListener(){

			private static final long serialVersionUID = -8368839172588393747L;

			@Override
			public void buttonClick( ClickEvent event ){
				buttonImages.click();
			}
		} );
        btnReports = buildBtnFloat( "btn.reports.float", context.getString( "words.reports" ), true );
        btnReports.setIcon(FontAwesome.FILE_TEXT);
        btnReports.setVisible( false );
        btnReports.addClickListener( new ClickListener(){

			private static final long serialVersionUID = -951616351416582941L;

			@Override
			public void buttonClick( ClickEvent event ){
				buttonReports.click();
			}
		} );
        
        btnPatients = buildBtnFloat( "btn.patiens.float", context.getString( "words.patients" ), true );
        btnPatients.setIcon(FontIcoMoon.PATIENT);
        btnPatients.setVisible( false );
        btnPatients.addClickListener( new ClickListener(){

			private static final long serialVersionUID = -951616351416582941L;

			@Override
			public void buttonClick( ClickEvent event ){
				buttonPatients.click();
			}
		} );
        
        btnAppointments = buildBtnFloat( "btn.cattions.float", context.getString( "words.appointments" ), true );
        btnAppointments.setIcon(FontAwesome.CALENDAR);
        btnAppointments.setVisible( false );
        btnAppointments.addClickListener( new ClickListener(){

			private static final long serialVersionUID = -951616351416582941L;

			@Override
			public void buttonClick( ClickEvent event ){
				buttonAppointments.click();
			}
		} );
        
		CssLayout hide = new CssLayout( btnFullScreen, btnPatients, btnAppointments, btnStudies, btnImages, btnReports );
		hide.addStyleName( ImedigTheme.FULLSCREEN_INDICATOR );
		hide.setHeight( "-1px" );
		hide.setWidth( "0px" );
		layout.addComponent( hide );
	}
	
	private static Button buildBtnFloat(String id, String description, boolean showcaption){
		Button btn = new Button( );
		btn.setIcon( FontIcoMoon.WINDOW_MAXIMIZE  );
		btn.setImmediate( true );
		//btn.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP);
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.setId( id );
		if (showcaption) btn.setCaption( description );
		
		btn.setDescription( description );
		
		return btn;
	}

	private Component buildTop()
	{
		topBar = new HorizontalLayout();
		topBar.setWidth( "100%" );
		topBar.setHeight( "-1px" );
		topBar.setSpacing( false );
		topBar.setMargin( new MarginInfo( false, true ) );
		topBar.setId( "menu-layout" );
		topBar.addStyleName( ImedigTheme.MENU_LAYOUT );

		logoBar = new HorizontalLayout();
		logoBar.setSpacing( false );
		logoBar.setMargin( false );
		logoBar.setHeight( "-1px" ); 

		selectButtonsBar = new HorizontalLayout();
		selectButtonsBar.setHeight( "-1px" );
		selectButtonsBar.setMargin( false );

		buttonsBar = new HorizontalLayout();
		buttonsBar.setHeight( "-1px" );
		buttonsBar.setMargin( false );

		topBar.addComponents( logoBar, selectButtonsBar, buttonsBar );
		topBar.setComponentAlignment( logoBar, Alignment.MIDDLE_LEFT );
		topBar.setComponentAlignment( selectButtonsBar, Alignment.MIDDLE_CENTER );
		topBar.setComponentAlignment( buttonsBar, Alignment.MIDDLE_RIGHT );
		//topBar.setExpandRatio( btnsLayout, 1.0f );

		return topBar;
	}

	private void showGlobalButtons()
	{
		buttonsBar.removeAllComponents();

		Button btn;
		
		buttonManual = new Button( context.getString( "words.manual" ), FontAwesome.BOOK );
		buttonManual.setDescription( context.getString( "words.user.manual" ) );
		setStyleButtonBar( buttonManual );
		buttonManual.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				showManual();
			}
		} );
		buttonsBar.addComponent( buttonManual );
		
		if ( LicenseManager.getInstance().hasBasicRights() )
		{
			buttonPatients= new Button( context.getString( "words.patients" ) , FontIcoMoon.PATIENT);
			buttonPatients.setDescription( context.getString( "words.patients.view" ) );
			setStyleButtonBar( buttonPatients );
			buttonPatients.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 2267293731045479204L;
	
				public void buttonClick( ClickEvent event )
				{
					showPatiens();
				}
			} );

			selectButtonsBar.addComponent( buttonPatients );
		}
		
		if ( LicenseManager.getInstance().hasCitationsRights() )
		{
			buttonAppointments= new Button( context.getString( "words.appointments" ) , FontAwesome.CALENDAR);
			buttonAppointments.setDescription( context.getString( "words.appointments.view" ) );
			setStyleButtonBar( buttonAppointments );
			buttonAppointments.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -9001228234723748388L;
	
				public void buttonClick( ClickEvent event )
				{
					showAppointments();
				}
			} );
			
			selectButtonsBar.addComponent( buttonAppointments );
		}
		
		if ( LicenseManager.getInstance().hasBasicRights() )
		{
			buttonImages= new Button( context.getString( "words.images" ) , FontIcoMoon.ROOT_CATEGORY);
			buttonImages.setDescription( context.getString( "words.image.view" ) );
			
			setStyleButtonBar( buttonImages );
			buttonImages.setVisible( false );
			buttonImages.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 6196485721108489326L;
	
				public void buttonClick( ClickEvent event )
				{
					showImages();
				}
			} );

			buttonReports = new Button( context.getString( "words.reports" ) , FontAwesome.FILE_TEXT);
			buttonReports.setDescription( context.getString( "words.reports" ) );
			setStyleButtonBar( buttonReports );
			buttonReports.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3827413316030851767L;
	
				public void buttonClick( ClickEvent event )
				{
					showReports();
				}
			} );
		
			buttonStudies = new Button( context.getString( "words.studies" ) , FontAwesome.SEARCH);
			buttonStudies.setDescription( context.getString( "words.studies" ) );
			setStyleButtonBar( buttonStudies );
			buttonStudies.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3827413316030851767L;
	
				public void buttonClick( ClickEvent event )
				{
					showQueryStudies();
				}
			} );

			selectButtonsBar.addComponents( buttonStudies, buttonImages, buttonReports );
		}

		if ( getContext().hasRight( AUTH_CONFIGURACION ) )
		{
			btn = new Button( context.getString( "words.configuration" ), FontAwesome.GEAR );
			//btn.setIcon( new ThemeResource( "images/config.png" ) );
			btn.setDescription( context.getString( "words.configuration" ) );
			setStyleButtonBar( btn );

			btn.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3827413316030851767L;

				public void buttonClick( ClickEvent event )
				{
					doShowConfiguration();
				}
			} );
			buttonsBar.addComponent( btn );
			
			if ( Settings.LOCAL_server )
			{
				btn = new Button( context.getString( "words.system" ), FontAwesome.SERVER );
				//btn.setIcon( new ThemeResource( "images/server.png" ) );
				btn.setDescription( context.getString( "words.system" ) );
				setStyleButtonBar( btn );
	
				btn.addClickListener( new Button.ClickListener()
				{
					private static final long serialVersionUID = -4602848575140845698L;
	
					public void buttonClick( ClickEvent event )
					{
						doShowSystemConfiguration();
					}
				} );
				buttonsBar.addComponent( btn );
			}
		}

		btn = new Button( context.getString( "words.status" ), FontAwesome.TACHOMETER );
		btn.setDescription( context.getString( "StorageStatusDlg.title" ) );
		setStyleButtonBar( btn );

		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8633533166630222974L;

			public void buttonClick( ClickEvent event )
			{
				statusStorage();
			}
		} );
		buttonsBar.addComponent( btn );

		if ( getContext().hasRight( AUTH_ADMINISTRACION ) )
		{
			btn = new Button( context.getString( "words.administration" ), FontAwesome.GEARS );
			btn.setDescription( context.getString( "words.administration" ) );
			setStyleButtonBar( btn );

			btn.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3827413316030851767L;

				public void buttonClick( ClickEvent event )
				{
					doShowAdministration();
				}
			} );
			buttonsBar.addComponent( btn );
		}

		btn = new Button( context.getString( "words.logout" ), FontAwesome.POWER_OFF);
		btn.setDescription( context.getString( "words.logout" ) );
		setStyleButtonBar( btn );

		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				Logout();
			}
		} );
		buttonsBar.addComponent( btn );
	}
	
	private static void setStyleButtonBar(Button button){
		button.addStyleName( ValoTheme.BUTTON_LARGE );
		button.addStyleName( ValoTheme.BUTTON_ICON_ALIGN_TOP );
		button.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		button.addStyleName( ImedigTheme.BUTTON_TOOLBAR );
	}
	
	private void showLogoLayout()
	{
		this.logoBar.removeAllComponents();
		logoBar.setSpacing( true );
		
		Image logo = new Image(null, new ThemeResource( "images/logobanner.png" ));
		logo.setWidth( LOGO_WIDTH );
		//label.setHeight( "100%" );
		logoBar.addComponent( logo );
		logoBar.setComponentAlignment( logo, Alignment.MIDDLE_LEFT );
		
		Label label = new Label( getContext().getCentros().get( 0 ).getNombre() );
		logoBar.addComponent( label );
		logoBar.setComponentAlignment( label, Alignment.MIDDLE_CENTER );

	}

	private Usuario getUsuario( ImedigContext ctx )
	{
    	try
    	{
    		return (Usuario)usuariosManager.getRow( ctx, ctx.getUsuario().getId() );
    	}
		catch ( Throwable e )
		{
			return ctx.getUsuario();
		}
	}
	
	public void showAppointments()
	{
		removeSelectedStyle();
		settingSelectedButton( buttonAppointments, btnAppointments );
		
		contents.removeAllComponents();
		if (appointmentsViewer == null){
			appointmentsViewer = new AppointmentsViewer( context);
			if (fullScreen) appointmentsViewer.showCaption();
		}
		contents.addComponent( appointmentsViewer );
	}

	public void showPatiens(){
		removeSelectedStyle();
		settingSelectedButton( buttonPatients, btnPatients );
		
		contents.removeAllComponents();
		if (patientViewer == null){
			patientViewer = new PatientsViewer( context);
			if (fullScreen) patientViewer.showCaption();
		}
		contents.addComponent( patientViewer );
	}
	
	public void showImages()
	{
		removeSelectedStyle();
		settingSelectedButton( buttonImages, btnImages );
		
		contents.removeAllComponents();
		if (imageViewer == null){
			imageViewer = new ViewerWnd( context, getUser( getUsuario( getContext() ) ) );
		}
		contents.addComponent( imageViewer );
	}
	
	private void removeSelectedStyle(){
		if (btnSelected != null){
			btnSelected.removeStyleName( ImedigTheme.BUTTON_MENU_SELECTED );
		}
		
		if (floatSelected != null){
			floatSelected.removeStyleName( ImedigTheme.BUTTON_SELECTED );
		}
	}

	private void settingSelectedButton(Button btnTool, Button btnFloat){
		btnSelected = btnTool;
		btnSelected.addStyleName( ImedigTheme.BUTTON_MENU_SELECTED );

		floatSelected = btnFloat;
		floatSelected.addStyleName( ImedigTheme.BUTTON_SELECTED );
	}

	public void showQueryStudies()
	{
		removeSelectedStyle();
		settingSelectedButton( buttonStudies, btnStudies );

		contents.removeAllComponents();
		if (queryViewer == null){
			queryViewer = new QueryViewer( context, getUser( getUsuario( getContext() ) ) );
			if (fullScreen) queryViewer.showCaption();
		}
		contents.addComponent( queryViewer );
	}
	
	private static User getUser(Usuario usuario){
		User user = new User();
		user.setLogin( usuario.getLogin());
		user.setQuery( Utils.getInt( usuario.getQuery(), 1 ) );
		user.setCompression( usuario.getCompresion());
		user.setUid( "" );
		
		String filter = usuario.getFiltro();
		user.setFilter( filter == null || filter.isEmpty() ? "*" : filter );
		
		return user;
	}

	private boolean userHasCenterAccess()
	{
		try
		{
			CentroQuery query = new CentroQuery();
			query.setUsuario( getContext().getUsuario().getId() );
			List<DetalleCentro> centros = centrosManager.getRows( getContext(), query );
			for ( DetalleCentro centro : centros )
				if ( centro.getId().equals( detalleCentro.getId() ) )
					return true;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return false;
	}

	private void showMainApp()
	{
		if ( !LicenseManager.getInstance().isValid() )
			Notification.show( getContext().getString( "error.license" ), Notification.Type.ERROR_MESSAGE );
		
		try
		{
			showLogoLayout();
			showGlobalButtons();
			
			if ( userHasCenterAccess() ){
				//showImages();
				showMainView();
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	private void showMainView(){
		Integer count = 0;
		
		if (getContext().hasProfile( Perfil.PROFILE_RADIOLOGIST )){
			count = getCountReport( getContext(), null, Informe.STATUS_INFORMED, Informe.STATUS_REQUESTED);
		}else if (getContext().hasProfile( Perfil.PROFILE_DOCTOR )){
			count = getCountReport( getContext(), getContext().getUsuario().getId(), Informe.STATUS_APROVED);
		}
		
		imageViewer = new ViewerWnd( context, getUser( getUsuario( getContext() ) ) );
		if (count > 0 ){
			showReports(true);
		}else{
			showQueryStudies();
		}
	}

	private void showManual()
	{
		new ShowExternalUrlDlg( getContext(), context.getString( "words.user.manual" ), getContext().getData( "Url" ) + "VAADIN/manual-usuario.pdf" ).showModalWindow();
	}

	private void showReports()
	{
		showReports( false );
	}
	
	private void showReports(boolean defaultSearch)
	{
		removeSelectedStyle();
		settingSelectedButton( buttonReports, btnReports );

		contents.removeAllComponents();
		
		if (reportsViewer == null){
			reportsViewer = new ReportsViewer( getContext(), defaultSearch );
			if (fullScreen) reportsViewer.showCaption();
		}
		
		contents.addComponent( reportsViewer );
	}

	private void doShowConfiguration()
	{
		ConfigurationDlg dlg = new ConfigurationDlg( getContext() );

		getUI().addWindow( dlg );
		
		dlg.focus();
	}

	private void doShowSystemConfiguration()
	{
		SystemSettingsDlg dlg = new SystemSettingsDlg( getContext() );

		getUI().addWindow( dlg );
		
		dlg.focus();
	}

	private void doShowAdministration()
	{
		AdministrationDlg dlg = new AdministrationDlg( getContext() );

		getUI().addWindow( dlg );

		dlg.focus();
	}
	
    private String getExceptionMessage( ImedigException e )
    {
    	switch ( e.getImedigError() )
    	{
    		case ImedigException.REFLECTION_ERROR:
    			return context.getString( "error.reflection" );
    	}
    	
    	return "";
    }

    
	public void startMainWindow()
	{
		getContext().addData( "MainWindow", getUI() );

		try
		{
			showMainApp();
		}
		catch ( Throwable e )
		{
			if ( e instanceof ImedigException )
			{
				String error = getExceptionMessage( (ImedigException)e );

				if ( error.isEmpty() )
					error = context.getString( "error.unknown" );

				messageAndExit( context.getString( "words.error" ), context.getResources(), error );
			}
			else
				messageAndExit( context.getString( "words.error" ), context.getResources(), context.getString( "error.unknown" ) );
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

	@Override
	public void refreshVisibleContent()
	{
	}

	@Override
	public void doAction( Action action )
	{
		if (action == null)
			return;

		if (action instanceof FullScreen) {
			topBar.setVisible( false );
		}else if (action instanceof ExitFullScreen) {
			topBar.setVisible( true );
		}else if (action instanceof ShowReportsListAction) {
			showReports(false);
			reportsViewer.refreshVisibleContent();
		}else if (action instanceof OpenStudies){
			hasImage = true;
			showImages();
			buttonImages.setVisible( true );
			if (fullScreen) btnImages.setVisible( true );
		}else if (action instanceof QueryStudies){
			hasImage = false;
			showQueryStudies();
			buttonImages.setVisible( false );
			if (fullScreen) btnImages.setVisible( false );
		}
	}
	
	private Integer getCountReport(ImedigContext ctx,Integer refiere, Integer...status){
		//ImedigManager queryMan = (ImedigManager) IOCManager.getInstanceOf( this.VtoDataRef.getFieldManagerImp() );
		DetallesInformesManager manager = IOCManager.getInstanceOf( DetallesInformesManager.class ); 
		try
		{
			InformeQuery query = new InformeQuery();
			query.setRefiere( refiere );
			query.setEstados( Arrays.asList( status ) );
			return manager.getNumberOfRows( ctx, query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return 0;
	}
}
