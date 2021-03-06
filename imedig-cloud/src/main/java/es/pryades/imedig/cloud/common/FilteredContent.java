package es.pryades.imedig.cloud.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedig;
import es.pryades.imedig.core.common.TableImedigPaged;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public abstract class FilteredContent extends VerticalLayout implements Property.ValueChangeListener
{
	private static final long serialVersionUID = -7355442891546060426L;

	private static final Logger LOG = Logger.getLogger( FilteredContent.class );

	@Setter @Getter protected ImedigContext context;

	protected Button btnApply;
	protected HorizontalLayout rowQuery;

	private TableImedigPaged tableRows;

	private Button btnAdd;
	private Button btnModify;
	private Button btnDelete;

	protected VerticalLayout mainLayout;
	protected HorizontalLayout oppLayout;
	protected HorizontalLayout cellOppLayoutLeft;
	protected HorizontalLayout cellOppLayoutCenter;
	protected HorizontalLayout cellOppLayoutRight;

	protected String adminCntOpBusquedaFormTitle;
	protected String adminCntResultLayoutTitle;
	protected boolean isInitialized;

	private String[] visibleCols;
	
	public FilteredContent( ImedigContext ctx )
	{
		this.context = ctx;
		this.isInitialized = false;
		
		setSizeFull();
		
		setSpacing( true );
	}

	public abstract String getTabTitle();

	public abstract Component getQueryComponent();

	public void initQueryComponents()
	{
		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );
		rowButton.setHeight( "100%" );

		btnApply = new Button(FontAwesome.SEARCH);
		btnApply.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnApply.setDescription( context.getString( "words.search" ) );
		btnApply.setClickShortcut( KeyCode.ENTER );
		
		rowButton.addComponent( btnApply );
		//rowButton.setComponentAlignment( bttnApply, Alignment.MIDDLE_CENTER );
		
		addButtonApplyFilterClickListener();
		
		Component component = getQueryComponent();
		
		if ( component != null )
		{
			rowQuery = new HorizontalLayout();
			rowQuery.setSpacing( true );
			rowQuery.setMargin( new MarginInfo( true, false, false, true ) );
			//rowQuery.setWidth( "100%" );
			rowQuery.addComponent( component );
			rowQuery.addComponent( btnApply );
			rowQuery.setComponentAlignment( btnApply, Alignment.BOTTOM_LEFT );
			rowQuery.setExpandRatio( component, 0.95f );
			rowQuery.setExpandRatio( btnApply, 0.05f );
			
		}
	}
	
	public abstract TableImedigPaged getTableRows();
	public abstract String[] getVisibleCols();
	public abstract String getResouceKey();

	public boolean isAddAvailable()
	{
		return false;
	}

	public boolean isModifyAvailable()
	{
		return false;
	}

	public boolean isDeleteAvailable()
	{
		return false;
	}

	public boolean isExtrasAvailable()
	{
		return false;
	}

	public List<Component> getExtraOperations()
	{
		return null;
	}

	public void initComponents()
	{
		if (isInitialized) return;
		
		isInitialized = true;
		
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing( true );
		//mainLayout.setMargin( false ); //new MarginInfo( false, true, true, true ) );
		mainLayout.setSizeFull();

		initQueryComponents();
		
		if ( rowQuery != null ){
			addComponent( rowQuery );
		}
		
		addComponent( mainLayout );
		setExpandRatio( mainLayout, 1.0f );
		
		tableRows = getTableRows();
		
		if ( tableRows != null )
		{
			//tableRows.setMargin( new MarginInfo(true, false, false, false));

			visibleCols = getVisibleCols();
			if ( visibleCols != null )
			{
				HashMap<String, String> tableHeadersName = new HashMap<String, String>();
				for ( String atribute : visibleCols )
					tableHeadersName.put( atribute, getContext().getString( getResouceKey() + ".headerName." + atribute ) );
	
				tableRows.setTableHeadersNames( tableHeadersName );
				tableRows.setVisibleCols( new ArrayList<String>( Arrays.asList( this.visibleCols ) ) );
			}
			
			tableRows.initializeTable();
			tableRows.getTable().addValueChangeListener( this );
		}

		if ( isAddAvailable() )
		{
			btnAdd = new Button();
			btnAdd.setCaption( getContext().getString( "words.add" ) );
			btnAdd.setEnabled( isAddAvailable() );
			bttnAddListener();
		}

		if ( isModifyAvailable() )
		{
			btnModify = new Button();
			btnModify.setCaption( getContext().getString( "words.modify" ) );
			btnModify.setEnabled( false );
			bttnModifyListener();
		}

		if ( isDeleteAvailable() )
		{
			btnDelete = new Button();
			btnDelete.setCaption( getContext().getString( "words.delete" ) );
			btnDelete.setEnabled( false );
			bttnDeleteListener();
		}

		HorizontalLayout space = new HorizontalLayout();
		space.setWidthUndefined();
		cellOppLayoutLeft = new HorizontalLayout(space);
		cellOppLayoutLeft.setSpacing( true );
		cellOppLayoutLeft.setMargin( false );

		cellOppLayoutCenter = new HorizontalLayout();
		cellOppLayoutCenter.setSpacing( true );
		cellOppLayoutCenter.setMargin( false );

		space = new HorizontalLayout();
		space.setWidthUndefined();
		cellOppLayoutRight = new HorizontalLayout();
		cellOppLayoutRight.setSpacing( true );
		cellOppLayoutRight.setMargin( false );/*new MarginInfo( false, true, true, false ) */
		HorizontalLayout right = new HorizontalLayout(cellOppLayoutRight, space);
		right.setSpacing( true );
		right.setMargin( false );

		oppLayout = new HorizontalLayout(cellOppLayoutLeft, cellOppLayoutCenter, right);
		oppLayout.setMargin( false );
		oppLayout.setWidth( "100%" );
		oppLayout.setHeight( "38px" );
		oppLayout.setComponentAlignment( cellOppLayoutLeft, Alignment.MIDDLE_LEFT );
		oppLayout.setComponentAlignment( cellOppLayoutCenter, Alignment.MIDDLE_CENTER );
		oppLayout.setComponentAlignment( right, Alignment.MIDDLE_RIGHT );

		
		if ( isAddAvailable() || isModifyAvailable() || isDeleteAvailable() || isExtrasAvailable() )
		{
			if ( isAddAvailable() )
				addOpLeft( btnAdd );
			
			if ( isModifyAvailable() )
				addOpLeft( btnModify );
			
			if ( isDeleteAvailable() )
				addOpLeft( btnDelete );
			
			if ( isExtrasAvailable() )
			{
				List<Component> extras = getExtraOperations();
				if ( extras != null )
					for ( Component extra : extras )
						addOpLeft( extra );
			}
		}

		if ( tableRows != null )
		{
			//cellOppLayoutRight.setMargin( true );
			cellOppLayoutCenter.addComponent( tableRows.getTablePageOppContainer() );

			mainLayout.addComponent( tableRows );
			mainLayout.setExpandRatio( tableRows, 1.0f );
		}

		if ( oppLayout.getComponentCount() > 0 )
			mainLayout.addComponent( oppLayout );
		
		initSpecificContents();
		
		refreshVisibleContent();
	}
	
	protected void addOpLeft( Component component  ){
		cellOppLayoutLeft.addComponent( component );
	}

	protected void addOpCenter( Component component  ){
		cellOppLayoutCenter.addComponent( component );
	}

	protected void addOpRight( Component component  ){
		cellOppLayoutRight.addComponent( component );
	}

	public void initSpecificContents()
	{
	}

	public String validateBeforeApplyFilters()
	{
		return "";
	}

	public void addButtonApplyFilterClickListener()
	{
		btnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -1024068691968782009L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				String validateResult = validateBeforeApplyFilters();

				if ( validateResult.compareTo( "" ) == 0 )
				{
					refreshVisibleContent();
				}
				else
				{
					Notification.show( validateResult, Notification.Type.ERROR_MESSAGE );
				}
			}
		} );
	}

	public abstract void onAddRow();
	public abstract void onModifyRow( Object row );
	public abstract void onDeleteRow( Object row );

	private void bttnAddListener()
	{
		if ( btnAdd != null )
		{
			btnAdd.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -4845780258823572542L;

				public void buttonClick( ClickEvent event )
				{
					onAddRow();
				}
			} );
		}
	}

	private void bttnModifyListener()
	{
		if ( btnModify != null )
		{
			btnModify.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 2064750895174837758L;

				public void buttonClick( ClickEvent event )
				{
					Integer rowId = (Integer)tableRows.getTable().getValue();
	
					if ( rowId != null )
					{
						try
						{
							onModifyRow( tableRows.getRowValue( rowId ) );
						}
						catch ( Throwable e )
						{
							e.printStackTrace();
						}
					}
					else
					{
						onSelectedRow( null );
					}
				}
			} );
		}
	}

	private void bttnDeleteListener()
	{
		btnDelete.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 410917858047113542L;

			public void buttonClick( ClickEvent event )
			{
				Integer rowId = (Integer)tableRows.getTable().getValue();

				if ( rowId != null )
				{
					try
					{
						onDeleteRow( tableRows.getRowValue( rowId ) );
					}
					catch ( Throwable e )
					{
						Utils.logException( e, LOG );
					}
				}
				else
				{
					onSelectedRow( null );
				}
			}
		} );
	}

	public void refreshVisibleContent()
	{
		try
		{
			applySelectedFilters();

			if ( tableRows != null)
				tableRows.refreshVisibleContent( tableRows.getCurrPage() );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		onSelectedRow( null );
	}

	public abstract Query getQueryObject();

	public void applySelectedFilters()
	{
		if ( tableRows != null )
		{
			QueryFilterRef queryFilteRef = new QueryFilterRef( getQueryObject() );
			
			tableRows.getImedigPaginatorFilter().setPaginatorQueryFilterRef( queryFilteRef );
		}
	}

	public TableImedig getTableValues()
	{
		return tableRows;
	}

	public abstract void onSelectedRow( Object row );
	
	@Override
	public void valueChange( ValueChangeEvent event )
	{
		if ( tableRows != null )
		{
			Integer rowId = (Integer)tableRows.getTable().getValue();
	
			if ( rowId != null )
			{
				try
				{
					onSelectedRow( tableRows.getRowValue( rowId ) );
				}
				catch ( Throwable e )
				{
					e.printStackTrace();
				}
			}
			else
				onSelectedRow( null );
		}
	}
	
	public void setEnabledAdd( boolean enable )
	{
		if ( btnAdd != null )
			btnAdd.setEnabled( enable );
	}

	public void setEnabledModify( boolean enable )
	{
		if ( btnModify != null )
			btnModify.setEnabled( enable );
	}

	public void setEnabledDelete( boolean enable )
	{
		if ( btnDelete != null )
			btnDelete.setEnabled( enable );
	}

	public Object getSelectedRow()
	{
		if ( tableRows != null )
		{
			Integer rowId = (Integer)tableRows.getTable().getValue();
	
			if ( rowId != null )
			{
				try
				{
					return tableRows.getRowValue( rowId );
				}
				catch ( Throwable e )
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public int getNumberOfRows()
	{
		return tableRows != null ? tableRows.getTotalRows() : 0;
	}
}
