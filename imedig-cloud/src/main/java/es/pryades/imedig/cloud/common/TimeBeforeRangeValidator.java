package es.pryades.imedig.cloud.common;

import org.joda.time.LocalTime;

import com.vaadin.data.validator.AbstractValidator;

public class TimeBeforeRangeValidator extends AbstractValidator<LocalTime>
{
	private final TimeField afterTimeField;
	
	public TimeBeforeRangeValidator(TimeField prev, String message)
	{
		super( message );
		afterTimeField = prev;
	}

	@Override
	protected boolean isValidValue( LocalTime value )
	{
		if (afterTimeField.getValue() == null) return true;
		if (value == null) return false;
		
		return value.isBefore( afterTimeField.getValue() );
	}

	@Override
	public Class<LocalTime> getType()
	{
		return LocalTime.class;
	}

}
