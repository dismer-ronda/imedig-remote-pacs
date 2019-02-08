package es.pryades.imedig.viewer.components.citations;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

import es.pryades.imedig.cloud.dto.Estudio;
import lombok.Getter;
import lombok.Setter;

public class CitationEvent extends BasicEvent
{

	private static final long serialVersionUID = -7147097006359825905L;
	
	@Getter @Setter
	private Estudio data;

	public CitationEvent()
	{
		super();
	}
	
	public CitationEvent(String caption, String description, Date startDate, Date endDate, Estudio estudio) {
        super( caption, description, startDate, endDate );
        data = estudio;
    }
}
