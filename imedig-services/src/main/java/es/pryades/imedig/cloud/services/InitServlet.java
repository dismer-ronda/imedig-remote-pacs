package es.pryades.imedig.cloud.services;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.velocity.app.Velocity;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger( InitServlet.class );

	public void init() 
    {
        try 
		{
			Settings.Init();
			
	    	IOCManager.Init();

	    	BootLoader.bootup();
	    	
	    	Properties p = new Properties();
	    	p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
	    	Velocity.init( p );

    	    LOG.info( "started" );
		} 
		catch ( Exception e ) 
		{
			if ( !(e instanceof ImedigException) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}
	
	@Override
	public void destroy()
	{
		LOG.info( "destroy servlet called" );
		
		RemoveStudiesProcessor.stopProcessor();

		String prefix = getClass().getSimpleName() +" destroy() ";
	    ServletContext ctx = getServletContext();
	    
	    try 
	    {
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        
	        while(drivers.hasMoreElements()) 
	        {
	            DriverManager.deregisterDriver(drivers.nextElement());
	        }
	    } 
	    catch(Exception e) 
	    {
	        ctx.log( prefix + "Exception caught while deregistering JDBC drivers", e );
	    }
	    
	    ctx.log( prefix + " complete" );

	    super.destroy();
	}
}
