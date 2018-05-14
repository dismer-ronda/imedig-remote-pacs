package es.pryades.imedig.wiewer.echo3;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Color;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;

public class AppUtils 
{
	private static final Logger LOG = LoggerFactory.getLogger( AppUtils.class );
	
	public static Label getLabel( String text, int length )
	{
		if ( text == null )
			text = "";
		
		if ( length != -1 && text.length() > length )
			text = text.substring( 0, length - 1 ) + "..."; 
		
		Label label = new Label( text );
		
		label.setForeground( Color.BLACK );
		label.setLineWrap( false );
		
		return label;
	}
	
	public static ResourceImageReference getGrayIcon( String name, int size )
	{
		return new ResourceImageReference( "/es/pryades/imedig/wiewer/resource/image/icon/custom/" + name + "-" + size + "-gray.png" );
	}

	public static ResourceImageReference getIcon( String name, int size )
	{
		return new ResourceImageReference( "/es/pryades/imedig/wiewer/resource/image/icon/custom/" + name + "-" + size + ".png" );
	}
	
	public static ResourceImageReference getCursor( String name, int size )
	{
		return new ResourceImageReference( "/es/pryades/imedig/wiewer/resource/image/icon/custom/" + name + "-16.png" );
	}
}
