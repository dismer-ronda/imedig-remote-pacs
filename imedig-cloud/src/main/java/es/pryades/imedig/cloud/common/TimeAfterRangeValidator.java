package es.pryades.imedig.cloud.common;

import org.joda.time.LocalTime;

import com.vaadin.data.validator.AbstractValidator;

public class TimeAfterRangeValidator extends AbstractValidator<LocalTime>
{
	private final TimeField prevTimeField;
	
	public TimeAfterRangeValidator(TimeField prev, String message)
	{
		super( message );
		prevTimeField = prev;
	}

	@Override
	protected boolean isValidValue( LocalTime value )
	{
		if (prevTimeField.getValue() == null) return true;
		if (value == null) return false;
		
		return value.isAfter( prevTimeField.getValue() );
	}

	@Override
	public Class<LocalTime> getType()
	{
		return LocalTime.class;
	}

}
