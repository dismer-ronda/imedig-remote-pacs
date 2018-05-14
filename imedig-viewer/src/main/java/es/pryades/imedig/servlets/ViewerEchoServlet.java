package es.pryades.imedig.servlets;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.wiewer.echo3.ImedigApplication;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.WebContainerServlet;

/**
 * EchoServer implementation.
 */
public class ViewerEchoServlet extends WebContainerServlet 
{
    private static final Logger LOG = Logger.getLogger( ViewerEchoServlet.class );

    private static final long serialVersionUID = 1L;

    private boolean initialized; 
    private String errorMessage;

    public ViewerEchoServlet()
    {
    	try 
    	{
			initialized = true;
        	errorMessage = "";
		} 
    	catch ( Throwable e ) 
		{
        	initialized = false;
    		errorMessage = e.toString();
    		
    		LOG.error( errorMessage );
		}
    }
    
    private String getServiceName()
    {
    	String parts[] = getActiveConnection().getRequest().getRequestURI().split( "\\/" );
    	
    	if ( parts.length > 0 )
    	{
        	LOG.info( "service name " +  parts[1] );

        	return parts[1];
    	}
    	
    	return "";
    }
    
    private String getServerAddress()
    {
    	int port = getActiveConnection().getRequest().getServerPort();
    	
    	String address = getActiveConnection().getRequest().getServerName() + (port == 80 ? "" : ":" + port);
    	
    	LOG.info( "server address " +  address );

    	return address;
    }

    public ApplicationInstance newApplicationInstance() 
    {
    	ImedigApplication app = new ImedigApplication(); 
    	
    	app.setInitialized( initialized );
    	app.setErrorMessage( errorMessage );
    	app.setSourceAddress( getActiveConnection().getRequest().getRemoteAddr() );
    	app.setSourceViewer( getActiveConnection().getRequest().getHeader( "User-Agent" ) );

    	app.setServerAddress( getServerAddress() );
    	app.setServerService( getServiceName() );

    	app.setServerUrl( Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer" ); //"http://" + getServerAddress() + "/" + getServiceName() );
    	 
    	LOG.info( "server url " +  app.getServerUrl() );
    	LOG.info( "server url old " + "http://" + getServerAddress() + "/" + getServiceName() );

        return app;
    }
    
    public int getInstanceMode() 
    {
        return INSTANCE_MODE_WINDOW;
    }

}
