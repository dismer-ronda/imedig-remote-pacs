package es.pryades.imedig.viewer.components.appointments;

import java.util.Date;
import java.util.List;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

public class AppointmentEventProvider1 implements CalendarEventProvider
{
	private static final long serialVersionUID = -1775971585784664700L;

	private Calendar calendar;
	private AppointmentEventResource eventResource;
	
	public AppointmentEventProvider1( Calendar calendar, AppointmentEventResource eventResource )
	{
		this.calendar = calendar;
		this.eventResource = eventResource;
	}
	
	@Override
	public List<CalendarEvent> getEvents( Date startDate, Date endDate )
	{
		if ( calendar.isMonthlyMode() )
			return eventResource.getMonthlyEvents( startDate, endDate );
		
		return eventResource.getWeeklyEvents( startDate, endDate );
	}

}
