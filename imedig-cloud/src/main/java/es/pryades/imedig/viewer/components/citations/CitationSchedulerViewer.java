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
	@Getter
	private Calendar ciationsCalendar;
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
		
		ciationsCalendar = new Calendar();
		ciationsCalendar.setLocale( UI.getCurrent().getLocale() );
		ciationsCalendar.setFirstDayOfWeek( java.util.Calendar.MONDAY );
		//ciationsCalendar.setStartDate( UtilsCalendar.getFirstDayMonth( new Date() ) );

		periodPanel = new CalendarPeriodPanel( ctx , this);
		addComponent( periodPanel );		

		settingHandlers();
 		
		//java.util.Calendar cal = new GregorianCalendar().getInstance();
		//cal.setTime( new Date() );
		//cal.add( java.util.Calendar.MONTH, 1 );
		//ciationsCalendar.setEndDate( UtilsCalendar.getLastDayMonth( cal.getTime() ) );
		ciationsCalendar.setSizeFull();
		addComponent( ciationsCalendar );
		setExpandRatio( ciationsCalendar, 1.0f );
	}

	private void settingHandlers()
	{
		ciationsCalendar.setHandler( (DateClickHandler)periodPanel );
		ciationsCalendar.setHandler( (WeekClickHandler)periodPanel );
		ciationsCalendar.setHandler( (ForwardHandler)periodPanel );
		ciationsCalendar.setHandler( (BackwardHandler)periodPanel );
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
		ciationsCalendar.setStartDate( start );
		ciationsCalendar.setEndDate( end );
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
