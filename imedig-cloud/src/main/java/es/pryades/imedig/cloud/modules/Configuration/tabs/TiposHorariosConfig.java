package es.pryades.imedig.cloud.modules.Configuration.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewTipoHorario;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.TipoHorarioVto;
import es.pryades.imedig.cloud.vto.controlers.TipoHorarioControlerVto;
import es.pryades.imedig.cloud.vto.refs.TipoHorarioVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author hector.licea
 * 
 */
public class TiposHorariosConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = 6696081394473978754L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( TiposHorariosConfig.class );

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public TiposHorariosConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( TipoHorarioVto.class, new TipoHorarioVto(), new TipoHorarioVtoFieldRef(), new QueryFilterRef( new TipoHorario() ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return TipoHorarioControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "tipoHorarioConfig.table";
	}

	@Override
	public String getTabTitle()
	{
		return getContext().getString( "tipoHorarioConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null );
		setEnabledDelete( row != null );
	}

	public void onModifyRow( Object row )
	{
		new ModalNewTipoHorario( context, Operation.OP_MODIFY, (TipoHorario)row, TiposHorariosConfig.this, "configuracion.tipos.horarios.modificar" ).showModalWindow();
	}

	@Override
	public void onAddRow()
	{
		new ModalNewTipoHorario( context, Operation.OP_ADD, null, TiposHorariosConfig.this, "configuracion.tipos.horarios.adicionar" ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewTipoHorario( context, Operation.OP_DELETE, (TipoHorario)row, TiposHorariosConfig.this, "configuracion.tipos.horarios.borrar" ).showModalWindow();
	}

	@Override
	public Query getQueryObject()
	{
		return new TipoHorario();
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
