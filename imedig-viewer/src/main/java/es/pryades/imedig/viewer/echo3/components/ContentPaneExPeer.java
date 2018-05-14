package es.pryades.imedig.viewer.echo3.components;

import nextapp.echo.app.Component;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.util.Context;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;

public class ContentPaneExPeer extends AbstractComponentSynchronizePeer {

	private static final Service JS_SERVICE = JavaScriptService.forResource(
			"es.pryades.imedig.viewer.echo3.components.ContentPaneEx", "es/pryades/imedig/viewer/echo3/components/ContentPaneEx.js");

	static {
		WebContainerServlet.getServiceRegistry().add(JS_SERVICE);
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getClientComponentType(boolean)
	 */
	public String getClientComponentType(boolean shortType) {
		return "es.pryades.imedig.viewer.echo3.components.ContentPaneEx";
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getComponentClass()
	 */
	@SuppressWarnings("rawtypes")
	public Class getComponentClass() {
		return ContentPaneEx.class;
	}

	/**
	 * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(nextapp.echo.app.util.Context, Component)
	 */
	public void init(Context context, Component component) {
		super.init(context, component);
		ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
		serverMessage.addLibrary(JS_SERVICE.getId());
	}

	
	public ContentPaneExPeer() {
	        super();
	        //addOutputProperty(ContentPaneEx.WIDTH_PROPERTY);
	        //addOutputProperty(ContentPaneEx.HEIGHT_PROPERTY);
	        addEvent(new EventPeer(ContentPaneEx.INPUT_ACTION,ContentPaneEx.ACTION_LISTENERS_PROPERTY) {
	            public boolean hasListeners(Context context, Component c) {
	                return ((ContentPaneEx) c).hasActionListeners();
	            }
	        });
	    }

    @SuppressWarnings("rawtypes")
	public Class getInputPropertyClass(String propertyName) {
        if (ContentPaneEx.WIDTH_PROPERTY.equals(propertyName)) {
            return Integer.class;
        }
        if (ContentPaneEx.HEIGHT_PROPERTY.equals(propertyName)) {
            return Integer.class;
        }
		return null;
    }

    public void storeInputProperty(Context context, Component component, String propertyName, int propertyIndex, Object newValue) {
        if (propertyName.equals(ContentPaneEx.WIDTH_PROPERTY)) {
            ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
            clientUpdateManager.setComponentProperty(component,	ContentPaneEx.WIDTH_PROPERTY, newValue);
        }
        if (propertyName.equals(ContentPaneEx.HEIGHT_PROPERTY)) {
            ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
            clientUpdateManager.setComponentProperty(component,	ContentPaneEx.HEIGHT_PROPERTY, newValue);
        }
    }

}
