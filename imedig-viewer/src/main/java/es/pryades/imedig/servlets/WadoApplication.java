package es.pryades.imedig.servlets;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import es.pryades.imedig.wado.query.ReportInfoResource;
import es.pryades.imedig.wado.retrieve.ImageStandardResource;

public class WadoApplication extends Application 
{
    public WadoApplication() 
    {
        super();
    }

    @Override
    public Restlet createInboundRoot() 
    {  
       Router router = new Router( getContext() );  
 
       router.attach( "/echo", EchoResource.class );        
       router.attach( "/report/{user}", ReportInfoResource.class );        
       router.attach( "/wado", ImageStandardResource.class );        
	
       return router;  
    }  	
}
