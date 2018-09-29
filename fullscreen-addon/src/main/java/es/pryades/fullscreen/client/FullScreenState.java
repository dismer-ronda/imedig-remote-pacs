package es.pryades.fullscreen.client;

import com.vaadin.shared.JavaScriptExtensionState;

public class FullScreenState extends JavaScriptExtensionState {

    // State can have both public variable and bean properties
    public boolean fullscreen = false;
    public String triggerClass =  "";
}
