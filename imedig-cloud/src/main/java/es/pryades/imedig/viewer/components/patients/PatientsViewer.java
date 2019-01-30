package es.pryades.imedig.viewer.components.patients;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings({"unchecked"})
public class PatientsViewer extends FilteredContent implements ModalParent, Property.ValueChangeListener
{
	private static final long serialVersionUID = -1246528047282752893L;

	private static final Logger LOG = Logger.getLogger( PatientsViewer.class );

	@Setter @Getter private String nombre;
	@Setter @Getter private String identificador;
	@Setter @Getter private Integer fecha;
	
	private BeanItem<PatientsViewer> bi;
	
	private TextField textNombre;
	private TextField textIdentificador;
	private ComboBox comboFecha;

	private PacientesManager pacientesManager;
	private EstudiosManager estudiosManager;
	
	private static final String COMBO_WIDTH = "200px";
	private static final String TEXT_WIDTH = "200px";
	
	//private static final Integer PERFIL_IMAGENOLOGO = 3;
	//private static final Integer PERFIL_USUARIO = 2;
	
	//private boolean defaultSearch;
	//private boolean showMessage;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public PatientsViewer( ImedigContext ctx )
	{
		super( ctx );
		
		pacientesManager = (PacientesManager) IOCManager.getInstanceOf( PacientesManager.class );
		estudiosManager = (EstudiosManager) IOCManager.getInstanceOf( EstudiosManager.class );
		
		setSizeFull();
		setMargin( true );
		initComponents();
		setEnabledAdd( true );
	}
	
	public String[] getVisibleCols()
	{
		return PacienteControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "pacienteConfig.table";
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( PacienteVto.class, new PacienteVto(), new PacienteVtoFieldRef(), new QueryFilterRef( new Paciente() ), getContext(), Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String getTabTitle()
	{
		return getContext().getString( "words.patients" );
	}
	
	
	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null );
		setEnabledDelete( false );
	}

	@Override
	public Query getQueryObject()
	{
		return getQuery();
	}
	
	private Paciente getQuery(){
		Paciente query = new Paciente();
		
		if (StringUtils.isNotEmpty( nombre )){
			query.setNombre( nombre );
		}

		if (StringUtils.isNotEmpty( identificador )){
			query.setUid( identificador );
		}

		return query;
	}

	public void onModifyRow( Object row )
	{
		PacienteVto paciente = (PacienteVto)row;
		
		InformeImagen query = new InformeImagen();
		query.setInforme( paciente.getId() );
		
		String right = getContext().hasRight( "informes.crear" ) ? "informes.crear" : "informes.solicitar";
		
		List<InformeImagen> images;
		
		try
		{
			//images = informeImagenesManager.getRows( getContext(), query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			images = new ArrayList<InformeImagen>();
		}
		
		//new ModalNewInforme( getContext(), Operation.OP_MODIFY, informe, images, PatientsViewer.this, right ).showModalWindow();
	}

	public void onDeleteRow( Object row )
	{
		DetalleInforme informe = (DetalleInforme)row;
		
		InformeImagen query = new InformeImagen();
		query.setInforme( informe.getId() );
		
		List<InformeImagen> images;
		
		try
		{
			//images = informeImagenesManager.getRows( getContext(), query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			images = new ArrayList<InformeImagen>();
		}

		//new ModalNewInforme( getContext(), Operation.OP_DELETE, informe, images, PatientsViewer.this, "informes.crear" ).showModalWindow();
	}
	
	public void setDateFilter( InformeQuery queryObj )
	{
		switch ( fecha )
		{
			case 1:
				queryObj.setDesde( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 2:
				queryObj.setDesde( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 3:
				queryObj.setDesde( Long.parseLong( Utils.getLastWeekDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 4:
				queryObj.setDesde( Long.parseLong( Utils.getLastMonthDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 5:
				queryObj.setDesde( Long.parseLong( Utils.getLastYearDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			default:
			break;
		}
	}
	
	private Component getQueryFecha()
	{
		comboFecha = new ComboBox(getContext().getString( "words.date" ));
		comboFecha.setWidth( COMBO_WIDTH );
		comboFecha.setNullSelectionAllowed( false );
		comboFecha.setTextInputAllowed( false );
		comboFecha.setPropertyDataSource( bi.getItemProperty( "fecha" ) );
		comboFecha.addItem( 0 );
		comboFecha.setItemCaption( 0, getContext().getString( "words.all" ) );
		comboFecha.addItem( 1 );
		comboFecha.setItemCaption( 1, getContext().getString( "words.today" ) );
		comboFecha.addItem( 2 );
		comboFecha.setItemCaption( 2, getContext().getString( "words.yesterday" ) );
		comboFecha.addItem( 3 );
		comboFecha.setItemCaption( 3, getContext().getString( "words.lastweek" ) );
		comboFecha.addItem( 4 );
		comboFecha.setItemCaption( 4, getContext().getString( "words.lastmonth" ) );
		comboFecha.addItem( 5 );
		comboFecha.setItemCaption( 5, getContext().getString( "words.lastyear" ) );
		
		return getRow( comboFecha );
	}

	
	public Component getQueryNombre()
	{
		textNombre = new TextField( getContext().getString( "words.name" ), bi.getItemProperty( "nombre" ) );
		textNombre.setNullRepresentation( "" );
		textNombre.setWidth( TEXT_WIDTH );
		return getRow( textNombre );
	}

	public Component getQueryIdentificador()
	{
		textIdentificador = new TextField( getContext().getString( "QueryForm.PatientId" ), bi.getItemProperty( "identificador" ) );
		textIdentificador.setNullRepresentation( "" );
		textIdentificador.setWidth( TEXT_WIDTH );
		return getRow( textIdentificador );
	}


	@Override
	public Component getQueryComponent()
	{
		fecha = 4;
//		paciente = "";
//		estudio = "";
//		estado = -1;
//		icd10cm = "";
//		claves = "";
		
		bi = new BeanItem<PatientsViewer>( this );
	
		CssLayout query = new CssLayout();
		query.setWidth( "100%" );
		query.addComponents( getQueryNombre(), getQueryIdentificador(), getQueryFecha());
		return query;
		
	}

	@Override
	public void onAddRow()
	{
		new ModalNewPaciente( context, Operation.OP_ADD, null, this, "configuracion.pacientes" ).showModalWindow();
	}

	@Override
	public boolean isAddAvailable()
	{
		return true;
	}

	@Override
	public boolean isModifyAvailable()
	{
		return true;
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return true;
	}

	@Override
	public boolean isExtrasAvailable()
	{
		return true;
	}
	
	private static final Component getRow(Component component){
		FormLayout layout = new FormLayout( component );
		layout.setMargin( new MarginInfo( false, true, false, false ) );
		layout.setSpacing( false );
		layout.setWidthUndefined();
		layout.addStyleName( ImedigTheme.FILTER_MARGIN );
		return layout;
	}
	
}
