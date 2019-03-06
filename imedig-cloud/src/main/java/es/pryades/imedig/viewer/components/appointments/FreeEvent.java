package es.pryades.imedig.viewer.components.appointments;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

public class FreeEvent extends BasicEvent
{
	private static final long serialVersionUID = -404118887254553070L;

	public FreeEvent()
	{
		super();
		setStyleName( "free" );
	}
	
	public FreeEvent(Date startDate, Date endDate) {
		this();
		setStart( startDate );
		setEnd( endDate );
    }
}
