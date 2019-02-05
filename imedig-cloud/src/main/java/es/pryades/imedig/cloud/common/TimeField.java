package es.pryades.imedig.cloud.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class TimeField extends CustomField<Date>
{

	private static final long serialVersionUID = -6289756470700324446L;
	
	private HorizontalLayout root;
	private ComboBox hours;
	private ComboBox minutes;
	private ComboBox ampm;
	
	private int minHours = 0;
	private int maxHours = 23;
	
	private boolean use24HourFormat = true;
	
	private static final String AM = "AM";
	private static final String PM = "PM";
	
	public TimeField(){
		super();
		buildComponents();
	}

	public TimeField(String caption){
		this();
		setCaption( caption );
	}

	private void buildComponents()
	{
		hours = buildComboBox();
		fillHours();
		hours.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -6959562886441068713L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				updateInternalValue();
			}
		} );

		minutes = buildComboBox();
		fillMinutes();
		hours.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -641008316493683413L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				updateInternalValue();
			}
		} );
		ampm = buildComboBox();
		ampm.setWidth( "70px" );
		fillAmPm();
		Label label = new Label( ":" );
		label.setWidth( "10px" );
		Label label2 = new Label( "" );
		label2.setWidth( "6px" );
		label.addStyleName( ValoTheme.TEXTFIELD_ALIGN_CENTER );
		
		root = new HorizontalLayout( hours, label, minutes, label2, ampm );
		root.setComponentAlignment( label, Alignment.MIDDLE_CENTER );
		root.setSizeUndefined();
		updateComponents();
	}

	private ComboBox buildComboBox()
	{
		ComboBox combo = new ComboBox();
		combo.setImmediate( true );
		combo.setNullSelectionAllowed( false );
		combo.setNewItemsAllowed( false );
		combo.setFilteringMode( FilteringMode.OFF );
		combo.setTextInputAllowed( false );
		combo.setWidth( "65px" );
		
		return combo;
	}

	@Override
	protected Component initContent()
	{
		return root;
	}
	
	public void set24HourFormat(boolean use24hour) {
		use24HourFormat = use24hour;
		updateComponents();
	}

	private void updateComponents()
	{
		final Date val = getValue();
		
		if (use24HourFormat) 
			ampm.setVisible( false );
		else
			ampm.setVisible( true );
		
		if (val == null){
			hours.setValue( null );
			minutes.setValue( null );
			ampm.setValue( null );
			return;
		}
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( val );
		if (use24HourFormat) 
			hours.setValue( calendar.get( Calendar.HOUR_OF_DAY ) );
		else{
			hours.setValue( calendar.get( Calendar.HOUR ) );
			if (calendar.get( Calendar.AM_PM ) == Calendar.AM)
				ampm.setValue( AM );
			else
				ampm.setValue( PM );
		}
		
		minutes.setValue( calendar.get(Calendar.MINUTE) );
	}

	public boolean is24HourFormat() {
		return use24HourFormat;
	}
	
	@Override
	public void setTabIndex(int tabIndex) {
		hours.setTabIndex(tabIndex);
		minutes.setTabIndex(tabIndex);
		ampm.setTabIndex(tabIndex);
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		hours.setReadOnly(readOnly);
		minutes.setReadOnly(readOnly);
		ampm.setReadOnly(readOnly);
		super.setReadOnly(readOnly);
	}
	
	@Override
	public void setValue( Date newFieldValue ) throws com.vaadin.data.Property.ReadOnlyException, ConversionException
	{
		super.setValue( newFieldValue );
	}

	@Override
	public Class<? extends Date> getType()
	{
		return Date.class;
	}
	
	private void fillHours() {

		hours.removeAllItems();

		for (int i = minHours; i <= maxHours; i++) {
			hours.addItem(i);
			if (!use24HourFormat) {
				final Integer val = i == 0 || i == 12 ? 12 : i % 12;
				hours.setItemCaption(i, val + "");
			}else{
				hours.setItemCaption(i, i < 10 ? "0" + i : i + "");
			}
		}
	}

	private void fillMinutes() {

		minutes.removeAllItems();
		for (int i = 0; i < 60; i++) {
			minutes.addItem(i);
			minutes.setItemCaption(i, i < 10 ? "0" + i : i + "");
		}
	}

	private void fillAmPm()
	{
		ampm.addItem( AM );
		ampm.addItem( PM );
	}

	@Override
	protected void setInternalValue(Date newValue) {
		super.setInternalValue(newValue);
		updateComponents();
	}
	
	@Override
	public Date getValue()
	{
		return super.getValue();
	}
	
	private void updateInternalValue() {
		if (hours.getValue() == null || minutes.getValue() == null) return;
		
		Integer h = (Integer)hours.getValue();
		Integer m = (Integer)minutes.getValue();
		
		Date value = getValue();
		if (value == null){
			value = new Date();
		}
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( value );
		calendar.set( Calendar.HOUR_OF_DAY, h );
		calendar.set( Calendar.MINUTE, m );
		
		setValue( calendar.getTime() );
	}
	
	public Integer getHour(){
		return (Integer)hours.getValue();
	}

	public Integer getMinute(){
		return (Integer)minutes.getValue();
	}

}
