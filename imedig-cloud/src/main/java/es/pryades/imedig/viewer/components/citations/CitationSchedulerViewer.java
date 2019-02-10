package es.pryades.imedig.viewer.components.citations;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

public class CitationSchedulerViewer extends VerticalLayout implements ModalParent, EventClickHandler
{
	
	private static final long serialVersionUID = 1683377378764769059L;

	private ImedigContext ctx;
	@Getter
	private Instalacion instalacion;
	
	private CitationsEventProvider eventProvider;
	Calendar citationsCalendar;
	private CalendarPeriodPanel periodPanel;
	
	
	public CitationSchedulerViewer( ImedigContext ctx, Instalacion instalacion)
	{
		this.ctx = ctx;
		this.instalacion = instalacion;
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}

	private void buildComponents()
	{
		
		citationsCalendar = new Calendar();
		citationsCalendar.setLocale( UI.getCurrent().getLocale() );
		citationsCalendar.setFirstDayOfWeek( java.util.Calendar.MONDAY );
		citationsCalendar.setWeeklyCaptionFormat( "dd/MM/yyyy" );
		citationsCalendar.setTimeFormat( TimeFormat.Format24H );
		citationsCalendar.setEventCaptionAsHtml( true );
		citationsCalendar.setFirstVisibleHourOfDay( 8 );
		citationsCalendar.setLastVisibleHourOfDay( 17 );
		
		eventProvider = new CitationsEventProvider( ctx, instalacion, citationsCalendar );
		citationsCalendar.setEventProvider( eventProvider );

		periodPanel = new CalendarPeriodPanel( ctx , this);
		addComponent( periodPanel );		

		settingHandlers();
 		
		//java.util.Calendar cal = new GregorianCalendar().getInstance();
		//cal.setTime( new Date() );
		//cal.add( java.util.Calendar.MONTH, 1 );
		//ciationsCalendar.setEndDate( UtilsCalendar.getLastDayMonth( cal.getTime() ) );
		citationsCalendar.setWidth( "100%" );
		citationsCalendar.setHeight( "1200px" );
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setContent( citationsCalendar );
		panel.addStyleName( ValoTheme.PANEL_BORDERLESS );
		addComponent( panel );
		setExpandRatio( panel, 1.0f );
	}

	private void settingHandlers()
	{
		citationsCalendar.setHandler( (DateClickHandler)periodPanel );
		citationsCalendar.setHandler( (WeekClickHandler)periodPanel );
		citationsCalendar.setHandler( (ForwardHandler)periodPanel );
		citationsCalendar.setHandler( (BackwardHandler)periodPanel );
		citationsCalendar.setHandler( (EventClickHandler)this);
//		ciationsCalendar.setHandler( new RangeSelectHandler()
//		{
//			
//			@Override
//			public void rangeSelect( RangeSelectEvent event )
//			{
//				Notification.show( "RangeSelectEvent" , Notification.Type.HUMANIZED_MESSAGE );
//				
//			}
//		});
	}
	
	public void setDates(Date start, Date end){
		citationsCalendar.setStartDate( start );
		citationsCalendar.setEndDate( end );
	}
	
	public void newCitation(){
		ModalCitationDlg dlg = new ModalCitationDlg( ctx, Operation.OP_ADD, instalacion, null, this, "administracion.citas" );
		dlg.setDate( getStartDate() );
		dlg.showModalWindow();
	}
	
	private Date getStartDate(){
		Date start = citationsCalendar.getStartDate();
		Date end = citationsCalendar.getEndDate();
		Date today = new Date();
		
		if (end.before( today )) return today;
		
		if (today.before( end ) && !today.before( start )) return today;
		
		if (DateUtils.isSameDay( today, end )) return today;
		
		return start;
	}

	@Override
	public void refreshVisibleContent()
	{
		citationsCalendar.markAsDirty();
		
	}

	@Override
	public void eventClick( EventClick event )
	{
		CitationEvent citationEvent = (CitationEvent)event.getCalendarEvent();
		Operation operation = citationEvent.getData() == null ? Operation.OP_ADD : Operation.OP_MODIFY;
		ModalCitationDlg dlg = new ModalCitationDlg( ctx, operation, instalacion, citationEvent.getData(), this, "administracion.citas" );
		dlg.setDate( citationEvent.getStart() );
		dlg.setEndDate( citationEvent.getEnd() );
		dlg.showModalWindow();
	}

}
