package es.pryades.imedig.viewer.components.appointments;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

public class OldEvent extends BasicEvent
{
	private static final long serialVersionUID = -404118887254553070L;

	public OldEvent()
	{
		super();
		setStyleName( "old" );
	}
	
	public OldEvent(Date startDate, Date endDate) {
		this();
        setStart( startDate );
        setEnd( endDate );
    }
}
