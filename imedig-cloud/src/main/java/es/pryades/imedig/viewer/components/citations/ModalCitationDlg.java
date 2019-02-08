package es.pryades.imedig.viewer.components.citations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FiltrerAddSelect;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.lazy.LazyContainer;
import es.pryades.imedig.cloud.common.lazy.PacienteLazyProvider;
import es.pryades.imedig.cloud.common.lazy.ReferidorLazyProvider;
import es.pryades.imedig.cloud.common.lazy.SearchCriteria;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

public class ModalCitationDlg extends ModalWindowsCRUD
{

	private static final long serialVersionUID = -831617884634887703L;
	
	private static final Logger LOG = Logger.getLogger( ModalCitationDlg.class );

	private Instalacion instalacion;
	private Estudio newEstudio;
	private CitationVo vo;
	
	private FiltrerAddSelect selectPaciente;
	private FiltrerAddSelect selectReferidor;
	private TextField editInstalacion;
	private ComboBox comboTipo;
	private DateField dateFieldFecha;
	private TimeField timeInicio;
	private TimeField timeFin;
	
	private PacienteLazyProvider pacienteLazyProvider;
	private ReferidorLazyProvider referidorLazyProvider;
	
	private EstudiosManager manager;
	
	public ModalCitationDlg(ImedigContext ctx, Operation modalOperation, Instalacion instalacion, Estudio oldEstudio, ModalParent parentWindow, String right){
		super( ctx, parentWindow, modalOperation, oldEstudio, right );
		
		this.instalacion = instalacion;
		
		manager = (EstudiosManager)IOCManager.getInstanceOf( EstudiosManager.class );
		
		setWidth( "800px" );
		initComponents();
	}
	
