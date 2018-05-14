package es.pryades.imedig.cloud.services;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.apache.log4j.Logger;


public class ServicesServlet extends Application
{
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
