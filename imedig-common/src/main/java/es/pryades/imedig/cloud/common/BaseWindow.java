package es.pryades.imedig.cloud.common;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.ui.Window;

@SuppressWarnings("unused")
public class BaseWindow extends Window 
{
	private static final Logger LOG = Logger.getLogger( BaseWindow.class );
	
	private static final long serialVersionUID = -3120890599731227926L;
	
	private ResourceBundle resources;

	public BaseWindow( String title, ResourceBundle resources )
	{
		super( title );
		
		setResources( resources );
	}

	public ResourceBundle getResources() {
		return resources;
	}

	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

	public String getString( String key )
	{
		try 
		{
			return resources.getString( key );
		}
		catch ( Throwable e )
		{
			return key;
		}
	}
	
	public void messageAndExit( String title, ResourceBundle resources, String message )
	{
		MessageDlg dlg = new MessageDlg( title, resources, message );
		
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -5303587015039065226L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					Logout();
			    }
			}
		);
		
		getParent().getUI().addWindow( dlg );
	}
	
	public void Logout()
	{
        getUI().close();
	}

	public void showSubWindow( Window parent, Window child )
	{
		getUI().addWindow( child );
	}
	
}
