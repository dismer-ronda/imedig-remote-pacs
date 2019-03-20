package es.pryades.imedig.viewer.components.appointments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FiltrerAddSelect;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.lazy.LazyContainer;
import es.pryades.imedig.cloud.common.lazy.PacienteLazyProvider;
import es.pryades.imedig.cloud.common.lazy.ReferidorLazyProvider;
import es.pryades.imedig.cloud.common.lazy.SearchCriteria;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.CitasManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.RecursosManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.KeyValue;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.actions.UpdateAppointmentPatient;
import es.pryades.imedig.viewer.actions.UpdatePatient;

public class ModalAppointmentDlg extends ModalWindowsCRUD implements ModalParent
{

	private static final long serialVersionUID = -831617884634887703L;

	private static final Logger LOG = Logger.getLogger( ModalAppointmentDlg.class );

	private Recurso recurso;
	private Cita newCita;
	private AppointmentVo vo;

	private FiltrerAddSelect selectPaciente;
	private FiltrerAddSelect selectReferidor;
	private ComboBox comboRecurso;
	private ComboBox comboTipo;
	private DateField dateFieldFecha;
	private ComboBox timeInicio;
	private ComboBox comboBoxDuracion;
	//private ComboBox comboBoxEstado;
	private List<Integer> duracion;
	
	private PacienteLazyProvider pacienteLazyProvider;
	private ReferidorLazyProvider referidorLazyProvider;

	private CitasManager manager;

	private AppointmentEventResource eventResource;

	private Date date;
	private final boolean outOfCalendar ;
	private List<KeyValue<LocalTime, Integer>> freeTimes;

	public ModalAppointmentDlg( ImedigContext ctx, Operation modalOperation, Recurso recurso, Cita oldCita, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, oldCita, right );

		if ( oldCita == null )
			throw new NullPointerException( "Cita nula" );

		this.outOfCalendar = false;
		this.recurso = recurso;
		this.date = Utils.getDateHourFromLong( oldCita.getFecha() );

