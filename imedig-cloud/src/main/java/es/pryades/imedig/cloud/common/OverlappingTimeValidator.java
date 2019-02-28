package es.pryades.imedig.cloud.common;

import java.util.List;

import org.joda.time.LocalTime;

import com.vaadin.data.validator.AbstractValidator;

import es.pryades.imedig.cloud.dto.TimeRange;

public class OverlappingTimeValidator extends AbstractValidator<LocalTime>
{
	private static final long serialVersionUID = -7695865359986503462L;
	
	private List<TimeRange<LocalTime>> ranges;
	
	public OverlappingTimeValidator(List<TimeRange<LocalTime>> ranges, String message)
	{
		super( message );
		
		this.ranges = ranges;
	}

	public void setRanges( List<TimeRange<LocalTime>> ranges )
	{
		this.ranges = ranges;
	}

	@Override
	protected boolean isValidValue( LocalTime value )
	{
		for ( TimeRange<LocalTime> timeRange : ranges )
		{
			if (isInside( value, timeRange )) return false;
		}
		
		return true;
	}
	
	private boolean isInside(LocalTime value, TimeRange<LocalTime> range){
		LocalTime start = range.getStart();
		LocalTime end = range.getEnd();
		
		//Para conocer si está dentro de un intervalo
		return value.equals( start ) //Si el valor es el mismo que el inicio del rango 
				|| value.equals( end ) //Si el valor es el mismo que el final del rango
				|| (value.isAfter( start ) && value.isBefore( end ));//Si el valor está entre el inicio y final
		
	}

	@Override
	public Class<LocalTime> getType()
	{
		return LocalTime.class;
	}

}
