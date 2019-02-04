package es.pryades.imedig.viewer.components.citations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * @author hector.licea
 *
 */
public class UtilsCalendar
{
	public static Date getFirstDayMonth( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		calendar.set( Calendar.DAY_OF_MONTH, 1 );

		return calendar.getTime();
	}
	
	public static Date getLastDayMonth( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		calendar.set( Calendar.DAY_OF_MONTH, calendar.getActualMaximum( Calendar.DAY_OF_MONTH ));
		
		return calendar.getTime();
	}
	
	public static Date getFirstDayWeek( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY )
		{
			calendar.add( Calendar.DAY_OF_MONTH, -1 );
		}

		return calendar.getTime();
	}
	
	public static Date getLastDayWeek( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY )
		{
			calendar.add( Calendar.DAY_OF_MONTH, 1 );
		}

		return calendar.getTime();
	}
	
	public static int getCurrentMonth( )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( new Date() );
		
		return calendar.get(Calendar.MONTH);
	}
	
	public static int getCurrentYear( )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( new Date() );
		
		return calendar.get(Calendar.YEAR);
	}
	

}
