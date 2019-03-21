package es.pryades.imedig.cloud.modules.Configuration.tabs;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_INFORMES_PLATILLAS_ADD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_INFORMES_PLATILLAS_DEL;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_INFORMES_PLATILLAS_MOD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TODO;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.InformePlantilla;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.InformePlantillaQuery;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewInformePlantilla;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.InformePlantillaVto;
import es.pryades.imedig.cloud.vto.controlers.InformePlantillaControlerVto;
import es.pryades.imedig.cloud.vto.refs.InformePlantillaVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class InformesPlantillasConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = 1615067269082314851L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( InformesPlantillasConfig.class );

	public InformesPlantillasConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public String[] getVisibleCols()
	{
		return InformePlantillaControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "imagenesConfig.tableImagenesConfig";
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( InformePlantillaVto.class, new InformePlantillaVto(), new InformePlantillaVtoFieldRef(), new QueryFilterRef( new InformePlantillaQuery() ), context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	@Override
	public String getTabTitle()
	{
		return getContext().getString( "informesPlantillasConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null && getContext().hasRight( DERECHO_CONFIG_INFORMES_PLATILLAS_MOD ));
		setEnabledDelete( row != null && getNumberOfRows() > 1 && getContext().hasRight( DERECHO_CONFIG_INFORMES_PLATILLAS_DEL ));
	}

	@Override
	public Query getQueryObject()
	{
		InformePlantillaQuery query = new InformePlantillaQuery();
		
		if ( !context.hasRight( DERECHO_CONFIG_TODO ) )
			query.setUsuario( context.getUsuario().getId() );

		return query;
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}

	@Override
	public void onAddRow()
	{
		new ModalNewInformePlantilla( context, Operation.OP_ADD, null, InformesPlantillasConfig.this, DERECHO_CONFIG_INFORMES_PLATILLAS_ADD ).showModalWindow();
	}

	public void onModifyRow( Object row )
	{
		new ModalNewInformePlantilla( context, Operation.OP_MODIFY, (InformePlantilla)row, InformesPlantillasConfig.this, DERECHO_CONFIG_INFORMES_PLATILLAS_MOD ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewInformePlantilla( context, Operation.OP_DELETE, (InformePlantilla)row, InformesPlantillasConfig.this, DERECHO_CONFIG_INFORMES_PLATILLAS_DEL ).showModalWindow();
	}

	@Override
	public boolean isAddAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_INFORMES_PLATILLAS_ADD );
	}

	@Override
	public boolean isModifyAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_INFORMES_PLATILLAS_MOD );
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_INFORMES_PLATILLAS_DEL );
	}
}
