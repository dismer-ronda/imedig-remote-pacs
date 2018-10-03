package es.pryades.imedig.cloud.modules.Reports;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dal.InformesPlantillasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformePlantilla;
import es.pryades.imedig.cloud.dto.query.InformePlantillaQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public final class ReportConfigDlg extends Window
{
	private static final long serialVersionUID = -8298106169212775934L;

	private static final Logger LOG = Logger.getLogger( ReportConfigDlg.class );

	@Setter @Getter private String orientation;
	@Setter @Getter private String pagesize;
	@Setter @Getter private Integer template;
	@Setter @Getter private Boolean images;
	
	protected BeanItem<ReportConfigDlg> bi;

	private Label lbOrientation;
	private Label lbSize;
	private Label lbTemplate;

	private ComboBox comboOrientation;
	private ComboBox comboSize;
	private ComboBox comboTemplate;
	private CheckBox checkImages;

	protected VerticalLayout layout;
	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesContainer;

	protected Button bttnOk;
	protected Button bttnCancelar;
	
	@Setter @Getter private boolean accepted;
	
	@Getter private ImedigContext context;
	private InformesPlantillasManager manager;
	private DetalleInforme informe;
	
	/**
	 * 
	 * @param ctx
	 * @param resource
	 * @param modalOperation
	 * @param orgCentro
	 * @param parentWindow
	 */
	public ReportConfigDlg( ImedigContext ctx, String caption, DetalleInforme informe )
	{
		super();
		
		this.context = ctx;
		this.informe = informe;
		
		manager = (InformesPlantillasManager) IOCManager.getInstanceOf( InformesPlantillasManager.class );
		
		addCloseShortcut( KeyCode.ESCAPE );
		center();

		setWidth( "340px" );
		setHeight( "-1px" );

		layout = new VerticalLayout();
		
		layout.setSizeUndefined();
		layout.setWidth( "100%" );
		layout.setMargin( true );
		layout.setSpacing( true );

		setContent( layout );
		
		setModal( true );
		setResizable( false );
		setClosable( true );

		setCaption( caption );

		orientation = ctx.getOrientation();
		pagesize = ctx.getPagesize();
		template = ctx.getTemplate();
		images = ctx.getImages();

		initComponents();
	}

	public void initComponents()
	{
		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );

		operacionesContainer = new HorizontalLayout();
		operacionesContainer.setSpacing( true );

		bttnOk = new Button( context.getString( "words.ok" ) );
		bttnCancelar = new Button( context.getString( "words.cancel" ) );

		bttnCancelar.focus();

		operacionesContainer.addComponent( bttnOk );
		operacionesContainer.addComponent( bttnCancelar );
		operacionesContainer.setComponentAlignment( bttnOk, Alignment.BOTTOM_RIGHT );
		operacionesContainer.setComponentAlignment( bttnCancelar, Alignment.BOTTOM_RIGHT );

		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesContainer );
		layout.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( operacionesContainer, 1.0f );

		bi = new BeanItem<ReportConfigDlg>( this );

		lbOrientation = new Label( context.getString( "words.orientation" ) );
		lbOrientation.setWidth( "120px" );

		lbSize = new Label( context.getString( "words.pagesize" ) );
		lbSize.setWidth( "120px" );

		lbTemplate = new Label( context.getString( "words.template" ) );
		lbTemplate.setWidth( "120px" );

		comboOrientation = new ComboBox();
		comboOrientation.setWidth( "100%" );
		comboOrientation.setTextInputAllowed( false );
		comboOrientation.setNullSelectionAllowed( false );
		comboOrientation.setPropertyDataSource( bi.getItemProperty( "orientation" ) );

		comboSize = new ComboBox();
		comboSize.setWidth( "100%" );
		comboSize.setTextInputAllowed( false );
		comboSize.setNullSelectionAllowed( false );
		comboSize.setPropertyDataSource( bi.getItemProperty( "pagesize" ) );

		comboTemplate = new ComboBox();
		comboTemplate.setWidth( "100%" );
		comboTemplate.setTextInputAllowed( false );
		comboTemplate.setNullSelectionAllowed( false );
		comboTemplate.setPropertyDataSource( bi.getItemProperty( "template" ) );

		checkImages = new CheckBox( context.getString( "ReportConfigDlg.images" ) );
		checkImages.setWidth( "100%" );
		checkImages.setPropertyDataSource( bi.getItemProperty( "images" ) );

		fillComboBoxes();
		
		HorizontalLayout rowOrientation = new HorizontalLayout();
		rowOrientation.setWidth( "100%" );
		rowOrientation.addComponent( lbOrientation );
		rowOrientation.addComponent( comboOrientation );
		rowOrientation.setExpandRatio( comboOrientation, 1.0f );

		HorizontalLayout rowSize = new HorizontalLayout();
		rowSize.setWidth( "100%" );
		rowSize.addComponent( lbSize );
		rowSize.addComponent( comboSize );
		rowSize.setExpandRatio( comboSize, 1.0f );

		HorizontalLayout rowTemplate = new HorizontalLayout();
		rowTemplate.setWidth( "100%" );
		rowTemplate.addComponent( lbTemplate );
		rowTemplate.addComponent( comboTemplate );
		rowTemplate.setExpandRatio( comboTemplate, 1.0f );

		componentsContainer.addComponent( rowSize );
		componentsContainer.addComponent( rowOrientation );
		componentsContainer.addComponent( rowTemplate );
		componentsContainer.addComponent( checkImages );
		
		bttnOkListener();
		bttnCancelListener();
	}

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	public void closeModalWindow( boolean result )
	{
		this.accepted = result;
		
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}

	private void bttnOkListener()
	{
		bttnOk.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5858612612327120717L;

			public void buttonClick( ClickEvent event )
			{
				if ( orientation != null && pagesize != null && template != null && template != 0 )
				{
					context.setOrientation( orientation );
					context.setPagesize( pagesize );
					context.setTemplate( template );
					context.setImages( images );
					
					closeModalWindow( true );
				}
			}
		} );
	}
	
	private void bttnCancelListener()
	{
		bttnCancelar.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7591024099361245222L;

			public void buttonClick( ClickEvent event )
			{
				closeModalWindow( false );
			}
		} );
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
				comboOrientation.addItem( "portrait" );
				comboOrientation.setItemCaption( "portrait", context.getString( "words.portrait" ) );
				comboOrientation.addItem( "landscape" );
				comboOrientation.setItemCaption( "landscape", context.getString( "words.landscape" ) );
				
				comboSize.addItem( "A4" );
				comboSize.setItemCaption( "A4", context.getString( "words.A4" ) );
				comboSize.addItem( "letter" );
				comboSize.setItemCaption( "letter", context.getString( "words.letter" ) );

				InformePlantillaQuery query = new InformePlantillaQuery();
				query.setCentro( informe.getCentro() );
				List<InformePlantilla> rows = manager.getRows( context, query );

				for ( InformePlantilla plantilla : rows )
				{
					comboTemplate.addItem( plantilla.getId() );
					comboTemplate.setItemCaption( plantilla.getId(), plantilla.getNombre() );
					
					if ( template == 0 )
						comboTemplate.select( plantilla.getId() );
				}
			}
			catch ( Throwable e )
			{
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof ImedigException ) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					context.closeSessionCloud();
				}
				catch ( Throwable e )
				{
				}
			}
		}
	}
}
