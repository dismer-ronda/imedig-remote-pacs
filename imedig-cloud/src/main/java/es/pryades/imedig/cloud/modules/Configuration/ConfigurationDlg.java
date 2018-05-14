package es.pryades.imedig.cloud.modules.Configuration;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

@SuppressWarnings("serial")
public class ConfigurationDlg extends Window 
{
	private ImedigContext ctx;

	private VerticalLayout mainLayout;

	public ConfigurationDlg( ImedigContext aCtx )
	{
		super( "" );
		
		ctx = aCtx;

		setCaption( ctx.getString( "ConfigurationDlg.title" ) );

		setResizable( false );
		setModal( true );
		setClosable( true );
		addCloseShortcut( KeyCode.ESCAPE );
		setWidth( "1280px" );
		setHeight( "800px" );
		center();

		mainLayout = new VerticalLayout();

		mainLayout.setWidth( "100%" );
		mainLayout.setHeight( "100%" );
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		setContent( mainLayout );
		
		ImedigTabbedContainerConfig instance = new ImedigTabbedContainerConfig();

		instance.setContext( ctx );
		instance.setMainLayout( mainLayout );
		
		try
		{
			instance.render();
		}
		catch ( ImedigException e )
		{
		}
	}
}
