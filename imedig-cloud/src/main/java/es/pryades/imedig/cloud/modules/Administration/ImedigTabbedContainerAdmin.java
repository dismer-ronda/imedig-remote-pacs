package es.pryades.imedig.cloud.modules.Administration;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.modules.Administration.tabs.AccesosAdmin;
import es.pryades.imedig.cloud.modules.Administration.tabs.AdminContent;

/**
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
@Setter
@Getter
public class ImedigTabbedContainerAdmin implements TabSheet.SelectedTabChangeListener
{
	private static final Logger LOG = Logger.getLogger( ImedigTabbedContainerAdmin.class );
	
	private ImedigContext context;
	private Layout mainLayout;
	
	private VerticalLayout content;
	private TabSheet tabsheet; 
	private List<AdminContent> tabContentList; 

	/**
	 * Create and Initialize the components
	 */
	public ImedigTabbedContainerAdmin()
	{

	}

	/**
	 * 
	 * 
	 */
	public void selectedTabChange( SelectedTabChangeEvent event )
	{

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
			{
				content = new VerticalLayout();
			}

			if ( tabsheet == null )
				tabsheet = new TabSheet();

			// initializing tabContent List
			if ( tabContentList == null )
			{
				tabContentList = new ArrayList<AdminContent>();

				// adding tabs

				// Accesos Admin Tab
				if ( ctx.hasRight( "administracion.acceso" ) )
				{ // cambiar a administracion.costos
					tabContentList.add( new AccesosAdmin( ctx ) );
				}

				for ( AdminContent item : tabContentList )
				{
					item.setSizeFull();
					tabsheet.addTab( item );
					tabsheet.getTab( item ).setCaption( item.getTabTitle() );
				}
			}

		}
		catch ( Exception exc )
		{
			throw new ImedigException( exc, LOG, ImedigException.UNKNOWN );
		}
	}

	public void render() throws ImedigException
	{
		this.initComponents();

		content.setWidth( "100%" );
		content.setHeight( "100%" );
		content.setMargin( true );
		content.setSpacing( true );
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