		init();
		initComponents();
	}

	public ModalAppointmentDlg( ImedigContext ctx, Operation modalOperation, Recurso recurso, Date date, ModalParent parentWindow, String right )
	{
		this( ctx, modalOperation, recurso, date, parentWindow, right, false );
	}
	
	public ModalAppointmentDlg( ImedigContext ctx, Operation modalOperation, Recurso recurso, Date date, ModalParent parentWindow, String right, boolean out )
	{
		super( ctx, parentWindow, modalOperation, null, right );

		if ( date == null )
			throw new NullPointerException( "Fecha nula" );

		this.recurso = recurso;
		this.date = date;
		this.outOfCalendar = out;

		init();
		initComponents();
		if (out){
			newAppointmentOutOfCalendar();
		}
	}

	private void init()
	{
		manager = (CitasManager)IOCManager.getInstanceOf( CitasManager.class );
		eventResource = new AppointmentEventResource( getContext(), recurso );

		freeTimes = eventResource.getFreeTimes( date );
		generarDuracion();
	}

	private void generarDuracion()
	{
		if (recurso == null) return;
		
		duracion = new ArrayList<>();

		for ( int i = 1; i <= 10; i++ )
		{
			duracion.add( recurso.getTiempominimo() * i );
		}
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		setWidth( "800px" );

		vo = toVo( (Cita)orgDto );
		try
		{
			newCita = (Cita)Utils.clone( (Cita)orgDto );
		}
		catch ( Throwable e1 )
		{
			newCita = new Cita();
			newCita.setEstado( Constants.APPOINTMENT_STATUS_PLANING );
		}

		bi = new BeanItem<ImedigDto>( vo );

		selectPaciente = new FiltrerAddSelect( getString( "modalAppointmentDlg.lbPaciente" ) );
		selectPaciente.setWidth( "100%" );
		selectPaciente.setRequired( true );
		pacienteLazyProvider = new PacienteLazyProvider( getContext() );
		LazyContainer dataSourcePaciente = new LazyContainer( Paciente.class, pacienteLazyProvider, new SearchCriteria() );
		dataSourcePaciente.setMinFilterLength( 0 );
		selectPaciente.setItemCaptionPropertyId( "nombreCompletoConIdentificador" );
		selectPaciente.setContainerDataSource( dataSourcePaciente );
		selectPaciente.setPropertyDataSource( bi.getItemProperty( "paciente" ) );
		selectPaciente.getButtonAdd().addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 6646662136591844468L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				new PacienteAppointmentDlg( context, Operation.OP_ADD, null, ModalAppointmentDlg.this, "configuracion.pacientes.adicionar" ).showModalWindow();
			}
		} );
		
		if (!getContext().hasRight( "configuracion.pacientes.adicionar" )) 
			selectPaciente.getButtonAdd().setEnabled( false );

		if (recurso == null){
			comboRecurso = new ComboBox( getString( "modalAppointmentDlg.lbRecurso.equipo" ));
		}else if ( Constants.TYPE_IMAGING_DEVICE.equals( recurso.getTipo() ) )
		{
			comboRecurso = new ComboBox( getString( "modalAppointmentDlg.lbRecurso.equipo" ));
		}
		else
		{
			comboRecurso = new ComboBox( getString( "modalAppointmentDlg.lbRecurso.consulta" ) );
		}
		comboRecurso.setWidth( "100%" );
		comboRecurso.setPropertyDataSource( bi.getItemProperty( "recurso" ) );
		comboRecurso.setNullSelectionAllowed( false );
		comboRecurso.setNewItemsAllowed( false );
		comboRecurso.setFilteringMode( FilteringMode.CONTAINS );
		comboRecurso.setRequired( true );
		fillRecursos(Constants.TYPE_IMAGING_DEVICE);
		if (recurso != null){
			comboRecurso.setReadOnly( true );
		}
		comboRecurso.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -5261438380912080072L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				eventResource.setResource( vo.getRecurso() );
				generarDuracion();
				fillDuracion();
			}
		} );

		selectReferidor = new FiltrerAddSelect( getString( "modalAppointmentDlg.lbReferidor" ) );
		selectReferidor.setWidth( "100%" );
		selectReferidor.setRequired( true );
		selectReferidor.setVisibleAdd( false );

		referidorLazyProvider = new ReferidorLazyProvider( getContext() );
		LazyContainer dataSourceReferidor = new LazyContainer( Usuario.class, referidorLazyProvider, new SearchCriteria() );
		dataSourceReferidor.setMinFilterLength( 0 );
		selectReferidor.setItemCaptionPropertyId( "nombreCompleto" );
		selectReferidor.setContainerDataSource( dataSourceReferidor );
		selectReferidor.setPropertyDataSource( bi.getItemProperty( "referidor" ) );

		comboTipo = new ComboBox( getString( "modalAppointmentDlg.lbTipo" ) );
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
				if (outOfCalendar){
					fillDuracionOutOfCalendar();
				}else{
					fillDuracion();
				}
				vo.setDuracion( getProximaDuracion( vo.getTipo().getDuracion() ) );
				
				if (!comboBoxDuracion.isReadOnly()){
					comboBoxDuracion.markAsDirty();
				}else{
					comboBoxDuracion.setReadOnly( false );
					comboBoxDuracion.markAsDirty();
					comboBoxDuracion.setReadOnly( true );
				}
			}

		} );

		dateFieldFecha = new DateField( getString( "modalAppointmentDlg.lbFecha" ), bi.getItemProperty( "fecha" ) );
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
				updateHoraInicio(true);
				//fillDuracion();
			}
		} );
		
		if (orgDto == null){
			dateFieldFecha.setRangeStart( new Date() );
		}else{
			Date today = new Date();
			if (today.after( vo.getFecha() )){
				dateFieldFecha.setRangeStart( vo.getFecha() );
			}else{
				dateFieldFecha.setRangeStart( today );
			}
		}

		timeInicio = new ComboBox();
		timeInicio.setRequired( true );
		timeInicio.setWidth( "95px" );
		timeInicio.setPropertyDataSource( bi.getItemProperty( "fechainicio" ) );
		timeInicio.setNullSelectionAllowed( false );
		fillTimeInicio(false);
		timeInicio.setImmediate( true );
		timeInicio.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -1750614759248144130L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				actualizaDuracion();
			}
		} );
		

