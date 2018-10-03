package es.pryades.imedig.servlets;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = -7500581934341077564L;
	
	private static final Logger LOG = Logger.getLogger( InitServlet.class );

	@Override
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);

		ImageIO.scanForPlugins();
		
    	BootLoader.bootup();
	}

	@Override
	public void destroy()
	{
		LOG.info( "destroy servlet called" );
		
		super.destroy();
	}
}
