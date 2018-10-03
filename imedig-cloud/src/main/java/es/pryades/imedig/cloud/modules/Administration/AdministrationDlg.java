package es.pryades.imedig.cloud.modules.Administration;

import java.util.ResourceBundle;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

public class AdministrationDlg extends Window 
{
	private static final long serialVersionUID = -7930120929949164496L;
	
	private ImedigContext ctx;
	private ResourceBundle resource;

	private VerticalLayout mainLayout;

	public AdministrationDlg( ImedigContext aCtx )
	{
		super( "" );
		
		ctx = aCtx;

		setCaption( ctx.getString( "AdministrationDlg.title" ) );
		
		setResizable( false );
		setModal( true );
		setClosable( true );
		addCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "1024px" );
		setHeight( "600px" );
		center();

		mainLayout = new VerticalLayout();
		
		mainLayout.setWidth( "100%" );
		mainLayout.setHeight( "100%" );
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		setContent( mainLayout );
		
		ImedigTabbedContainerAdmin instance = new ImedigTabbedContainerAdmin();

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
