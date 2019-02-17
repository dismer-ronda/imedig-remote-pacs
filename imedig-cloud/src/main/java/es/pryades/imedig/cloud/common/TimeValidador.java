package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.validator.AbstractStringValidator;

public class TimeValidador extends AbstractStringValidator implements Serializable
{
	private static final long serialVersionUID = -7040777470971930516L;

	private transient Matcher matcher = null;
	private Pattern pattern = Pattern.compile( UtilsUI.TIME_REGEX );

	public TimeValidador()
	{
		this( "" );
	}

	public TimeValidador( String errorMessage )
	{
		super( errorMessage );
	}

	@Override
	protected boolean isValidValue( String value )
	{
		if ( StringUtils.isBlank( value ) )
			return false;

		return getMatcher( value ).matches();
	}

	private Matcher getMatcher( String value )
	{
		if ( matcher == null )
		{
			matcher = pattern.matcher( value );
		}
		return matcher;
	}

}
