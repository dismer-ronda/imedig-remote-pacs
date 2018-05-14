package es.pryades.imedig.wiewer.echo3;

import java.util.ArrayList;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.Table;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.app.list.DefaultListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.User;

public class QueryDlg extends WindowPane implements ModalContainer
{
    @SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger( QueryDlg.class );
    
	private static final long serialVersionUID = 1L;

	ModalContainer container;
	private int page;
	private Table tableStudies;
	private String queryUrl;
	private String token;
	private Label labelPage;
	private ArrayList<String> selectedStudies;
	protected ResourceBundle resourceBundle;
	private User user;

	private Column column7;

	private Row rowPageButtons;

	private TextField textPatientName;

	private TextField textNHC;

	private SelectField selectModality;

	private SelectField selectDate;

	private Button btnQuery;
	private Button btnOpen;
	private Button btnExit;

	private Row rowOpen;
	
	private ImedigApplication app;

	/**
	 * Creates a new <code>QueryDlg</code>.
	 */
	public QueryDlg( ModalContainer container, ImedigApplication app, User user ) 
	{
		super();

		this.container = container;
		this.app = app;
		this.user = user;
				
		// Add design-time configured components.
		initComponents();
		page = 0;

		tableStudies = null;
	    
		initModalityList();
		initDateList();
	}

	void initModalityList()
	{
		DefaultListModel defList = new DefaultListModel();
		
		String modList = resourceBundle.getString( "QueryForm.Modalities" );
		
		String modalities[] = modList.split( "," );
		
		defList.add( 0, "" );
		
		for ( String modality : modalities )
		{
			defList.add( modality );
		}

		selectModality.setModel( defList );
	}

	/**
	 * Returns the user's application instance, cast to its specific type.
	 *
	 * @return The user's application instance.
	 */
	protected ImedigApplication getApplication() {
		return app;
	}

	/**
	 * Configures initial state of component.
	 * WARNING: AUTO-GENERATED METHOD.
	 * Contents will be overwritten.
	 */
	private void initComponents()
	{
		resourceBundle = getApplication().getResourceBundle(); //ResourceBundle.getBundle( "es.pryades.imedig.wiewer.resource.localization.Messages", ApplicationInstance.getActive().getLocale() );
		this.setTitle( resourceBundle.getString( "QueryForm.Title" ) );
		this.setClosable( false );
		this.setWidth( new Extent( 800, Extent.PX ) );
		this.setHeight( new Extent( 480, Extent.PX ) );
		this.setMovable( false );
		this.setResizable( false );
		this.setModal( true );
		this.setBackground( Color.LIGHTGRAY );
		
		Column column1 = new Column();
		column1.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		column1.setCellSpacing( new Extent( 5, Extent.PX ) );
		add( column1 );
		
		Row row2 = new Row();
		row2.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		row2.setCellSpacing( new Extent( 5, Extent.PX ) );
		row2.setBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );

		column1.add( row2 );
		
		Label label2 = new Label();
		label2.setText( resourceBundle.getString( "QueryForm.PatientName" ) );
		row2.add( label2 );
		textPatientName = new TextField();
		textPatientName.setStyleName( "Default" );
		textPatientName.setWidth( new Extent( 100, Extent.PX ) );
		row2.add( textPatientName );
		Label label3 = new Label();
		label3.setText( resourceBundle.getString( "QueryForm.PatientId" ) );
		row2.add( label3 );
		textNHC = new TextField();
		textNHC.setStyleName( "Default" );
		textNHC.setWidth( new Extent( 100, Extent.PX ) );
		row2.add( textNHC );
		Label label5 = new Label();
		label5.setText( resourceBundle.getString( "QueryForm.Modality" ) );
		row2.add( label5 );
		selectModality = new SelectField();
		row2.add( selectModality );
		Label label6 = new Label();
		label6.setText( resourceBundle.getString( "QueryForm.StudyDate" ) );
		row2.add( label6 );
		selectDate = new SelectField();
		row2.add( selectDate );
		btnQuery = new Button();
		btnQuery.setStyleName( "Default" );
		ResourceImageReference imageReference1 = new ResourceImageReference( "/org/freedesktop/tango/22x22/actions/system-search.png" );
		btnQuery.setIcon( imageReference1 );
		btnQuery.setInsets( new Insets( new Extent( 6, Extent.PX ), new Extent( 2, Extent.PX ) ) );
		btnQuery.setToolTipText( resourceBundle.getString( "QueryForm.QueryTip" ) );

