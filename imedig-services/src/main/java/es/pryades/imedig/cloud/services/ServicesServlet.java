package es.pryades.imedig.cloud.services;

import java.io.Serializable;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.apache.log4j.Logger;


public class ServicesServlet extends Application implements Serializable
{
	private static final long serialVersionUID = -1534290900556902982L;
	
	private static final Logger LOG = Logger.getLogger( ServicesServlet.class );
	
    public ServicesServlet() 
    {
        super();
	}

    @Override
    public Restlet createInboundRoot() 
    {  
    	Router router = new Router( getContext() );
	       
    	router.attach( "/echo/{serial}", EchoResource.class );
    	router.attach( "/report/{id}", ReportResource.class );
    	router.attach( "/image/{id}", ImageResource.class );
	    	
    	router.attach( "/login", LoginResource.class );
    	router.attach( "/reports", ReportsResource.class );
    	
    	router.attach( "/status/{queryDate}", StatusResource.class );

    	LOG.info( "started" );
	    
	    return router;  
    }
}
