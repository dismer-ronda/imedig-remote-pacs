package es.pryades.imedig.cloud.modules.Configuration;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

public class ConfigurationDlg extends Window 
{
	private static final long serialVersionUID = 7516227543802099450L;

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
		mainLayout.setId( "main.config.layout" );

		setContent( mainLayout );
		
		ClickListener closeListener = new ClickListener(){
			
			private static final long serialVersionUID = -4176274270634149738L;

			@Override
			public void buttonClick( ClickEvent event ){
				ConfigurationDlg.this.close();
			}
		};
		
		ImedigTabbedContainerConfig instance = new ImedigTabbedContainerConfig(closeListener);

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
