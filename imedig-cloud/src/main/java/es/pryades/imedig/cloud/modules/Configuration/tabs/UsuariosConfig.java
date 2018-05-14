package es.pryades.imedig.cloud.modules.Configuration.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.ImagenQuery;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewImage;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewUsuario;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.ImagenVto;
import es.pryades.imedig.cloud.vto.UsuarioVto;
import es.pryades.imedig.cloud.vto.controlers.ImagenControlerVto;
import es.pryades.imedig.cloud.vto.controlers.UsuarioControlerVto;
import es.pryades.imedig.cloud.vto.refs.ImagenVtoFieldRef;
import es.pryades.imedig.cloud.vto.refs.UsuarioVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public class UsuariosConfig extends FilteredContent implements ModalParent
{
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
		setEnabledModify( row != null );
		setEnabledDelete( row != null );
	}

	@Override
	public Query getQueryObject()
	{
		UsuarioQuery query = new UsuarioQuery();
		
		if ( !context.hasRight( "configuracion.todo" ) )
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
		new ModalNewUsuario( context, Operation.OP_ADD, null, UsuariosConfig.this, "configuracion.usuarios.adicionar" ).showModalWindow();
	}

	public void onModifyRow( Object row )
	{
		new ModalNewUsuario( context, Operation.OP_MODIFY, (Usuario)row, UsuariosConfig.this, "configuracion.usuarios.modificar" ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewUsuario( context, Operation.OP_DELETE, (Usuario)row, UsuariosConfig.this, "configuracion.usuarios.borrar" ).showModalWindow();
	}

	@Override
	public boolean isAddAvailable()
	{
		return true;
	}

	@Override
	public boolean isModifyAvailable()
	{
		return true;
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return true;
	}
}
