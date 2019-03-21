package es.pryades.imedig.cloud.modules.Configuration.tabs;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_CENTROS_MOD;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_CONFIG_TODO;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewCentro;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.CentroVto;
import es.pryades.imedig.cloud.vto.controlers.CentroControlerVto;
import es.pryades.imedig.cloud.vto.refs.CentroVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class CentrosConfig extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = -2753769923301427451L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( CentrosConfig.class );

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public CentrosConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( CentroVto.class, new CentroVto(), new CentroVtoFieldRef(), new QueryFilterRef( new CentroQuery() ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return CentroControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "centrosConfig.tableCentrosConfig";
	}

	@Override
	public String getTabTitle()
	{
		return getContext().getString( "centrosConfig.tabName" );
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledModify( row != null );
	}

	public void onModifyRow( Object row )
	{
		new ModalNewCentro( context, Operation.OP_MODIFY, (DetalleCentro)row, CentrosConfig.this, DERECHO_CONFIG_CENTROS_MOD ).showModalWindow();
	}

	@Override
	public Query getQueryObject()
	{
		CentroQuery query = new CentroQuery();
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
	}

	@Override
	public void onDeleteRow( Object row )
	{
	}

	@Override
	public boolean isModifyAvailable()
	{
		return getContext().hasRight( DERECHO_CONFIG_CENTROS_MOD );
	}
}
