package es.pryades.imedig.viewer.components.citations;

import java.util.Date;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

public class CitationSchedulerViewer extends VerticalLayout implements ModalParent
{
	
	private static final long serialVersionUID = 1683377378764769059L;

	private ImedigContext ctx;
	@Getter
	private Instalacion instalacion;
	
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
		//ciationsCalendar.setStartDate( UtilsCalendar.getFirstDayMonth( new Date() ) );

		periodPanel = new CalendarPeriodPanel( ctx , this);
		addComponent( periodPanel );		

		settingHandlers();
 		
		//java.util.Calendar cal = new GregorianCalendar().getInstance();
		//cal.setTime( new Date() );
		//cal.add( java.util.Calendar.MONTH, 1 );
		//ciationsCalendar.setEndDate( UtilsCalendar.getLastDayMonth( cal.getTime() ) );
		citationsCalendar.setSizeFull();
		addComponent( citationsCalendar );
		setExpandRatio( citationsCalendar, 1.0f );
	}

	private void settingHandlers()
	{
		citationsCalendar.setHandler( (DateClickHandler)periodPanel );
		citationsCalendar.setHandler( (WeekClickHandler)periodPanel );
		citationsCalendar.setHandler( (ForwardHandler)periodPanel );
		citationsCalendar.setHandler( (BackwardHandler)periodPanel );
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
		dlg.setDate( new Date() );
		dlg.showModalWindow();
	}

	@Override
	public void refreshVisibleContent()
	{
		// TODO Auto-generated method stub
		
	}

}
