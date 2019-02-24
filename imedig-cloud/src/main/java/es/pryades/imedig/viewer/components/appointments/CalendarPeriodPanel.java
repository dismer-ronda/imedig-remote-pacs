package es.pryades.imedig.viewer.components.appointments;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.calendar.DateConstants;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicBackwardHandler;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicForwardHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.core.dto.ImedigContext;

public class CalendarPeriodPanel extends HorizontalLayout implements WeekClickHandler, DateClickHandler, ForwardHandler, BackwardHandler
{
	private static final long serialVersionUID = -8035020531428644459L;

	private ImedigContext ctx;
	
	private OptionGroup optionView;
	private Button buttonAddEvent;
	private ComboBox comboYear;
	private ComboBox comboMonth;
	private DateField dateFieldDay;
	
	private HorizontalLayout insideLayout;
	//private HorizontalLayout diaryLayout;
	private HorizontalLayout weeklyLayout;
	private HorizontalLayout monthlyLayout;
	
	private static final int PERIOD_MONTHLY = 1;
	private static final int PERIOD_WEEKLY = 2;
	
	private AppointmentSchedulerViewer viewer;
	private BasicDateClickHandler basicDateClickHandler;
	private BasicWeekClickHandler basicWeekClickHandler;
	private BasicForwardHandler basicForwardHandler;
	private BasicBackwardHandler basicBackwardHandler;
	
	private Integer lastWeek = null;
	private Integer lastYear = null;
	
	
	public CalendarPeriodPanel(ImedigContext ctx, AppointmentSchedulerViewer viewer){
		this.ctx = ctx;
		this.viewer = viewer;
		setWidth( "100%" );
		
		insideLayout = new HorizontalLayout();
		insideLayout.setSpacing( true );
		addComponent( insideLayout );
		setComponentAlignment( insideLayout, Alignment.MIDDLE_LEFT );
		
		basicDateClickHandler = new BasicDateClickHandler();
		basicWeekClickHandler = new BasicWeekClickHandler();
		basicForwardHandler = new BasicForwardHandler();
		basicBackwardHandler = new BasicBackwardHandler();
		
		buildComponent();
	}
	
	private void buildComponent()
	{
		optionView = new OptionGroup( ctx.getString( "words.view" ) );
		optionView.addStyleName( ValoTheme.OPTIONGROUP_HORIZONTAL );
		insideLayout.addComponent( optionView );
		insideLayout.setComponentAlignment( optionView, Alignment.BOTTOM_LEFT );
		fillOptionView();
		optionView.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -836123805155475215L;
			@Override
			public void valueChange( ValueChangeEvent event )
			{
				changeView();
			}
		} );
		
		/*comboView = new ComboBox( ctx.getString( "words.view" ) );
		comboView.setNullSelectionAllowed( false );
		comboView.setNewItemsAllowed( false );
		insideLayout.addComponent( comboView );
		insideLayout.setComponentAlignment( comboView, Alignment.BOTTOM_LEFT );
		fillComboView();
		comboView.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -836123805155475215L;
			@Override
			public void valueChange( ValueChangeEvent event )
			{
				changeView();
			}
		} );*/
		//buildAddButton();

		buildMonthlyComponents();
		buildWeeklyComponents();
		//buildDiaryComponents();
		
		insideLayout.addComponents( monthlyLayout, weeklyLayout/*, diaryLayout*/ );
		insideLayout.setComponentAlignment( monthlyLayout, Alignment.BOTTOM_LEFT );
		insideLayout.setComponentAlignment( weeklyLayout, Alignment.BOTTOM_LEFT );
		//insideLayout.setComponentAlignment( diaryLayout, Alignment.BOTTOM_LEFT );
		//changeView();
		optionView.setValue( PERIOD_MONTHLY );
	}


	private void buildAddButton()
	{
		buttonAddEvent = new Button( FontAwesome.CLOCK_O );
		buttonAddEvent.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		buttonAddEvent.addStyleName( ValoTheme.BUTTON_PRIMARY );
		buttonAddEvent.setDescription( ctx.getString( "words.appointments.add" ) );
		insideLayout.addComponent( buttonAddEvent );
		insideLayout.setComponentAlignment( buttonAddEvent, Alignment.BOTTOM_LEFT );
		
		buttonAddEvent.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 7034719015606420707L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				viewer.newCitation();
			}
		} );
		
	}

	private void buildMonthlyComponents()
	{
		comboMonth = new ComboBox( ctx.getString( "words.month" ) );
		comboMonth.setNullSelectionAllowed( false );
		comboMonth.setNewItemsAllowed( false );
		fillMonths();
		comboMonth.setValue( UtilsCalendar.getCurrentMonth() );

		comboYear = new ComboBox( ctx.getString( "words.year" ) );
		comboYear.setNullSelectionAllowed( false );
		comboYear.setNewItemsAllowed( false );
		fillYears();
		comboYear.setValue( UtilsCalendar.getCurrentYear() );

		comboMonth.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -7882174190476897841L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				monthlyView();
			}
		} );

		
		comboYear.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -7882174190476897841L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				monthlyView();
			}
		} );
		
		monthlyLayout = new HorizontalLayout( comboMonth, comboYear );
		monthlyLayout.setSpacing( true );
	}


	private void fillYears()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( new Date() );
		Integer year = calendar.get( Calendar.YEAR ) - 1;
		
		for ( int i = 0; i < 5; i++ )
		{
			Integer id = year++;
			comboYear.addItem( id );
			comboYear.setItemCaption( id , ""+id);
		}
	}

	private void buildWeeklyComponents()
	{
		weeklyLayout = new HorizontalLayout();
	}


	private void buildDiaryComponents()
	{
		dateFieldDay = new DateField( ctx.getString( "words.day" ) );
		dateFieldDay.setResolution( Resolution.DAY );
		dateFieldDay.setDateFormat( "dd/MM/yyyy" );
		dateFieldDay.setValue( new Date() );
		
		dateFieldDay.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -30896360324368694L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				diaryView();
			}
		} );
		
