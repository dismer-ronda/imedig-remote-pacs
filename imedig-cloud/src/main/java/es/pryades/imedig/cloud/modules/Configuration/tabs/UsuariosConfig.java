package es.pryades.imedig.cloud.modules.Configuration.tabs;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TODO;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_USUARIOS_ADD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_USUARIOS_DEL;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_USUARIOS_MOD;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewUsuario;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.UsuarioVto;
import es.pryades.imedig.cloud.vto.controlers.UsuarioControlerVto;
import es.pryades.imedig.cloud.vto.refs.UsuarioVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class UsuariosConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = 1121627091435186860L;

	public UsuariosConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public String[] getVisibleCols()
	{
		return UsuarioControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "usuariosConfig.tableUsuarioConfig";
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( UsuarioVto.class, new UsuarioVto(), new UsuarioVtoFieldRef(), new QueryFilterRef( new UsuarioQuery() ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	@Override
	public String getTabTitle()
	{
		return getContext().getString( "usuariosConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null && getContext().hasRight(DERECHO_CONFIG_USUARIOS_MOD));
		setEnabledDelete( row != null && getContext().hasRight(DERECHO_CONFIG_USUARIOS_DEL));
	}

	@Override
	public Query getQueryObject()
	{
		UsuarioQuery query = new UsuarioQuery();
		
		if ( !context.hasRight( DERECHO_CONFIG_TODO ) )
			query.setCentros( context.getCentros() );

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
		new ModalNewUsuario( context, Operation.OP_ADD, null, UsuariosConfig.this, DERECHO_CONFIG_USUARIOS_ADD ).showModalWindow();
	}

	public void onModifyRow( Object row )
	{
		new ModalNewUsuario( context, Operation.OP_MODIFY, (Usuario)row, UsuariosConfig.this, DERECHO_CONFIG_USUARIOS_MOD ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewUsuario( context, Operation.OP_DELETE, (Usuario)row, UsuariosConfig.this, DERECHO_CONFIG_USUARIOS_DEL ).showModalWindow();
	}

	@Override
	public boolean isAddAvailable()
	{
		return getContext().hasRight(DERECHO_CONFIG_USUARIOS_ADD);
	}

	@Override
	public boolean isModifyAvailable()
	{
		return getContext().hasRight(DERECHO_CONFIG_USUARIOS_MOD);
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return getContext().hasRight(DERECHO_CONFIG_USUARIOS_DEL);
	}
}
