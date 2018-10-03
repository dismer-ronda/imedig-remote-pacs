package es.pryades.fullscreen.client;

import com.vaadin.shared.JavaScriptExtensionState;

public class FullScreenState extends JavaScriptExtensionState {

	private static final long serialVersionUID = 1198357669748865629L;
	
	// State can have both public variable and bean properties
    public boolean fullscreen = false;
    public String triggerClass =  "";
}
