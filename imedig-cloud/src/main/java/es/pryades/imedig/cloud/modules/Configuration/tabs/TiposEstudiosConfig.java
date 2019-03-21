package es.pryades.imedig.cloud.modules.Configuration.tabs;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TIPOS_ESTUDIOS_ADD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TIPOS_ESTUDIOS_DEL;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TIPOS_ESTUDIOS_MOD;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewTipoEstudio;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.TipoEstudioVto;
import es.pryades.imedig.cloud.vto.controlers.TipoEstudioControlerVto;
import es.pryades.imedig.cloud.vto.refs.TipoEstudioVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author hector.licea
 * 
 */
public class TiposEstudiosConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = -2902340113398310394L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( TiposEstudiosConfig.class );

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public TiposEstudiosConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( TipoEstudioVto.class, new TipoEstudioVto(), new TipoEstudioVtoFieldRef(), new QueryFilterRef( new TipoEstudio() ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return TipoEstudioControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "tipoEstudioConfig.table";
	}

	@Override
	public String getTabTitle()
	{
		return getContext().getString( "tipoEstudioConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null && getContext().hasRight( DERECHO_CONFIG_TIPOS_ESTUDIOS_MOD ) );
		setEnabledDelete( row != null && getContext().hasRight( DERECHO_CONFIG_TIPOS_ESTUDIOS_DEL ));
	}

	public void onModifyRow( Object row )
	{
		new ModalNewTipoEstudio( context, Operation.OP_MODIFY, (TipoEstudio)row, TiposEstudiosConfig.this, DERECHO_CONFIG_TIPOS_ESTUDIOS_MOD ).showModalWindow();
	}

	@Override
	public void onAddRow()
	{
		new ModalNewTipoEstudio( context, Operation.OP_ADD, null, TiposEstudiosConfig.this, DERECHO_CONFIG_TIPOS_ESTUDIOS_ADD ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewTipoEstudio( context, Operation.OP_DELETE, (TipoEstudio)row, TiposEstudiosConfig.this, DERECHO_CONFIG_TIPOS_ESTUDIOS_DEL ).showModalWindow();
	}

	@Override
	public Query getQueryObject()
	{
		return new TipoEstudio();
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}

	@Override
	public boolean isAddAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_TIPOS_ESTUDIOS_ADD );
	}

	@Override
	public boolean isModifyAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_TIPOS_ESTUDIOS_MOD );
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_TIPOS_ESTUDIOS_DEL );
	}
}
