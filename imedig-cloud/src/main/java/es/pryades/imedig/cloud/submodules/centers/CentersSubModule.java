package es.pryades.imedig.cloud.submodules.centers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import lombok.Getter;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.OptionData;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.submodules.AbstractCentersSubModule;

public class CentersSubModule extends AbstractCentersSubModule implements ListenerCenterSelect
{
	private static final long serialVersionUID = 64871981232494197L;

	private static final Logger LOG = Logger.getLogger( CentersSubModule.class );
	
	private VerticalLayout mainColumn;

	@Getter
	private CentersView centersView;

	@SuppressWarnings("rawtypes")
	@Override
	protected List getSubModuleOptions() throws ImedigException
	{
		return new ArrayList<OptionData>();
	}

	@Override
	protected String getSubModuleName()
	{
		return getContext().getString( "words.centers" );
	}

	@Override
	protected void renderSubModule() throws ImedigException
	{
		mainColumn = new VerticalLayout();
		mainColumn.setWidth( "100%" );
		mainColumn.setMargin( new MarginInfo( false, true, false, true ) );
		mainColumn.setSpacing( false );

		centersView = new CentersView( getContext() );
		centersView.setListenerCenterSelect( this );

		mainColumn.addComponent( centersView );

		getMainLayout().addComponent( mainColumn );
	}

	@Override
	public void clicked( OptionData option ) throws ImedigException
	{
	}

	@Override
	public void centerSelected( Integer id )
	{
		try
		{
	    	DetallesCentrosManager centrosManager = (DetallesCentrosManager) IOCManager.getInstanceOf( DetallesCentrosManager.class );

	    	DetalleCentro detalleCentro = (DetalleCentro)centrosManager.getRow( getContext(), id  );
	    	
	    	String url = centrosManager.getCentroUrl( getContext(), detalleCentro, "" );
	    	
	    	if ( url != null )
	    		new ShowExternalViewerDlg( getContext(), detalleCentro, url, true ).showModalWindow();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
