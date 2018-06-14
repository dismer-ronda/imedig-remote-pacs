package es.pryades.imedig.viewer.application;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.components.ViewerWnd;
import lombok.Getter;
import lombok.Setter;

/**
 * The Application's "main" class
 */
@Theme(value = "imedigviewer")
@PreserveOnRefresh
public class ViewerApplicationUI extends UI
{
	private static final long serialVersionUID = 683154667075459739L;

	private static final Logger LOG = Logger.getLogger( ViewerApplicationUI.class );
	
	private ViewerWnd window;
	
    @Getter @Setter private User user;
    @Getter @Setter private String serverUrl;

    private String queryString;
	private String sourceIP;
	
	private HashMap<Integer,Imagen> images;

	ResourceBundle resources;
	
	public ViewerApplicationUI()
	{
		images = new HashMap<Integer,Imagen>();
    	
		//imagenesManager = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );
	}

	Locale getLanguage( String langs )
    {
        Locale locale;

        try 
        {
	        locale = AppUtils.getLocaleFromBrowser( this, langs );
	        if ( locale != null )
	        	return locale;
		} 
        catch ( Throwable e ) 
        {
			Utils.logException( e, LOG );
        	
        	LOG.warn( "Browswer language is not supported. Default language will be used" );
		}
        
        return AppUtils.getDefaultLocale();
    }
	
	@Override
    public void init( VaadinRequest request )
    {
		resources = ResourceBundle.getBundle( "Messages", getLanguage(Settings.Languages ));
		serverUrl = Utils.getEnviroment( "CLOUD_URL" ) + request.getContextPath();
		
		settingUserFromRequest( request );
		
		if (user == null)
		{
			Notification.show( resources.getString( "App.Title" ), resources.getString( "Application.AccessDenied" ), Notification.Type.ERROR_MESSAGE );
		}else{
			initViewerWnd(user);
			setContent( window );
		}
    }
	
	public static String getText(String keyword) {
        return ((ViewerApplicationUI) UI.getCurrent()).resources.getString(keyword);
    }
	
	private void initViewerWnd(User user)
	{
		window = new ViewerWnd(user);
		
	}

	private void settingUserFromRequest(VaadinRequest request){
		String token = request.getParameter( "token" );
		String code = Utils.decrypt( request.getParameter( "code" ), Settings.TrustKey );
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		
        Utils.getParameters( code, parameters );
        String ts = parameters.get( "ts" );
        
        user = null;
        
    	if ( Utils.isValidRequest( token, "IMEDIG" + ts, ts, Settings.TrustKey, 0 ) ) 
    	{
    		LOG.debug( "login = " + parameters.get( "login" ) );
    		LOG.debug( "filter = " + parameters.get( "filter" ) );
    		LOG.debug( "query = " + parameters.get( "query" ) );
    		LOG.debug( "compression = " + parameters.get( "compression" ) );
    		LOG.debug( "uid = " + parameters.get( "uid" ) );
    		
    		user = new User();
    		user.setLogin( parameters.get( "login" ) );
    		user.setQuery( Utils.getInt( parameters.get( "query" ), 1 ) );
    		user.setCompression( parameters.get( "compression" ) );
    		user.setUid( parameters.get( "uid" ) );
    		
    		String filter = parameters.get( "filter" );
    		user.setFilter( filter == null || filter.isEmpty() ? "*" : filter );
    	}
    	else if ( "false".equals( Settings.IMEDIG_Auth ) )
    	{
    		user = new User();
    		user.setLogin( "demo" );
    		user.setQuery( 1 );
    		user.setCompression( "image/png" );
    		user.setFilter( "*" );
    		user.setUid( null );
    	}
	}

}
