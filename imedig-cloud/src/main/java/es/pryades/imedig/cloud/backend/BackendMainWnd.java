package es.pryades.imedig.cloud.backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.MessageDlg;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.AccesosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dal.ParametrosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Acceso;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Administration.AdministrationDlg;
import es.pryades.imedig.cloud.modules.Configuration.ConfigurationDlg;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.Reports.ReportsDlg;
import es.pryades.imedig.cloud.modules.Reports.ShowExternalUrlDlg;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.components.ViewerWnd;
import lombok.Getter;

public class BackendMainWnd extends VerticalLayout implements ModalParent
{
	private static final Logger LOG = Logger.getLogger( BackendMainWnd.class );

	private static final long serialVersionUID = 5722841685959892036L;

	private static final String LOGO_WIDTH = "116px";

	private static final String AUTH_CONFIGURACION = "configuracion";
	private static final String AUTH_ADMINISTRACION = "administracion";

	private LoginPanel loginPanel;

	private VerticalLayout mainLayout;
	//private HorizontalLayout banner;

	private HorizontalLayout topBar;
	private HorizontalLayout logoBar;
	private HorizontalLayout buttonsBar;

	private VerticalLayout contents;
	
	private Button buttonReports;
	private Button buttonManual;
	protected Button bttnRequest;
	protected Button bttnReport;

	private DetallesCentrosManager centrosManager;
	private AccesosManager accesosManager;
	private UsuariosManager usuariosManager;
	private InformesManager informesManager;

	@Getter
	private ImedigContext context;

	private ViewerWnd viewer;
	private DetalleCentro detalleCentro;
	@Getter 
	private InformeImagen imagen;

