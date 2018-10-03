package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public class PluginsResources implements Serializable
{
	private static final long serialVersionUID = 3257169740871717940L;

	public static ResourceBundle getResources( String language )
	{
		return ResourceBundle.getBundle( "plugins_language", new Locale( language ) );
	}
}
