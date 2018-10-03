package es.pryades.imedig.cloud.modules.Reports;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import com.vaadin.ui.UI;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public final class ShowExternalUrlDlg extends Window
{
	private static final long serialVersionUID = 7927312120567260994L;

	private static final Logger LOG = Logger.getLogger( ShowExternalUrlDlg.class );
	
	private String url;
	
	protected VerticalLayout layout;
	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesContainer;

	protected Button bttnClose;
	
	@Getter private ImedigContext context;

	/**
	 * 
	 * @param ctx
	 * @param resources
	 * @param modalOperation
	 * @param orgCentro
	 * @param parentWindow
	 */
	public ShowExternalUrlDlg( ImedigContext ctx, String caption, String url  )
	{
		super();
		
		this.context = ctx;
		this.url = url;
		
		addCloseShortcut( KeyCode.ESCAPE );

		layout = new VerticalLayout();
		
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setWidth( "1024px" );
		layout.setHeight( "768px" );

		setContent( layout );

		setModal( true );
		setResizable( false );
		setClosable( false );

		setCaption( caption );

		center();

		initComponents();
	}

	public void initComponents()
	{
		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );
		componentsContainer.setSizeFull();
		
		operacionesContainer = new HorizontalLayout();

		bttnClose = new Button( context.getString( "words.close" ) );

		bttnClose.focus();

		operacionesContainer.addComponent( bttnClose );
		operacionesContainer.setComponentAlignment( bttnClose, Alignment.BOTTOM_RIGHT );

		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesContainer );
		layout.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( componentsContainer, 1.0f );

		ExternalResource resource = new ExternalResource( url );
		BrowserFrame e = new BrowserFrame( null, resource );
	    //e.set.setType(Embedded.TYPE_BROWSER);
	    e.setSizeFull();

		componentsContainer.addComponent( e );
		
		bttnCloseListener();
	}

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	private void bttnCloseListener()
	{
		bttnClose.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7591024099361245222L;

			public void buttonClick( ClickEvent event )
			{
				getUI().removeWindow( ShowExternalUrlDlg.this );
			}
		} );
	}
}
