package es.pryades.imedig.cloud.common;

import java.io.Serializable;

import org.vaadin.inputmask.InputMask;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

public class UtilsUI implements Serializable
{
	private static final long serialVersionUID = -7640992619515566832L;

	public static final String TIME_REGEX = "(?:[01]\\d|2[0123]):(?:[012345]\\d)";

	private UtilsUI(){
		
	}
	
	public static TextField createTimeImput( String caption, Property<?> dataSource ) {
		TextField text1 = new TextField(caption, dataSource);
		text1.setImmediate( true );
		text1.setNullRepresentation( "" );
		InputMask mask = new InputMask( TIME_REGEX );
		mask.setRegexMask( true );
		mask.setPlaceholder( " " );
		mask.extend( text1 );
		
		return text1;
	}
}
