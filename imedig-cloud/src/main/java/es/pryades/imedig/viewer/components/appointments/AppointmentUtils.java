package es.pryades.imedig.viewer.components.appointments;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalTime;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.DayPlan;

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
}
