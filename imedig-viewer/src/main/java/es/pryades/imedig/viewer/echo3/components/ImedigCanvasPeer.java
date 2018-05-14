package es.pryades.imedig.viewer.echo3.components;

import nextapp.echo.app.Component;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.util.Context;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;

public class ImedigCanvasPeer extends AbstractComponentSynchronizePeer {

	private static final Service JS_SERVICE = JavaScriptService.forResource(
			"es.pryades.imedig.viewer.echo3.components.ImedigCanvas", "es/pryades/imedig/viewer/echo3/components/ImedigCanvas.js");

	static {
		WebContainerServlet.getServiceRegistry().add(JS_SERVICE);
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getClientComponentType(boolean)
	 */
	public String getClientComponentType(boolean shortType) {
		return "es.pryades.imedig.viewer.echo3.components.ImedigCanvas";
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getComponentClass()
	 */
	@SuppressWarnings("rawtypes")
	public Class getComponentClass() {
		return ImedigCanvas.class;
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(nextapp.echo.app.util.Context, Component)
	 */
	public void init(Context context, Component component) {
		super.init(context, component);
		ServerMessage serverMessage = (ServerMessage) context
				.get(ServerMessage.class);
		serverMessage.addLibrary(JS_SERVICE.getId());
	}
	
	public ImedigCanvasPeer() 
	{
        super();
    
        addOutputProperty( ImedigCanvas.PROPERTY_SELECTION_MODE );
        addOutputProperty( ImedigCanvas.PROPERTY_SHOW_LINE );
        addOutputProperty( ImedigCanvas.CURSOROFFSETX_PROPERTY );
        addOutputProperty( ImedigCanvas.CURSOROFFSETY_PROPERTY );
        
        addEvent(new EventPeer(ImedigCanvas.INPUT_ACTION,ImedigCanvas.ACTION_LISTENERS_PROPERTY) {
            public boolean hasListeners(Context context, Component c) {
                return ((ImedigCanvas) c).hasActionListeners();
            }
        });
    }

	@SuppressWarnings("rawtypes")
	public Class getInputPropertyClass(String propertyName) 
	{
	    if (ImedigCanvas.REFWIDTH_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.REFHEIGHT_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.ROIX1_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.ROIY1_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.ROIX2_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.ROIY2_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.WHEEL_PROPERTY.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
	    if (ImedigCanvas.PROPERTY_ACTION_EVENT.equals(propertyName)) 
	    {
	        return Integer.class;
	    }
		return null;
	}

public void storeInputProperty(Context context, Component component, String propertyName, int propertyIndex, Object newValue) {
    if (propertyName.equals(ImedigCanvas.REFWIDTH_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.REFWIDTH_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.REFHEIGHT_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.REFHEIGHT_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.ROIX1_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.ROIX1_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.ROIY1_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.ROIY1_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.ROIX2_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.ROIX2_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.ROIY2_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.ROIY2_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.WHEEL_PROPERTY)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.WHEEL_PROPERTY, newValue);
    }
    if (propertyName.equals(ImedigCanvas.PROPERTY_ACTION_EVENT)) {
        ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
        clientUpdateManager.setComponentProperty(component,	ImedigCanvas.PROPERTY_ACTION_EVENT, newValue);
    }
}}
