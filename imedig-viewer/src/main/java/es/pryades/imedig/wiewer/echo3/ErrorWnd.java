package es.pryades.imedig.wiewer.echo3;

import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.RowLayoutData;

public class ErrorWnd extends ContentPane implements GenericWnd
{
	private static final long serialVersionUID = 1L;

	protected ResourceBundle resourceBundle;

	private String title;
	private String text;

	/**
	 * Creates a new <code>ErrorWnd</code>.
	 */
	public ErrorWnd( String title, String text )
	{
		super();

		this.title = title;
		this.text = text;
		
		// Add design-time configured components.
		initComponents();
	}

	/**
	 * Returns the user's application instance, cast to its specific type.
	 * 
	 * @return The user's application instance.
	 */
	protected ImedigApplication getApplication()
	{
		return (ImedigApplication)getApplicationInstance();
	}

	/**
	 * Configures initial state of component. WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents()
	{
		resourceBundle = getApplication().getResourceBundle(); //ResourceBundle.getBundle( "es.pryades.imedig.wiewer.resource.localization.Messages", ApplicationInstance.getActive().getLocale() );
		
		WindowPane windowPane1 = new WindowPane();
		windowPane1.setStyleName( "Default" );
		windowPane1.setTitle( title );
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
		ColumnLayoutData column1LayoutData = new ColumnLayoutData();
		column1LayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		column1LayoutData.setHeight( new Extent( 100, Extent.PERCENT ) );
		column1LayoutData.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		column1.setLayoutData( column1LayoutData );
		windowPane1.add( column1 );
		
		Label label1 = new Label();
		label1.setText( text );
		RowLayoutData label1LayoutData = new RowLayoutData();
		label1LayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		label1LayoutData.setWidth( new Extent( 100, Extent.PERCENT ) );
		label1LayoutData.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		label1.setLayoutData( label1LayoutData );
		column1.add( label1 );
	}

	public void takeControl( ApplicationInstance app )
	{
	}
}