	@Override
	public void initComponents()
	{
		super.initComponents();

		vo = toVo( (Estudio) orgDto );
		try
		{
			newEstudio = (Estudio) Utils.clone( (Estudio) orgDto );
		}
		catch ( Throwable e1 )
		{
			newEstudio = new Estudio();
		}

		bi = new BeanItem<ImedigDto>( vo );

		selectPaciente = new FiltrerAddSelect( getContext().getString( "modalCitationDlg.lbPaciente" ) );
		selectPaciente.setWidth( "100%" );
		selectPaciente.setRequired( true );
		
		pacienteLazyProvider = new PacienteLazyProvider( getContext() );
		LazyContainer dataSourcePaciente = new LazyContainer(Paciente.class, pacienteLazyProvider, new SearchCriteria());
		dataSourcePaciente.setMinFilterLength(0);
		selectPaciente.setItemCaptionPropertyId("nombreCompletoConIdentificador");
		selectPaciente.setContainerDataSource(dataSourcePaciente);
		selectPaciente.setPropertyDataSource( bi.getItemProperty( "paciente" ) );
				
		if (Constants.TYPE_IMAGING_DEVICE.equals( instalacion.getTipo() )){
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.equipo" ));
		}else{
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.consulta" ));
		}
		editInstalacion.setValue( instalacion.getNombre() );
		editInstalacion.setReadOnly( true );
		editInstalacion.setWidth( "100%" );
		
		selectReferidor = new FiltrerAddSelect( getContext().getString( "modalCitationDlg.lbReferidor" ) );
		selectReferidor.setWidth( "100%" );
		selectReferidor.setRequired( true );
		selectReferidor.setVisibleAdd( false );
		
		referidorLazyProvider = new ReferidorLazyProvider( getContext() );
		LazyContainer dataSourceReferidor = new LazyContainer(Usuario.class, referidorLazyProvider, new SearchCriteria());
		dataSourceReferidor.setMinFilterLength(0);
		selectReferidor.setItemCaptionPropertyId("nombreCompleto");
		selectReferidor.setContainerDataSource(dataSourceReferidor);
		selectReferidor.setPropertyDataSource( bi.getItemProperty( "referidor" ) );
		
		comboTipo = new ComboBox( getContext().getString( "modalCitationDlg.lbTipo" ) );
		comboTipo.setWidth( "70%" );
		comboTipo.setFilteringMode( FilteringMode.CONTAINS );
		comboTipo.setNullSelectionAllowed( false );
		comboTipo.setNewItemsAllowed( false );
		comboTipo.setRequired( true );
		fillTipoEstudio();
		comboTipo.setPropertyDataSource( bi.getItemProperty( "tipo" ) );
		comboTipo.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = 4354444023011104900L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				updateFecha();
			}
		} );
		
		dateFieldFecha = new DateField( getContext().getString( "modalCitationDlg.lbFecha" ), bi.getItemProperty( "fecha" ));
		dateFieldFecha.setDateFormat( "dd/MM/yyyy" );
		dateFieldFecha.setResolution( Resolution.DAY );
		dateFieldFecha.setRequired( true );
		dateFieldFecha.setImmediate( true );
		dateFieldFecha.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = 4997912190631165344L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				updateFecha();
			}
		} );
		
		
		timeInicio = new TimeField( getContext().getString( "modalCitationDlg.lbHoraInicio" ) );
		timeInicio.set24HourFormat( true );
		timeInicio.setRequired( true );
		timeInicio.setImmediate( true );
		timeInicio.setPropertyDataSource( bi.getItemProperty( "fechainicio" ) );
		timeInicio.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -5095557634162961055L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				updateFechaInicio();
			}
		} );
		
		timeFin = new TimeField( getContext().getString( "modalCitationDlg.lbHoraFin" ) );
		timeFin.set24HourFormat( true );
		timeFin.setRequired( true );
		timeFin.setPropertyDataSource( bi.getItemProperty( "fechafin" ) );
		timeFin.setImmediate( true );
		
		FormLayout layout = new FormLayout( selectPaciente, editInstalacion, selectReferidor, comboTipo, dateFieldFecha, timeInicio, timeFin );
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setWidth( "100%" );
	
		componentsContainer.addComponent( layout );
	}

	private CitationVo toVo(Estudio estudio)
	{
		CitationVo result = new CitationVo();
		
		if (estudio == null) return result;
		
		result.setFecha( Utils.getDateHourFromLong( estudio.getFecha() ) );
		result.setFechainicio( Utils.getDateHourFromLong( estudio.getFecha() ) );
		result.setFechafin( Utils.getDateHourFromLong( estudio.getFechafin() ) );
		
		try
		{
			TiposEstudiosManager tiposEstudiosManager = (TiposEstudiosManager) IOCManager.getInstanceOf( TiposEstudiosManager.class );
			result.setTipo( (TipoEstudio)tiposEstudiosManager.getRow( getContext(), estudio.getTipo() )  );
			
			PacientesManager pacientesManager = (PacientesManager) IOCManager.getInstanceOf( PacientesManager.class );
			result.setPaciente( (Paciente)pacientesManager.getRow( getContext(), estudio.getPaciente() ));

			UsuariosManager usuariosManager = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );
			result.setReferidor( (Usuario)usuariosManager.getRow( getContext(), estudio.getReferidor()));
		}
		catch ( Throwable t )
		{
			LOG.error( "Error creando vo", t );
		}
		
		return result;

	}
	
	private void updateFecha(){
		vo.setFechainicio( dateFieldFecha.getValue() );
		timeInicio.markAsDirty();
		if (vo.getTipo() == null) return;
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( vo.getFechainicio() );
		calendar.add( Calendar.MINUTE, vo.getTipo().getDuracion() );
		vo.setFechafin( calendar.getTime() );
		//timeFin.add( Calendar.MINUTE, vo.getTipo().getDuracion() );
		//dateFieldFecha.getValue();
		timeFin.markAsDirty();
	}
	
	private void updateFechaInicio(){
		if (vo.getTipo() == null) return;
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( vo.getFechainicio() );
		calendar.add( Calendar.MINUTE, vo.getTipo().getDuracion() );
		vo.setFechafin( calendar.getTime() );
		timeFin.markAsDirty();
	}
	
	private void toDto()
	{
		newEstudio.setPaciente( vo.getPaciente().getId() );
		newEstudio.setInstalacion( instalacion.getId() );
		newEstudio.setTipo( vo.getTipo().getId() );
		newEstudio.setReferidor( vo.getReferidor().getId() );
		newEstudio.setFecha( Utils.getDateAsLong( vo.getFechainicio() ) );
		newEstudio.setFechafin( Utils.getDateAsLong( vo.getFechafin() ) );
		newEstudio.setUid( "UID" );
	}

	private void fillTipoEstudio()
	{
		TipoEstudio query = new TipoEstudio();
		query.setTipo( instalacion.getTipo() );
		TiposEstudiosManager manager = (TiposEstudiosManager) IOCManager.getInstanceOf( TiposEstudiosManager.class );
		
		try
		{
			for ( TipoEstudio item : (List<TipoEstudio>)manager.getRows( getContext(), query ) )
			{
				comboTipo.addItem( item );
				//comboTipo.setItemCaption( item, item.getNombre() );
				
			}
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			if (!isValid()) return false;
			
			toDto();
			
			manager.setRow( getContext(), null, newEstudio );
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private boolean isValid() throws ImedigException{
		
		if (!isValidRequired()){
			throw new ImedigException( new RuntimeException( "Parámetros incorrectos" ), LOG, ImedigException.NOT_NULL_VIOLATION );
		}
		
		if (!vo.getFechainicio().before( vo.getFechafin() )){
			Notification.show( getContext().getString( "modalCitationDlg.error.rango.horas" ), Notification.Type.ERROR_MESSAGE );
			return false;
		}
		
		Date today = new Date();
		if (!today.before( vo.getFechafin() )){
			Notification.show( getContext().getString( "modalCitationDlg.error.today" ), Notification.Type.ERROR_MESSAGE );
			return false;
		}
		
		return true;
	}
	
	private boolean isValidRequired(){
		return     vo.getPaciente() != null 
				&& vo.getReferidor() != null
				&& vo.getTipo() != null
				&& vo.getFecha() != null
				&& vo.getFechafin() != null
				&& vo.getFechafin() != null;
	}
	
	@Override
	protected boolean onModify()
	{
		return true;
	}

	@Override
	protected boolean onDelete()
	{
		return false;
	}

	@Override
	protected void defaultFocus()
	{
		// TODO Auto-generated method stub
		
	}
	
	public void setDate(Date date){
		dateFieldFecha.setValue( date );
		timeInicio.setValue( date );
		timeFin.setValue( date );
	}
	
	@Override
	protected String getWindowResourceKey()
	{
		return "modalCitationDlg";
	}
}
