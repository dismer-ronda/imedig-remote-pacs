package es.pryades.imedig.cloud.modules.Reports;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

/**
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
@Setter
@Getter
public class TabbedContainerReport implements TabSheet.SelectedTabChangeListener
{
	private static final Logger LOG = Logger.getLogger( TabbedContainerReport.class );

	private ImedigContext context;
	private Layout mainLayout;

	private VerticalLayout content;
	private TabSheet tabsheet;
	private List<FilteredContent> tabContentList;

	/**
	 * 
	 * 
	 */
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab( tabsheet.getSelectedTab() );

		FilteredContent configuration = (FilteredContent)tab.getComponent();
		configuration.initComponents();
	}

	/**
	 * 
	 * 
	 */
	private void initComponents() throws ImedigException
	{
		try
		{
			ImedigContext ctx = getContext();

			if ( content == null )
				content = new VerticalLayout();

			if ( tabsheet == null )
				tabsheet = new TabSheet();

			if ( tabContentList == null )
			{
				tabContentList = new ArrayList<FilteredContent>();

				tabContentList.add( new ReportsListTab( ctx ) );
				//tabContentList.add( new ReportsStatisticsTab( ctx ) );
				//tabContentList.add( new PacsStatisticsTab( ctx ) );
				
				for ( FilteredContent item : tabContentList )
				{
					item.setSizeFull();
					tabsheet.addTab( item );
					tabsheet.getTab( item ).setCaption( item.getTabTitle() );
				}

				// Inicilizando y seleccionado el rpimer tab
				FilteredContent configuration = (FilteredContent)tabContentList.get( 0 );
				configuration.initComponents();
				tabsheet.setSelectedTab( configuration );
				
				tabsheet.addSelectedTabChangeListener( this );
			}
		}
		catch ( Throwable exc )
		{
			exc.printStackTrace();
			throw new ImedigException( exc, LOG, ImedigException.UNKNOWN );
		}
	}

	public void render() throws ImedigException
	{
		this.initComponents();

		content.setSizeFull();

		tabsheet.setSizeFull();

		content.addComponent( tabsheet );
		content.setExpandRatio( tabsheet, 1.0f );

		getMainLayout().removeAllComponents();
		getMainLayout().addComponent( content );

		((VerticalLayout)getMainLayout()).setHeight( "100%" );
		((VerticalLayout)getMainLayout()).setExpandRatio( content, 1.0f );
	}
}
