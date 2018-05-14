package es.pryades.imedig.cloud.modules.Configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.InformesPlantillasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.InformePlantilla;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

@SuppressWarnings("serial")
public class ModalNewInformePlantilla extends ModalWindowsCRUD 
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewInformePlantilla.class );

	protected InformePlantilla newInformePlantilla;

	private TextField editImageNombre;
	private TextArea editText;

	private InformesPlantillasManager plantillasManager;
	
	public ModalNewInformePlantilla( ImedigContext ctx, Operation modalOperation, InformePlantilla orgInformePlantilla, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, orgInformePlantilla, right );

		plantillasManager = (InformesPlantillasManager) IOCManager.getInstanceOf( InformesPlantillasManager.class );
		
		initComponents();
		
		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newInformePlantilla = (InformePlantilla) Utils.clone( orgDto );
		}
		catch ( Throwable e1 )
		{
			newInformePlantilla = new InformePlantilla();
		}

		bi = new BeanItem<ImedigDto>( newInformePlantilla );

		editImageNombre = new TextField( getContext().getString( "modalNewInformePlantilla.lbName" ) );
		editImageNombre.setWidth( "100%" );
		editImageNombre.setPropertyDataSource( bi.getItemProperty( "nombre" ) );
		editImageNombre.setImmediate( true );
		editImageNombre.setNullRepresentation( "" );

		editText = new TextArea( getContext().getString( "modalNewInformePlantilla.lbText" ) );
		editText.setHeight( "600px" );
		editText.setWidth( "100%" );
		editText.setPropertyDataSource( bi.getItemProperty( "datos" ) );
		editText.setImmediate( true );
		editText.setNullRepresentation( "" );

		componentsContainer.addComponent( editImageNombre );
		componentsContainer.addComponent( editText );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewInformePlantilla";
	}

	@Override
	protected void defaultFocus()
	{
		editText.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			plantillasManager.setRow( context, null, newInformePlantilla );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onModify()
	{
		try
		{
			plantillasManager.setRow( context, orgDto, newInformePlantilla );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onDelete()
	{
		try
		{
			plantillasManager.delRow( context, newInformePlantilla );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
}
