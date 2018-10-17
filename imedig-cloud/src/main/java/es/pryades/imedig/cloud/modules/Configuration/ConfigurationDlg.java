package es.pryades.imedig.cloud.modules.Configuration;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
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
		
		settingClose();
	}
	
	private void settingClose(){
		Button bttnClose = new Button( ctx.getString( "words.close" ) );

		HorizontalLayout closeContainer = new HorizontalLayout(bttnClose);
		closeContainer.setComponentAlignment( bttnClose, Alignment.BOTTOM_RIGHT );
		mainLayout.addComponent( closeContainer );
		mainLayout.setComponentAlignment( closeContainer, Alignment.BOTTOM_RIGHT );
		bttnClose.addClickListener( new ClickListener(){
			
			@Override
			public void buttonClick( ClickEvent event ){
				ConfigurationDlg.this.close();
				
			}
		} );
	}
}