	public BackendMainWnd( ImedigContext ctx )
	{
		this.context = ctx;
		
		centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );
		accesosManager = (AccesosManager)IOCManager.getInstanceOf( AccesosManager.class );
		usuariosManager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );
		informesManager = (InformesManager)IOCManager.getInstanceOf( InformesManager.class );
		
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

	private void registerAccess()
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
	
	public void Logout()
	{
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

		contents = new VerticalLayout();
		contents.setImmediate( false );
		contents.setSizeFull();
		contents.setMargin( false );

		mainLayout.addComponent( contents );
		mainLayout.setExpandRatio( contents, 1.0f );

		return mainLayout;
	}

	private Component buildTop()
	{
		VerticalLayout topLayout = new VerticalLayout();
		topLayout.setWidth( "100%" );

		topBar = new HorizontalLayout();
		topBar.setWidth( "100%" );
		topBar.setHeight( "-1px" ); //BAR_HEIGHT );
		topBar.setSpacing( false );
		topBar.setMargin( true );
		topBar.addStyleName( "menu-layout" );

		topLayout.addComponent( topBar );
		topLayout.setComponentAlignment( topBar, Alignment.MIDDLE_CENTER );

//		banner = new HorizontalLayout();
//		banner.setWidth( "100.0%" );
//		banner.setHeight( "1px" );
//		banner.setMargin( false );
//		banner.setSpacing( false );
//		banner.setStyleName( "banner" );
//
//		topLayout.addComponent( banner );

		logoBar = new HorizontalLayout();
		logoBar.setSpacing( false );
		logoBar.setMargin( false );
		logoBar.setHeight( "-1px" ); //LOGO_HEIGHT );

		topBar.addComponent( logoBar );
		topBar.setComponentAlignment( logoBar, Alignment.MIDDLE_LEFT );

		buttonsBar = new HorizontalLayout();
		buttonsBar.setHeight( "-1px" ); //BAR_HEIGHT );
		buttonsBar.setMargin( false );
		buttonsBar.setSpacing( true );

		topBar.addComponent( buttonsBar );
		topBar.setComponentAlignment( buttonsBar, Alignment.MIDDLE_RIGHT );
		topBar.setExpandRatio( buttonsBar, 1.0f );

		return topLayout;
	}

	private void showGlobalButtons()
	{
		buttonsBar.removeAllComponents();

		Button btn;
		
		buttonManual = new Button( context.getString( "words.manual" ), FontAwesome.BOOK );
		//buttonManual.setIcon( new ThemeResource( "images/manual.png" ) );
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

		buttonReports = new Button( context.getString( "words.reports" ) , FontAwesome.FILE_TEXT);
		//buttonReports.setIcon( new ThemeResource( "images/reports.png" ) );
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
		buttonsBar.addComponent( buttonReports );

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
		//btn.setIcon( new ThemeResource( "images/storage-status.png" ) );
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
			//btn.setIcon( new ThemeResource( "images/admin.png" ) );
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
		//btn.setIcon( new ThemeResource( "images/logout.png" ) );
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

		Label label = new Label();
		label.setIcon( new ThemeResource( "images/logobanner.png" ) );
		label.setWidth( LOGO_WIDTH );
		label.setHeight( "100%" );
		logoBar.addComponent( label );
		logoBar.setComponentAlignment( label, Alignment.MIDDLE_LEFT );
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
	
	private void doRequest()
	{
		ReportInfo reportInfo = informesManager.getReportInfo( getContext(), detalleCentro );
		
		if ( reportInfo != null )
		{
			List<InformeImagen> imagenes = new ArrayList<InformeImagen>();
			InformeImagen imagen = new InformeImagen();
			imagen.setUrl( reportInfo.getUrl() );
			imagen.setIcon( reportInfo.getIcon() );
			imagenes.add( imagen );
			
			DetalleInforme informe = new DetalleInforme();

			informe.setCentro( detalleCentro.getId() );
			informe.setEstudio_acceso( reportInfo.getHeader().getAccessionNumber() );
			informe.setEstudio_id( reportInfo.getHeader().getStudyID() );
			informe.setEstudio_uid( reportInfo.getHeader().getStudyInstanceUID() );
			informe.setModalidad( reportInfo.getHeader().getModality() );
			informe.setPaciente_id( reportInfo.getHeader().getPatientID() );
			informe.setPaciente_nombre( reportInfo.getHeader().getPatientName() );
			informe.setCentro_ip( detalleCentro.getIp() );
			informe.setCentro_puerto( detalleCentro.getPuerto() );

			informe.setHorario_nombre( detalleCentro.getHorario_nombre() );
			
			informe.setEstado( 0 );
			informe.setProtegido( 0 );

			final ModalNewInforme report = new ModalNewInforme( getContext(), Operation.OP_ADD, informe, imagenes, this, "informes.solicitar" );
			
			report.addCloseListener(
				new Window.CloseListener() 
				{
					private static final long serialVersionUID = -7551376683736330872L;

					@Override
				    public void windowClose( CloseEvent e ) 
				    {
						if ( report.isAdded() )
							showReports();
				    }
				}
			);
			report.showModalWindow();
		}
	}
	
	private String getCurrentImageUrl()
	{
		ReportInfo reportInfo = informesManager.getReportInfo( getContext(), detalleCentro );
		
		String imageUrl = reportInfo != null ? Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer" + reportInfo.getUrl() : null;
		
		LOG.info(  "imageUrl " + imageUrl );
		
		return imageUrl;
		
	}

	private StreamResource createResource() 
	{
		return new StreamResource( 
			new StreamSource() 
			{
				private static final long serialVersionUID = 8919773793750134590L;

				@Override
	            public InputStream getStream() 
	            {
	                try 
	                {
	                	return new URL( getCurrentImageUrl() ).openStream();
	                } 
	                catch ( IOException e ) 
	                {
	                    e.printStackTrace();
	                    return null;
	                }
	
	            }
	        }, 
	        Utils.getUUID() + ".png" );
    }
	
	public void showViewer()
	{
//		VerticalLayout componentsContainer = new VerticalLayout();
//		componentsContainer.setMargin( false );
//		componentsContainer.setSpacing( true );
//		componentsContainer.setSizeFull();
		
		HorizontalLayout operacionesContainer = new HorizontalLayout();
		operacionesContainer.setSpacing( true );

		if ( getContext().hasRight( "informes.crear" ) )
		{
			bttnRequest = new Button( getContext().getString( "words.report.request" ) );
			bttnRequest.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 8238931466005672144L;
	
				public void buttonClick( ClickEvent event )
				{
					doRequest();
				}
			} );
		}
		
		Button bttnDownload= new Button( getContext().getString( "words.download" ) );
		/*bttnDownload.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 1731772233385937305L;

			public void buttonClick( ClickEvent event )
			{
				doDownload();
			}
		} );*/

		FileDownloader fileDownloaderCsv = new FileDownloader( createResource() );
        fileDownloaderCsv.extend( bttnDownload );
        
		operacionesContainer.addComponent( bttnDownload );
		if ( bttnRequest != null )
			operacionesContainer.addComponent( bttnRequest );
		
		long ts = new Date().getTime();
		
		Usuario usuario = getUsuario( getContext() );

		String extra = "ts=" + ts + 
				"&login=" + usuario.getLogin() + 
				"&filter=" + usuario.getFiltro() +
				"&query=" + usuario.getQuery() +
				"&compression=" + usuario.getCompresion() +
				"&uid=" + "";
		
		String token = "token=" + Utils.getTokenString( "IMEDIG" + ts, Settings.TrustKey );
		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ) ;

		//String url =  Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer/viewer" + "?" + token + "&" + code + "&debug";
		//String url =  Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer-vaadin" + "?" + token + "&" + code + "&debug";

		//ExternalResource resource = new ExternalResource( url );
		//BrowserFrame e = new BrowserFrame( null, resource );
	    //e.setType(Embedded.TYPE_BROWSER);
	    //e.setSizeFull();
		
		viewer = new ViewerWnd( context, getUser( usuario ) );

		//componentsContainer.addComponent( viewer );
		
		HorizontalLayout banner = new HorizontalLayout();
		banner.setWidth( "100%" );
		banner.setMargin( true );
		banner.addStyleName( "menu-layout" );
		banner.addComponent( operacionesContainer );
		banner.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		
		contents.addComponent( viewer );
		contents.addComponent( banner );
		contents.setComponentAlignment( banner, Alignment.BOTTOM_CENTER );
		contents.setExpandRatio( viewer, 1.0f );
	}
	
	private User getUser(Usuario usuario){
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
		try
		{
			showLogoLayout();
			showGlobalButtons();
			
			if ( userHasCenterAccess() )
				showViewer();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	private void showManual()
	{
		new ShowExternalUrlDlg( getContext(), context.getString( "words.user.manual" ), getContext().getData( "Url" ) + "VAADIN/manual-usuario.pdf" ).showModalWindow();
	}

	private void showReports()
	{
		//instance.getCentersView().notifyShowReports();
		
		ReportsDlg dlg = new ReportsDlg( getContext() );

		getUI().addWindow( dlg );
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
}
