package es.pryades.imedig.cloud.common;

import org.joda.time.LocalTime;

import com.vaadin.data.validator.AbstractValidator;

public class TimeLimitRangeValidator extends AbstractValidator<LocalTime>
{
	private static final long serialVersionUID = -7695865359986503462L;
	
	private LocalTime startLimit;
	private LocalTime endLimit;
	
	public TimeLimitRangeValidator(LocalTime startLimit, LocalTime endLimit, String message)
	{
		super( message );
		this.startLimit = startLimit;
		this.endLimit = endLimit;
	}

	public void setStartLimit( LocalTime startLimit )
	{
		this.startLimit = startLimit;
	}

	public void setEndLimit( LocalTime endLimit )
	{
		this.endLimit = endLimit;
	}

	@Override
	protected boolean isValidValue( LocalTime value )
	{
		return value.isAfter( startLimit ) && value.isBefore( endLimit );
	}

	@Override
	public Class<LocalTime> getType()
	{
		return LocalTime.class;
	}

}
