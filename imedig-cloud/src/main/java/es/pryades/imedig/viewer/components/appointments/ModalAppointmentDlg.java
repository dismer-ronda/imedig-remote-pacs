package es.pryades.imedig.viewer.components.appointments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FiltrerAddSelect;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.TimeField2;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.lazy.LazyContainer;
import es.pryades.imedig.cloud.common.lazy.PacienteLazyProvider;
import es.pryades.imedig.cloud.common.lazy.ReferidorLazyProvider;
import es.pryades.imedig.cloud.common.lazy.SearchCriteria;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.CitasManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

public class ModalAppointmentDlg extends ModalWindowsCRUD
{

	private static final long serialVersionUID = -831617884634887703L;

	private static final Logger LOG = Logger.getLogger( ModalAppointmentDlg.class );

	private Instalacion instalacion;
	private Cita newCita;
	private AppointmentVo vo;

	private FiltrerAddSelect selectPaciente;
	private FiltrerAddSelect selectReferidor;
	private TextField editInstalacion;
	private ComboBox comboTipo;
	private DateField dateFieldFecha;
	private TimeField2 timeInicio;
	private ComboBox comboBoxDuracion;
	private List<Integer> duracion;

	private PacienteLazyProvider pacienteLazyProvider;
	private ReferidorLazyProvider referidorLazyProvider;

	private CitasManager manager;

	public ModalAppointmentDlg( ImedigContext ctx, Operation modalOperation, Instalacion instalacion, Cita oldCita, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, oldCita, right );

		this.instalacion = instalacion;

		manager = (CitasManager)IOCManager.getInstanceOf( CitasManager.class );

		setWidth( "800px" );
		generarDuracion();

