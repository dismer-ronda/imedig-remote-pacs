package es.pryades.fabricjs.client;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class FabricJsState extends JavaScriptComponentState {

    // State can have both public variable and bean properties
    public String figureConfiguration = "{}";
    public String notesConfiguration = "{}";    
    public String notes = "[]";
    public String bgImage = "";
    public String figures = "[]";
    public String dimensions = "{}";
    public String action = "NONE";
    public String cursor = "default";
    public String commands = "[{\"canvasAction\":\"NONE\"}]";
    public String drawMode;

}
