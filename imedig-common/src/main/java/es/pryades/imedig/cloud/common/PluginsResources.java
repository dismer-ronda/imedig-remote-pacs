package es.pryades.imedig.cloud.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class PluginsResources 
{
	public static ResourceBundle getResources( String language )
	{
		return ResourceBundle.getBundle( "plugins_language", new Locale( language ) );
	}
}
