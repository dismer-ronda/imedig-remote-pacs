package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.TipoHorario;
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
	private ComboBox comboBoxTiempoMin;
	private ComboBox comboBoxModalidad;
	private ComboBox comboBoxHorario;

	private InstalacionesManager instalacionesManager;
	private TipoHorarioManager tipoHorarioManager;
	
	private static final List<Integer> NUMBERS = Arrays.asList( 5, 10, 12, 15, 20, 30, 60 );
	
	public ModalNewInstalacion( ImedigContext ctx, Operation modalOperation, Instalacion instalacion, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, instalacion, right );

		setWidth( "750px" );

		instalacionesManager = (InstalacionesManager)IOCManager.getInstanceOf( InstalacionesManager.class );
		tipoHorarioManager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );

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
			newInstalacion = (Instalacion)Utils.clone( (Instalacion)orgDto );
		}
		catch ( Throwable e1 )
		{
			newInstalacion = new Instalacion();
			newInstalacion.setTiempominimo( 10 );
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
		
		comboBoxHorario = new ComboBox( getContext().getString( "modalNewInst.lbHorario" ) );
		comboBoxHorario.setPropertyDataSource( bi.getItemProperty( "tipo_horario" ) );
		comboBoxHorario.setWidth( "100%" );
		comboBoxHorario.setNewItemsAllowed( false );
		comboBoxHorario.setNullSelectionAllowed( false );
		comboBoxHorario.setRequired( false );;
		fillHorarios( comboBoxHorario );

		StringToIntegerConverter converter = new StringToIntegerConverter();
		
		comboBoxTiempoMin = new ComboBox( getContext().getString( "modalNewInst.lbTiempoMinimo" ) );
		comboBoxTiempoMin.setWidth( "80px" );
		comboBoxTiempoMin.setNullSelectionAllowed( false );
		comboBoxTiempoMin.setNewItemsAllowed( false );
		fillTiemposMinimos(comboBoxTiempoMin);
		comboBoxTiempoMin.setPropertyDataSource( bi.getItemProperty( "tiempominimo" ) );
		
//		InputMask mask = new InputMask( "[0-9]{0,3}" );
//		mask.setRegexMask( true );
//		mask.setPlaceholder( " " );
//		mask.extend( editTiempoMin );
//		editTiempoMin.setValue( "30" );

		FormLayout layout = new FormLayout( editNombre, editAEtitle, comboBoxModalidad, comboBoxHorario, comboBoxTiempoMin );
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );

		componentsContainer.addComponent( layout );
	}

	private void fillTiemposMinimos( ComboBox comboBox )
	{
		for ( Integer n : NUMBERS )
		{
			comboBox.addItem( n );
		}
	}

	private void fillHorarios( ComboBox comboBox )
	{
		TipoHorario query = new TipoHorario();
		query.setTipo_instalacion( getTipo() );
		try
		{
			List<TipoHorario> tipos = tipoHorarioManager.getRows( getContext(), query );
			for ( TipoHorario tipo : tipos )
			{
				comboBox.addItem( tipo.getId() );
				comboBox.setItemCaption( tipo.getId(), tipo.getNombre() );
			}
		}
		catch ( Throwable e )
		{
			LOG.error( "Error", e );
		}
	}

	protected abstract void fillModalidad( ComboBox comboBox );

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
			newInstalacion.setTipo( getTipo() );
			instalacionesManager.setRow( context, (Instalacion)orgDto, newInstalacion );

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
