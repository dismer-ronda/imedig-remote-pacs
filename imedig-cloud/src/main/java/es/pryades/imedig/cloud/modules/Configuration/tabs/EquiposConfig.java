package es.pryades.imedig.cloud.modules.Configuration.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_RECURSOS_ADD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_RECURSOS_DEL;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_RECURSOS_MOD;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewEquipo;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.RecursoVto;
import es.pryades.imedig.cloud.vto.controlers.RecursoControlerVto;
import es.pryades.imedig.cloud.vto.refs.RecursoVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author hector.licea
 * 
 */
public class EquiposConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = -4614986510639711401L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( EquiposConfig.class );

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public EquiposConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public TableImedigPaged getTableRows()
	{
		Recurso query = new Recurso();
		//query.setTipo( 1 );
		
		return new TableImedigPaged( RecursoVto.class, new RecursoVto(), new RecursoVtoFieldRef(), new QueryFilterRef( query ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return RecursoControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "recursoConfig.table";
	}

	@Override
	public String getTabTitle()
	{
		return getContext().getString( "equipoConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null && getContext().hasRight( DERECHO_CONFIG_RECURSOS_MOD ));
		setEnabledDelete( row != null && getContext().hasRight( DERECHO_CONFIG_RECURSOS_DEL ));
	}

	public void onModifyRow( Object row )
	{
		new ModalNewEquipo( context, Operation.OP_MODIFY, (Recurso)row, EquiposConfig.this, DERECHO_CONFIG_RECURSOS_MOD ).showModalWindow();
	}

	@Override
	public void onAddRow()
	{
		new ModalNewEquipo( context, Operation.OP_ADD, null, EquiposConfig.this, DERECHO_CONFIG_RECURSOS_ADD ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewEquipo( context, Operation.OP_DELETE, (Recurso)row, EquiposConfig.this, DERECHO_CONFIG_RECURSOS_DEL).showModalWindow();
	}

	@Override
	public Query getQueryObject()
	{
		Recurso query = new Recurso();
		query.setTipo( 1 );
		return query;
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}

	@Override
	public boolean isAddAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_RECURSOS_ADD );
	}

	@Override
	public boolean isModifyAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_RECURSOS_MOD );
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_RECURSOS_DEL );
	}
}
