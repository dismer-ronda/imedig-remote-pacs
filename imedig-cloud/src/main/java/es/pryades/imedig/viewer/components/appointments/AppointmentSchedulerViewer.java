package es.pryades.imedig.viewer.components.appointments;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
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
	//private AppointmentEventResource eventResource;
	private CalendarPeriodPanel periodPanel;
	private PlanificacionHorario planificacionHorario;
	
	private Integer firstHour;
	private Integer lastHour;
	private Integer calendarHeight= 1000;
	
	private static final Integer DEFAULT_HEIGHT = 1000;
	private static final String heightper = "600px";
	
	public AppointmentSchedulerViewer( ImedigContext ctx, Recurso recurso)
	{
		this.ctx = ctx;
		this.recurso = recurso;
		
		settingResourceWorkingPlan();
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}

	private void settingResourceWorkingPlan()
	{
		planificacionHorario = getPlanificacionHorarioFromJson();

		firstHour = findFirstHour();
		lastHour = findLastHour();
		
		LocalTime start = new LocalTime( firstHour, 0, 0 );
		LocalTime end = new LocalTime( lastHour, 0, 0 );
		Integer min = Minutes.minutesBetween( start, end ).getMinutes();
		Integer spaces = min/recurso.getTiempominimo();
		calendarHeight = spaces*35;
		if (calendarHeight < DEFAULT_HEIGHT){
			calendarHeight = DEFAULT_HEIGHT;
		}
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
		
		//eventResource = new AppointmentEventResource( ctx, recurso );
		eventProvider = new AppointmentEventProvider( ctx, recurso, citationsCalendar );
		citationsCalendar.setEventProvider( eventProvider );

		periodPanel = new CalendarPeriodPanel( ctx , this);
		addComponent( periodPanel );		

		settingHandlers();
		settingInitWeek();
 		
		citationsCalendar.setWidth( "100%" );
		citationsCalendar.setHeight( calendarHeight+"px" );

		Panel panel = new Panel();
		panel.setSizeFull();
		VerticalLayout content = new VerticalLayout( citationsCalendar );
		content.setWidth( "100%" );
		//content.setHeight( "100%" );
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
	
	private void settingInitWeek(){
		periodPanel.dateClick( new DateClickEvent( citationsCalendar, new Date() ) );
	}

	public void monthlyView(Date start, Date end){
		citationsCalendar.setHeight( heightper );
		setDates( start, end );
	}
	
	public void weeklyView(){
		citationsCalendar.setHeight( calendarHeight+"px" );
	}
	
	private void setDates(Date start, Date end){
		citationsCalendar.setStartDate( start );
		citationsCalendar.setEndDate( end );
	}
	
	@Override
	public void refreshVisibleContent()
	{
		citationsCalendar.markAsDirty();
	}

	@Override
	public void eventClick( EventClick event )
	{
		
		CalendarEvent e = event.getCalendarEvent();
		Date today = new Date();
		
		if (e instanceof OldEvent){
			return;
		}
		
		if (e instanceof FreeEvent && today.after( e.getStart() )){
			Notification.show( ctx.getString( "modalAppointmentDlg.error.today" ), Notification.Type.ERROR_MESSAGE );
			return;
		}
		
		if (e instanceof AppointmentEvent && today.after( e.getStart() ) && !Utils.isSameDay( today, e.getStart())){
			return;
		}
		
		//Operation operation = citationEvent.getData() == null ? Operation.OP_ADD : Operation.OP_MODIFY;
		
		ModalAppointmentDlg dlg = null;
		if (e instanceof FreeEvent){
			dlg = new ModalAppointmentDlg( ctx, Operation.OP_ADD, recurso, e.getStart(), this, "administracion.citas" );
		}else if (e instanceof AppointmentEvent){
			dlg = new ModalAppointmentDlg( ctx, Operation.OP_MODIFY, recurso, ((AppointmentEvent)e).getData(), this, "administracion.citas" );
		}
		dlg.showModalWindow();
	}
	
	public void addNewOutOfCalendarAppointment(){
		new ModalAppointmentDlg( ctx, Operation.OP_ADD, recurso, new Date(), this, "administracion.citas", true ).showModalWindow();
	}
}
