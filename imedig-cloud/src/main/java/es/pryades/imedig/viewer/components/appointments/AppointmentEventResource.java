package es.pryades.imedig.viewer.components.appointments;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import com.vaadin.ui.components.calendar.event.CalendarEvent;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CitasManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.KeyValue;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.PlanificacionHorario;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.TimeRange;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import lombok.ToString;

public class AppointmentEventResource implements Serializable
{
	private static final Logger LOG = Logger.getLogger( AppointmentEventResource.class );

	private static final long serialVersionUID = 3898341386506846073L;

	private static final SimpleDateFormat dayDateFormatter = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat( "HH:mm" );
	private static final List<Integer> ALL_DAYS = Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY );
	private static final List<Integer> WEEK_DAYS = Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY );
	
	private static final Comparator<TimeRange<LocalTime>> TIME_RANGE_COMPARATOR = new Comparator<TimeRange<LocalTime>>()
	{
		@Override
		public int compare( TimeRange<LocalTime> lt1, TimeRange<LocalTime> lt2 )
		{
			return lt1.getStart().compareTo( lt2.getStart() );
		}
	};
	
	private Calendar mainCalendar = GregorianCalendar.getInstance();
	
	private Recurso recurso;
	private ImedigContext ctx;

	private CitasManager citasManager;
	private TiposEstudiosManager tiposEstudiosManager;
	private PacientesManager pacientesManager;
	private TipoHorarioManager tipoHorarioManager;

	private Date start;
	private Date end;
	private Map<Integer, TimeRange<LocalTime>> workingPlanByDay;
	private Map<Integer, List<TimeRange<LocalTime>>> breaksByDay;
	private Map<Integer, List<TimeRange<LocalTime>>> detailWorkinPlanByDay;
	private Map<Integer, List<CalendarEvent>> mapEvents;

	private int amount = 10;

	private TipoHorario tipoHorario;
	private PlanificacionHorario planificacionHorario;
	
	

	public AppointmentEventResource( ImedigContext ctx, Recurso recurso )
	{
		this.ctx = ctx;
		this.recurso = recurso;

		citasManager = (CitasManager)IOCManager.getInstanceOf( CitasManager.class );
		tiposEstudiosManager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class );
		pacientesManager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class );
		tipoHorarioManager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );

		mapEvents = new HashMap<>();
		extractResourceWorkinPlan();
	}

	public List<CalendarEvent> getMonthlyEvents( Date startDate, Date endDate )
	{
		start = startDate;
		end = endDate;
		
		mapEvents.clear();

		try
		{
			return convertToCalendarEvents( getCitas( startDate, endDate ) );
		}
		catch ( Throwable e )
		{
			LOG.error( "ERROR", e );
		}

		return new ArrayList<>();
	}

	public List<CalendarEvent> getWeeklyEvents( Date startDate, Date endDate )
	{
		start = startDate;
		end = endDate;
		
		mapEvents.clear();

		try
		{
			return convertToCalendarEvents( getCitas( startDate, endDate ), startDate, endDate );
		}
		catch ( Throwable e )
		{
			LOG.error( "ERROR", e );
		}

		return new ArrayList<>();
	}
	
	/*
	 * Leer las citas del recurso dadas dos fechas.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<Cita> getCitas(Date startDate, Date endDate){
		CitaQuery query = new CitaQuery();
		query.setRecurso( recurso.getId() );
		query.setFecha_desde( Utils.getDateAsLong( startDate ) );
		query.setFecha_hasta( Utils.getDateAsLong( endDate ) );

		try
		{
			return (List<Cita>)citasManager.getRows( ctx, query );
		}
		catch ( Throwable e )
		{
			LOG.error( "ERROR", e );
		}

		return new ArrayList<>();
	}

	/*
	 * Extraer el horario de trabajo para ese recurso
	 */
	private void extractResourceWorkinPlan()
	{
		planificacionHorario = getPlanificacionHorarioFromJson();

		amount = recurso.getTiempominimo();
		workingPlanByDay = new HashMap<>();
		breaksByDay = new HashMap<>();

		switch ( tipoHorario.getTipo_horario() )
		{
			case Constants.SCHEDULER_ALL_EQUALS:
				extractPlanAllEquals();
				break;
			case Constants.SCHEDULER_ALL_WEEK_DAYS:
				extractPlanWeekDays();
				break;
			case Constants.SCHEDULER_CUSTOM:
				extractPlanCustom();
				break;

			default:
				break;
		}
		createDetailWorkingPlan();
	}

	private void extractPlanAllEquals()
	{
		DayPlan<String> plan = planificacionHorario.getDiaryPlan().get( 0 );

		for ( Integer day : ALL_DAYS )
		{
			workingPlanByDay.put( day, AppointmentUtils.timeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( day, plan.getBreaks() );
		}

	}

	private void extractPlanWeekDays()
	{
		List<DayPlan<String>> daysplan = new ArrayList<>( planificacionHorario.getDiaryPlan() );
		DayPlan<String> plan = daysplan.get( 0 );

		for ( Integer day : WEEK_DAYS )
		{
			workingPlanByDay.put( day, AppointmentUtils.timeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( day, plan.getBreaks() );
		}

		daysplan.remove( plan );
		for ( DayPlan<String> dayplan : daysplan )
		{
			workingPlanByDay.put( dayplan.getDay(), AppointmentUtils.timeRange( dayplan.getWorkingTime().getStart(), dayplan.getWorkingTime().getEnd() ) );

			settingBreaks( dayplan.getDay(), dayplan.getBreaks() );
		}
	}

	private void extractPlanCustom()
	{
		for ( DayPlan<String> plan : planificacionHorario.getDiaryPlan() )
		{
			workingPlanByDay.put( plan.getDay(), AppointmentUtils.timeRange( plan.getWorkingTime().getStart(), plan.getWorkingTime().getEnd() ) );

			settingBreaks( plan.getDay(), plan.getBreaks() );
		}
	}

	private void settingBreaks( Integer day, List<TimeRange<String>> breaks )
	{
		List<TimeRange<LocalTime>> breaksDay = new ArrayList<>();

		for ( TimeRange<String> range : breaks )
		{
			breaksDay.add( AppointmentUtils.timeRange( range.getStart(), range.getEnd() ) );
		}

		breaksByDay.put( day, breaksDay );
	}

	private PlanificacionHorario getPlanificacionHorarioFromJson()
	{
		try
		{
			tipoHorario = (TipoHorario)tipoHorarioManager.getRow( ctx, recurso.getTipo_horario() );

			if ( StringUtils.isBlank( tipoHorario.getDatos() ) )
				return new PlanificacionHorario();
			return (PlanificacionHorario)Utils.toPojo( tipoHorario.getDatos(), PlanificacionHorario.class, false );
		}
		catch ( Throwable e )
		{
		}
		return new PlanificacionHorario();
	}
	
	private void createDetailWorkingPlan(){
		detailWorkinPlanByDay = new HashMap<>();
		
		for ( Integer day : ALL_DAYS )
		{
			detailWorkinPlanByDay.put( day, new ArrayList<TimeRange<LocalTime>>() );
			
			if (workingPlanByDay.get( day ) == null) continue;
			
			TimeRange<LocalTime> wPlan = workingPlanByDay.get( day );
			
			List<TimeRange<LocalTime>> breaks = breaksByDay.get( day );
			
			if (breaks != null){
				Collections.sort( breaks, TIME_RANGE_COMPARATOR );
			}else{
				breaks = new ArrayList<>();
			}
			
			TimeRange<LocalTime> breakTime = Utils.head( breaks ); 
			breaks = Utils.tail( breaks );
			
			LocalTime start = wPlan.getStart();
			
			while(start.isBefore( wPlan.getEnd() )){
				LocalTime end = start.plusMinutes( amount );
				
				if (isInsideTime( end, breakTime )){
					end = breakTime.getStart();
					detailWorkinPlanByDay.get(day).add( new FreeTimeRange(start, end ) );
					detailWorkinPlanByDay.get(day).add( new BreakTimeRange(breakTime.getStart(), breakTime.getEnd() ) );

					start = breakTime.getEnd();
					breakTime = Utils.head( breaks ); 
					breaks = Utils.tail( breaks );
				}else if (end.isAfter( wPlan.getEnd() )){
					end = wPlan.getEnd();
					detailWorkinPlanByDay.get(day).add( new FreeTimeRange( start, end ) );
					start = wPlan.getEnd();
				}else{
					detailWorkinPlanByDay.get(day).add( new FreeTimeRange( start, end ) );
					start = end;
				}
			}
		}
	}
	
	private static boolean isInsideTime(LocalTime time, TimeRange<LocalTime> timeRange){
		return timeRange != null && (time.isEqual( timeRange.getStart() ) || time.isAfter( timeRange.getStart()));
	}

	public List<KeyValue<LocalTime, Integer>> getFreeTimes(Date date){
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		TimeRange<LocalTime> workingTime = workingPlanByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) );
		
		if (workingTime == null) return new ArrayList<>();
		
		List<TimeRange<LocalTime>> detail = detailWorkinPlanByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) );
		List<Cita> citas = getCitas( 
				Utils.getDateWithTime( Utils.getFirstSecondAsDate( date ), workingTime.getStart() ), 
				Utils.getDateWithTime( Utils.getLastSecondAsDate( date ), workingTime.getEnd() ) );
		
		List<TimeRange<LocalTime>> citasTime = convertTimeRange( citas );
		
		List<TimeRange<LocalTime>> temp = new ArrayList<>();
		TimeRange<LocalTime> citaTime = Utils.head( citasTime );
		citasTime = Utils.tail( citasTime );
		
		Iterator<TimeRange<LocalTime>> it = detail.iterator();
		
		TimeRange<LocalTime> timeRange = (TimeRange<LocalTime>)it.next();
		do{
			if (timeRange instanceof BreakTimeRange){
				temp.add( new BreakTimeRange( timeRange.getStart(), timeRange.getEnd() ) );
				timeRange = (TimeRange<LocalTime>)it.next();
			}else if (citaTime != null && timeRange.getStart().equals( citaTime.getStart() )){
				temp.add( new InUseTimeRange(citaTime.getStart(), citaTime.getEnd() ) );
				while(it.hasNext() && citaTime != null && citaTime.getEnd().isAfter( timeRange.getStart() )){
					timeRange = (TimeRange<LocalTime>)it.next();
				}
				citaTime = Utils.head( citasTime );
				citasTime = Utils.tail( citasTime );
			}else{
				temp.add( new FreeTimeRange(timeRange.getStart(), timeRange.getEnd() ) );
				timeRange = (TimeRange<LocalTime>)it.next();
			}
		}while ( it.hasNext() );
		
		return calculeFreeTime( temp );
	}
	
	private List<KeyValue<LocalTime, Integer>> calculeFreeTime(List<TimeRange<LocalTime>> ranges){
		List<KeyValue<LocalTime, Integer>> result = new ArrayList<>();
		
		TimeRange<LocalTime> lastrange = null;
		
		for ( TimeRange<LocalTime> range : ranges )
		{
			lastrange = range;
			
			if (range instanceof BreakTimeRange || range instanceof InUseTimeRange){
				LocalTime time = range.getStart();
				
				for ( KeyValue<LocalTime, Integer> keyValue : result )
				{
					if (!keyValue.getValue().equals( 0 )){
						continue;
					}
					
					keyValue.setValue( Minutes.minutesBetween( keyValue.getKey(), time ).getMinutes() );
				}
				lastrange = null;
				continue;
			}
			
			result.add( new KeyValue<LocalTime, Integer>( range.getStart(), 0 ) );
		}
		
		if (lastrange != null){
			for ( KeyValue<LocalTime, Integer> keyValue : result ){
				if (!keyValue.getValue().equals( 0 )){
					continue;
				}
				
				keyValue.setValue( Minutes.minutesBetween( keyValue.getKey(), lastrange.getEnd() ).getMinutes() );
			}
		}
		return result;
	}
	
	private List<TimeRange<LocalTime>> convertTimeRange( List<Cita> citas )
	{

		List<TimeRange<LocalTime>> result = new ArrayList<>();

		for ( Cita cita : citas ){
			
			result.add( AppointmentUtils.convertToTimeRange( cita ));
		}

		Collections.sort( result, TIME_RANGE_COMPARATOR );

		return result;
	}
	
	private List<CalendarEvent> convertToCalendarEvents( List<Cita> citas ) throws Throwable
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

	private List<CalendarEvent> convertToCalendarEvents( List<Cita> citas, Date start, Date end ) throws Throwable
	{

		List<CalendarEvent> result = new ArrayList<>();

		List<TimeRange<Date>> wokingDates = getWokingDates( start, end );
		Map<Integer, List<CalendarEvent>> byDay = convertToEventsByDay( citas );

		Calendar calendar = GregorianCalendar.getInstance();

		for ( TimeRange<Date> range : wokingDates )
		{
			calendar.setTime( range.getStart() );

			Integer weekDay = calendar.get( Calendar.DAY_OF_WEEK );

			List<CalendarEvent> eventsDay = byDay.get( weekDay );

			if ( eventsDay == null )
			{
				eventsDay = new ArrayList<>();
			}

			List<CalendarEvent> events = calendarEvents( eventsDay, range.getStart(), range.getEnd() );
			
			mapEvents.put( weekDay, events );

			result.addAll( events );
		}
		
		//Esto es una prueba
		/*calendar.setTime( start );
		Date temp = calendar.getTime();
		while(temp.before( end )){
			List<KeyValue<LocalTime, Integer>> freeTime = getFreeTimes(temp);
			System.out.println( freeTime );
			calendar.add( Calendar.DAY_OF_MONTH, 1 );
			temp = calendar.getTime();
		}*/

		return result;
	}

	private List<CalendarEvent> calendarEvents( List<CalendarEvent> events, Date start, Date end )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( start );

		if ( !hasBreaks( calendar.get( Calendar.DAY_OF_WEEK ) ))
		{
			return calendarEventsDay( events, start, end );
		}

		return calendarEventsDayWithBreak( events, start, end );
	}
	
	private boolean hasBreaks(int day){
		return breaksByDay.get( day ) != null && !breaksByDay.get( day ).isEmpty();
	}
	
	private List<CalendarEvent> calendarEventsDay( List<CalendarEvent> events, Date start, Date end )
	{

		List<CalendarEvent> result = new ArrayList<>();

		mainCalendar.setTime( start );

		Date start1 = start;
		Date end1 = incNextTimePeriod();
		CalendarEvent event = Utils.head( events );
		Date today = new Date();

		while ( start1.before( end ) )
		{

			if ( AppointmentUtils.inside( event, start1, end1 ) )
			{
				start1 = addEvent( result, event, start1, end1 );
				events = Utils.tail( events );
				event = Utils.head( events );

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
		List<TimeRange<Date>> breaksDate = AppointmentUtils.getRangesDate( start, breaksTime );
		
		Date start1 = mainCalendar.getTime();
		
		for ( TimeRange<Date> breakDate : breaksDate )
		{
			result.addAll( calendarEventsDay( events, start1, breakDate.getStart() ) );
			start1 = breakDate.getEnd();
		}

		TimeRange<Date> timeRange = breaksDate.get( breaksDate.size()-1 );
		result.addAll( calendarEventsDay( events, timeRange.getEnd(), end ) );
		
		return result;
	}
	
	private Date incNextTimePeriod()
	{
		mainCalendar.add( Calendar.MINUTE, amount );

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


	/*
	 * 
	 */
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
			if ( workingPlanByDay.get( day ) != null )
			{
				TimeRange<LocalTime> workingPlan = workingPlanByDay.get( day );
				Date temp = cFrom.getTime();
				result.add( AppointmentUtils.convertToRangeDate( temp, workingPlan ) );
				cFrom.setTime( temp );
			}
			cFrom.add( Calendar.DAY_OF_WEEK, 1 );
			fromTmp = cFrom.getTime();
		}

		return result;

	}

	private Map<Integer, List<CalendarEvent>> convertToEventsByDay( List<Cita> citas ) throws Throwable
	{
		List<CalendarEvent> events = convertToCalendarEvents( citas );
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

	private CalendarEvent toEvent( Cita cita ) throws Throwable
	{

		Paciente paciente = (Paciente)pacientesManager.getRow( ctx, cita.getPaciente() );

		AppointmentEvent event = new AppointmentEvent();
		event.setCaption( paciente.getNombreCompleto() );
		event.setDescription( getDescription( paciente, cita ) );
		event.setStart( Utils.getDateHourFromLong( cita.getFecha() ) );
		event.setEnd( Utils.getDateHourFromLong( cita.getFechafin() ) );
		event.setData( cita );

		if ( cita.getEstado() == Constants.APPOINTMENT_STATUS_PLANING )
			event.setStyleName( "cita" );
		else if ( cita.getEstado() == Constants.APPOINTMENT_STATUS_EXECUTING )
			event.setStyleName( "ejecucion" );
		else
			event.setStyleName( "finalizada" );

		return event;
	}

	private String getDescription( Paciente paciente, Cita cita ) throws Throwable
	{
		String fecha = dayDateFormatter.format( Utils.getDateHourFromLong( cita.getFecha() ) );
		String inicio = timeFormatter.format( Utils.getDateHourFromLong( cita.getFecha() ) );
		String fin = timeFormatter.format( Utils.getDateHourFromLong( cita.getFechafin() ) );
		TipoEstudio tipoEstudio = (TipoEstudio)tiposEstudiosManager.getRow( ctx, cita.getTipo() );

		StringBuilder s = new StringBuilder();
		s.append( "<b>" ).append( ctx.getString( "modalNewPaciente.lbIdentificador" ) ).append( ": </b>" ).append( paciente.getUid() ).append( "<br/>" ).append( "<b>" ).append( ctx.getString( "modalNewPaciente.lbNombre" ) ).append( ": </b>" ).append( paciente.getNombreCompleto() ).append( "<br/>" ).append( "<b>" ).append( ctx.getString( "words.facility" ) ).append( ": </b>" ).append( recurso.getNombre() ).append( "<br/>" ).append( "<b>" ).append( ctx.getString( "modalAppointmentDlg.lbTipo" ) ).append( ": </b>" ).append( tipoEstudio.getNombre() ).append( "<br/>" ).append( "<b>" ).append( ctx.getString( "words.date" ) ).append( ": </b>" ).append( fecha ).append( "  " ).append( "<b>" ).append( ctx.getString( "words.time" ) ).append( ": </b>" ).append( inicio ).append( " - " ).append( fin );

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
	
//	public List<TimeRange<LocalTime>> getScheduler(Date date){
//		Calendar calendar = GregorianCalendar.getInstance();
//		calendar.setTime( date );
//		
//		Date start = Utils.getFirstSecondAsDate( date );
//		Date end = Utils.getLastSecondAsDate( date );	
//		
//		List<Cita> citas = getCitas( start, end );
//		
//		if ( !hasBreaks( calendar.get( Calendar.DAY_OF_WEEK ) ))
//		{
//			
//		}
//		
//		
//	}
	
	@ToString(callSuper= true)
	private class BreakTimeRange extends TimeRange<LocalTime>{
		private static final long serialVersionUID = -6592218573934449485L;
		
		public BreakTimeRange(LocalTime start, LocalTime end){
			super( start, end );
		}
	}
	
	@ToString(callSuper= true)
	private class FreeTimeRange extends TimeRange<LocalTime>{
		private static final long serialVersionUID = 204707683283905450L;
		
		public FreeTimeRange(LocalTime start, LocalTime end){
			super( start, end );
		}
		
	}

	private class InUseTimeRange extends TimeRange<LocalTime>{
		private static final long serialVersionUID = 8447592380162903841L;
		
		public InUseTimeRange(LocalTime start, LocalTime end){
			super( start, end );
		}
	}

}
