package es.pryades.imedig.cloud.submodules.centers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.components.ViewerWnd;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public final class ShowExternalViewerDlg extends Window implements ModalParent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ShowExternalViewerDlg.class );
	
	private String url;
	private String estudioUid;
	
	protected VerticalLayout layout;
	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesContainer;

	protected Button bttnRequest;
	protected Button bttnReport;
	protected Button bttnClose;
	
	@Getter private ImedigContext context;

	private InformesManager informesManager;
	private DetalleCentro centro;
	private boolean request;
	
	private ViewerWnd viewer;
	
	@Getter private InformeImagen imagen;
	
	/**
	 * 
	 * @param ctx
	 * @param resources
	 * @param modalOperation
	 * @param orgCentro
	 * @param parentWindow
	 */
	public ShowExternalViewerDlg( ImedigContext ctx, DetalleCentro centro, String url, boolean request )
	{
		super();
		setWidth( "1280px" );
		setHeight( "830px" );
		this.context = ctx;
		this.estudioUid = url;
		this.centro = centro;
		this.request = request;
		
		informesManager = (InformesManager)IOCManager.getInstanceOf( InformesManager.class );
		
		addCloseShortcut( KeyCode.ESCAPE );

		layout = new VerticalLayout();
		
		layout.setMargin( false );
		layout.setSpacing( false );
		layout.setSizeFull();

		setContent( layout );

		setModal( true );
		setResizable( false );
		setClosable( false );
		
		setCaption( centro.getNombre() );

		initComponents();

		center();
	}

	public void initComponents()
	{
		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );
		componentsContainer.setSizeFull();
		
		operacionesContainer = new HorizontalLayout();
		operacionesContainer.setMargin( true );
		operacionesContainer.setSpacing( true );

		bttnClose = new Button( getContext().getString( "words.close" ) );
		
		if ( getContext().hasRight( "informes.crear" ) )
		{
			
			if ( request ) 
				bttnRequest = new Button( getContext().getString( "words.report.request" ) );
			else
				bttnReport = new Button( getContext().getString( "words.report.add" ) );
	
			if ( request ) 
				operacionesContainer.addComponent( bttnRequest );
			else
				operacionesContainer.addComponent( bttnReport );
		}
		
		operacionesContainer.addComponent( bttnClose );
		
		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesContainer );
		layout.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( componentsContainer, 1.0f );
		
		viewer = new ViewerWnd( context, getUser( context.getUsuario() ), true );
		componentsContainer.addComponent( viewer );
		viewer.doAction( new OpenStudies( this, Arrays.asList( estudioUid ) ) );

		bttnCloseListener();
		if ( request ) 
			bttnRequestListener();
		else
			bttnReportListener();

		bttnClose.focus();
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

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	private void bttnRequestListener()
	{
		if ( bttnRequest != null )
			bttnRequest.addClickListener( new Button.ClickListener()
			{
				public void buttonClick( ClickEvent event )
				{
					doRequest();
				}
			} );
	}
	private void bttnReportListener()
	{
		if ( bttnReport != null )
			bttnReport.addClickListener( new Button.ClickListener()
			{
				public void buttonClick( ClickEvent event )
				{
					doReport();
				}
			} );
	}

	private void bttnCloseListener()
	{
		bttnClose.addClickListener( new Button.ClickListener()
		{
			public void buttonClick( ClickEvent event )
			{
				getUI().removeWindow( ShowExternalViewerDlg.this );
			}
		} );
	}
	
	private void doRequest()
	{
		ReportInfo reportInfo = informesManager.getReportInfo( getContext(), centro );
		
		if ( reportInfo != null )
		{
			List<InformeImagen> imagenes = new ArrayList<InformeImagen>();
			InformeImagen imagen = new InformeImagen();
			imagen.setUrl( reportInfo.getUrl() );
			imagen.setIcon( reportInfo.getIcon() );
			imagenes.add( imagen );
			
			DetalleInforme informe = new DetalleInforme();

			informe.setCentro( centro.getId() );
			informe.setEstudio_acceso( reportInfo.getHeader().getAccessionNumber() );
			informe.setEstudio_id( reportInfo.getHeader().getStudyID() );
			informe.setEstudio_uid( reportInfo.getHeader().getStudyInstanceUID() );
			informe.setModalidad( reportInfo.getHeader().getModality() );
			informe.setPaciente_id( reportInfo.getHeader().getPatientID() );
			informe.setPaciente_nombre( reportInfo.getHeader().getPatientName() );
			informe.setCentro_ip( centro.getIp() );
			informe.setCentro_puerto( centro.getPuerto() );

			informe.setHorario_nombre( centro.getHorario_nombre() );
			
			informe.setEstado( 0 );
			informe.setProtegido( 0 );

			new ModalNewInforme( getContext(), Operation.OP_ADD, informe, imagenes, this, "informes.solicitar" ).showModalWindow();
		}
	}

	private void doReport()
	{
		//ReportInfo reportInfo = informesManager.getReportInfo( getContext(), centro );
		ReportInfo reportInfo = viewer.getReportInfo();
		
		if ( reportInfo != null )
		{
			imagen = new InformeImagen();
			imagen.setUrl( reportInfo.getUrl() );
			imagen.setIcon( reportInfo.getIcon() );
			
			getUI().removeWindow( this );
		}
	}

	@Override
	public void refreshVisibleContent()
	{
	}
}
