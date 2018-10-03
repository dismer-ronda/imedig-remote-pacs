package es.pryades.imedig.cloud.modules.Reports;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

public class ReportsDlg extends Window 
{
	private static final long serialVersionUID = -6286380398905849561L;

	private ImedigContext ctx;

	private VerticalLayout mainLayout;
	
	private ReportsListTab reportsTab;

	public ReportsDlg( ImedigContext aCtx )
	{
		super( "" );
		
		ctx = aCtx;

		setCaption( ctx.getString( "ReportsDlg.title" ) );
		
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
		
		reportsTab = new ReportsListTab( ctx );
		reportsTab.initComponents();
		reportsTab.setSizeFull();
		mainLayout.addComponent( reportsTab );
		
		/*TabbedContainerReport instance = new TabbedContainerReport();

		instance.setContext( ctx );
		instance.setMainLayout( mainLayout );
		
		try
		{
			instance.render();
		}
		catch ( ImedigException e )
		{
		}*/
	}
}
