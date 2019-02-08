package es.pryades.imedig.viewer.components.citations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.query.EstudioQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class CitationsEventProvider implements CalendarEventProvider
{
	private Instalacion instalacion;
	private ImedigContext ctx;
	private EstudiosManager estudiosManager;
	private TiposEstudiosManager tiposEstudiosManager;
	private PacientesManager pacientesManager;

	public CitationsEventProvider(ImedigContext ctx, Instalacion instalacion)
	{
		this.ctx = ctx;
		this.instalacion = instalacion;
		
		estudiosManager = (EstudiosManager)IOCManager.getInstanceOf( EstudiosManager.class ); 
		tiposEstudiosManager = (TiposEstudiosManager)IOCManager.getInstanceOf( TiposEstudiosManager.class ); 
		pacientesManager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class ); 
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
			return toCalendarEvents( (List<Estudio>)estudiosManager.getRows( ctx, query ));
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	private List<CalendarEvent> toCalendarEvents(List<Estudio> estudios) throws Throwable{
		
		List<CalendarEvent> result = new ArrayList<>();
		
		for ( Estudio estudio : estudios )
		{
			result.add( toEvent( estudio ) );
		}
		
		return result;
	}
	
	private CalendarEvent toEvent(Estudio estudio) throws Throwable{
		
		TipoEstudio tipoEstudio = (TipoEstudio)tiposEstudiosManager.getRow( ctx, estudio.getTipo() );
		Paciente paciente  = (Paciente)pacientesManager.getRow( ctx, estudio.getPaciente() ); 
		
		CitationEvent event = new CitationEvent();
		event.setCaption( tipoEstudio.getNombre()+"<br/>"+paciente.getNombreCompletoConIdentificador() );
		event.setDescription( paciente.getNombreCompletoConIdentificador() );
		event.setStart( Utils.getDateHourFromLong( estudio.getFecha() ) );
		event.setEnd( Utils.getDateHourFromLong( estudio.getFechafin() ) );
		event.setData( estudio );
		
		return event;
	}

	
}
