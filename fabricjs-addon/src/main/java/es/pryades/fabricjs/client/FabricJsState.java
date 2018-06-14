package es.pryades.fabricjs.client;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class FabricJsState extends JavaScriptComponentState {

    // State can have both public variable and bean properties
    public String canvasConfiguration = "{}";   
    public String notes = "{}";
    public String lines = "{}";
    public String dimensions = "{}";
    public String action = "NONE";        
    public String command = "{\"canvasAction\":\"NONE\"}";
}
