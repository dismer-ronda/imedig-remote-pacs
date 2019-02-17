package es.pryades.imedig.viewer.components.citations;

import java.util.ArrayList;
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

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DatosIntalacion;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TimeRange;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.query.EstudioQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class CitationsEventProvider implements CalendarEventProvider
{
	private static final long serialVersionUID = 16654819655615842L;

	private Instalacion instalacion;
	private ImedigContext ctx;
	private EstudiosManager estudiosManager;
	private TiposEstudiosManager tiposEstudiosManager;
	private PacientesManager pacientesManager;
	private DatosIntalacion datosIntalacion;

	private Calendar mainCalendar = GregorianCalendar.getInstance();
	private Map<Integer, TimeRange<LocalTime>> mapWorking;
	private Map<Integer, List<TimeRange<LocalTime>>> breaksByDay;

	private com.vaadin.ui.Calendar citationsCalendar;

	private int timeField = Calendar.MINUTE;
	private int amount = 10;

	public CitationsEventProvider( ImedigContext ctx, Instalacion instalacion, com.vaadin.ui.Calendar citationsCalendar )
	{
		this.ctx = ctx;
		this.instalacion = instalacion;
		this.citationsCalendar = citationsCalendar;

		estudiosManager = (EstudiosManager)IOCManager.getInstanceOf( EstudiosManager.class );
		tiposEstudiosManager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class );
		pacientesManager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class );

		settingPlan();
	}

	private void settingPlan()
	{
		DatosIntalacion datos = getExtraInformationFromJson();
		amount = datos.getTiempominimo();
		mapWorking = new HashMap<>();
		breaksByDay = new HashMap<>();
		for ( DayPlan<String> plan : datos.getWorkingPlan().getDiaryPlan() )
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

	private DatosIntalacion getExtraInformationFromJson()
	{
		if ( StringUtils.isBlank( instalacion.getDatos() ) )
			return new DatosIntalacion();

		try
		{
			return (DatosIntalacion)Utils.toPojo( instalacion.getDatos(), DatosIntalacion.class, false );
		}
		catch ( Throwable e )
		{
		}
		return new DatosIntalacion();
	}

	@Override
	public List<CalendarEvent> getEvents( Date startDate, Date endDate )
	{
		EstudioQuery query = new EstudioQuery();
		query.setInstalacion( instalacion.getId() );
		query.setFecha_desde( Utils.getDateAsLong( startDate ) );
		query.setFecha_hasta( Utils.getDateAsLong( endDate ) );

		try
		{
			if ( citationsCalendar.isMonthlyMode() )
			{
				return toCalendarEvents( (List<Estudio>)estudiosManager.getRows( ctx, query ) );
			}
			else
			{
				return toCalendarEvents( (List<Estudio>)estudiosManager.getRows( ctx, query ), startDate, endDate );
			}
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	private List<CalendarEvent> toCalendarEvents( List<Estudio> estudios ) throws Throwable
	{

		List<CalendarEvent> result = new ArrayList<>();

		for ( Estudio estudio : estudios )
		{
			result.add( toEvent( estudio ) );
		}

		return result;
	}

	private List<CalendarEvent> toCalendarEvents( List<Estudio> estudios, Date start, Date end ) throws Throwable
	{

		List<CalendarEvent> result = new ArrayList<>();

		List<TimeRange<Date>> wokingDates = getWokingDates( start, end );
		Map<Integer, List<CalendarEvent>> eventsByDay = eventsByDay( estudios );

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
	

	private Map<Integer, List<CalendarEvent>> eventsByDay( List<Estudio> estudios ) throws Throwable
	{
		Comparator<CalendarEvent> comparator = new Comparator<CalendarEvent>()
		{
			@Override
			public int compare( CalendarEvent o1, CalendarEvent o2 )
			{
				return o1.getStart().compareTo( o2.getStart() );
			}
		};

		List<CalendarEvent> events = toCalendarEvents( estudios );
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

		while ( start1.before( end ) )
		{

			if ( event != null && inside( event, start1, end1 ) )
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
				result.add( freeEvent( start1, end1 ) );
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
		Date end1 = incNextTimePeriod();
		CalendarEvent event = head( events );
		
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

//				cFrom.set( Calendar.SECOND, 0 );
//				cFrom.set( Calendar.HOUR_OF_DAY, workingDay.getEnd().getHourOfDay() );
//				cFrom.set( Calendar.MINUTE, workingDay.getEnd().getMinuteOfHour() );
//				Date dFrom = cFrom.getTime();
//
//				cFrom.set( Calendar.SECOND, 0 );
//				cFrom.set( Calendar.HOUR_OF_DAY, workingDay.getStart().getHourOfDay() );
//				cFrom.set( Calendar.MINUTE, workingDay.getStart().getMinuteOfHour() );
//				Date dStart = cFrom.getTime();
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

	private boolean isBreak( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		List<TimeRange<LocalTime>> breaks = breaksByDay.get( calendar.get( Calendar.DAY_OF_WEEK ) );

		if ( breaks == null || breaks.isEmpty() )
			return false;

		LocalTime time = new LocalTime( calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ) );

		for ( TimeRange<LocalTime> range : breaks )
		{
			if ( time.equals( range.getStart() ) || (time.isAfter( range.getStart() ) && time.isBefore( range.getEnd() )) )
				return true;
		}

		return false;
	}

	private boolean isWorking( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return mapWorking.get( calendar.get( Calendar.DAY_OF_WEEK ) ) != null;
	}

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

	private static boolean inside( Date startD, Date start, Date end )
	{
		return (start.equals( startD ) || start.before( startD )) && end.after( startD );
	}

	private static boolean inside( CalendarEvent event, Date start, Date end )
	{
		return (start.equals( event.getStart() ) || start.before( event.getStart() )) && end.after( event.getStart() );
	}

	private CalendarEvent toEvent( Estudio estudio ) throws Throwable
	{

		TipoEstudio tipoEstudio = (TipoEstudio)tiposEstudiosManager.getRow( ctx, estudio.getTipo() );
		Paciente paciente = (Paciente)pacientesManager.getRow( ctx, estudio.getPaciente() );

		CitationEvent event = new CitationEvent();
		event.setCaption( paciente.getNombreCompleto() );
		event.setDescription( paciente.getNombreCompletoConIdentificador() + "<br/>" + "Otra linea" );
		event.setStart( Utils.getDateHourFromLong( estudio.getFecha() ) );
		event.setEnd( Utils.getDateHourFromLong( estudio.getFechafin() ) );
		event.setData( estudio );

		return event;
	}

	private CalendarEvent freeEvent( Date start, Date end )
	{

		CitationEvent event = new CitationEvent();
		event.setStart( start );
		event.setEnd( end );
		event.setStyleName( "color2" );

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
