package es.pryades.imedig.viewer.components.citations;

import java.util.Date;
import java.util.List;

import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import es.pryades.imedig.cloud.dto.Instalacion;

public class CitationsEventProvider implements CalendarEventProvider
{
	private Instalacion instalacion;
	

	@Override
	public List<CalendarEvent> getEvents( Date startDate, Date endDate )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
