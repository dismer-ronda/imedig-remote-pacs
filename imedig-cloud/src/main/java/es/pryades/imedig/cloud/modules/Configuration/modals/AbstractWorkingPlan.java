package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalTime;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

import es.pryades.imedig.cloud.common.TimeAfterRangeValidator;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.TimeRange;

public abstract class AbstractWorkingPlan extends GridLayout
{
	private static final long serialVersionUID = 8077644458209233319L;

	protected final ImedigContext ctx;
	private List<DayPlan<String>> diaryPlan;
	private Map<Integer, DiaryComponents> diaryComponents = new HashMap<>();

	private static final int COL_DAY = 0;
	private static final int COL_START = 1;
	private static final int COL_END = 2;
	private static final int COL_BREAK = 3;

	private static final String TIME_FORMAT = "HH:mm";
	private static final String DEFAULT_START = "08:00";
	private static final String DEFAULT_END = "18:00";

	//private Validator validator = new TimeValidador();

	private final List<Integer> days;

	public AbstractWorkingPlan( ImedigContext ctx, List<DayPlan<String>> diaryPlan )
	{
		super( 4, 8 );
		this.ctx = ctx;
		this.diaryPlan = diaryPlan;

		setMargin( true );
		setSpacing( true );
		setWidth( "100%" );

		days = initDays();

		buildComponents();
	}

	protected abstract List<Integer> initDays();

	private void buildComponents()
	{
		addHeader();
		int row = 1;
		for ( Integer day : days )
		{
			addDay( day, row );
			++row;
		}
	}

	private void addDay( int day, int row )
	{
		CheckBox checkBox = getCheckBox( day );

		TimeField fieldStart = new TimeField();
		fieldStart.setValue( LocalTime.parse( DEFAULT_START ) );
		final TimeField fieldEnd = new TimeField();
		fieldEnd.setWidth( "60px" );
		fieldEnd.setValue( LocalTime.parse(DEFAULT_END) );
		fieldEnd.setCaption( "" );
		FormLayout l = new FormLayout( fieldEnd );
		l.setMargin( false );
		l.setWidth( "-1px" );

		addComponent( checkBox, COL_DAY, row );
		addComponent( fieldStart, COL_START, row );
		addComponent( l, COL_END, row );
		
		fieldEnd.setValidationVisible( true );
		Validator validatorEnd = new TimeAfterRangeValidator( fieldStart, ctx.getString( "words.daterange.error" ) );
		fieldEnd.addValidator( validatorEnd );
		final ValueChangeListener changeListener = new ValueChangeListener()
		{
			private static final long serialVersionUID = 46676985918961579L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				fieldEnd.setValidationVisible(false);
				try {
					fieldEnd.validate();
				} catch (InvalidValueException e) {
					fieldEnd.setValidationVisible(true);
				}
			}
		};
		
		fieldStart.addValueChangeListener( changeListener );
		fieldEnd.addValueChangeListener( changeListener );

		BreakComponent breakComponent = new BreakComponent( ctx, day );
		addComponent( breakComponent, COL_BREAK, row );

		setComponentAlignment( fieldStart, Alignment.TOP_CENTER );
		setComponentAlignment( l, Alignment.TOP_CENTER );
		setComponentAlignment( breakComponent, Alignment.TOP_CENTER );

		diaryComponents.put( day, new DiaryComponents( day, checkBox, fieldStart, fieldEnd, breakComponent ) );
		checkBox.setValue( true );

