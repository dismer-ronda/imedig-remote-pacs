package es.pryades.imedig.viewer.components.citations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
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
	private static final long serialVersionUID = 16654819655615842L;
	
	private Instalacion instalacion;
	private ImedigContext ctx;
	private EstudiosManager estudiosManager;
	private TiposEstudiosManager tiposEstudiosManager;
	private PacientesManager pacientesManager;
	
	private Calendar mainCalendar = GregorianCalendar.getInstance();
	
	private com.vaadin.ui.Calendar citationsCalendar;

	private int timeField = Calendar.MINUTE;
	private int amount = 10;

	public CitationsEventProvider(ImedigContext ctx, Instalacion instalacion, com.vaadin.ui.Calendar citationsCalendar)
	{
		this.ctx = ctx;
		this.instalacion = instalacion;
		this.citationsCalendar = citationsCalendar;
		
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
			if (citationsCalendar.isMonthlyMode()){
				return toCalendarEvents( (List<Estudio>)estudiosManager.getRows( ctx, query ));
			}else{
				return toCalendarEvents( (List<Estudio>)estudiosManager.getRows( ctx, query ), startDate, endDate);
			}
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
	
	private List<CalendarEvent> toCalendarEvents(List<Estudio> estudios, Date start, Date end) throws Throwable{
		
		List<CalendarEvent> result = new ArrayList<>();
		
		//Calendar calendar = GregorianCalendar.getInstance();
		//calendar.setTime( start );
		mainCalendar.setTime( start );
		
		Date start1 = start;
		
		//calendar.add( Calendar.HOUR_OF_DAY, 1 );
		//Date end1 = calendar.getTime();
		
		//Date end1 = Utils.getLastSecondHourAsDate( start1 );
		Date end1 = incNextTimePeriod();
		
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
		
		CalendarEvent event = head( events );
		
		while (!end.before( start1 )){
			
			if (event != null && inside(event, start1, end1)){
				start1 = addEvent(result, event, start1, end1);
				events = tail( events );
				event = head( events );
				
				while(start1.after( end1 )){
					end1 = incNextTimePeriod();
				}
				//if (start1.after( end1 )){
					//end1 = Utils.getLastSecondHourAsDate( start1 );
				//}
				
				//if (!Utils.isSameDay( start1, end1 )){
				//	start1 = end1;
				//	end1 = Utils.getLastSecondHourAsDate( start1 );
				//}
				
			}else{
				result.add( freeEvent( start1, end1 ) );
				start1 = end1;
				//calendar.add( Calendar.HOUR_OF_DAY, 1 );
				end1 = incNextTimePeriod();
				while(start1.after( end1 )){
					end1 = incNextTimePeriod();
				}
				//start1 = calendar.getTime();
				//end1 = Utils.getLastSecondHourAsDate( start1 );
				//end1 = incNextTimePeriod();
			}
			
		}
		
		return result;
	}
	
	private Date incNextTimePeriod()
	{
		mainCalendar.add( timeField, amount );
		
		return mainCalendar.getTime();
	}

	private Date addEvent( List<CalendarEvent> events, CalendarEvent event, Date start, Date end )
	{
		if (start.equals( event.getStart() )){
			events.add( event );
		}else{
			events.add( freeEvent( start, event.getStart() ) );
			events.add( event );
		}
		
		return event.getEnd();
	}

	private static boolean inside( CalendarEvent event, Date start, Date end )
	{
		return (start.equals( event.getStart() ) || start.before( event.getStart() )) && end.after( event.getStart() ) ;
	}

	private CalendarEvent toEvent(Estudio estudio) throws Throwable{
		
		TipoEstudio tipoEstudio = (TipoEstudio)tiposEstudiosManager.getRow( ctx, estudio.getTipo() );
		Paciente paciente  = (Paciente)pacientesManager.getRow( ctx, estudio.getPaciente() ); 
		
		CitationEvent event = new CitationEvent();
		event.setCaption( paciente.getNombreCompleto());
		event.setDescription( paciente.getNombreCompletoConIdentificador() );
		event.setStart( Utils.getDateHourFromLong( estudio.getFecha() ) );
		event.setEnd( Utils.getDateHourFromLong( estudio.getFechafin() ) );
		event.setData( estudio );
		
		return event;
	}
	
	private CalendarEvent freeEvent(Date start, Date end) {
		
		CitationEvent event = new CitationEvent();
		event.setStart( start );
		event.setEnd( end );
		event.setStyleName( "color2" );
		
		return event;
	}

	private static <T> T head(List<T> list){
		if (list == null || list.isEmpty()) return null;
		
		return list.get( 0 );
	}

	private static <T> T end(List<T> list){
		if (list == null || list.isEmpty()) return null;
		
		return list.get( list.size()-1 );
	}

	private static <T> List<T> init(List<T> list){
		if (list.isEmpty()) return list;
		
		return list.subList( 0, list.size()-1);
	}

	private static <T> List<T> tail(List<T> list){
		if (list.isEmpty()) return null;
		
		return list.subList( 1, list.size());
	}

}
