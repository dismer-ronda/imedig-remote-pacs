package es.pryades.imedig.cloud.submodules.centers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;

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
		
		this.context = ctx;
		this.url = url;
		this.centro = centro;
		this.request = request;
		
		informesManager = (InformesManager)IOCManager.getInstanceOf( InformesManager.class );
		
		addCloseShortcut( KeyCode.ESCAPE );

		layout = new VerticalLayout();
		
		layout.setMargin( false );
		layout.setSpacing( false );
		layout.setWidth( "1280px" );
		layout.setHeight( "800px" );

		setContent( layout );

		setModal( false );
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

		ExternalResource resource = new ExternalResource( url );
		BrowserFrame e = new BrowserFrame( null, resource );
	    //e.setType(Embedded.TYPE_BROWSER);
	    e.setSizeFull();

		componentsContainer.addComponent( e );
		
		bttnCloseListener();
		if ( request ) 
			bttnRequestListener();
		else
			bttnReportListener();

		bttnClose.focus();
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
		ReportInfo reportInfo = informesManager.getReportInfo( getContext(), centro );
		
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