//		diaryLayout = new HorizontalLayout( dateFieldDay );
//		diaryLayout.setSpacing( true );
	}


	private void fillOptionView()
	{
		optionView.addItem( PERIOD_MONTHLY );
		optionView.setItemCaption( PERIOD_MONTHLY, ctx.getString( "words.monthly" ) );
		//optionView.setItemIcon( PERIOD_MONTHLY, FontAwesome.TH );
		optionView.addItem( PERIOD_WEEKLY );
		optionView.setItemCaption( PERIOD_WEEKLY, ctx.getString( "words.weekly" ) );
		//optionView.setItemIcon( PERIOD_WEEKLY, FontAwesome.TH_LARGE );
//		optionView.addItem( PERIOD_DIARY );
//		optionView.setItemCaption( PERIOD_DIARY, ctx.getString( "words.diary" ) );
		//optionView.setItemIcon( PERIOD_DIARY, FontAwesome.TH_LIST );
	}
	
	private void changeView(){
		Integer period = (Integer)optionView.getValue();
		switch ( period )
		{
//			case PERIOD_DIARY:
//				diaryView();
//				break;
			case PERIOD_WEEKLY:
				weeklyView();
				break;
			case PERIOD_MONTHLY:
				monthlyView();
				break;
			default:
				break;
		}
		visibleViewComponent();
	}


	private void diaryView()
	{
		dateClick(new DateClickEvent( viewer.citationsCalendar, dateFieldDay.getValue() ));
	}


	private void weeklyView()
	{
		if (lastWeek == null){
			Calendar calendar =  GregorianCalendar.getInstance();
			calendar.setTime( new Date() );
			lastYear = calendar.get( Calendar.YEAR);
			lastWeek = calendar.get( Calendar.WEEK_OF_YEAR);
		}
		
		WeekClick weekEvent = new WeekClick( viewer.citationsCalendar, lastWeek, lastYear );
		weekClick( weekEvent );
	}


	private void monthlyView()
	{
		Calendar calendar =  GregorianCalendar.getInstance();
		calendar.set( Calendar.DAY_OF_MONTH, 1 );
		calendar.set( Calendar.MONTH, (Integer)comboMonth.getValue() );
		calendar.set( Calendar.YEAR, (Integer)comboYear.getValue() );
		
		Date start = UtilsCalendar.getFirstDayMonth( calendar.getTime() );
		Date end = UtilsCalendar.getLastDayMonth( calendar.getTime() );
		
		viewer.setDates( start, end );
	}
	
	private void visibleViewComponent(){
		Integer period = (Integer)optionView.getValue();
		//diaryLayout.setVisible( period.equals( PERIOD_DIARY ) );
		weeklyLayout.setVisible( period.equals( PERIOD_WEEKLY ) );
		monthlyLayout.setVisible( period.equals( PERIOD_MONTHLY ) );
	}


	private void fillMonths(){
		comboMonth.addItem( Calendar.JANUARY );
		comboMonth.setItemCaption( Calendar.JANUARY, ctx.getString("month."+(Calendar.JANUARY+1)));
		comboMonth.addItem( Calendar.FEBRUARY );
		comboMonth.setItemCaption( Calendar.FEBRUARY, ctx.getString("month."+(Calendar.FEBRUARY+1)));
		comboMonth.addItem( Calendar.MARCH );
		comboMonth.setItemCaption( Calendar.MARCH, ctx.getString("month."+(Calendar.MARCH+1)));
		comboMonth.addItem( Calendar.APRIL );
		comboMonth.setItemCaption( Calendar.APRIL, ctx.getString("month."+(Calendar.APRIL+1)));
		comboMonth.addItem( Calendar.MAY );
		comboMonth.setItemCaption( Calendar.MAY, ctx.getString("month."+(Calendar.MAY+1)));
		comboMonth.addItem( Calendar.JUNE );
		comboMonth.setItemCaption( Calendar.JUNE, ctx.getString("month."+(Calendar.JUNE+1)));
		comboMonth.addItem( Calendar.JULY );
		comboMonth.setItemCaption( Calendar.JULY, ctx.getString("month."+(Calendar.JULY+1)));
		comboMonth.addItem( Calendar.AUGUST );
		comboMonth.setItemCaption( Calendar.AUGUST, ctx.getString("month."+(Calendar.AUGUST+1)));
		comboMonth.addItem( Calendar.SEPTEMBER );
		comboMonth.setItemCaption( Calendar.SEPTEMBER, ctx.getString("month."+(Calendar.SEPTEMBER+1)));
		comboMonth.addItem( Calendar.OCTOBER );
		comboMonth.setItemCaption( Calendar.OCTOBER, ctx.getString("month."+(Calendar.OCTOBER+1)));
		comboMonth.addItem( Calendar.NOVEMBER );
		comboMonth.setItemCaption( Calendar.NOVEMBER, ctx.getString("month."+(Calendar.NOVEMBER+1)));
		comboMonth.addItem( Calendar.DECEMBER );
		comboMonth.setItemCaption( Calendar.DECEMBER, ctx.getString("month."+(Calendar.DECEMBER+1)));
	}

	@Override
	public void dateClick( DateClickEvent event )
	{
		return;
//		optionView.setValue( PERIOD_DIARY );
//		dateFieldDay.setValue( event.getDate() );
//		
//		basicDateClickHandler.dateClick( event );
	}

	@Override
	public void weekClick( WeekClick event )
	{
		lastWeek = event.getWeek();
		lastYear = event.getYear();
		
		optionView.setValue( PERIOD_WEEKLY );
		basicWeekClickHandler.weekClick( event );
	}

	@Override
	public void backward( BackwardEvent event )
	{
		Date start = event.getComponent().getStartDate();
		Date end = event.getComponent().getEndDate();
		
		settingDayOrWeek( start, end, -1 );
		
		//basicBackwardHandler.backward( event );
	}

	@Override
	public void forward( ForwardEvent event )
	{
		Date start = event.getComponent().getStartDate();
		Date end = event.getComponent().getEndDate();
		
		settingDayOrWeek( start, end, 1 );

		//basicForwardHandler.forward( event );
	}
	
	private void settingDayOrWeek(Date start, Date end, int amount){
		if (getDurationInDays( start, end ) == 1){
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime( end );
			calendar.add( Calendar.DATE, amount );
			dateFieldDay.setValue( calendar.getTime() );
		}else{
			Calendar calendar =  GregorianCalendar.getInstance();
			calendar.setTime( start );
			calendar.add( Calendar.WEEK_OF_YEAR, amount );
			lastYear = calendar.get( Calendar.YEAR);
			lastWeek = calendar.get( Calendar.WEEK_OF_YEAR);
			WeekClick weekEvent = new WeekClick( viewer.citationsCalendar, lastWeek, lastYear );
			weekClick( weekEvent );
		}
	}

	private int getDurationInDays(Date start, Date end){
        int duration = (int) (((end.getTime()) - start.getTime())/ DateConstants.DAYINMILLIS);
        duration++;
        
        if (duration<0) duration = -duration;
        
        return duration;
	}
}
