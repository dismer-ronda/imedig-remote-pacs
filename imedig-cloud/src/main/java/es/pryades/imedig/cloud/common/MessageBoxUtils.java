package es.pryades.imedig.cloud.common;

import java.util.ResourceBundle;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.MessageBoxListener;

public class MessageBoxUtils{
	
	public static MessageBox showMessageBox(ResourceBundle resources, Icon icon, String title, String plainTextMessage, MessageBoxListener listener, ButtonId... buttonIds) {
		
		MessageBox messageBox = MessageBox.showPlain(icon, title, plainTextMessage, listener, buttonIds);
		
		for (ButtonId buttonId : buttonIds) {
			messageBox.getButton(buttonId).setCaption( caption(resources, buttonId) );
		}
		
		return messageBox;
	}
	
	private static String caption(ResourceBundle resources, ButtonId btnId){
		switch ( btnId )
		{
			case OK:
				return resources.getString( "words.ok" );
			case CANCEL:
				return resources.getString( "words.cancel" );
			case YES:
				return resources.getString( "words.yes" );
			case NO:
				return resources.getString( "words.no" );
			case CLOSE:
				return resources.getString( "words.close" );
			default:
				break;
		}
		
		return "";
	}

	public static MessageBox showMessageBox(ResourceBundle resources, Icon icon, String title, String plainTextMessage, ButtonId... buttonIds) {
		MessageBox messageBox = MessageBox.showPlain(icon, title, plainTextMessage, buttonIds);
		for (ButtonId buttonId : buttonIds) {
			messageBox.getButton(buttonId).setCaption( caption(resources, buttonId) );
		}
		return messageBox;
	}

}
