package es.pryades.imedig.wiewer.echo3;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Window;
import nextapp.echo.webcontainer.ClientProperties;
import nextapp.echo.webcontainer.ContainerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;

/**
 * Application instance implementation.
 */
@SuppressWarnings( "rawtypes" )
public class ImedigApplication extends ApplicationInstance 
{
    private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger( ImedigApplication.class );

    @Getter
	private ResourceBundle resourceBundle;

    @Getter
    @Setter
    private User user;

    private ContainerContext context;
	private Map queryString;

    /**
     * Main window of user interface.
     */
    @Getter
    @Setter
    private Window mainWindow;

    private ViewerWnd viewerWnd = null;
    private ErrorWnd errorWnd = null;
    private GenericWnd lastWnd = null;
    private BrowserWnd browserWnd = null;
    
    @Getter
    @Setter
    private boolean initialized;
    @Getter
    @Setter
    private String errorMessage;
    @Getter
    @Setter
    private String sourceAddress;
    @Getter
    @Setter
    private String sourceViewer;
    @Getter
    @Setter
    private String serverAddress;
    @Getter
    @Setter
    private String serverUrl;
    @Getter
    @Setter
    private String serverService;

    /**
     * Returns the active <code>ApplicationInstance</code> cast to the 
     * appropriate type.
     * 
     * @return the active <code>ApplicationInstance</code>
     */
    public static ImedigApplication getApp() 
    {
        return (ImedigApplication) getActive();
    }

    private String getParameter( String param )
    {
    	if ( queryString == null )
    		return "";
    	
    	String params[] = ((String[]) queryString.get(param));
    	
    	return params != null ? params[0] : "";
    }
    
    public int returnClientWidth()
    {
    	ClientProperties properties = context.getClientProperties();
    	
    	return properties.getInt(ClientProperties.SCREEN_WIDTH, -1);
    }

    public int returnClientHeight()
    {
    	ClientProperties properties = context.getClientProperties();
    	
    	return properties.getInt(ClientProperties.SCREEN_HEIGHT, -1);
    }
    
	String getResourceString( String name )
	{
		return resourceBundle.getString( "Application." +  name );
	}
	
    public String getBrowserLanguage()
    {
    	ClientProperties properties = context.getClientProperties();
    	
    	return (String)properties.get(ClientProperties.NAVIGATOR_LANGUAGE);
    }
    
    /**
     * @see nextapp.echo.app.ApplicationInstance#init()
     */
    public Window init() 
    {
        setStyleSheet( Styles.DEFAULT_STYLE_SHEET );
        context = (ContainerContext) getContextProperty( ContainerContext.CONTEXT_PROPERTY_NAME );
        
		resourceBundle = ResourceBundle.getBundle( "es.pryades.imedig.wiewer.resource.localization.Messages", new Locale( getBrowserLanguage() ) );

        queryString = context.getInitialRequestParameterMap();
        
		user = new User();

    	mainWindow = new Window();
    	
		String token = getParameter( "token" );
		String code = Utils.decrypt( getParameter( "code" ), Settings.TrustKey );
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		
        Utils.getParameters( code, parameters );
        String ts = parameters.get( "ts" );

    	if ( isIncompatibleBrowser() )
    	{
			browserWnd = new BrowserWnd( this );
			
	        mainWindow.setContent( browserWnd );
    	}
    	else if ( Utils.isValidRequest( token, "IMEDIG" + ts, ts, Settings.TrustKey, 0 ) ) 
    	{
    		LOG.debug( "login = " + parameters.get( "login" ) );
    		LOG.debug( "filter = " + parameters.get( "filter" ) );
    		LOG.debug( "query = " + parameters.get( "query" ) );
    		LOG.debug( "compression = " + parameters.get( "compression" ) );
    		LOG.debug( "uid = " + parameters.get( "uid" ) );
    		
    		user.setLogin( parameters.get( "login" ) );
    		user.setQuery( Utils.getInt( parameters.get( "query" ), 1 ) );
    		user.setCompression( parameters.get( "compression" ) );
    		user.setUid( parameters.get( "uid" ) );
    		
    		String filter = parameters.get( "filter" );
    		user.setFilter( filter == null || filter.isEmpty() ? "*" : filter );

        	activateViewerWnd( null );
    	}
    	else if ( "false".equals( Settings.IMEDIG_Auth ) )
    	{
    		user.setLogin( "demo" );
    		user.setQuery( 1 );
    		user.setCompression( "image/png" );
    		user.setFilter( "*" );
    		user.setUid( null );

        	activateViewerWnd( null );
    	}
    	else
    	{
    		errorMessage = resourceBundle.getString( "Application.AccessDenied" );
    		
    		activateErrorWnd( null );
    	}
    	
        return mainWindow;
    }
    
	public void activateViewerWnd( GenericWnd current )
	{
		if ( viewerWnd == null )
			viewerWnd = new ViewerWnd( this, returnClientHeight(), user );
	
		lastWnd = current;

    	mainWindow.setContent( viewerWnd );
    	
		viewerWnd.takeControl( this );
	}

	public void activateErrorWnd( GenericWnd current )
	{
		if ( errorWnd == null )
			errorWnd = new ErrorWnd( resourceBundle.getString( "App.Title" ), getErrorMessage() );
		
		lastWnd = current;
		
        mainWindow.setContent( errorWnd );
		errorWnd.takeControl( this );
	}
	
	public void activateLastWnd( GenericWnd current )
	{
		if ( lastWnd != null )
		{
			mainWindow.setContent( (ContentPane)lastWnd );
			lastWnd.takeControl( this );
			
			if ( current == errorWnd )
				lastWnd = null;
			else
				lastWnd = current;
		}
	}

	public boolean isBackEnabled()
	{
		return lastWnd != null;
	}

	public boolean isIncompatibleBrowser()
	{
		LOG.info( "sourceViewer " + sourceViewer );
		
		if ( !sourceViewer.contains( "MSIE" ) )
			return false;
		
		if ( sourceViewer.contains( "MSIE 9" ) )
			return false;
		
		if ( sourceViewer.contains( "MSIE 10" ) )
			return false;

		if ( sourceViewer.contains( "MSIE 11" ) )
			return false;
		
		return true;
	}
}
