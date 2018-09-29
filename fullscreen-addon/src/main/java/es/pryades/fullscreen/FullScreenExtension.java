package es.pryades.fullscreen;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import es.pryades.fullscreen.client.FullScreenState;
import es.pryades.fullscreen.listeners.FullScreenChangeListener;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;

// This is the server-side UI component that provides public API 
// for FabricJs
@JavaScript({"screenfull/screenfull.min.js", "fullscreen_connector.js"})
public class FullScreenExtension extends AbstractJavaScriptExtension {

    
    private FullScreenChangeListener fullScreenChangeListener;
    
    public FullScreenExtension() {

    }

    public void trigger(Button button) {
        extend(button);
        String buttonClass = "fullscreen_" + RandomStringUtils.randomAlphabetic(10); 
        button.addStyleName(buttonClass);
        getState().triggerClass = buttonClass;
        
        addFunction("changeFullScreen", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
               boolean isFullScreen = arguments.getBoolean(0);
               if(fullScreenChangeListener!=null){
                   fullScreenChangeListener.onChange(isFullScreen);
               }
            }
        });
    }

    @Override
    protected FullScreenState getState() {
        return (FullScreenState) super.getState(); //To change body of generated methods, choose Tools | Templates.
    }

    public FullScreenChangeListener getFullScreenChangeListener() {
        return fullScreenChangeListener;
    }

    public void setFullScreenChangeListener(FullScreenChangeListener fullScreenChangeListener) {
        this.fullScreenChangeListener = fullScreenChangeListener;
    }

    
    
    

}
