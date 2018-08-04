package es.pryades.imedig.cloud.modules.Reports;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.cloud.submodules.centers.ShowExternalViewerDlg;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public final class ModalNewInforme extends ModalWindowsCRUD
{
	private static final Logger LOG = Logger.getLogger( ModalNewInforme.class );

	protected DetalleInforme newInforme;
	
	@Setter protected List<InformeImagen> imagenes;

	protected List<CheckBox> cbs;

	private TextField editPacienteId;
	private TextField editPacienteNombre;
	private TextField editEstudioId;
	private TextField editEstudioAcceso;
	private TextField editClaves;
	private CKEditorTextField editTexto;
	private TextField editInforma;
	private ComboBox comboRefiere;
	private TextField editIcd10cm;
	private Button bttnIcd10cm;
	private HorizontalLayout rowImagenes;
	private CheckBox checkProtegido;

	private InformesManager informesManager;
	private UsuariosManager usuariosManager;
	
	private boolean request;
	
	@Getter
	private boolean added = false;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 * @param modalOperation
	 * @param orgCentro
	 * @param parentWindow
	 */
	public ModalNewInforme( ImedigContext ctx, Operation modalOperation, DetalleInforme orgDto, List<InformeImagen> imagenes, ModalParent parentWindow, String derecho )
	{
		super( ctx, parentWindow, modalOperation, orgDto, derecho );
		
		this.imagenes = imagenes;
		
		setWidth( "1024px" );
		
		//setModal( false );
		
		initComponents();
		
		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
	}

	public String getOpName()
	{
		return operation.equals( Operation.OP_ADD ) ? "do.request" : operation.getOpName();
	}
	

	@Override
	public void initComponents()
	{
		super.initComponents();

		informesManager = (InformesManager) IOCManager.getInstanceOf( InformesManager.class );
		usuariosManager = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );

		request = operation.equals( Operation.OP_ADD );
		
		try
		{
			newInforme = (DetalleInforme) Utils.clone( (DetalleInforme) orgDto );
		}
		catch ( Throwable e1 )
		{
			newInforme = new DetalleInforme();
		}

		bi = new BeanItem<ImedigDto>( newInforme );

		editPacienteId = new TextField( getContext().getString( "modalNewReport.lbPatientId" ), bi.getItemProperty( "paciente_id" ) );
		editPacienteId.setWidth( "100%" );
		editPacienteId.setNullRepresentation( "" );
		editPacienteId.setReadOnly( true );
		
		editPacienteNombre = new TextField( getContext().getString( "modalNewReport.lbPatientName" ), bi.getItemProperty( "paciente_nombre" ) );
		editPacienteNombre.setWidth( "100%" );
		editPacienteNombre.setNullRepresentation( "" );

		editEstudioId = new TextField( getContext().getString( "modalNewReport.lbStudyId" ), bi.getItemProperty( "estudio_id" ) );
		editEstudioId.setWidth( "100%" );
		editEstudioId.setNullRepresentation( "" );
		editEstudioId.setReadOnly( true );

		editEstudioAcceso = new TextField( getContext().getString( "modalNewReport.lbStudyAccession" ), bi.getItemProperty( "estudio_acceso" ) );
		editEstudioAcceso.setWidth( "100%" );
		editEstudioAcceso.setNullRepresentation( "" );
		editEstudioAcceso.setReadOnly( true );

		editClaves = new TextField( getContext().getString( "modalNewReport.lbKeywords" ), bi.getItemProperty( "claves" ) );
		editClaves.setWidth( "100%" );
		editClaves.setNullRepresentation( "" );
		editClaves.setReadOnly( request );
		
		CKEditorConfig config = new CKEditorConfig();
        config.disableElementsPath();
        config.setResizeEnabled( false );
        config.addCustomToolbarLine("{ items : ['Bold', 'Italic', 'Underline', '-', " +
        		"'NumberedList','BulletedList', '-', " +
        		"'Indent', 'Outdent', '-', " +
        		"'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', " +
        		"'Font','FontSize', 'TextColor', '-', " +
        		"'Table', '-', " +
//        		"'Copy','Paste','PasteText','PasteFromWord', '-', " +        		
//				"'TextColor', 'BGColor', " + 
        		"'RemoveFormat', '-', 'SpecialChar' ] }");
        //config.addExtraConfig( "fontSize_sizes", "'16/16px;24/24px;48/48px;'" );
        
		editTexto = new CKEditorTextField( config );
		editTexto.setWidth( "100%" );
		editTexto.setHeight( "400px" );
		editTexto.setValue( newInforme.getTexto() );
		
		//editTexto.setNullRepresentation( "" );

		comboRefiere = new ComboBox(getContext().getString( "modalNewReport.lbReferrer" ));
		comboRefiere.setWidth( "100%" );
		comboRefiere.setTextInputAllowed( false );
		comboRefiere.setNullSelectionAllowed( true );
		comboRefiere.setPropertyDataSource( bi.getItemProperty( "refiere" ) );

		editInforma = new TextField(getContext().getString( "modalNewReport.lbReport" ));
		editInforma.setWidth( "100%" );
		editInforma.setValue( newInforme.getInformaNombreCompleto() );
		editInforma.setReadOnly( true );

		editIcd10cm = new TextField( bi.getItemProperty( "icd10cm" ) );
		editIcd10cm.setWidth( "100%" );
		editIcd10cm.setNullRepresentation( "" );
		editIcd10cm.setReadOnly( request );

		bttnIcd10cm = new Button(FontAwesome.SEARCH);
		//bttnIcd10cm.setWidth( "16px" );
		bttnIcd10cm.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		bttnIcd10cm.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		bttnIcd10cm.addStyleName( ValoTheme.BUTTON_TINY );
		bttnIcd10cm.setDescription( getContext().getString( "words.search" ) );
		bttnIcd10cmListener();
		
		rowImagenes = new HorizontalLayout();
		rowImagenes.setWidth( "-1px" );
		rowImagenes.setHeight( "64px" );
		rowImagenes.setSpacing( true );
		rowImagenes.setMargin( false );

		cbs = new ArrayList<CheckBox>();

		for ( InformeImagen imagen : imagenes )
			addImage( imagen );
		
		checkProtegido = new CheckBox(  getContext().getString( "modalNewReport.protected" ) );
		checkProtegido.setWidth( "100%" );
		checkProtegido.setValue( newInforme.getProtegido() == 1 );
		
		fillComboBoxes();

		HorizontalLayout rowIcd10cm = new HorizontalLayout();
		rowIcd10cm.setWidth( "100%" );
		rowIcd10cm.addComponent( editIcd10cm );
		rowIcd10cm.addComponent( bttnIcd10cm );
		rowIcd10cm.setComponentAlignment( bttnIcd10cm, Alignment.MIDDLE_CENTER );
		rowIcd10cm.setExpandRatio( editIcd10cm, 1.0f );
		rowIcd10cm.setCaption( getContext().getString( "modalNewReport.lbIcd10cm" ) );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		
		FormLayout rowRefiere = new FormLayout();
		rowRefiere.setWidth( "100%" );
		rowRefiere.setMargin( false );
		rowRefiere.addComponent( comboRefiere );

		FormLayout rowInforma = new FormLayout();
		rowInforma.setWidth( "100%" );
		rowInforma.setMargin( false );
		rowInforma.addComponent( editInforma );
		
		row4.addComponent( rowRefiere );
		row4.addComponent( rowInforma );
		
		FormLayout left = new FormLayout(editPacienteId, editEstudioId, rowIcd10cm);
		left.setMargin( false );
		
		FormLayout right = new FormLayout(editPacienteNombre, editEstudioAcceso, editClaves);
		right.setMargin( false );
		HorizontalLayout top = new HorizontalLayout( left, right );
		top.setWidth( "100%" );
		top.setSpacing( true );
		

		componentsContainer.addComponent( top );
		componentsContainer.addComponent( editTexto  );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( checkProtegido );
		
		if ( imagenes.size() > 0 )
			componentsContainer.addComponent( rowImagenes );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewReport";
	}

	@Override
	protected void defaultFocus()
	{
		editTexto.focus();
	}

	private List<InformeImagen> getSelectedImages()
	{
		List<InformeImagen> imgs = new ArrayList<InformeImagen>();
		
		for ( CheckBox cb : cbs )
		{
			if ( cb.getValue() )
				imgs.add( (InformeImagen)cb.getData() );
		}
		
		return imgs;
	}

	protected boolean onAdd()
	{
		try
		{
			newInforme.setId( null );
			newInforme.setFecha( Utils.getTodayAsLong( newInforme.getHorario_nombre() ) );
			newInforme.setTexto( (String)editTexto.getValue() );
			newInforme.setProtegido( ((Boolean)checkProtegido.getValue()).booleanValue() ? 1 : 0 );
			
			informesManager.addReport( context, newInforme, getSelectedImages() );

			added = true;
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onModify()
	{
		try
		{
			newInforme.setFecha( Utils.getTodayAsLong( newInforme.getHorario_nombre() ) );
			newInforme.setTexto( (String)editTexto.getValue() );
			newInforme.setProtegido( ((Boolean)checkProtegido.getValue()).booleanValue() ? 1 : 0 );
			
			if ( getContext().hasRight( "informes.crear" ) )
			{
				newInforme.setEstado( 1 );
				newInforme.setInforma( getContext().getUsuario().getId() );
			}
			
			informesManager.modifyReport( context, (Informe) orgDto, newInforme, getSelectedImages() );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onDelete()
	{
		try
		{
			informesManager.deleteReport( context, newInforme );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void fillComboBoxes()
	{
		SqlSession session = context.getSessionCloud();

		boolean finish = ( session == null );

		try
		{
			if ( finish )
				session = context.openSessionCloud();

			try
			{
				UsuarioQuery query = new UsuarioQuery();
				query.setCentro( newInforme.getCentro() );
				
				List<Usuario> usuarios = usuariosManager.getRows( context, query );

				for ( Usuario usuario : usuarios )
				{
					comboRefiere.addItem( usuario.getId() );
					comboRefiere.setItemCaption( usuario.getId(), usuario.getNombreCompleto() );
				}
			}
			catch ( ImedigException e )
			{
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					context.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	}

	public void showImageDlg( InformeImagen image )
	{
		try
		{
	    	DetallesCentrosManager centrosManager = (DetallesCentrosManager) IOCManager.getInstanceOf( DetallesCentrosManager.class );

	    	DetalleCentro detalleCentro = (DetalleCentro)centrosManager.getRow( getContext(), newInforme.getCentro() );
	    	
	    	String url = centrosManager.getCentroUrl( getContext(), detalleCentro, newInforme.getEstudio_uid() );
	    	
	    	if ( url != null )
	    	{
	    		final ShowExternalViewerDlg viewerDlg = new ShowExternalViewerDlg( getContext(), detalleCentro, newInforme.getEstudio_uid(), false );
	    		
	    		viewerDlg.addCloseListener
				( 
					new Window.CloseListener() 
					{
						@Override
					    public void windowClose( CloseEvent e ) 
					    {
							InformeImagen image = viewerDlg.getImagen();
							
							if ( image != null )
								addImage( image );
					    }
					}
				);
				
	    		viewerDlg.showModalWindow();
	    	}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	public void addImage( InformeImagen image )
	{
		CheckBox cb = new CheckBox();
		cb.setValue( true );
		cb.setData( image );
		cb.setVisible( !request );
		cbs.add( cb );
		
		rowImagenes.addComponent( cb );
		rowImagenes.setComponentAlignment( cb, Alignment.MIDDLE_LEFT );
		
		Button btn = new Button();
		btn.setData( image );
		btn.setWidth( "64px" );
		btn.setHeight( "64px" );
		btn.addStyleName( ValoTheme.BUTTON_BORDERLESS );
		btn.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btn.addClickListener( new Button.ClickListener()
		{
			public void buttonClick( ClickEvent event )
			{
				showImageDlg( (InformeImagen)event.getButton().getData() );
			}
		} );

		btn.setIcon( new ExternalResource( Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-cloud" + image.getIcon() ) );
	
		rowImagenes.addComponent( btn );
		rowImagenes.setComponentAlignment( btn, Alignment.MIDDLE_LEFT );
	}
	
	public boolean addImage( ReportInfo reportInfo, InformeImagen image )
	{
		if ( reportInfo.getHeader().getPatientID().equals( newInforme.getPaciente_id() ) && reportInfo.getHeader().getStudyID().equals( newInforme.getEstudio_id() ) )
		{
			imagenes.add( image );
			
			addImage( image );
			
			return true;
		}
		
		return false;
	}

	private void onIcd10cm()
	{
		new ShowExternalUrlDlg( getContext(), "ICD-10 Version 2010", "http://apps.who.int/classifications/icd10/browse/2010/en" ).showModalWindow();
	}

	private void bttnIcd10cmListener()
	{
		bttnIcd10cm.addClickListener( new Button.ClickListener()
		{
			public void buttonClick( ClickEvent event )
			{
				onIcd10cm();
			}
		} );
	}

}
