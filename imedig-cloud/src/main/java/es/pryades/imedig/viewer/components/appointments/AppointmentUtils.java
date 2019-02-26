package es.pryades.imedig.viewer.components.appointments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.LocalTime;

import com.vaadin.ui.components.calendar.event.CalendarEvent;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.TimeRange;

public class AppointmentUtils implements Serializable
{

	private static final long serialVersionUID = -2568124720869254849L;

	public static LocalTime getTime( String time )
	{
		return LocalTime.parse( time );
	}
	
	public static Integer getEarlyHour(List<DayPlan<String>> plans)
	{
		if (plans == null )	return 0;
		
		
		LocalTime time = Utils.getTime( "23:59" );
		
		for ( DayPlan<String> plan : plans )
		{
			LocalTime time2 = Utils.getTime( plan.getWorkingTime().getStart() );
			if (time2.isBefore( time )) time = time2;
		}
		
		return time.getHourOfDay();
	}
	
	public static Integer getLaterHour(List<DayPlan<String>> plans)
	{
		if (plans == null )	return 23;
		
		
		LocalTime time = Utils.getTime( "00:00" );
		
		for ( DayPlan<String> plan : plans )
		{
			LocalTime time2 = Utils.getTime( plan.getWorkingTime().getEnd() );
			if (time2.isAfter( time )) time = time2;
		}
		
		//if (time.getMinuteOfHour() == 0) return time.getHourOfDay();
		
		return time.getHourOfDay();
	}
	
	public static boolean isAfter( String time1, String time2 )
	{
		LocalTime t1 = getTime( time1 );
		LocalTime t2 = getTime( time2 );
		
		return t1.isAfter( t2 );
	}

	public static boolean isBefore( String time1, String time2 )
	{
		LocalTime t1 = getTime( time1 );
		LocalTime t2 = getTime( time2 );
		
		return t1.isBefore( t2 );
	}
	
	public static TimeRange<Date> convertToRangeDate( Date date, TimeRange<LocalTime> timeRange )
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
	
	public static TimeRange<LocalTime> convertToTimeRange( Cita cita  )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( Utils.getDateHourFromLong( cita.getFecha() ) );
		
		LocalTime start = new LocalTime( calendar.get(Calendar.HOUR_OF_DAY), calendar.get( Calendar.MINUTE), 0 );
		
		calendar.setTime( Utils.getDateHourFromLong( cita.getFechafin() ) );
		LocalTime end = new LocalTime( calendar.get(Calendar.HOUR_OF_DAY), calendar.get( Calendar.MINUTE), 0 );
		
		return new TimeRange<LocalTime>( start, end );
	}
	
	public static List<TimeRange<Date>> getRangesDate( Date date, List<TimeRange<LocalTime>> timeRanges )
	{
		List<TimeRange<Date>> result = new ArrayList<>();
		
		for ( TimeRange<LocalTime> tr : timeRanges )
		{
			result.add( convertToRangeDate( date, tr ) );
		}
		
		return result;
	}
	
	public static TimeRange<LocalTime> timeRange( String start, String end )
	{
		LocalTime s = AppointmentUtils.getTime( start );
		LocalTime e = AppointmentUtils.getTime( end );

		return new TimeRange<LocalTime>( s, e );
	}

	public static boolean inside( CalendarEvent event, Date start, Date end )
	{
		if (event == null) return false;
		
		return (start.equals( event.getStart() ) || start.before( event.getStart() )) && end.after( event.getStart() );
	}
}
