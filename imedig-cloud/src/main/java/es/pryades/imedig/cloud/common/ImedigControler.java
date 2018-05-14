package es.pryades.imedig.cloud.common;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import lombok.Getter;
import lombok.Setter;

public abstract class ImedigControler {
	@Getter @Setter
	private ImedigContext context;
	
	@Getter @Setter
	private ResourceBundle resource;
	
	@Getter @Setter
	private AbstractComponent viewContainer;
	
	@Getter
	private Map<Integer, String> msgErrors;
	
	public ImedigControler(ImedigContext aContext, ResourceBundle aResource, AbstractComponent aViewContainer) {
		setContext(aContext);
		setResource(aResource);
		setViewContainer(aViewContainer);
		
		msgErrors = new HashMap<Integer, String>();
	}
	
	public void show(){
		init();
		
		render();
	}
	
	protected abstract void init();

	protected abstract void render();
	
	protected void showMessageError( Integer error ) {
		String msg = msgErrors.get( error );
		
		if (msg == null)
			Notification.show(getResource().getString("error.unknown" ) + " " + error, Notification.Type.ERROR_MESSAGE);
		else
			Notification.show(getResource().getString( msg ), Notification.Type.ERROR_MESSAGE);
	}
}
