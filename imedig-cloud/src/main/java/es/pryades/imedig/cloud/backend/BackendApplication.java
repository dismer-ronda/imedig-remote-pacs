package es.pryades.imedig.cloud.backend;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.DatabaseImageSource;
import es.pryades.imedig.cloud.common.MessageDlg;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * The Application's "main" class
 */
@Theme("imedig")
@Title("Imedig")
@PreserveOnRefresh
public class BackendApplication extends UI //implements HttpServletRequestListener
{
	private static final long serialVersionUID = 683154667075459739L;

	private static final Logger LOG = Logger.getLogger( BackendApplication.class );
	
	private static final String VERSION = "2.0.2-SNAPSHOT";
	
	private BackendMainWnd window;

    private String queryString;
	private String sourceIP;
	
	private HashMap<Integer,Imagen> images;

	private ImedigContext ctx;

	private ImagenesManager imagenesManager;
	
	ResourceBundle resources;
	
	//@Getter @Setter private String serverUrl;
	
	public BackendApplication()
	{
		images = new HashMap<Integer,Imagen>();
    	
		imagenesManager = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );
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
		resources = ResourceBundle.getBundle( "language", getLanguage( Settings.LANGUAGES ) );
		//serverUrl = Utils.getEnviroment( "CLOUD_URL" ) + request.getContextPath();
		
		ctx = new ImedigContext();
		
		ctx.setResources( resources );
    	
		BackendMainWnd wnd = (BackendMainWnd)getContent();
		
		if ( wnd != null )
			wnd.removeAllComponents();
    	
		window = new BackendMainWnd( ctx );
		
		setContent( window );

    	ctx.addData( "Application", this );
    	ctx.addData( "Resources", resources );
    	ctx.addData( "MainWnd", window );
    	ctx.addData( "Url", Page.getCurrent().getLocation().toString() );
    	
    	LOG.info(  "Url " + ctx.getData( "Url") );

    	window.showLogin();
    	
    	executeJScripts();
    }
	
	@Override
	protected void refresh(VaadinRequest request) {
		super.refresh( request );
		copyright();
    }

	public void messageAndExit( String title, ResourceBundle resources, String message )
	{
		MessageDlg dlg = new MessageDlg( title, resources, message );
		
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -7921174154171992358L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					onClose( e );
			    }
			}
		);
		
		getUI().addWindow( dlg );
	}
	
	private void onClose( CloseEvent event )
	{
        close();
	}

    public void onRequestStart( HttpServletRequest request, HttpServletResponse response) 
    {
	    String qs = request.getQueryString();
	    
	    if ( queryString != null && qs != null && !qs.equals( queryString ) )
	    {
		    HashMap<String,String> parameters = new HashMap<String,String>();
	        
			Utils.getParameters( qs, parameters );

			if ( parameters.get( "code" ) != null && parameters.get( "token" ) != null )
			{
				setQueryString( request.getQueryString() );
		    	setSourceIP( request.getRemoteAddr() );
	
		    	close();
			}
	    }
	    else
	    {
	    	setQueryString( request.getQueryString() );
	    	setSourceIP( request.getRemoteAddr() );
	    }
    }

	public void onRequestEnd( HttpServletRequest request, HttpServletResponse response ) 
	{
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getSourceIP() {
		return sourceIP;
	}	

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public StreamResource getImage( ImedigContext ctx, int image )
	{
		Imagen img = images.get( image );
		
		if ( img == null )
		{
			try
			{
				img = (Imagen) imagenesManager.getRow( ctx, image );
				
				if ( img.getDatos() == null )
					return null;
				
				images.put( image, img );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );

				return null;
			}
		}
		
		try
		{
			StreamResource.StreamSource source = (StreamSource)new DatabaseImageSource( img );
		
			return new StreamResource( source, "" + image + ".jpg" );
		}
		catch ( Throwable e )
		{
			return null;
		}
	}
	
	private void executeJScripts(){
		copyright();
	}
	
	private void copyright() {
        final String jsdivcopyright = 
        		"var divcopyright=document.createElement('div');" +
                "divcopyright.className='copyright';" +
                "divcopyright.innerHTML='Copyright &copy; "+getYear()+" <a href=\"https://www.pryades.es\" target=\"_blank\"><b>Pryades Soluciones Informáticas SL</b></a>" + "  v"+getVersion()+"';" +
                "document.body.appendChild(divcopyright);";
        
        JavaScript.getCurrent().execute(jsdivcopyright);
    }
	
	private int getYear(){
		return new GregorianCalendar().get( Calendar.YEAR );
	}
	
	private String getVersion(){
		return VERSION;
		//return this.getClass().getPackage().getImplementationVersion();
	}
}
