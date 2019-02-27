package es.pryades.imedig.viewer.components.appointments;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.PlanificacionHorario;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

public class AppointmentSchedulerViewer extends VerticalLayout implements ModalParent, EventClickHandler
{
	
	private static final long serialVersionUID = 1683377378764769059L;

	private ImedigContext ctx;
	@Getter
	private Recurso recurso;
	private CalendarEventProvider eventProvider;
	Calendar citationsCalendar;
	private AppointmentEventResource eventResource;
	private CalendarPeriodPanel periodPanel;
	private PlanificacionHorario planificacionHorario;
	
	private Integer firstHour;
	private Integer lastHour;
	
	public AppointmentSchedulerViewer( ImedigContext ctx, Recurso recurso)
	{
		this.ctx = ctx;
		this.recurso = recurso;
		
		settingInstalationWorkingPlan();
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}

	private void settingInstalationWorkingPlan()
	{
		planificacionHorario = getPlanificacionHorarioFromJson();

		firstHour = findFirstHour();
		lastHour = findLastHour();
	}
	
	private PlanificacionHorario getPlanificacionHorarioFromJson()
	{
		try
		{
			TipoHorarioManager manager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );
			TipoHorario tipoHorario = (TipoHorario)manager.getRow( ctx, recurso.getTipo_horario() );
			
			if (StringUtils.isBlank( tipoHorario.getDatos()) ) return new PlanificacionHorario();
			return (PlanificacionHorario)Utils.toPojo( tipoHorario.getDatos(), PlanificacionHorario.class, false );
		}
		catch ( Throwable e )
		{
		}
		return new PlanificacionHorario();
	}

	private Integer findFirstHour()
	{
		if (planificacionHorario.getDiaryPlan() == null)
			return 8;
		
		return AppointmentUtils.getEarlyHour( planificacionHorario.getDiaryPlan() );
	}
	
	private Integer findLastHour()
	{
		if (planificacionHorario.getDiaryPlan() == null)
			return 18;
		
		return AppointmentUtils.getLaterHour( planificacionHorario.getDiaryPlan() );
	}
		
	private void buildComponents()
	{
		citationsCalendar = new Calendar();
		citationsCalendar.setLocale( UI.getCurrent().getLocale() );
		citationsCalendar.setFirstDayOfWeek( java.util.Calendar.MONDAY );
		citationsCalendar.setWeeklyCaptionFormat( "dd/MM/yyyy" );
		citationsCalendar.setTimeFormat( TimeFormat.Format24H );
		citationsCalendar.setEventCaptionAsHtml( true );
		citationsCalendar.setFirstVisibleHourOfDay( firstHour );
		citationsCalendar.setLastVisibleHourOfDay( lastHour );
		
		eventResource = new AppointmentEventResource( ctx, recurso );
		//eventProvider = new AppointmentEventProvider( citationsCalendar, eventResource );
		eventProvider = new AppointmentEventProvider( ctx, recurso, citationsCalendar );
		citationsCalendar.setEventProvider( eventProvider );

		periodPanel = new CalendarPeriodPanel( ctx , this);
		addComponent( periodPanel );		

		settingHandlers();
 		
		//java.util.Calendar cal = new GregorianCalendar().getInstance();
		//cal.setTime( new Date() );
		//cal.add( java.util.Calendar.MONTH, 1 );
		//ciationsCalendar.setEndDate( UtilsCalendar.getLastDayMonth( cal.getTime() ) );
		citationsCalendar.setWidth( "100%" );
		citationsCalendar.setHeight( "1100px" );
		Panel panel = new Panel();
		panel.setSizeFull();
		VerticalLayout content = new VerticalLayout( citationsCalendar );
		content.setWidth( "100%" );
		content.setMargin( new MarginInfo( false, true, false, false ) );
		panel.setContent( content );
		panel.addStyleName( ValoTheme.PANEL_BORDERLESS );
		addComponent( panel );
		setExpandRatio( panel, 1.0f );
	}

	private void settingHandlers()
	{
		citationsCalendar.setHandler( (DateClickHandler)periodPanel );
		citationsCalendar.setHandler( (WeekClickHandler)periodPanel );
		citationsCalendar.setHandler( (ForwardHandler)periodPanel );
		citationsCalendar.setHandler( (BackwardHandler)periodPanel );
		citationsCalendar.setHandler( (EventClickHandler)this);
		citationsCalendar.setHandler( (EventMoveHandler)null);
		citationsCalendar.setHandler( (EventResizeHandler)null);
		citationsCalendar.setHandler( (RangeSelectHandler)null);
	}
	
	public void setDates(Date start, Date end){
		citationsCalendar.setStartDate( start );
		citationsCalendar.setEndDate( end );
	}
	
//	public void newCitation(){
//		ModalAppointmentDlg dlg = new ModalAppointmentDlg( ctx, Operation.OP_ADD, recurso, null, this, "administracion.citas" );
//		dlg.setDate( getStartDate() );
//		dlg.showModalWindow();
//	}
	
	private Date getStartDate(){
		Date start = citationsCalendar.getStartDate();
		Date end = citationsCalendar.getEndDate();
		Date today = new Date();
		
		if (end.before( today )) return today;
		
		if (today.before( end ) && !today.before( start )) return today;
		
		if (DateUtils.isSameDay( today, end )) return today;
		
		return start;
	}

	@Override
	public void refreshVisibleContent()
	{
		citationsCalendar.markAsDirty();
		
	}

	@Override
	public void eventClick( EventClick event )
	{
		AppointmentEvent citationEvent = (AppointmentEvent)event.getCalendarEvent();
		Operation operation = citationEvent.getData() == null ? Operation.OP_ADD : Operation.OP_MODIFY;
		
		ModalAppointmentDlg dlg = null;
		if (operation.equals( Operation.OP_ADD )){
			dlg = new ModalAppointmentDlg( ctx, operation, recurso, citationEvent.getStart(), this, "administracion.citas" );
		}else{
			dlg = new ModalAppointmentDlg( ctx, operation, recurso, citationEvent.getData(), this, "administracion.citas" );
		}
		
		//dlg.setDuracionMin( calDuracionInMin( citationEvent.getStart(), citationEvent.getEnd() ) );
		//dlg.setEndDate( citationEvent.getEnd() );
		dlg.showModalWindow();
	}

	private Integer calDuracionInMin( Date from, Date to )
	{
		Long mili = to.getTime() - from.getTime(); // diferencia de tiempo en
													// milisegundos
		Long seg = mili / 1000;// llevarlo a segundo
		Long min = seg / 60; // llevarlo a minutos

		return min.intValue();
	}
}
