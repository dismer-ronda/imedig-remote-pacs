package es.pryades.imedig.cloud.modules.Configuration.modals;

import org.apache.log4j.Logger;
import org.vaadin.risto.stepper.IntStepper;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public final class ModalNewTipoEstudio extends ModalWindowsCRUD
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5384562124198472933L;

	private static final Logger LOG = Logger.getLogger( ModalNewTipoEstudio.class );

	protected TipoEstudio newTipoEstudio;

	private TextField editNombre;
	private IntStepper stepperDuracion;

	private TiposEstudiosManager tiposEstudiosManager;

	public ModalNewTipoEstudio( ImedigContext ctx, Operation modalOperation, TipoEstudio tipoEstudio, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, tipoEstudio, right );
		
		setWidth( "600px" );
		
		tiposEstudiosManager = (TiposEstudiosManager) IOCManager.getInstanceOf( TiposEstudiosManager.class );
		
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
			newTipoEstudio = (TipoEstudio) Utils.clone( (TipoEstudio) orgDto );
		}
		catch ( Throwable e1 )
		{
			newTipoEstudio = new TipoEstudio();
			newTipoEstudio.setDuracion( 15 );
		}

		bi = new BeanItem<ImedigDto>( newTipoEstudio );

		editNombre = new TextField( getContext().getString( "modalNewStudyType.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );

		stepperDuracion = new IntStepper( getContext().getString( "modalNewStudyType.lbDuracion" ));
		stepperDuracion.setPropertyDataSource( bi.getItemProperty( "duracion" ) );
		stepperDuracion.setWidth( "60px" );
		
		FormLayout layout = new FormLayout(editNombre, stepperDuracion);
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );
		
		componentsContainer.addComponent( layout );
		
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewStudyType";
	}

	@Override
	protected void defaultFocus()
	{
		editNombre.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			tiposEstudiosManager.setRow( context, null, newTipoEstudio );

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
			tiposEstudiosManager.setRow( context, (TipoEstudio) orgDto, newTipoEstudio );

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
			tiposEstudiosManager.delRow( context, newTipoEstudio );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
}
