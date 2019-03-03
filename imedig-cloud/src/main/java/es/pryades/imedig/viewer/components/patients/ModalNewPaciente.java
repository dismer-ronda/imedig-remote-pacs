package es.pryades.imedig.viewer.components.patients;

import java.util.Date;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.components.converters.DateToIntegerConverter;

/**
 * 
 * @author hector.licea
 * 
 */
public class ModalNewPaciente extends ModalWindowsCRUD
{
	private static final long serialVersionUID = 87756269648630442L;

	private static final Logger LOG = Logger.getLogger( ModalNewPaciente.class );

	protected Paciente newPaciente;

	private TextField editNombre;
	private TextField editApellido1;
	private TextField editApellido2;
	private OptionGroup groupSexo;
	private DateField dateFechaNacimiento;
	private TextField editIdentificador;
	
	private TextField editEMail;
	private TextField editTelefono;
	private TextField editMovil;

	private PacientesManager pacientesManager;

	public ModalNewPaciente( ImedigContext ctx, Operation modalOperation, Paciente oldPaciente, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, oldPaciente, right );
		
		pacientesManager = (PacientesManager) IOCManager.getInstanceOf( PacientesManager.class );
		
		initComponents();
		
		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
		
		setWidth( "850px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newPaciente = (Paciente) Utils.clone( (Paciente) orgDto );
		}
		catch ( Throwable e1 )
		{
			newPaciente = new Paciente();
		}

		bi = new BeanItem<ImedigDto>( newPaciente );

		editNombre = new TextField( getContext().getString( "modalNewPaciente.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setNullRepresentation( "" );
		editNombre.setWidth( "100%" );
		editNombre.setMaxLength( 50 );
		editNombre.setRequired( true );

		editApellido1 = new TextField( getContext().getString( "modalNewPaciente.lbApellido1" ), bi.getItemProperty( "apellido1" ) );
		editApellido1.setNullRepresentation( "" );
		editApellido1.setWidth( "100%" );
		editApellido1.setMaxLength( 50 );
		editApellido1.setRequired( true );

		editApellido2 = new TextField( getContext().getString( "modalNewPaciente.lbApellido2" ), bi.getItemProperty( "apellido2" ) );
		editApellido2.setNullRepresentation( "" );
		editApellido2.setWidth( "100%" );
		editApellido2.setMaxLength( 50 );
		
		editIdentificador = new TextField( getContext().getString( "modalNewPaciente.lbIdentificador" ), bi.getItemProperty( "uid" ) );
		editIdentificador.setNullRepresentation( "" );
		editIdentificador.setMaxLength( 64 );
		editIdentificador.setRequired( true );
		
		editEMail = new TextField( getContext().getString( "modalNewPaciente.lbEmail" ), bi.getItemProperty( "email" ) );
		editEMail.setNullRepresentation( "" );
		editEMail.setMaxLength( 128 );

		editTelefono = new TextField( getContext().getString( "modalNewPaciente.lbTelefono" ), bi.getItemProperty( "telefono" ) );
		editTelefono.setNullRepresentation( "" );
		editTelefono.setMaxLength( 20 );

		editMovil = new TextField( getContext().getString( "modalNewPaciente.lbCelular" ), bi.getItemProperty( "movil" ) );
		editMovil.setNullRepresentation( "" );
		editMovil.setMaxLength( 20 );

		dateFechaNacimiento = new DateField( getContext().getString( "modalNewPaciente.lbFNacimiento" ));
		dateFechaNacimiento.setConverter( new DateToIntegerConverter() );
		dateFechaNacimiento.setPropertyDataSource(bi.getItemProperty( "fecha_nacimiento" ));
		dateFechaNacimiento.setDateFormat( "dd/MM/yyyy" );
		dateFechaNacimiento.setResolution( Resolution.DAY );
		dateFechaNacimiento.setRequired( true );
		//dateFechaNacimiento.setRangeStart( Utils.getDateFromInt( 19730101 ) );
		//dateFechaNacimiento.setRangeEnd( new Date() );
		dateFechaNacimiento.addValidator( new IntegerRangeValidator( getContext().getString( "modalNewPaciente.error.fnacimiento" ), null, Utils.getDateAsInt( new Date() ) ) );
		
		groupSexo = new OptionGroup( getContext().getString( "modalNewPaciente.lbSexo" ) );
		groupSexo.setRequired( true );
		
		groupSexo.addItem( "M" );
		groupSexo.setItemCaption( "M", getContext().getString( "words.sex.male" ) );
		groupSexo.addItem( "F" );
		groupSexo.setItemCaption( "F", getContext().getString( "words.sex.female" ) );
		groupSexo.addItem( "O" );
		groupSexo.setItemCaption( "O", getContext().getString( "words.sex.other" ) );
		
		groupSexo.setPropertyDataSource( bi.getItemProperty( "sexo" ) );
		groupSexo.addStyleName( ValoTheme.OPTIONGROUP_HORIZONTAL );
		
		HorizontalLayout layout = new HorizontalLayout( editNombre, editApellido1, editApellido2, dateFechaNacimiento );
		layout.setSpacing( true );
		layout.setWidth( "100%" );
		componentsContainer.addComponent( layout );
		layout = new HorizontalLayout( groupSexo, editIdentificador);
		layout.setSpacing( true );
		componentsContainer.addComponent( layout );
		
		Panel panel = new Panel(getContext().getString( "modalNewPaciente.Datos.Contacto" ));
		panel.addStyleName("light");
		panel.setWidth( "100%" );
		
		layout = new HorizontalLayout(editEMail, editTelefono, editMovil);
		layout.setWidth( "100%" );
		layout.setSpacing( true );
		layout.setMargin( true );
		panel.setContent( layout );
		componentsContainer.addComponent( panel );
		
		int index = 1;
		editNombre.setTabIndex( index++ );
		editApellido1.setTabIndex( index++ );
		editApellido2.setTabIndex( index++ );
		dateFechaNacimiento.setTabIndex( index++ );
		groupSexo.setTabIndex( index++ );
		editIdentificador.setTabIndex( index++ );
		editEMail.setTabIndex( index++ );
		editTelefono.setTabIndex( index++ );
		editMovil.setTabIndex( index++ );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewPaciente";
	}

	@Override
	protected void defaultFocus()
	{
		editNombre.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newPaciente.setId( null );
			
			pacientesManager.setRow( context, null, newPaciente );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ) );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			pacientesManager.setRow( context, (Paciente) orgDto, newPaciente );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ) );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			pacientesManager.delRow( context, newPaciente );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ) );
		}

		return false;
	}
}