		initComponents();
	}

	private void generarDuracion()
	{
		duracion = new ArrayList<>();

		for ( int i = 1; i <= 10; i++ )
		{
			duracion.add( instalacion.getTiempominimo() * i );
		}
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		vo = toVo( (Cita)orgDto );
		try
		{
			newCita = (Cita)Utils.clone( (Cita)orgDto );
		}
		catch ( Throwable e1 )
		{
			newCita = new Cita();
		}

		bi = new BeanItem<ImedigDto>( vo );

		selectPaciente = new FiltrerAddSelect( getContext().getString( "modalAppointmentDlg.lbPaciente" ) );
		selectPaciente.setWidth( "100%" );
		selectPaciente.setRequired( true );
		selectPaciente.getButtonAdd().setVisible( false );

		pacienteLazyProvider = new PacienteLazyProvider( getContext() );
		LazyContainer dataSourcePaciente = new LazyContainer( Paciente.class, pacienteLazyProvider, new SearchCriteria() );
		dataSourcePaciente.setMinFilterLength( 0 );
		selectPaciente.setItemCaptionPropertyId( "nombreCompletoConIdentificador" );
		selectPaciente.setContainerDataSource( dataSourcePaciente );
		selectPaciente.setPropertyDataSource( bi.getItemProperty( "paciente" ) );

		if ( Constants.TYPE_IMAGING_DEVICE.equals( instalacion.getTipo() ) )
		{
			editInstalacion = new TextField( getContext().getString( "modalAppointmentDlg.lbInstalacion.equipo" ) );
		}
		else
		{
			editInstalacion = new TextField( getContext().getString( "modalAppointmentDlg.lbInstalacion.consulta" ) );
		}
		editInstalacion.setValue( instalacion.getNombre() );
		editInstalacion.setReadOnly( true );
		editInstalacion.setWidth( "100%" );

		selectReferidor = new FiltrerAddSelect( getContext().getString( "modalAppointmentDlg.lbReferidor" ) );
		selectReferidor.setWidth( "100%" );
		selectReferidor.setRequired( true );
		selectReferidor.setVisibleAdd( false );

		referidorLazyProvider = new ReferidorLazyProvider( getContext() );
		LazyContainer dataSourceReferidor = new LazyContainer( Usuario.class, referidorLazyProvider, new SearchCriteria() );
		dataSourceReferidor.setMinFilterLength( 0 );
		selectReferidor.setItemCaptionPropertyId( "nombreCompleto" );
		selectReferidor.setContainerDataSource( dataSourceReferidor );
		selectReferidor.setPropertyDataSource( bi.getItemProperty( "referidor" ) );

		comboTipo = new ComboBox( getContext().getString( "modalAppointmentDlg.lbTipo" ) );
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
				fillDuracion( comboBoxDuracion );
				vo.setDuracion( getProximaDuracion( vo.getTipo().getDuracion() ) );
				comboBoxDuracion.markAsDirty();
			}

		} );

		dateFieldFecha = new DateField( getContext().getString( "modalAppointmentDlg.lbFecha" ), bi.getItemProperty( "fecha" ) );
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

		timeInicio = new TimeField2();
		timeInicio.setRequired( true );
		timeInicio.setWidth( "60px" );
		timeInicio.setPropertyDataSource( bi.getItemProperty( "fechainicio" ) );
		timeInicio.setImmediate( true );

		comboBoxDuracion = new ComboBox( getContext().getString( "modalAppointmentDlg.lbDuracion" ) );
		comboBoxDuracion.setWidth( "125px" );
		comboBoxDuracion.setNullSelectionAllowed( false );
		comboBoxDuracion.setNewItemsAllowed( false );
		fillDuracion( comboBoxDuracion );
		comboBoxDuracion.setPropertyDataSource( bi.getItemProperty( "duracion" ) );
		FormLayout formLayout = new FormLayout( comboBoxDuracion );
		formLayout.setMargin( false );

		HorizontalLayout layoutTime = new HorizontalLayout( timeInicio, formLayout );
		layoutTime.setCaption( getContext().getString( "modalAppointmentDlg.lbHoraInicio" ) );
		layoutTime.setSpacing( true );

		FormLayout layout = new FormLayout( selectPaciente, editInstalacion, selectReferidor, comboTipo, dateFieldFecha, layoutTime );
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setWidth( "100%" );

		componentsContainer.addComponent( layout );

		if (orgDto != null) {
			selectPaciente.setReadOnly( true );
			addCancelarCita();
		}
	}

	private void addCancelarCita()
	{
		Button button = new Button( getContext().getString( "modalAppointmentDlg.wndCaption.cancel" ) );
		button.setImmediate( true );
		button.addStyleName( ValoTheme.BUTTON_DANGER );
		operacionesContainer.addComponent( button );
		button.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -7170543331188736832L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( "modalAppointmentDlg.confirm.cancel" ), new ConfirmDialog.Listener()
				{
					private static final long serialVersionUID = -7797844014333609680L;

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
		for ( Integer item : duracion )
		{
			if ( min > item )
				continue;
			return item;
		}

		return duracion.get( 0 );
	}

	private void fillDuracion( ComboBox comboBox )
	{
		comboBox.getContainerDataSource().removeAllItems();

		for ( Integer item : duracion )
		{
			if ( vo.getTipo() == null )
			{
				addMinute( comboBox, item );
			}
			else
			{
				if ( vo.getTipo().getDuracion() <= item )
				{
					addMinute( comboBox, item );
				}
			}

		}
	}

	private static void addMinute( ComboBox comboBox, Integer min )
	{
		if ( min < 61 )
		{
			comboBox.addItem( min );
			return;
		}

		String caption = Utils.elapseTimeFromMin( min );
		comboBox.addItem( min );
		comboBox.setItemCaption( min, caption );
	}

	private AppointmentVo toVo( Cita cita )
	{
		AppointmentVo result = new AppointmentVo();

		if ( cita == null )
			return result;

		result.setFecha( Utils.getDateHourFromLong( cita.getFecha() ) );
		result.setFechainicio( Utils.getDateHourFromLong( cita.getFecha() ) );
		result.setDuracion( calDuracion( Utils.getDateHourFromLong( cita.getFecha() ), Utils.getDateHourFromLong( cita.getFechafin() ) ) );
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

	private Integer calDuracion( Date from, Date to )
	{
		Long mili = to.getTime() - from.getTime(); // diferencia de tiempo en
													// milisegundos
		Long seg = mili / 1000;// llevarlo a segundo
		Long min = seg / 60; // llevarlo a minutos

		return min.intValue();
	}

	private void updateFecha()
	{
		vo.setFechainicio( dateFieldFecha.getValue() );
		timeInicio.markAsDirty();
	}

	private void toDto()
	{
		newCita.setPaciente( vo.getPaciente().getId() );
		newCita.setInstalacion( instalacion.getId() );
		newCita.setTipo( vo.getTipo().getId() );
		newCita.setReferidor( vo.getReferidor().getId() );
		newCita.setFecha( Utils.getDateAsLong( vo.getFechainicio() ) );

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( vo.getFechainicio() );
		calendar.add( Calendar.MINUTE, vo.getDuracion() );
		newCita.setFechafin( Utils.getDateAsLong( calendar.getTime() ) );
		newCita.setUid( "UID" );
	}

	private void fillTipoEstudio()
	{
		TipoEstudio query = new TipoEstudio();
		query.setTipo( instalacion.getTipo() );
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
			// TODO Auto-generated catch block
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

			manager.setRow( getContext(), null, newCita );

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

		try
		{
			if ( !timeInicio.isValid() || comboBoxDuracion.getValue() == null )
			{
				throw new RuntimeException();
			}
		}
		catch ( Throwable t )
		{
			throw new ImedigException( new RuntimeException( "Parámetros incorrectos" ), LOG, ImedigException.NOT_NULL_VIOLATION );
		}

		if ( !isValidRequired() )
		{
			throw new ImedigException( new RuntimeException( "Parámetros incorrectos" ), LOG, ImedigException.NOT_NULL_VIOLATION );
		}

		Date datei = Utils.setTimeToDate( vo.getFecha(), vo.getFechainicio() );

		Date today = new Date();
		if ( today.after( datei ) )
		{
			Notification.show( getContext().getString( "modalAppointmentDlg.error.today" ), Notification.Type.ERROR_MESSAGE );
			return false;
		}

		return true;
	}

	private boolean isValidRequired()
	{
		return vo.getPaciente() != null && vo.getReferidor() != null && vo.getTipo() != null && vo.getFecha() != null && vo.getFechainicio() != null && vo.getDuracion() != null;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			if ( !isValid() )
				return false;

			toDto();

			manager.setRow( getContext(), (Cita)orgDto, newCita );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			manager.delRow( getContext(), orgDto );

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
		// TODO Auto-generated method stub

	}

	public void setDate( Date date )
	{
		vo.setFecha( date );
		vo.setFechainicio( date );
		vo.setDuracion( 10 );

		dateFieldFecha.markAsDirty();
		timeInicio.markAsDirty();
		comboBoxDuracion.markAsDirty();
	}

	public void setDuracionMin( Integer duracion )
	{
		vo.setDuracion( duracion );
		comboBoxDuracion.markAsDirty();
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalAppointmentDlg";
	}
}
