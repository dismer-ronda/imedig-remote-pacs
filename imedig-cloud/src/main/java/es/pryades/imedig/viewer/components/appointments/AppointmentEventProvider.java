package es.pryades.imedig.viewer.components.appointments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;

import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CitasManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.PlanificacionHorario;
import es.pryades.imedig.cloud.dto.TimeRange;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class AppointmentEventProvider implements CalendarEventProvider
{
	private static final long serialVersionUID = 16654819655615842L;

	private Instalacion instalacion;
	private ImedigContext ctx;
	private CitasManager citasManager;
	private TiposEstudiosManager tiposEstudiosManager;
	private PacientesManager pacientesManager;
	private TipoHorarioManager tipoHorarioManager;
	
	private TipoHorario tipoHorario;
	private PlanificacionHorario planificacionHorario;

	private Calendar mainCalendar = GregorianCalendar.getInstance();
	private Map<Integer, TimeRange<LocalTime>> mapWorking;
	private Map<Integer, List<TimeRange<LocalTime>>> breaksByDay;
	
	private static final SimpleDateFormat dayDateFormatter = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat( "HH:mm" );

	private com.vaadin.ui.Calendar citationsCalendar;

	private int timeField = Calendar.MINUTE;
	private int amount = 10;

	public AppointmentEventProvider( ImedigContext ctx, Instalacion instalacion, com.vaadin.ui.Calendar citationsCalendar )
	{
		this.ctx = ctx;
		this.instalacion = instalacion;
		this.citationsCalendar = citationsCalendar;

		citasManager = (CitasManager)IOCManager.getInstanceOf( CitasManager.class );
		tiposEstudiosManager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class );
		pacientesManager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class );
		tipoHorarioManager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );
		
		settingPlan();
	}

	private void settingPlan()
	{
		planificacionHorario = getPlanificacionHorarioFromJson();
		
		amount = instalacion.getTiempominimo();
		mapWorking = new HashMap<>();
		breaksByDay = new HashMap<>();
		
		settingPlan( tipoHorario.getTipo_horario() );
	}
	
	private void settingPlan(int type)
	{
		switch ( type )
		{
			case Constants.SCHEDULER_ALL_EQUALS:
				settingPlanAllEquals();
				break;
			case Constants.SCHEDULER_ALL_WEEK_DAYS:
				settingPlanWeekDays();
				break;
			case Constants.SCHEDULER_CUSTOM:
				settingPlanCustom();
				break;

			default:
				break;
		}
	}

	private void settingPlanAllEquals()
	{
		DayPlan<String> plan = planificacionHorario.getDiaryPlan().get( 0 );
		
		List<Integer> days = Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY );
		for ( Integer day : days )
		{
			mapWorking.put( day, getTimeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( day, plan.getBreaks() );
		}
		
	}

	private void settingPlanWeekDays()
	{
		List<DayPlan<String>> daysplan = new ArrayList<>( planificacionHorario.getDiaryPlan() );
		DayPlan<String> plan = daysplan.get( 0 );
		
		List<Integer> days = Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY );
		for ( Integer day : days )
		{
			mapWorking.put( day, getTimeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( day, plan.getBreaks() );
		}
		
		daysplan.remove( plan );
		for ( DayPlan<String> dayplan : daysplan )
		{
			mapWorking.put( dayplan.getDay(), getTimeRange( dayplan.getWorkingTime().getStart(), dayplan.getWorkingTime().getEnd() ) );

			settingBreaks( dayplan.getDay(), dayplan.getBreaks() );
		}
	}

	private void settingPlanCustom()
	{
		for ( DayPlan<String> plan : planificacionHorario.getDiaryPlan() )
		{
			mapWorking.put( plan.getDay(), getTimeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( plan.getDay(), plan.getBreaks() );
		}
	}

	private void settingBreaks( Integer day, List<TimeRange<String>> breaks )
	{
		List<TimeRange<LocalTime>> breaksDay = new ArrayList<>();

		for ( TimeRange<String> range : breaks )
		{
			breaksDay.add( getTimeRange( range.getStart(), range.getEnd() ) );
		}

		breaksByDay.put( day, breaksDay );
	}

	private static TimeRange<LocalTime> getTimeRange( String start, String end )
	{
		LocalTime s = AppointmentUtils.getTime( start );
		LocalTime e = AppointmentUtils.getTime( end );

		return new TimeRange<LocalTime>( s, e );
	}

	private PlanificacionHorario getPlanificacionHorarioFromJson()
	{
		try
		{
			tipoHorarioManager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );
			tipoHorario = (TipoHorario)tipoHorarioManager.getRow( ctx, instalacion.getTipo_horario() );
			
			if (StringUtils.isBlank( tipoHorario.getDatos()) ) return new PlanificacionHorario();
			return (PlanificacionHorario)Utils.toPojo( tipoHorario.getDatos(), PlanificacionHorario.class, false );
		}
		catch ( Throwable e )
		{
		}
		return new PlanificacionHorario();
	}

	@Override
	public List<CalendarEvent> getEvents( Date startDate, Date endDate )
	{
		CitaQuery query = new CitaQuery();
		query.setInstalacion( instalacion.getId() );
		query.setFecha_desde( Utils.getDateAsLong( startDate ) );
		query.setFecha_hasta( Utils.getDateAsLong( endDate ) );

		try
		{
			List<CalendarEvent> events = null;
			if ( citationsCalendar.isMonthlyMode() )
			{
				events = toCalendarEvents( (List<Cita>)citasManager.getRows( ctx, query ) );
			}
			else
			{
				events = toCalendarEvents( (List<Cita>)citasManager.getRows( ctx, query ), startDate, endDate );
			}
			
			return events;
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	private List<CalendarEvent> toCalendarEvents( List<Cita> citas ) throws Throwable
	{

		List<CalendarEvent> result = new ArrayList<>();

		for ( Cita cita : citas )
		{
			result.add( toEvent( cita ) );
		}
		
		Comparator<CalendarEvent> comparator = new Comparator<CalendarEvent>()
		{
			@Override
			public int compare( CalendarEvent o1, CalendarEvent o2 )
			{
				return o1.getStart().compareTo( o2.getStart() );
			}
		};

		Collections.sort( result, comparator );

		return result;
	}

	private List<CalendarEvent> toCalendarEvents( List<Cita> citas, Date start, Date end ) throws Throwable
	{

		List<CalendarEvent> result = new ArrayList<>();

		List<TimeRange<Date>> wokingDates = getWokingDates( start, end );
		Map<Integer, List<CalendarEvent>> eventsByDay = eventsByDay( citas );

		Calendar calendar = GregorianCalendar.getInstance();

		for ( TimeRange<Date> range : wokingDates )
		{
			calendar.setTime( range.getStart() );

			Integer weekDay = calendar.get( Calendar.DAY_OF_WEEK );

			List<CalendarEvent> eventsDay = eventsByDay.get( weekDay );

			if ( eventsDay == null )
			{
				eventsDay = new ArrayList<>();
			}

			result.addAll( calendarEvents( eventsDay, range.getStart(), range.getEnd() ) );
		}

		return result;
	}
	

	private Map<Integer, List<CalendarEvent>> eventsByDay( List<Cita> citas ) throws Throwable
	{
		Comparator<CalendarEvent> comparator = new Comparator<CalendarEvent>()
		{
			@Override
			public int compare( CalendarEvent o1, CalendarEvent o2 )
			{
				return o1.getStart().compareTo( o2.getStart() );
			}
		};

		List<CalendarEvent> events = toCalendarEvents( citas );
		Collections.sort( events, comparator );

		Map<Integer, List<CalendarEvent>> result = new HashMap<>();

		Calendar calendar = GregorianCalendar.getInstance();

		for ( CalendarEvent event : events )
		{
			calendar.setTime( event.getStart() );
			Integer dayweek = calendar.get( Calendar.DAY_OF_WEEK );
			if ( result.get( dayweek ) == null )
			{
				result.put( dayweek, new ArrayList<CalendarEvent>() );
			}
			result.get( dayweek ).add( event );
		}

		return result;
	}

	private List<CalendarEvent> calendarEvents( List<CalendarEvent> events, Date start, Date end )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( start );

		if ( breaksByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) ) == null 
				||  breaksByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) ).isEmpty())
		{
			return calendarEventsDay( events, start, end );
		}

		return calendarEventsDayWithBreak( events, start, end );
	}

	private List<CalendarEvent> calendarEventsDay( List<CalendarEvent> events, Date start, Date end )
	{

		List<CalendarEvent> result = new ArrayList<>();

		mainCalendar.setTime( start );

		Date start1 = start;
		Date end1 = incNextTimePeriod();
		CalendarEvent event = head( events );
		Date today = new Date();

		while ( start1.before( end ) )
		{

			if ( inside( event, start1, end1 ) )
			{
				start1 = addEvent( result, event, start1, end1 );
				events = tail( events );
				event = head( events );

				while ( start1.after( end1 ) )
				{
					end1 = incNextTimePeriod();
				}

			}
			else
			{
				if (end.after( today ) || end1.after( today )){
					if (end1.after( end )){
						result.add( freeEvent( start1, end ) );
					}else{
						result.add( freeEvent( start1, end1 ) );
					}
				}
				start1 = end1;
				end1 = incNextTimePeriod();
				while ( start1.after( end1 ) )
				{
					end1 = incNextTimePeriod();
				}
			}
		}

		return result;
	}

	private List<CalendarEvent> calendarEventsDayWithBreak( List<CalendarEvent> events, Date start, Date end )
	{

		List<CalendarEvent> result = new ArrayList<>();

		mainCalendar.setTime( start );

		List<TimeRange<LocalTime>> breaksTime = breaksByDay.get( mainCalendar.get( Calendar.DAY_OF_WEEK  ));
		List<TimeRange<Date>> breaksDate = toDates( start, breaksTime );
		
		Date start1 = mainCalendar.getTime();
		//Date end1 = incNextTimePeriod();
		//CalendarEvent event = head( events );
		
		for ( TimeRange<Date> breakDate : breaksDate )
		{
			result.addAll( calendarEventsDay( events, start1, breakDate.getStart() ) );
			start1 = breakDate.getEnd();
		}

		TimeRange<Date> timeRange = breaksDate.get( breaksDate.size()-1 );
		result.addAll( calendarEventsDay( events, timeRange.getEnd(), end ) );
		
		return result;
	}

	private List<TimeRange<Date>> getWokingDates( Date from, Date to )
	{

		List<TimeRange<Date>> result = new ArrayList<>();

		Calendar cFrom = GregorianCalendar.getInstance();
		cFrom.setTime( from );

		Date fromTmp = cFrom.getTime();

		Calendar cTo = GregorianCalendar.getInstance();
		cTo.setTime( to );

		while ( fromTmp.before( to ) )
		{
			Integer day = cFrom.get( Calendar.DAY_OF_WEEK );
			if ( mapWorking.get( day ) != null )
			{
				TimeRange<LocalTime> workingDay = mapWorking.get( day );
				Date temp = cFrom.getTime();
				result.add( toDate( temp, workingDay ) );
				cFrom.setTime( temp );
			}
			cFrom.add( Calendar.DAY_OF_WEEK, 1 );
			fromTmp = cFrom.getTime();
		}

		return result;

	}

	private static List<TimeRange<Date>> toDates( Date date, List<TimeRange<LocalTime>> timeRanges )
	{
		List<TimeRange<Date>> result = new ArrayList<>();
		
		for ( TimeRange<LocalTime> tr : timeRanges )
		{
			result.add( toDate( date, tr ) );
		}
		
		return result;
	}
	
	private static TimeRange<Date> toDate( Date date, TimeRange<LocalTime> timeRange )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.HOUR_OF_DAY, timeRange.getStart().getHourOfDay() );
		calendar.set( Calendar.MINUTE, timeRange.getStart().getMinuteOfHour() );
		Date dFrom = calendar.getTime();

		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.HOUR_OF_DAY, timeRange.getEnd().getHourOfDay() );
		calendar.set( Calendar.MINUTE, timeRange.getEnd().getMinuteOfHour() );
		Date dTo = calendar.getTime();
		
		return new TimeRange<Date>( dFrom, dTo );
	}

