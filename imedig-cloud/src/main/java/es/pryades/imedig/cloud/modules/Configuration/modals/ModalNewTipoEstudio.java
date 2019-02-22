package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.OptionGroup;
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
	private ComboBox comboBoxDuracion;
	private OptionGroup groupTipo;
	
	private static final List<Integer> NUMBERS = Arrays.asList( 10, 12, 15, 20, 30, 60 );

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
			newTipoEstudio.setDuracion( 10 );
		}

		bi = new BeanItem<ImedigDto>( newTipoEstudio );

		editNombre = new TextField( getContext().getString( "modalNewStudyType.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );
		editNombre.setRequired( true );
		
		groupTipo = new OptionGroup( getContext().getString( "modalNewStudyType.lbTipo" ) );
		groupTipo.addItem( 1 );
		groupTipo.setItemCaption( 1, getContext().getString( "facility.type.1" ) );
		groupTipo.setPropertyDataSource( bi.getItemProperty( "tipo" ) );
		groupTipo.setRequired( true );
		groupTipo.setValue( 1 );

		comboBoxDuracion = new ComboBox( getContext().getString( "modalNewStudyType.lbDuracion" ) );
		comboBoxDuracion.setWidth( "80px" );
		comboBoxDuracion.setNullSelectionAllowed( false );
		comboBoxDuracion.setNewItemsAllowed( false );
		comboBoxDuracion.setRequired( true );
		fillDuracion(comboBoxDuracion);
		comboBoxDuracion.setPropertyDataSource( bi.getItemProperty( "duracion" ) );
		
		FormLayout layout = new FormLayout(editNombre, groupTipo, comboBoxDuracion);
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );
		
		componentsContainer.addComponent( layout );
		
	}

	private void fillDuracion( ComboBox comboBox )
	{
		for ( Integer n : NUMBERS )
		{
			comboBox.addItem( n );
		}
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
