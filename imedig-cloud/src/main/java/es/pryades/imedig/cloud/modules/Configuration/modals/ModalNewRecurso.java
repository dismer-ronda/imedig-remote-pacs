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
import es.pryades.imedig.cloud.core.dal.RecursosManager;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public abstract class ModalNewRecurso extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -3703048869668565205L;

	private static final Logger LOG = Logger.getLogger( ModalNewRecurso.class );

	protected Recurso newRecurso;

	private TextField editNombre;
	private TextField editAEtitle;
	private ComboBox comboBoxTiempoMin;
	private ComboBox comboBoxModalidad;
	private ComboBox comboBoxHorario;

	private RecursosManager recursosManager;
	private TipoHorarioManager tipoHorarioManager;
	
	private static final List<Integer> NUMBERS = Arrays.asList( 5, 10, 15, 20, 30, 60 );
	
	public ModalNewRecurso( ImedigContext ctx, Operation modalOperation, Recurso recurso, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, recurso, right );

		setWidth( "750px" );

		recursosManager = (RecursosManager)IOCManager.getInstanceOf( RecursosManager.class );
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
			newRecurso = (Recurso)Utils.clone( (Recurso)orgDto );
		}
		catch ( Throwable e1 )
		{
			newRecurso = new Recurso();
			newRecurso.setTiempominimo( 10 );
		}

		bi = new BeanItem<ImedigDto>( newRecurso );

		editNombre = new TextField( getContext().getString( "modalNewRecurso.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );

		editAEtitle = new TextField( getContext().getString( "modalNewRecurso.lbAEtitle" ), bi.getItemProperty( "aetitle" ) );
		editAEtitle.setWidth( "100%" );
		editAEtitle.setNullRepresentation( "" );

		comboBoxModalidad = new ComboBox( getContext().getString( "modalNewRecurso.lbModalidad" ) );
		comboBoxModalidad.setPropertyDataSource( bi.getItemProperty( "modalidad" ) );
		comboBoxModalidad.setWidth( "100%" );
		comboBoxModalidad.setNewItemsAllowed( false );
		comboBoxModalidad.setNullSelectionAllowed( true );
		fillModalidad( comboBoxModalidad );
		
		comboBoxHorario = new ComboBox( getContext().getString( "modalNewRecurso.lbHorario" ) );
		comboBoxHorario.setPropertyDataSource( bi.getItemProperty( "tipo_horario" ) );
		comboBoxHorario.setWidth( "100%" );
		comboBoxHorario.setNewItemsAllowed( false );
		comboBoxHorario.setNullSelectionAllowed( false );
		comboBoxHorario.setRequired( false );;
		fillHorarios( comboBoxHorario );

		StringToIntegerConverter converter = new StringToIntegerConverter();
		
		comboBoxTiempoMin = new ComboBox( getContext().getString( "modalNewRecurso.lbTiempoMinimo" ) );
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
		query.setTipo_recurso( getTipo() );
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
			newRecurso.setTipo( getTipo() );
			recursosManager.setRow( context, null, newRecurso );

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
			newRecurso.setTipo( getTipo() );
			recursosManager.setRow( context, (Recurso)orgDto, newRecurso );

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
			recursosManager.delRow( context, newRecurso );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

}
