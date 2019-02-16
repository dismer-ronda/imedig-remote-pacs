package es.pryades.imedig.cloud.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.vaadin.inputmask.InputMask;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;

public class TimeField2 extends CustomField<Date>
{

	private static final long serialVersionUID = -6289756470700324446L;
	
	
	private static final String REGEX = "(?:[01]\\d|2[0123]):(?:[012345]\\d)";
	private static final DateFormat timeFormat = new SimpleDateFormat( "HH:mm" );
	private static final DateFormat timeIntFormat = new SimpleDateFormat( "HH:mm" );
	private TextField root;
	private InputMask mask;
	
	private boolean created = false;
	
	public TimeField2(){
		super();
		//setBuffered( true );
		buildComponents();
		created = true;
	}

	public TimeField2(String caption){
		this();
		setCaption( caption );
	}

	private void buildComponents()
	{
		root = new TextField( );
		root.setWidth( "100%" );
		root.setImmediate( true );
		root.setNullRepresentation( "" );
		mask = new InputMask( REGEX );
		mask.setRegexMask( true );
		mask.setPlaceholder( " " );
		mask.extend( root );
		root.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -3976104265140432118L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				updateInternalValue();
			}
		} );

		updateComponents();
	}

	@Override
	protected Component initContent()
	{
		return root;
	}
	
	private void updateComponents()
	{
		if (!created) return;
		
		final Date val = getValue();
		
		if (val == null){
			root.setValue( null );
			return;
		}
		
		root.setValue( timeFormat.format( clearDate( val )) );
	}
	
	@Override
	protected Date getInternalValue() {
		Date date = super.getInternalValue(); 
        return clearDate( date );
    }
	
	
	
	private static void clearDate(Calendar calendar){
		calendar.set( Calendar.DAY_OF_MONTH, 0 );
		calendar.set( Calendar.MONTH, 0 );
		calendar.set( Calendar.YEAR, 0 );
		calendar.set( Calendar.SECOND, 0 );
	}
	
	private static Date clearDate(Date date){
		if (date == null) return null;
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		clearDate( calendar );
		
		return calendar.getTime();
	}

	@Override
	public void setTabIndex(int tabIndex) {
		root.setTabIndex(tabIndex);
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		root.setReadOnly(readOnly);
		super.setReadOnly(readOnly);
	}
	
	@Override
	public Class<? extends Date> getType()
	{
		return Date.class;
	}

//	@Override
//	public void setValue(Date newValue) {
//		if (newValue == null){
//			super.setValue( null );
//			return;
//		}
//		super.setValue( clearDate( newValue ) );
//	}

	
	@Override
	protected void setInternalValue(Date newValue) {
		super.setInternalValue(clearDate( newValue ));
		updateComponents();
	}
	
	@Override
    public void markAsDirty() {
		super.markAsDirty();
		updateComponents();
    }
	
//	@Override
//	public Date getValue()
//	{
//		return super.getValue();
//	}
	
	public void add(int field, int value){
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( getValue() );
		calendar.add( field, value );
		setValue( calendar.getTime() );
	}
	
	private void updateInternalValue() {
		if (root.getValue() == null) return;
		
		String hhmm = root.getValue();
		
		try
		{
			setInternalValue( timeFormat.parse( hhmm ) );
		}
		catch ( ParseException e )
		{
			setValue( null );
		}
	}
	
	public Integer getTimeAsInteger(){
		if (getValue() == null) return null;
		return Integer.getInteger( timeIntFormat.format( getValue() ));
	}
}