//	private boolean isBreak( Date date )
//	{
//		Calendar calendar = GregorianCalendar.getInstance();
//		calendar.setTime( date );
//
//		List<TimeRange<LocalTime>> breaks = breaksByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) );
//
//		if ( breaks == null || breaks.isEmpty() )
//			return false;
//
//		LocalTime time = new LocalTime( calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ) );
//
//		for ( TimeRange<LocalTime> range : breaks )
//		{
//			if ( time.equals( range.getStart() ) || (time.isAfter( range.getStart() ) && time.isBefore( range.getEnd() )) )
//				return true;
//		}
//
//		return false;
//	}

	private Date incNextTimePeriod()
	{
		mainCalendar.add( timeField, amount );

		return mainCalendar.getTime();
	}

	private Date addEvent( List<CalendarEvent> events, CalendarEvent event, Date start, Date end )
	{
		if ( start.equals( event.getStart() ) )
		{
			events.add( event );
		}
		else
		{
			events.add( freeEvent( start, event.getStart() ) );
			events.add( event );
		}

		return event.getEnd();
	}

//	private static boolean inside( Date startD, Date start, Date end )
//	{
//		return (start.equals( startD ) || start.before( startD )) && end.after( startD );
//	}

	private static boolean inside( CalendarEvent event, Date start, Date end )
	{
		if (event == null) return false;
		
		return (start.equals( event.getStart() ) || start.before( event.getStart() )) && end.after( event.getStart() );
	}

	private CalendarEvent toEvent( Cita cita ) throws Throwable
	{

		
		Paciente paciente = (Paciente)pacientesManager.getRow( ctx, cita.getPaciente() );

		AppointmentEvent event = new AppointmentEvent();
		event.setCaption( paciente.getNombreCompleto() );
		event.setDescription( getDescription( paciente, cita ) );
		event.setStart( Utils.getDateHourFromLong( cita.getFecha() ) );
		event.setEnd( Utils.getDateHourFromLong( cita.getFechafin() ) );
		event.setData( cita );
		
		if (cita.getEstado() == Constants.APPOINTMENT_STATUS_PLANING)
			event.setStyleName( "cita" );
		else if (cita.getEstado() == Constants.APPOINTMENT_STATUS_EXECUTING)
			event.setStyleName( "ejecucion" );
		else
			event.setStyleName( "finalizada" );

		return event;
	}
	
	private String getDescription(Paciente paciente, Cita cita) throws Throwable{
		String fecha = dayDateFormatter.format( Utils.getDateHourFromLong( cita.getFecha() ));
		String inicio = timeFormatter.format( Utils.getDateHourFromLong( cita.getFecha()));
		String fin = timeFormatter.format( Utils.getDateHourFromLong( cita.getFechafin()));
		TipoEstudio tipoEstudio = (TipoEstudio)tiposEstudiosManager.getRow( ctx, cita.getTipo() );
		
		StringBuilder s = new StringBuilder();
		s.append( "<b>" ).append( ctx.getString( "modalNewPaciente.lbIdentificador") ).append( ": </b>" ).append( paciente.getUid() ).append( "<br/>" ).
		  append( "<b>" ).append( ctx.getString( "modalNewPaciente.lbNombre") ).append( ": </b>" ).append( paciente.getNombreCompleto() ).append( "<br/>" ).
		  append( "<b>" ).append( ctx.getString( "words.facility") ).append( ": </b>" ).append( instalacion.getNombre() ).append( "<br/>" ).
		  append( "<b>" ).append( ctx.getString( "modalAppointmentDlg.lbTipo") ).append( ": </b>" ).append( tipoEstudio.getNombre() ).append( "<br/>" ).
		  append( "<b>" ).append( ctx.getString( "words.date") ).append( ": </b>" ).append( fecha ).append( "  " ).
		  append( "<b>" ).append( ctx.getString( "words.time") ).append( ": </b>" ).append( inicio ).append( " - " ).append( fin );

		return s.toString(); 
	}

	private CalendarEvent freeEvent( Date start, Date end )
	{

		AppointmentEvent event = new AppointmentEvent();
		event.setStart( start );
		event.setEnd( end );
		event.setStyleName( "free" );

		return event;
	}

	private static <T> T head( List<T> list )
	{
		if ( list == null || list.isEmpty() )
			return null;

		return list.get( 0 );
	}

	private static <T> T end( List<T> list )
	{
		if ( list == null || list.isEmpty() )
			return null;

		return list.get( list.size() - 1 );
	}

	private static <T> List<T> init( List<T> list )
	{
		if ( list.isEmpty() )
			return list;

		return list.subList( 0, list.size() - 1 );
	}

	private static <T> List<T> tail( List<T> list )
	{
		if ( list.isEmpty() )
			return null;

		return list.subList( 1, list.size() );
	}

}
