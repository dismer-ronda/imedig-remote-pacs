package es.pryades.imedig.servlets;

import java.io.Serializable;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import es.pryades.imedig.wado.query.ReportInfoResource;
import es.pryades.imedig.wado.retrieve.ImageStandardResource;

public class WadoApplication extends Application implements Serializable 
{
	private static final long serialVersionUID = 2186349302980737031L;

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