		settingPlanValuesValue( day, checkBox, fieldStart, fieldEnd, breakComponent );
	}

	protected CheckBox getCheckBox( int day )
	{
		CheckBox checkBox = new CheckBox( ctx.getString( getDay( day ) ) );
		checkBox.setData( day );
		checkBox.setValue( true );
		
		return checkBox;
	}
	
	private void settingPlanValuesValue( int day, CheckBox checkBox, TimeField fieldStart, TimeField fieldEnd, BreakComponent breakComponent )
	{
		DayPlan<String> plan = getDayPlan( day );

		if ( plan == null )
		{
			if (!checkBox.isReadOnly()) checkBox.setValue( false );
			return;
		}

		checkBox.setValue( true );
		fieldStart.setValue( LocalTime.parse( plan.getWorkingTime().getStart()) );
		fieldEnd.setValue( LocalTime.parse( plan.getWorkingTime().getEnd()) );
		breakComponent.setBreaks( plan.getBreaks() );
	}

	private DayPlan<String> getDayPlan( Integer day )
	{
		if ( diaryPlan == null )
			return null;

		for ( DayPlan<String> dayPlan : diaryPlan )
		{
			if ( dayPlan.getDay().equals( day ) )
				return dayPlan;
		}

		return null;
	}

	private void addHeader()
	{
		Label labelDay = new Label( ctx.getString( "words.day" ) );
		labelDay.setWidth( "-1px" );
		addComponent( labelDay, COL_DAY, 0 );
		Label labelStart = new Label( ctx.getString( "words.start" ) );
		labelStart.setWidth( "-1px" );
		addComponent( labelStart, COL_START, 0 );
		Label labelEnd = new Label( ctx.getString( "words.end" ) );
		labelEnd.setWidth( "-1px" );
		addComponent( labelEnd, COL_END, 0 );
		Label labelBreak = new Label( ctx.getString( "words.breaks" ) );
		labelBreak.setWidth( "-1px" );
		addComponent( labelBreak, COL_BREAK, 0 );

		setComponentAlignment( labelStart, Alignment.TOP_CENTER );
		setComponentAlignment( labelEnd, Alignment.TOP_CENTER );
		setComponentAlignment( labelBreak, Alignment.TOP_CENTER );

		setColumnExpandRatio( COL_DAY, 1 );
		setColumnExpandRatio( COL_START, 2 );
		setColumnExpandRatio( COL_END, 3 );
		setColumnExpandRatio( COL_BREAK, 5 );

	}

	private static String getDay( int day )
	{
		switch ( day )
		{
			case Calendar.MONDAY:
				return "day.1";
			case Calendar.TUESDAY:
				return "day.2";
			case Calendar.WEDNESDAY:
				return "day.3";
			case Calendar.THURSDAY:
				return "day.4";
			case Calendar.FRIDAY:
				return "day.5";
			case Calendar.SATURDAY:
				return "day.6";
			case Calendar.SUNDAY:
				return "day.7";
			default:
				break;
		}

		return "";
	}

	public boolean isValid()
	{
		for ( Integer day : days )
		{
			if ( !diaryComponents.get( day ).isValid() )
				return false;
		}

		return true;
	}
	
	public boolean isValidRanges()
	{
		for ( Integer day : days )
		{
			if ( !diaryComponents.get( day ).isValidBreaksRanges() )
				return false;
		}

		return true;
	}



	public List<DayPlan<String>> getWeekPlan()
	{
		List<DayPlan<String>> result = new ArrayList<>();

		for ( Integer day : days )
		{
			DayPlan<String> plan = diaryComponents.get( day ).getDayPlan();

			if ( plan != null )
				result.add( plan );
		}

		return result;
	}

	private class DiaryComponents implements Serializable
	{
		private static final long serialVersionUID = 3896537981313587666L;

		int day;
		CheckBox checkBox;
		TimeField start;
		TimeField end;
		BreakComponent breaks;

		public DiaryComponents( int day, CheckBox checkBox, TimeField start, TimeField end, BreakComponent breaks )
		{
			this.day = day;
			this.checkBox = checkBox;
			this.start = start;
			this.end = end;
			this.breaks = breaks;

			settingListeners();
		}

		private boolean isValidBreaksRanges()
		{
			return breaks.isValidBreaksRanges();
		}

		public boolean isValid()
		{
			if ( !checkBox.getValue() )
				return true;

			return start.isValid() && end.isValid() && Utils.isValidTimeRange( start.getValue(), end.getValue() ) && breaks.isValidBreaksTime();
		}

		public DayPlan<String> getDayPlan()
		{
			if ( !checkBox.getValue() )
				return null;

			if ( !isValid() )
				return null;

			DayPlan<String> plan = new DayPlan<>();
			plan.setBreaks( breaks.getBreaksTime() );
			plan.setDay( day );
			plan.setWorkingTime( new TimeRange<String>( start.getValue().toString( TIME_FORMAT ), end.getValue().toString( TIME_FORMAT ) ) );

			return plan;
		}

		private void settingListeners()
		{
			//start.addValidator( validator );
			//end.addValidator( validator );

			checkBox.addValueChangeListener( new ValueChangeListener()
			{

				@Override
				public void valueChange( ValueChangeEvent event )
				{
					start.setEnabled( checkBox.getValue() );
					end.setEnabled( checkBox.getValue() );
					breaks.setEnabled( checkBox.getValue() );
					
					if (checkBox.getValue()) {
						start.focus();
					}
				}
			} );
		}
	}
}
