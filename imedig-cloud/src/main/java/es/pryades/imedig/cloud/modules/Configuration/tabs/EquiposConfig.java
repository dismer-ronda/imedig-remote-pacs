package es.pryades.imedig.cloud.modules.Configuration.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewEquipo;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.InstalacionVto;
import es.pryades.imedig.cloud.vto.controlers.InstalacionControlerVto;
import es.pryades.imedig.cloud.vto.refs.InstalacionVtoFieldRef;
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
		Instalacion query = new Instalacion();
		//query.setTipo( 1 );
		
		return new TableImedigPaged( InstalacionVto.class, new InstalacionVto(), new InstalacionVtoFieldRef(), new QueryFilterRef( query ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return InstalacionControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "instalacionConfig.table";
	}

	@Override
	public String getTabTitle()
	{
		return getContext().getString( "equipoConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null );
		setEnabledDelete( row != null );
	}

	public void onModifyRow( Object row )
	{
		new ModalNewEquipo( context, Operation.OP_MODIFY, (Instalacion)row, EquiposConfig.this, "configuracion.instalaciones" ).showModalWindow();
	}

	@Override
	public void onAddRow()
	{
		new ModalNewEquipo( context, Operation.OP_ADD, null, EquiposConfig.this, "configuracion.instalaciones" ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewEquipo( context, Operation.OP_DELETE, (Instalacion)row, EquiposConfig.this, "configuracion.instalaciones" ).showModalWindow();
	}

	@Override
	public Query getQueryObject()
	{
		Instalacion query = new Instalacion();
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
