package es.pryades.imedig.cloud.modules.Configuration.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.ImagenQuery;
import es.pryades.imedig.cloud.modules.Configuration.modals.ModalNewImage;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.ImagenVto;
import es.pryades.imedig.cloud.vto.controlers.ImagenControlerVto;
import es.pryades.imedig.cloud.vto.refs.ImagenVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ImagenesConfig extends FilteredContent implements ModalParent
{
	private static final long serialVersionUID = -6672140430163376710L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ImagenesConfig.class );

	public ImagenesConfig( ImedigContext ctx )
	{
		super( ctx );
	}

	public String[] getVisibleCols()
	{
		return ImagenControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "imagenesConfig.tableImagenesConfig";
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( ImagenVto.class, new ImagenVto(), new ImagenVtoFieldRef(), new QueryFilterRef( new ImagenQuery() ), this.context, Constants.DEFAULT_PAGE_SIZE );
	}
	
	@Override
	public String getTabTitle()
	{
		return getContext().getString( "imagenesConfig.tabName" );
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
		ImagenQuery query = new ImagenQuery();
		
		if ( !context.hasRight( "configuracion.todo" ) )
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
		new ModalNewImage( context, Operation.OP_ADD, null, ImagenesConfig.this, "configuracion.imagenes.adicionar" ).showModalWindow();
	}

	public void onModifyRow( Object row )
	{
		new ModalNewImage( context, Operation.OP_MODIFY, (Imagen)row, ImagenesConfig.this, "configuracion.imagenes.modificar" ).showModalWindow();
	}

	@Override
	public void onDeleteRow( Object row )
	{
		new ModalNewImage( context, Operation.OP_DELETE, (Imagen)row, ImagenesConfig.this, "configuracion.imagenes.borrar" ).showModalWindow();
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