		btnQuery.setBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) );
		btnQuery.setRolloverBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) ); 
		btnQuery.setFocusedBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) ); 
		
		btnQuery.setBackground( Color.LIGHTGRAY );
		btnQuery.setDisabledBackground( Color.LIGHTGRAY );
		
		btnQuery.setFocusedBackground( Color.LIGHTGRAY );
		btnQuery.setFocusedBorder( new Border( new Extent( 0, Extent.PX ), Color.BLACK, Border.STYLE_NONE ) ); //.STYLE_GROOVE ) );

		btnQuery.setPressedBackground( Color.LIGHTGRAY );
		btnQuery.setRolloverBackground( Color.DARKGRAY );
		
		btnQuery.addActionListener( new ActionListener()
		{
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed( ActionEvent e )
			{
				onQuery();
			}
		} );
		row2.add( btnQuery );

		rowOpen = new Row();
		rowOpen.setInsets( new Insets( new Extent( 10, Extent.PX ) ) );
		rowOpen.setCellSpacing( new Extent( 5, Extent.PX ) );
		RowLayoutData rowOpenLayoutData = new RowLayoutData();
		rowOpenLayoutData.setAlignment( new Alignment( Alignment.RIGHT, Alignment.TOP ) );
		rowOpen.setLayoutData( rowOpenLayoutData );
		row2.add( rowOpen );

		btnOpen = new Button();
		btnOpen.setStyleName( "Default" );
		
		btnOpen.setIcon( AppUtils.getIcon( "open", 24 ) );
		btnOpen.setDisabledIcon( AppUtils.getGrayIcon( "open", 24 ) );
		
		btnOpen.setText( resourceBundle.getString( "QueryForm.Open" ) );
		btnOpen.setInsets( new Insets( new Extent( 6, Extent.PX ), new Extent( 2, Extent.PX ) ) );
		btnOpen.setActionCommand( "open" );
		btnOpen.setToolTipText( resourceBundle.getString( "QueryForm.OpenTip" ) );
		
		btnOpen.setBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) );
		btnOpen.setRolloverBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) ); 
		btnOpen.setFocusedBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) ); 
		
		btnOpen.setBackground( Color.LIGHTGRAY );
		btnOpen.setDisabledBackground( Color.LIGHTGRAY );
		
		btnOpen.setPressedBackground( Color.LIGHTGRAY );
		btnOpen.setRolloverBackground( Color.DARKGRAY );
		btnOpen.addActionListener( new ActionListener()
		{
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed( ActionEvent e )
			{
				onOpenStudy( e );
			}
		} );
		rowOpen.add( btnOpen );
		
		btnExit = new Button();
		btnExit.setStyleName( "Default" );
		
		btnExit.setIcon( AppUtils.getIcon( "exit", 24 ) );
		
		btnExit.setText( resourceBundle.getString( "QueryForm.Close" ) );
		btnExit.setInsets( new Insets( new Extent( 6, Extent.PX ), new Extent( 2, Extent.PX ) ) );
		btnExit.setActionCommand( "close" );
		btnExit.setToolTipText( resourceBundle.getString( "QueryForm.CloseTip" ) );
		
		btnExit.setBackground( Color.LIGHTGRAY );
		btnExit.setDisabledBackground( Color.LIGHTGRAY );
		
		btnExit.setBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) );
		btnExit.setRolloverBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) ); 
		btnExit.setFocusedBorder( new Border( new Extent( 1, Extent.PX ), Color.LIGHTGRAY, Border.STYLE_SOLID ) ); 

		btnExit.setPressedBackground( Color.LIGHTGRAY );
		btnExit.setRolloverBackground( Color.DARKGRAY );
		btnExit.addActionListener( new ActionListener()
		{
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed( ActionEvent e )
			{
				onClose( e );
			}
		} );
		rowOpen.add( btnExit );

		rowPageButtons = new Row();
		rowPageButtons.setCellSpacing( new Extent( 5, Extent.PX ) );
		ColumnLayoutData rowPageButtonsLayoutData = new ColumnLayoutData();
		rowPageButtonsLayoutData.setAlignment( new Alignment( Alignment.CENTER, Alignment.DEFAULT ) );
		rowPageButtons.setLayoutData( rowPageButtonsLayoutData );
		column1.add( rowPageButtons );
		
		Row row5 = new Row();
		row5.setCellSpacing( new Extent( 5, Extent.PX ) );
		column1.add( row5 );
		
		column7 = new Column();
		column7.setCellSpacing( new Extent( 5, Extent.PX ) );
		RowLayoutData column7LayoutData = new RowLayoutData();
		column7LayoutData.setWidth( new Extent( 100, Extent.PERCENT ) );
		column7.setLayoutData( column7LayoutData );
		row5.add( column7 );
		
	}

	Button getPageButton( String tooltip, String icon )
	{
		Button button1 = new Button();
		button1.setStyleName("Default");
		ResourceImageReference imageReference5 = new ResourceImageReference( icon );
		button1.setIcon(imageReference5);
		button1.setPressedBorder(new Border(new Extent(0, Extent.PX),
				Color.BLACK, Border.STYLE_SOLID));
		button1.setRolloverBorder(new Border(new Extent(0, Extent.PX),
				Color.BLACK, Border.STYLE_SOLID));
		button1.setInsets(new Insets(new Extent(0, Extent.PX)));
		button1.setDisabledBorder(new Border(new Extent(0, Extent.PX),
				Color.BLACK, Border.STYLE_SOLID));
		button1.setFocusedBorder(new Border(new Extent(0, Extent.PX),
				Color.BLACK, Border.STYLE_SOLID));
		button1.setBorder(new Border(new Extent(0, Extent.PX), Color.BLACK,
				Border.STYLE_SOLID));
		button1.setToolTipText( resourceBundle.getString( tooltip ) );
		button1.setBackground( Color.LIGHTGRAY );
		
		return button1;
	}
	
	void initPageButtons()
	{
		rowPageButtons.removeAll();
		
		Button button1 = getPageButton( "QueryForm.FirstPage", "/org/freedesktop/tango/22x22/actions/go-first.png" );
		button1.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onFirstPage(e);
			}
		});
		rowPageButtons.add(button1);

		Button button2 = getPageButton( "QueryForm.PreviousPage", "/org/freedesktop/tango/22x22/actions/go-previous.png" );
		button2.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onPreviousPage(e);
			}
		});
		rowPageButtons.add(button2);

		labelPage = new Label();
		rowPageButtons.add(labelPage);

		Button button3 = getPageButton( "QueryForm.NextPage", "/org/freedesktop/tango/22x22/actions/go-next.png" );
		button3.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onNextPage(e);
			}
		});
		rowPageButtons.add(button3);

		Button button4 = getPageButton( "QueryForm.LastPage", "/org/freedesktop/tango/22x22/actions/go-last.png" );
		button4.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
	
			public void actionPerformed(ActionEvent e) {
				onLastPage(e);
			}
		});
		rowPageButtons.add(button4);

		updatePageLabel();
	}
	
	void initDateList()
	{
		DefaultListModel defList = new DefaultListModel();
		
		defList.add( 0, "" );
		defList.add( 1, resourceBundle.getString("QueryForm.Today") );
		defList.add( 2, resourceBundle.getString("QueryForm.Yesterday") );
		defList.add( 3, resourceBundle.getString("QueryForm.LastWeek") );
		defList.add( 4, resourceBundle.getString("QueryForm.LastMonth") );
		defList.add( 5, resourceBundle.getString("QueryForm.LastYear") );
		
		selectDate.setModel( defList );

		selectDate.setSelectedIndex( user.getQuery() );
		
		/*if ( "All".equals( user.getQuery() )
			selectDate.setSelectedIndex( 0 );
		else
			selectDate.setSelectedItem( resourceBundle.getString( "QueryForm." + Settings.DefaultQuery ) );*/ 
	}
	
	void initErrorText()
	{
		rowPageButtons.removeAll();
		
		Label labelError = new Label();
		
		labelError.setText( resourceBundle.getString( "QueryForm.error" ) );
		
		rowPageButtons.add( labelError );
	}

	String convertDate( int index )
	{
		switch ( index )
		{
			case 1:
				return Utils.getTodayDate("yyyyMMdd");

			case 2:
				return Utils.getYesterdayDate("yyyyMMdd");

			case 3:
				return Utils.getLastWeekDate("yyyyMMdd") + "-";

			case 4:
				return Utils.getLastMonthDate("yyyyMMdd") + "-";

			case 5:
				return Utils.getLastYearDate("yyyyMMdd") + "-";
				
			default:
				return "";
		}
	}

	public void onQuery()  
	{
		page = 0;

		tableStudies = new Table();

		QueryTableModel model = new QueryTableModel( 19 );
		QueryRenderer renderer = new QueryRenderer( resourceBundle );
		
		tableStudies.setHeaderVisible( true );
		tableStudies.setStyleName( "Default" );
		tableStudies.setWidth( new Extent( 100, Extent.PERCENT ) );
		tableStudies.setSelectionEnabled( false );
		tableStudies.setModel( model );
		tableStudies.setDefaultHeaderRenderer( renderer ); 
		tableStudies.setDefaultRenderer( Object.class, renderer );
		tableStudies.setBorder( new Border( new Extent( 1, Extent.PX ), new Color( Colors.INT_METRO_TEAL ), Border.STYLE_SOLID ) );
		
		model.setPatientName( "*" + textPatientName.getText() + "*" );
		model.setPatientId( textNHC.getText() );
		model.setReferringPhysicianName( user.getFilter() );
		model.setStudyDate( convertDate( selectDate.getSelectedIndex() ) );
		model.setModalitiesInStudy( selectModality.getSelectedItem().toString() );
		
		column7.removeAll();
		
		int ret = model.doQuery();
		
		if ( ret > 0  )
		{
			column7.add( tableStudies );
			
			initPageButtons();
			updatePageLabel();
		}
		else
			initErrorText();
	}

	public void updatePageLabel()
	{
		labelPage.setText( (page + 1) + "/" + ((QueryTableModel)tableStudies.getModel()).getNumberOfPages() );
	}
	
	public void setQueryUrl( String queryUrl ) 
	{
		this.queryUrl = queryUrl;
	}

	public String getQueryUrl() 
	{
		return queryUrl;
	}

	private void onOpenStudy(ActionEvent e) 
	{
		if ( tableStudies != null )
		{
			QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
		
			selectedStudies = renderer.getSelectedStudies();
			
			if ( selectedStudies.size() > 0 )
			{
				container.notifyCommand( e.getActionCommand(), this );
				getParent().remove( QueryDlg.this );
			}
		}
	}

	private void onClose(ActionEvent e) 
	{
		container.notifyCommand( e.getActionCommand(), this );
		getParent().remove( QueryDlg.this );
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	private void onFirstPage(ActionEvent e) 
	{
		if ( tableStudies != null )
		{
			if ( ((QueryTableModel)tableStudies.getModel()).firstPage() )
			{
				QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
				
				renderer.clearSelected();

				((QueryTableModel)tableStudies.getModel()).fireTableDataChanged();
		
				page = ((QueryTableModel)tableStudies.getModel()).getPage();
	
				updatePageLabel();
			}
		}
	}

	private void onPreviousPage(ActionEvent e) 
	{
		if ( tableStudies != null )
		{
			if ( ((QueryTableModel)tableStudies.getModel()).previousPage() )
			{
				QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
				
				renderer.clearSelected();

				((QueryTableModel)tableStudies.getModel()).fireTableDataChanged();
	
				page = ((QueryTableModel)tableStudies.getModel()).getPage();
		
				updatePageLabel();
			}
		}
	}

	private void onNextPage(ActionEvent e) 
	{
		if ( tableStudies != null )
		{
			if ( ((QueryTableModel)tableStudies.getModel()).nextPage() )
			{
				QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
				
				renderer.clearSelected();

				((QueryTableModel)tableStudies.getModel()).fireTableDataChanged();
	
				page = ((QueryTableModel)tableStudies.getModel()).getPage();
		
				updatePageLabel();
			}
		}
	}

	private void onLastPage(ActionEvent e) 
	{
		if ( tableStudies != null )
		{
			if ( ((QueryTableModel)tableStudies.getModel()).lastPage() )
			{
				QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
				
				renderer.clearSelected();

				((QueryTableModel)tableStudies.getModel()).fireTableDataChanged();
	
				page = ((QueryTableModel)tableStudies.getModel()).getPage();
		
				updatePageLabel();
			}
		}
	}

	public void setSelectedStudies(ArrayList<String> selectedStudies) {
		this.selectedStudies = selectedStudies;
	}

	public ArrayList<String> getSelectedStudies() {
		return selectedStudies;
	}

	public void notifyCommand(String command, ModalContainer child) 
	{
	}

	public void clearSelected()
	{
		if ( tableStudies != null )
		{
			QueryRenderer renderer = ((QueryRenderer)tableStudies.getDefaultRenderer(Object.class));
				
			renderer.clearSelected();
		}
	}
}
