package es.pryades.imedig.cloud.common;

import org.joda.time.LocalTime;

import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class TimeField extends CustomField<LocalTime>
{
	private static final long serialVersionUID = 5119116079055221227L;

	private static final LocalTime TIME_CERO = new LocalTime( 0, 0, 0 );

	private HorizontalLayout root;
	private ComboBox hourComboBox;
	private ComboBox minuteComboBox;

	protected boolean maskValueChange = false;

	private int minHours = 0;
	private int maxHours = 23;
	private int minuteSteep = 5;

	public TimeField()
	{
		hourComboBox = buildSelectedTime();
		minuteComboBox = buildSelectedTime();
		Label label = new Label( ":" );
		label.setWidth( "-1px" );

		root = new HorizontalLayout( hourComboBox, label, minuteComboBox );
		root.setHeight( null );
		root.setWidth( null );

		fillHours();
		fillMinutes();
		updateFields();
	}

	public TimeField( String caption )
	{
		this();
		setCaption( caption );
	}

	@Override
	protected Component initContent()
	{
		return root;
	}

	@Override
	public void setValue( LocalTime value )
	{
		//super.setValue( value );

		setHours( value.getHourOfDay() );
		setMinutes( value.getMinuteOfHour() );
	}

	public void setHours( int hours ) throws IllegalArgumentException
	{

		if ( hours < minHours || hours > maxHours )
		{
			throw new IllegalArgumentException( "Value '" + hours + "' is outside bounds '" + minHours + "' - '" + maxHours + "'" );
		}

		setHoursInternal( hours );

	}

	public int getMinutes()
	{
		return getMinutesInternal();
	}

	/**
	 * @param minutes
	 *
	 * @throws IllegalArgumentException
	 *             If the minute parameter is not compatible with
	 *             {@link #getMinuteInterval()}
	 */
	public void setMinutes( int minutes )
	{
		if ( minutes % minuteSteep != 0 )
		{
			throw new IllegalArgumentException( "Value '" + minutes + "' is not compatible with interval '" + minuteSteep + "'" );
		}

		setMinutesInternal( minutes );
	}

	private void updateFields()
	{

		maskValueChange = true;

		final Object val = getValue();

		// make sure to update alternatives, wont spoil value above
		fillHours();
		fillMinutes();

		if ( val == null )
		{
			// clear values
			hourComboBox.setValue( null );
			minuteComboBox.setValue( null );

			maskValueChange = false;
			return;
		}

		checkBoundsAndInterval();

		hourComboBox.setValue( getHoursInternal() );
		minuteComboBox.setValue( getMinutesInternal() );

		maskValueChange = false;
	}

	private void checkBoundsAndInterval()
	{
		// check hour bounds
		if ( getHoursInternal() < minHours )
		{
			// guess
			int compatibleVal = getHoursInternal();
			while ( compatibleVal < 24 )
			{
				if ( compatibleVal >= minHours )
				{
					break;
				}
				compatibleVal++;
			}
			if ( compatibleVal >= minHours )
			{
				setHoursInternal( compatibleVal );
			}
			else
			{
			}
		}
		else if ( getHoursInternal() > maxHours )
		{
			// guess
			int compatibleVal = getHoursInternal();
			while ( compatibleVal > 0 )
			{
				if ( compatibleVal <= maxHours )
				{
					break;
				}
				compatibleVal--;
			}
			if ( compatibleVal <= maxHours )
			{
				setHoursInternal( compatibleVal );
			}
			else
			{
				// no acceptable hour found. Most likely user is changing
				// bounds, and will fix with another call to the other bound.
			}
		}

		// check minute interval
		if ( getMinutesInternal() % minuteSteep != 0 )
		{
			// guess
			int compatibleVal = getMinutesInternal();
			while ( compatibleVal > 0 )
			{
				if ( compatibleVal % minuteSteep == 0 )
				{
					break;
				}
				compatibleVal--;
			}
			setMinutesInternal( compatibleVal );
		}
	}

	private int getMinutesInternal()
	{
		return getValue().getMinuteOfHour();
	}

	private int getHoursInternal()
	{
		return getValue().getHourOfDay();
	}

	private void setHoursInternal( int val )
	{
		if ( getValue() == null )
		{
			resetValue();
		}
		LocalTime value = getValue();
		value = value.withHourOfDay( val );
		setValue( value, true );
		hourComboBox.setValue( val );
	}

	private void setMinutesInternal( int val )
	{
		if ( getValue() == null )
		{
			resetValue();
		}
		LocalTime value = getValue();
		value = value.withMinuteOfHour( val );
		setValue( value, true );
		minuteComboBox.setValue( val );
	}

	private void fillHours()
	{

		hourComboBox.removeAllItems();

		for ( int i = minHours; i <= maxHours; i++ )
		{
			hourComboBox.addItem( i );
			hourComboBox.setItemCaption( i, String.format( "%02d", i ) );
		}
	}

	private void fillMinutes()
	{

		minuteComboBox.removeAllItems();
		for ( int i = 0; i < 60; i++ )
		{
			if ( i % minuteSteep == 0 )
			{
				minuteComboBox.addItem( i );
				minuteComboBox.setItemCaption( i, String.format( "%02d", i ) );
			}
		}
	}

	@Override
	public Class<? extends LocalTime> getType()
	{
		return LocalTime.class;
	}

	private ComboBox buildSelectedTime()
	{
		final ComboBox comboBox = new ComboBox();
		comboBox.setWidth( "55px" );
		comboBox.addStyleName( ValoTheme.COMBOBOX_SMALL );
		comboBox.setImmediate( true );
		comboBox.setNullSelectionAllowed( false );
		comboBox.setTextInputAllowed( false );
		comboBox.setNewItemsAllowed( false );
		comboBox.addValueChangeListener( new ValueChangeListener()
		{

			private static final long serialVersionUID = -6959562886441068713L;

			@Override
			public void valueChange( Property.ValueChangeEvent event )
			{
				if ( maskValueChange )
				{
					return;
				}
				maskValueChange = true;
				updateValue();
				maskValueChange = false;
				fireValueChange( true );
			}

		} );

		return comboBox;
	}

	private void updateValue()
	{
		if ( minuteComboBox.getValue() == null )
		{
			minuteComboBox.setValue( 0 );
		}
		if ( hourComboBox.getValue() == null )
		{
			hourComboBox.setValue( 0 );
		}

		resetValue();

		int h = (Integer)hourComboBox.getValue();
		int m = (Integer)minuteComboBox.getValue();

		setInternalValue( h, m );
	}

	private void setInternalValue( int h, int m )
	{
		setValue( new LocalTime( h, m, 0 ) );
	}

	private void resetValue()
	{
		super.setValue( TIME_CERO );
	}

	public void setMinuteSteep( int interval )
	{
		if ( interval < 0 )
		{
			interval = 0;
		}
		minuteSteep = interval;
		updateFields();
	}

	public int getMinuteSteep()
	{
		return minuteSteep;
	}

	public int getHourMin()
	{
		return minHours;
	}

	/**
	 * Set the minimum allowed value for the hour, in 24h format. Defaults to 0.
	 * <p>
	 * Changing this setting so that the field has a value outside the new
	 * bounds will lead to the field resetting its value to the first bigger
	 * value that is valid.
	 *
	 */
	public void setHourMin( int minHours )
	{

		if ( minHours < 0 )
		{
			minHours = 0;
		}
		if ( minHours > 23 )
		{
			minHours = 23;
		}

		this.minHours = minHours;
		updateFields();
	}

	public int getHourMax()
	{
		return maxHours;
	}

	/**
	 * Set the maximum allowed value for the hour, in 24h format. Defaults to
	 * 23.
	 * <p>
	 * Changing this setting so that the field has a value outside the new
	 * bounds will lead to the field resetting its value to the first smaller
	 * value that is valid.
	 *
	 */
	public void setHourMax( int maxHours )
	{

		if ( maxHours < 0 )
		{
			maxHours = 0;
		}
		if ( maxHours > 23 )
		{
			maxHours = 23;
		}

		this.maxHours = maxHours;
		updateFields();
	}

	@Override
	protected void fireValueChange( boolean repaintIsNotNeeded )
	{
		if ( maskValueChange )
		{
			return;
		}
		super.fireValueChange( repaintIsNotNeeded );
	}

	@Override
	public void setTabIndex( int tabIndex )
	{
		hourComboBox.setTabIndex( tabIndex );
		minuteComboBox.setTabIndex( tabIndex );
	}

	@Override
	public int getTabIndex()
	{
		return hourComboBox.getTabIndex();
	}

	@Override
	public void setReadOnly( boolean readOnly )
	{
		hourComboBox.setReadOnly( readOnly );
		minuteComboBox.setReadOnly( readOnly );
		super.setReadOnly( readOnly );
	}
	
	@Override
	public void focus(  )
	{
		hourComboBox.focus();
	}
}
