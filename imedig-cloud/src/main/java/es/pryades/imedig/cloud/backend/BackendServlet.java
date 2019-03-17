package es.pryades.imedig.cloud.backend;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

import com.vaadin.server.VaadinServlet;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.ibatis.ClientDalManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.core.common.LicenseManager;
import es.pryades.imedig.pacs.dal.ibatis.PacsDalManager;

public class BackendServlet extends VaadinServlet
{
	private static final long serialVersionUID = -3970385321251544382L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( BackendServlet.class );

	public void init( ServletConfig servletConfig ) throws ServletException
	{
		try
		{
			Settings.Init();

			IOCManager.Init();

        	if ( LicenseManager.LICENSE_ENABLED )
				LicenseManager.Init();

			ClientDalManager.Init( Settings.DB_engine, Settings.DB_driver, Settings.DB_url, Settings.DB_user, Settings.DB_password );
			PacsDalManager.Init( Settings.PACS_DB_driver, Settings.PACS_DB_url, Settings.PACS_DB_user, Settings.PACS_DB_password );

			Properties p = new Properties();
			p.setProperty( "runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem" );
			Velocity.init( p );
			
			ImedigContext ctx = new ImedigContext();

			CentrosManager centrosManager = (CentrosManager)IOCManager.getInstanceOf( CentrosManager.class );
			Centro centro = (Centro)centrosManager.getRow( ctx, 1 );
			
			LicenseManager.getInstance().setLicenseCode( centro.getDireccion() );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		super.init( servletConfig );
	}

/*	@Override
	protected void writeAjaxPageHtmlVaadinScripts( Window window, String themeName, Application application, BufferedWriter page, String appUrl, String themeUri, String appId, HttpServletRequest request ) throws ServletException, IOException
	{
		page.write( "<script type=\"text/javascript\">\n" );
		page.write( "//<![CDATA[\n" );
		page.write( "document.write(\"<script language='javascript' src='./VAADIN/widgetsets/es.pryades.imedig.cloud.backend.widgetset.Imedig_instanceWidgetset/ckeditor/ckeditor.js'><\\/script>\");\n" );
		page.write( "document.write(\"<script language='javascript' src='./VAADIN/widgetsets/es.pryades.imedig.cloud.backend.widgetset.Imedig_instanceWidgetset/ckeditor/config.js'><\\/script>\");\n" );
		page.write( "//]]>\n</script>\n" );
		super.writeAjaxPageHtmlVaadinScripts( window, themeName, application, page, appUrl, themeUri, appId, request );
	}*/
}
