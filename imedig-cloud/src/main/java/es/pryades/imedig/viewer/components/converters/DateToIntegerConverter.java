package es.pryades.imedig.viewer.components.converters;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import es.pryades.imedig.cloud.common.Utils;

public class DateToIntegerConverter implements Converter<Date, Integer>
{

	private static final long serialVersionUID = 8491752530353938991L;

	@Override
	public Integer convertToModel( Date value, Class<? extends Integer> targetType, Locale locale ) throws ConversionException
	{
		if (value == null) return null;
		
		return Utils.getDateAsInt( value );
	}

	@Override
	public Date convertToPresentation( Integer value, Class<? extends Date> targetType, Locale locale ) throws ConversionException
	{
		if (value == null) return null;
		
		return Utils.getDateFromInt( value );
	}

	@Override
	public Class<Integer> getModelType()
	{
		return Integer.class;
	}

	@Override
	public Class<Date> getPresentationType()
	{
		return Date.class;
	}

}
