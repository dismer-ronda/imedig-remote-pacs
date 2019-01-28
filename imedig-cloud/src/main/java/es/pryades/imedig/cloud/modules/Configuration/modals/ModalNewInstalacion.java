package es.pryades.imedig.cloud.modules.Configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public abstract class ModalNewInstalacion extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -3703048869668565205L;

	private static final Logger LOG = Logger.getLogger( ModalNewInstalacion.class );

	protected Instalacion newInstalacion;

	private TextField editNombre;
	private TextField editAEtitle;
	private ComboBox comboBoxModalidad;

	private InstalacionesManager instalacionesManager;

	public ModalNewInstalacion( ImedigContext ctx, Operation modalOperation, Instalacion instalacion, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, instalacion, right );
		
		setWidth( "600px" );
		
		instalacionesManager = (InstalacionesManager) IOCManager.getInstanceOf( InstalacionesManager.class );
		
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
			newInstalacion = (Instalacion) Utils.clone( (Instalacion) orgDto );
		}
		catch ( Throwable e1 )
		{
			newInstalacion = new Instalacion();
		}

		bi = new BeanItem<ImedigDto>( newInstalacion );

		editNombre = new TextField( getContext().getString( "modalNewInst.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );
		
		editAEtitle = new TextField( getContext().getString( "modalNewInst.lbAEtitle" ), bi.getItemProperty( "aetitle" ) );
		editAEtitle.setWidth( "100%" );
		editAEtitle.setNullRepresentation( "" );

		comboBoxModalidad = new ComboBox( getContext().getString( "modalNewInst.lbModalidad" ) );
		comboBoxModalidad.setPropertyDataSource( bi.getItemProperty( "modalidad" ) );
		comboBoxModalidad.setWidth( "100%" );
		comboBoxModalidad.setNewItemsAllowed( false );
		comboBoxModalidad.setNullSelectionAllowed( true );
		fillModalidad( comboBoxModalidad );
		
		FormLayout layout = new FormLayout(editNombre, editAEtitle, comboBoxModalidad);
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );
		
		componentsContainer.addComponent( layout );
		
	}
	
	protected abstract void fillModalidad(ComboBox comboBox);

	@Override
	protected String getWindowResourceKey()
	{
		return getMainResourceKey();
	}

	protected abstract String getMainResourceKey();
	
	@Override
	protected void defaultFocus()
	{
		editNombre.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			newInstalacion.setTipo( getTipo() );
			instalacionesManager.setRow( context, null, newInstalacion );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
	
	protected abstract Integer getTipo();

	protected boolean onModify()
	{
		try
		{
			instalacionesManager.setRow( context, (Instalacion) orgDto, newInstalacion );

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
			instalacionesManager.delRow( context, newInstalacion );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
}
