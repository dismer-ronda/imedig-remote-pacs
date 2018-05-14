package es.pryades.imedig.wiewer.echo3;

import java.util.ResourceBundle;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Column;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.Alignment;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.ApplicationInstance;

public class BrowserWnd extends ContentPane
{
	
	private static final long serialVersionUID = 1L;
	
	protected ResourceBundle resourceBundle;
	private ImedigApplication app;
	
	/**
	 * Creates a new <code>BrowserWnd</code>.
	 */
	public BrowserWnd( ImedigApplication app )
	{
		super();
		
		this.app = app;
		
		// Add design-time configured components.
		initComponents();
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents()
	{
		resourceBundle = app.getResourceBundle(); //ResourceBundle.getBundle( "es.pryades.imedig.wiewer.resource.localization.Messages", ApplicationInstance.getActive().getLocale() );
		WindowPane windowPane1 = new WindowPane();
		windowPane1.setStyleName( "Default" );
		windowPane1.setTitle( resourceBundle.getString( "BrowserWnd.Title" ) );
		windowPane1.setHeight( new Extent( 200, Extent.PX ) );
		windowPane1.setClosable( false );
		windowPane1.setWidth( new Extent( 600, Extent.PX ) );
		windowPane1.setResizable( false );
		windowPane1.setMovable( false );
		windowPane1.setModal( true );
		add( windowPane1 );
		Column column1 = new Column();
		column1.setInsets( new Insets( new Extent( 20, Extent.PX ) ) );
		column1.setCellSpacing( new Extent( 20, Extent.PX ) );
		windowPane1.add( column1 );
		Label label1 = new Label();
		label1.setText( resourceBundle.getString( "BrowserWnd.ErrorLine1" ) );
		RowLayoutData label1LayoutData = new RowLayoutData();
		label1LayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		label1LayoutData.setWidth( new Extent( 100, Extent.PERCENT ) );
		label1LayoutData.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		label1.setLayoutData( label1LayoutData );
		column1.add( label1 );
		Label label2 = new Label();
		label2.setText( resourceBundle.getString( "BrowserWnd.ErrorLine2" ) );
		ColumnLayoutData label2LayoutData = new ColumnLayoutData();
		label2LayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		label2.setLayoutData( label2LayoutData );
		column1.add( label2 );
	}
}
