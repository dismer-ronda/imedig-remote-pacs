package es.pryades.imedig.cloud.common;

import com.vaadin.ui.Label;

public class DialogLabel extends Label
{
	private static final long serialVersionUID = 6317348297871314651L;

	public DialogLabel( String text, String width )
	{
		super( text );
		
		setWidth( width );
		
		addStyleName( "right margin_right" );
	}

	public DialogLabel( String text, String width, String style )
	{
		super( text );
		
		setWidth( width );
		
		setStyleName( style );
	}
}