//		errorLabel = new Label( getContext().getString( "modalAppointmentDlg.error.no.time" ) );
//		errorLabel.addStyleName( "label_error" );
//		errorLabel.setVisible( false );
//		errorLabel.setWidth( "290px" );

		comboBoxDuracion = new ComboBox( getString( "modalAppointmentDlg.lbDuracion" ) );
		comboBoxDuracion.setWidth( "125px" );
		comboBoxDuracion.setNullSelectionAllowed( false );
		comboBoxDuracion.setNewItemsAllowed( false );
		fillDuracion();
		comboBoxDuracion.setPropertyDataSource( bi.getItemProperty( "duracion" ) );
		comboBoxDuracion.setImmediate( true );
		FormLayout formLayout = new FormLayout( comboBoxDuracion );
		formLayout.setMargin( false );
		
		HorizontalLayout layoutTime = new HorizontalLayout( timeInicio, formLayout );
		layoutTime.setCaption( getString( "modalAppointmentDlg.lbHoraInicio" ) );
		layoutTime.setSpacing( true );

//		comboBoxEstado = new ComboBox( getString( "modalAppointmentDlg.lbEstado" ) );
//		comboBoxEstado.setNullSelectionAllowed( false );
//		comboBoxEstado.setNewItemsAllowed( false );
//		fillEstados( comboBoxEstado );
//		comboBoxEstado.setPropertyDataSource( bi.getItemProperty( "estado" ) );
//		if ( orgDto == null || !Utils.isToday(vo.getFecha()))
//		{
//			comboBoxEstado.setVisible( false );
//		}
		
		FormLayout layout = new FormLayout( selectPaciente, comboRecurso, selectReferidor, comboTipo, dateFieldFecha, layoutTime);
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setWidth( "100%" );

		componentsContainer.addComponent( layout );

		if ( orgDto != null )
		{
			selectPaciente.setReadOnly( true );
			if ( newCita.getEstado() == Constants.APPOINTMENT_STATUS_PLANING )
			{
				addCancelarCita();
			}
			else
			{
				if (newCita.getEstado() == Constants.APPOINTMENT_STATUS_ENDED){
					bttnOperacion.setVisible( false );
					bttnCancelar.setCaption( getString( "words.close" ) );
				}
				readOnlyAll();
//				if (newCita.getEstado() == Constants.APPOINTMENT_STATUS_EXECUTING){
//					comboBoxEstado.setReadOnly( false );
//				}
			}
		}
		
		selectPaciente.focus();
	}
	
	private void newAppointmentOutOfCalendar()
	{
		dateFieldFecha.setReadOnly( true );
		timeInicio.getContainerDataSource().removeAllItems();
		KeyValue<LocalTime, Integer> time = new KeyValue<LocalTime, Integer>( LocalTime.now(), recurso.getTiempominimo() );
		timeInicio.addItem( time );
		timeInicio.setItemCaption( time, time.getKey().toString( "HH:mm" ) );
		vo.setFechainicio( time );
		timeInicio.markAsDirty();
		timeInicio.setReadOnly( true );
		comboBoxDuracion.setReadOnly( true );
	}


	
	private void actualizaDuracion(){
		fillDuracion();
		if ( vo.getTipo() == null) return;
		
		vo.setDuracion( getProximaDuracion( vo.getTipo().getDuracion() ) );
		comboBoxDuracion.markAsDirty();
	}

	private void fillRecursos(Integer type)
	{
		if (recurso != null){
			comboRecurso.addItem( recurso );
			return;
		}
		
		try
		{
			Recurso query = new Recurso();
			query.setTipo( type );
			RecursosManager manager = (RecursosManager)IOCManager.getInstanceOf( RecursosManager.class );
			List<Recurso> recursos;
			recursos = manager.getRows( getContext(), query );
			for ( Recurso recurso : recursos )
			{
				comboRecurso.addItem( recurso );
			}
		}
		catch ( Throwable e )
		{
		}
	}

	private void fillTimeInicio(boolean cambioDeFecha)
	{
		timeInicio.getContainerDataSource().removeAllItems();

		LocalTime now = null;
		if (Utils.isToday( vo.getFecha() )){
			now = LocalTime.now();
		}else{
			if (Utils.head( freeTimes ) != null){
				now = Utils.head( freeTimes ).getKey();
			}
		}
		
		for ( KeyValue<LocalTime, Integer> free : freeTimes )
		{
			if (now != null && now.isAfter( free.getKey() )) continue;
			
			timeInicio.addItem( free );
			timeInicio.setItemCaption( free, free.getKey().toString( "HH:mm" ) );
		}
		
		if (!cambioDeFecha && orgDto != null && vo.getFechainicio() != null && timeInicio.getItem( vo.getFechainicio() ) == null){
			KeyValue<LocalTime, Integer> free = new KeyValue<>( vo.getFechainicio().getKey(), 0 );
			timeInicio.addItem( free );
			timeInicio.setItemCaption( free, free.getKey().toString( "HH:mm" ) );
		}
	}

	private void readOnlyAll()
	{
		selectPaciente.setReadOnly( true );
		selectReferidor.setReadOnly( true );
		comboRecurso.setReadOnly( true );
		comboTipo.setReadOnly( true );
		dateFieldFecha.setReadOnly( true );
		timeInicio.setReadOnly( true );
		comboBoxDuracion.setReadOnly( true );
		//comboBoxEstado.setReadOnly( true );
	}

	private void fillEstados( ComboBox comboBox )
	{
		if (newCita.getEstado() == Constants.APPOINTMENT_STATUS_PLANING){
			comboBox.addItem( Constants.APPOINTMENT_STATUS_PLANING );
			comboBox.setItemCaption( Constants.APPOINTMENT_STATUS_PLANING, getString( "appointment.status." + Constants.APPOINTMENT_STATUS_PLANING ) );
		}
		comboBox.addItem( Constants.APPOINTMENT_STATUS_EXECUTING );
		comboBox.setItemCaption( Constants.APPOINTMENT_STATUS_EXECUTING, getString( "appointment.status." + Constants.APPOINTMENT_STATUS_EXECUTING ) );
		comboBox.addItem( Constants.APPOINTMENT_STATUS_ENDED );
		comboBox.setItemCaption( Constants.APPOINTMENT_STATUS_ENDED, getString( "appointment.status." + Constants.APPOINTMENT_STATUS_ENDED ) );
	}

	private void addCancelarCita()
	{
		Button button = new Button( getString( "modalAppointmentDlg.wndCaption.cancel" ) );
		button.setImmediate( true );
		button.addStyleName( ValoTheme.BUTTON_DANGER );
		operacionesContainer.addComponent( button, 0 );
		button.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -5225985568656917973L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				ConfirmDialog.show( (UI)getContext().getData( "Application" ), getString( "modalAppointmentDlg.confirm.cancel" ), new ConfirmDialog.Listener()
				{
					private static final long serialVersionUID = -5389849699030665587L;

					public void onClose( ConfirmDialog dialog )
					{
						if ( dialog.isConfirmed() )
						{
							if ( onDelete() )
							{
								closeModalWindow( true, true );
							}
						}
					}
				} );
			}
		} );
	}

	private Integer getProximaDuracion( Integer min )
	{
		Collection<Object> values = (Collection<Object>)comboBoxDuracion.getContainerDataSource().getItemIds();
		
		if (values == null || values.isEmpty()) return null;
		
		for ( Object item : values )
		{
			if ( min > (Integer)item )
				continue;
			return (Integer)item;
		}
		
		return (Integer)values.iterator().next();
	}

	private void fillDuracion()
	{
		comboBoxDuracion.getContainerDataSource().removeAllItems();

		Integer maxDuracion = duracion.get( duracion.size()-1 );
		
		if (vo.getFechainicio()== null){
			if (vo.getTipo() != null){
				Notification.show( getContext().getString( "modalAppointmentDlg.error.no.time"), Notification.Type.ERROR_MESSAGE );
			}
			return;
		}
		
		maxDuracion = vo.getFechainicio().getValue(); 
				
		for ( Integer item : duracion )
		{
			if (item > maxDuracion) continue;
			
			if ( vo.getTipo() == null )
			{
				addMinute( comboBoxDuracion, item );
			}
			else if ( vo.getTipo().getDuracion() <= item )
			{
				addMinute( comboBoxDuracion, item );
			}
		}
		
		if (vo.getTipo() != null && comboBoxDuracion.getContainerDataSource().getItemIds().isEmpty()){
			Notification.show( getContext().getString( "modalAppointmentDlg.error.no.time"), Notification.Type.ERROR_MESSAGE );
		}
	}
	
	private void fillDuracionOutOfCalendar()
	{
		comboBoxDuracion.getContainerDataSource().removeAllItems();

		for ( Integer item : duracion )
		{
			if ( vo.getTipo() == null )
			{
				addMinute( comboBoxDuracion, item );
			}
			else if ( vo.getTipo().getDuracion() <= item )
			{
				addMinute( comboBoxDuracion, item );
			}
		}
		
		if (vo.getTipo() != null && comboBoxDuracion.getContainerDataSource().getItemIds().isEmpty()){
			Notification.show( getContext().getString( "modalAppointmentDlg.error.no.time"), Notification.Type.ERROR_MESSAGE );
		}
	}

	private static void addMinute( ComboBox comboBox, Integer min )
	{
		String caption = Utils.elapseTimeFromMin( min );
		comboBox.addItem( min );
		comboBox.setItemCaption( min, caption );
	}

	private AppointmentVo toVo( Cita cita )
	{
		AppointmentVo result = new AppointmentVo();

		result.setRecurso( recurso );
		if ( cita == null )
		{
			result.setFecha( date );
			result.setFechainicio( getFreeTime( date ) );
			return result;
		}

		result.setFecha( Utils.getDateHourFromLong( cita.getFecha() ) );
		result.setFechainicio( addFreeTime( cita ) );
		result.setDuracion( calDuracion( Utils.getDateHourFromLong( cita.getFecha() ), Utils.getDateHourFromLong( cita.getFechafin() ) ) );
		result.setEstado( cita.getEstado() );
		try
		{
			TiposEstudiosManager tiposEstudiosManager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class );
			result.setTipo( (TipoEstudio)tiposEstudiosManager.getRow( getContext(), cita.getTipo() ) );

			PacientesManager pacientesManager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class );
			result.setPaciente( (Paciente)pacientesManager.getRow( getContext(), cita.getPaciente() ) );

			UsuariosManager usuariosManager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );
			result.setReferidor( (Usuario)usuariosManager.getRow( getContext(), cita.getReferidor() ) );
		}
		catch ( Throwable t )
		{
			LOG.error( "Error creando vo", t );
		}

		return result;
	}

	private KeyValue<LocalTime, Integer> addFreeTime( Cita cita )
	{
		LocalTime start = Utils.getTime( Utils.getDateHourFromLong( cita.getFecha() ) );
		LocalTime end = Utils.getTime( Utils.getDateHourFromLong( cita.getFechafin() ) );

		KeyValue<LocalTime, Integer> result = new KeyValue<LocalTime, Integer>( start, Minutes.minutesBetween( start, end ).getMinutes() );

		freeTimes.add( result );
		Comparator<KeyValue<LocalTime, Integer>> comparator = new Comparator<KeyValue<LocalTime, Integer>>()
		{

			@Override
			public int compare( KeyValue<LocalTime, Integer> o1, KeyValue<LocalTime, Integer> o2 )
			{
				return o1.getKey().compareTo( o2.getKey() );
			}
		};

		Collections.sort( freeTimes, comparator );
		
		Integer index = freeTimes.indexOf( result );
		if (index < freeTimes.size()-1){
			KeyValue<LocalTime, Integer> temp = freeTimes.get( index+1 );
			
			if (isNextFreeTime( result, temp )){
				result.setValue( result.getValue() + temp.getValue() );
				generateNextFreeTime(result, temp);
				Collections.sort( freeTimes, comparator );
			}
		}

		return result;
	}
	
	private void generateNextFreeTime( KeyValue<LocalTime, Integer> begin, KeyValue<LocalTime, Integer> end )
	{
		LocalTime from = begin.getKey().plusMinutes( recurso.getTiempominimo() );
		
		LocalTime to = end.getKey();
		
		while ( from.isBefore( to ) )
		{
			freeTimes.add( new KeyValue<LocalTime, Integer>( from, Minutes.minutesBetween( from, to ).getMinutes() + end.getValue() ) );
			from = from.plusMinutes( recurso.getTiempominimo() );
		}
		
	}

	private boolean isNextFreeTime(KeyValue<LocalTime, Integer> free, KeyValue<LocalTime, Integer> next){
		
		LocalTime time = free.getKey().plusMinutes( free.getValue() );
		
		return time.equals( next.getKey() );
		
	}

	private KeyValue<LocalTime, Integer> getFreeTime( Date date )
	{

		LocalTime time = Utils.getTime( date );
		for ( KeyValue<LocalTime, Integer> free : freeTimes )
		{
			if ( free.getKey().equals( time ) )
				return free;
		}

		return null;
	}

	private Integer calDuracion( Date from, Date to )
	{
		Long mili = to.getTime() - from.getTime(); // diferencia de tiempo en
													// milisegundos
		Long seg = mili / 1000;// llevarlo a segundo
		Long min = seg / 60; // llevarlo a minutos

		return min.intValue();
	}

	private void updateHoraInicio(boolean cambioDeFecha)
	{
		freeTimes = eventResource.getFreeTimes( vo.getFecha() );
		KeyValue<LocalTime, Integer> actual = vo.getFechainicio();
		
		fillTimeInicio(cambioDeFecha);
		vo.setFechainicio( null );
		if ( freeTimes.isEmpty() )
		{
			timeInicio.markAsDirty();
			return;
		}

		if ( orgDto == null )
		{
			vo.setFechainicio( freeTimes.get( 0 ) );
		}
		else
		{
			if ( Utils.isSameDay( vo.getFecha(), Utils.getDateHourFromLong( ((Cita)orgDto).getFecha() ) ) )
			{
				vo.setFechainicio( addFreeTime( (Cita)orgDto ) );
			}
			else
			{
				if (freeTimes.contains( actual )){
					vo.setFechainicio( freeTimes.get( freeTimes.indexOf( actual ) ) );
					actualizaDuracion();
				}else{
					vo.setFechainicio( freeTimes.get( 0 ) );
				}
			}
		}
		timeInicio.setValue( vo.getFechainicio() );
		timeInicio.markAsDirty();
		
	}

	private void toDto()
	{
		newCita.setPaciente( vo.getPaciente().getId() );
		newCita.setRecurso( recurso.getId() );
		newCita.setTipo( vo.getTipo().getId() );
		newCita.setReferidor( vo.getReferidor().getId() );
		newCita.setFecha( Utils.getDateAsLong( Utils.getDateWithTime( vo.getFecha(), vo.getFechainicio().getKey() ) ) );
		
		newCita.setEstado( vo.getEstado() );

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( Utils.getDateWithTime( vo.getFecha(), vo.getFechainicio().getKey() ) );
		calendar.add( Calendar.MINUTE, vo.getDuracion() );
		newCita.setFechafin( Utils.getDateAsLong( calendar.getTime() ) );
		newCita.setUid( "UID" );
	}

	private void fillTipoEstudio()
	{
		TipoEstudio query = new TipoEstudio();
		query.setTipo( recurso.getTipo() );
		query.setModalidad( recurso.getModalidad() );
		TiposEstudiosManager manager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class );

		try
		{
			for ( TipoEstudio item : (List<TipoEstudio>)manager.getRows( getContext(), query ) )
			{
				comboTipo.addItem( item );
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			if ( !isValid() )
				return false;

			toDto();
			
			if (outOfCalendar){
				ConfirmDialog.show( UI.getCurrent() , getString( "modalAppointmentDlg.confirm.appointment.worklist.outcalendar" ), new ConfirmDialog.Listener()
				{
					private static final long serialVersionUID = -1649199248924829162L;

					public void onClose( ConfirmDialog dialog )
					{
						if ( dialog.isConfirmed() )
						{
							try
							{
								newCita.setEstado( Constants.APPOINTMENT_STATUS_EXECUTING );
								finalizarCitasPreviasEnEjecucion();
								getContext().sendAction( new UpdateAppointmentPatient( this, recurso ) );
								manager.transferAppointmentWorklist( getContext(), newCita, recurso );
							}
							catch ( Throwable e )
							{
								showErrorMessage( new ImedigException( e, LOG, ImedigException.PACS_WORKLIST_ERROR ) );
							}
						}
					}
				} );
				
			}else{
				newCita.setEstado( Constants.APPOINTMENT_STATUS_PLANING );

				actualizar( null, newCita, false );
				//manager.setRow( getContext(), null, newCita );
				//getContext().sendAction( new UpdateAppointmentPatient( this, recurso ) );
			}

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private boolean isValid() throws ImedigException
	{
		validateBeginTime();

		if ( !isValidRequired() )
		{
			throw new ImedigException( new RuntimeException( "Parámetros incorrectos" ), LOG, ImedigException.NOT_NULL_VIOLATION );
		}

		Date today = new Date();
		if (orgDto == null){
			if (!outOfCalendar && today.after( vo.getFecha() )){
				Notification.show( getString( "modalAppointmentDlg.error.today" ), Notification.Type.ERROR_MESSAGE );
				return false;
			}
		}else{
			if (vo.getEstado() == Constants.APPOINTMENT_STATUS_PLANING && today.after( vo.getFecha() )){
				Notification.show( getString( "modalAppointmentDlg.error.today" ), Notification.Type.ERROR_MESSAGE );
				return false;
			}
		}

		return true;
	}
	
	private Date getToday(){
		if (outOfCalendar)
			return Utils.getDate( new Date(), Calendar.MINUTE, -5 );
		
		return new Date();
	}

	private void validateBeginTime() throws ImedigException
	{
		try
		{
			if (outOfCalendar){
				if (!timeInicio.isValid()){
					throw new RuntimeException();
				}
			}else{
				if ( !timeInicio.isValid() || comboBoxDuracion.getValue() == null )
				{
					throw new RuntimeException();
				}
			}
		}
		catch ( Throwable t )
		{
			throw new ImedigException( new RuntimeException( "Parámetros incorrectos" ), LOG, ImedigException.NOT_NULL_VIOLATION );
		}
	}

	private boolean isValidRequired()
	{
		return vo.getPaciente() != null && vo.getReferidor() != null && vo.getTipo() != null && vo.getRecurso() != null && vo.getFecha() != null && vo.getFechainicio() != null && vo.getDuracion() != null;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			if ( !isValid() )
				return false;

			toDto();

			if ( isCambioEstado() )
			{
				if (Constants.TYPE_IMAGING_DEVICE.equals( recurso.getTipo() ) && newCita.getEstado() == Constants.APPOINTMENT_STATUS_EXECUTING){
					ConfirmDialog.show( UI.getCurrent() , getString( "modalAppointmentDlg.confirm.appointment.worklist" ), new ConfirmDialog.Listener()
					{
						private static final long serialVersionUID = -6788803735853446128L;

						public void onClose( ConfirmDialog dialog )
						{
							if ( dialog.isConfirmed() )
							{
								try
								{
									actualizar( (Cita)orgDto, newCita, true );
									manager.transferAppointmentWorklist( getContext(), newCita, recurso );
									//manager.setRow( getContext(), (Cita)orgDto, newCita );
									//finalizarCitasPreviasEnEjecucion();
									//getContext().sendAction( new UpdateAppointmentPatient( this ) );
								}
								catch ( Throwable e )
								{
									showErrorMessage( new ImedigException( e, LOG, ImedigException.PACS_WORKLIST_ERROR ) );
								}
							}
						}
					} );
				}else{
					//manager.setRow( getContext(), (Cita)orgDto, newCita );
					actualizar( (Cita)orgDto, newCita, false );
				}
				
			}else{
				//manager.setRow( getContext(), (Cita)orgDto, newCita );
				actualizar( (Cita)orgDto, newCita, false );
			}
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
	
	private void actualizar(Cita old, Cita newrow, boolean finalizar) throws Throwable{
		manager.setRow( getContext(), old, newrow );
		
		if (finalizar) finalizarCitasPreviasEnEjecucion();
		
		getContext().sendAction( new UpdateAppointmentPatient( this, recurso ) );
	}
	
	private boolean isCambioEstado(){
		if (orgDto == null) return false;
		
		if (!((Cita)orgDto).getEstado().equals( newCita.getEstado() ))
			return true;
		
		return false;
	}

	private void finalizarCitasPreviasEnEjecucion() throws Throwable
	{
		CitaQuery query = new CitaQuery();
		query.setRecurso( recurso.getId() );
		query.setEstado( Constants.APPOINTMENT_STATUS_EXECUTING );
		List<Cita> citas = manager.getRows( getContext(), query );
		for ( Cita cita : citas )
		{
			if ( cita.getId().equals( newCita.getId() ) )
				continue;

			Cita c = Utils.clone2( cita );
			c.setEstado( Constants.APPOINTMENT_STATUS_ENDED );
			manager.setRow( getContext(), cita, c );
		}
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			manager.delRow( getContext(), orgDto );
			getContext().sendAction( new UpdateAppointmentPatient( this, recurso ) );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected void defaultFocus()
	{
		
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalAppointmentDlg";
	}
	
	void updatePaciente(Paciente paciente){
		setPaciente( paciente );
		getContext().sendAction( new UpdatePatient( this ) );
	}

	public void setPaciente(Paciente paciente){
		vo.setPaciente( paciente );
		selectPaciente.getComboBox().markAsDirty();
		
		selectPaciente.setReadOnly( true );
	}

	@Override
	public void refreshVisibleContent()
	{
		
	}
}
