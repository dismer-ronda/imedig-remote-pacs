package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.PerfilesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Perfil;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author Dismer Ronda
 * 
 */

@SuppressWarnings(
{ "rawtypes" })
public class ModalNewUsuario extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -7353739499761869546L;

	private static final Logger LOG = Logger.getLogger( ModalNewUsuario.class );

	protected Usuario newUsuario;

	private TextField editUserLogin;
	private TextField editUserEmail;
	private PasswordField editUserPwd;
	private TextField editUserTitulo;
	private TextField editUserNombre;
	private TextField editUserApellido1;
	private TextField editUserApellido2;
	private TextArea editUserContactos;
	private TwinColSelect twnColSelUsuariosCentros;
	private ComboBox comboPerfiles;
	private TextField editUserFiltro;
	private ComboBox comboQuery;
	private ComboBox comboCompresion;

	private CentrosManager centrosManager;
	private PerfilesManager perfilesManager;
	private UsuariosManager usuariosManager;
	
	/**
	 * 
	 * @param ctx
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewUsuario( ImedigContext ctx, Operation modalOperation, Usuario orgUsuario, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, orgUsuario, right );
	
		centrosManager = (CentrosManager) IOCManager.getInstanceOf( CentrosManager.class );
		perfilesManager = (PerfilesManager) IOCManager.getInstanceOf( PerfilesManager.class );
		usuariosManager = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );
		
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
			newUsuario = (Usuario) Utils.clone( (Usuario) orgDto );
		}
		catch ( Throwable e1 )
		{
			newUsuario = new Usuario();
		}

		bi = new BeanItem<ImedigDto>( newUsuario );

		editUserLogin = new TextField( getContext().getString( "modalNewUser.lbUserLogin" ), bi.getItemProperty( "login" ) );
		editUserLogin.setWidth( "100%" );
		editUserLogin.setNullRepresentation( "" );

		editUserEmail = new TextField( getContext().getString( "modalNewUser.lbUserEmail" ), bi.getItemProperty( "email" ) );
		editUserEmail.setWidth( "100%" );
		editUserEmail.setNullRepresentation( "" );

		editUserPwd = new PasswordField( getContext().getString( "modalNewUser.lbUserPwd" ), bi.getItemProperty( "pwd" ) );
		editUserPwd.setWidth( "100%" );
		editUserPwd.setNullRepresentation( "" );

		editUserTitulo = new TextField( getContext().getString( "modalNewUser.lbUserTitulo" ), bi.getItemProperty( "titulo" ) );
		editUserTitulo.setWidth( "100%" );
		editUserTitulo.setNullRepresentation( "" );

		editUserNombre = new TextField( getContext().getString( "modalNewUser.lbUserNombre" ), bi.getItemProperty( "nombre" ) );
		editUserNombre.setWidth( "100%" );
		editUserNombre.setNullRepresentation( "" );

		editUserApellido1 = new TextField( getContext().getString( "modalNewUser.lbUserApellido1" ), bi.getItemProperty( "ape1" ) );
		editUserApellido1.setWidth( "100%" );
		editUserApellido1.setNullRepresentation( "" );

		editUserApellido2 = new TextField( getContext().getString( "modalNewUser.lbUserApellido2" ), bi.getItemProperty( "ape2" ) );
		editUserApellido2.setWidth( "100%" );
		editUserApellido2.setNullRepresentation( "" );

		editUserContactos = new TextArea( getContext().getString( "modalNewUser.lbUserContactos" ), bi.getItemProperty( "contactos" ) );
		editUserContactos.setWidth( "100%" );
		editUserContactos.setNullRepresentation( "" );

		editUserFiltro = new TextField( getContext().getString( "modalNewUser.lbUserFiltro" ), bi.getItemProperty( "filtro" ) );
		editUserFiltro.setWidth( "100%" );
		editUserFiltro.setNullRepresentation( "" );

		twnColSelUsuariosCentros = new TwinColSelect();
		twnColSelUsuariosCentros.setRows( 7 );
		twnColSelUsuariosCentros.setNullSelectionAllowed( true );
		twnColSelUsuariosCentros.setMultiSelect( true );
		twnColSelUsuariosCentros.setImmediate( true );
		twnColSelUsuariosCentros.setLeftColumnCaption( getContext().getString( "modalNewUser.twnUsuCentroLeftCaption" ) );
		twnColSelUsuariosCentros.setRightColumnCaption( getContext().getString( "modalNewUser.twnUsuCentroRightCaption" ) );
		twnColSelUsuariosCentros.setWidth( "100%" );
		twnColSelUsuariosCentros.setHeight( "150px" );

		comboPerfiles = new ComboBox(getContext().getString( "modalNewUser.lbUserPerfil" ));
		comboPerfiles.setWidth( "100%" );
		comboPerfiles.setNullSelectionAllowed( false );
		comboPerfiles.setTextInputAllowed( false );
		comboPerfiles.setImmediate( true );
		comboPerfiles.setPropertyDataSource( bi.getItemProperty( "perfil" ) );
		
		fillComboPerfiles();
		
		comboQuery = new ComboBox();
		comboQuery.setWidth( "100%" );
		comboQuery.setNullSelectionAllowed( false );
		comboQuery.setTextInputAllowed( false );
		comboQuery.setImmediate( true );
		comboQuery.setPropertyDataSource( bi.getItemProperty( "query" ) );
		comboQuery.addItem( "0" );
		comboQuery.setItemCaption( "0", getContext().getString( "words.all" ) );
		comboQuery.addItem( "1" );
		comboQuery.setItemCaption( "1", getContext().getString( "words.today" ) );
		comboQuery.addItem( "2" );
		comboQuery.setItemCaption( "2", getContext().getString( "words.yesterday" ) );
		comboQuery.addItem( "3" );
		comboQuery.setItemCaption( "3", getContext().getString( "words.lastweek" ) );
		comboQuery.addItem( "4" );
		comboQuery.setItemCaption( "4", getContext().getString( "words.lastmonth" ) );
		comboQuery.addItem( "5" );
		comboQuery.setItemCaption( "5", getContext().getString( "words.lastyear" ) );
		
		comboCompresion = new ComboBox(getContext().getString( "modalNewUser.lbUserCompresion" ));
		comboCompresion.setWidth( "100%" );
		comboCompresion.setNullSelectionAllowed( false );
		comboCompresion.setTextInputAllowed( false );
		comboCompresion.setImmediate( true );
		comboCompresion.setPropertyDataSource( bi.getItemProperty( "compresion" ) );
		comboCompresion.addItem( "image/png" );
		comboCompresion.setItemCaption( "image/png", getContext().getString( "words.lossless" ) );
		comboCompresion.addItem( "image/jpeg" );
		comboCompresion.setItemCaption( "image/jpeg", getContext().getString( "words.lossy" ) );

		HorizontalLayout rowTwinCentrosUsuarios = new HorizontalLayout();
		rowTwinCentrosUsuarios.setWidth( "100%" );
		rowTwinCentrosUsuarios.setMargin( false );

		HorizontalLayout rowTwinCentrosUsuariosContainer = new HorizontalLayout();
		rowTwinCentrosUsuariosContainer.setWidth( "100%" );
		//rowTwinCentrosUsuariosContainer.setMargin( new MarginInfo( false, true, true, true ) );
		rowTwinCentrosUsuariosContainer.setMargin( false );

		// OJO DISMER 
		Form frmCentrosUsuarios = new Form( rowTwinCentrosUsuariosContainer );
		frmCentrosUsuarios.setCaption( getContext().getString( "modalNewUser.frmCentrosUsuario" ) );
		frmCentrosUsuarios.setWidth( "100%" );

		rowTwinCentrosUsuariosContainer.addComponent( twnColSelUsuariosCentros );

		rowTwinCentrosUsuarios.addComponent( frmCentrosUsuarios );
		rowTwinCentrosUsuarios.setExpandRatio( frmCentrosUsuarios, 1.0f );

		initializeTwinColSelUsuariosCentros();

		FormLayout formComp = new FormLayout( comboCompresion );
		formComp.setMargin( false );
		formComp.setWidth( "100%" );
		FormLayout formFilt = new FormLayout( editUserFiltro );
		formFilt.setMargin( false );
		formFilt.setWidth( "100%" );
		HorizontalLayout row5 = new HorizontalLayout(comboQuery, formComp, formFilt);
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.setMargin( false );
		row5.setCaption( getContext().getString( "modalNewUser.lbUserQuery" ) );
		
		FormLayout left = new FormLayout(editUserLogin, editUserTitulo, editUserApellido1, editUserEmail);
		FormLayout right = new FormLayout(editUserPwd, editUserNombre, editUserApellido2, comboPerfiles);
		left.setWidth( "100%" );
		left.setMargin( false );
		right.setWidth( "100%" );
		right.setMargin( false );
		
		HorizontalLayout top = new HorizontalLayout( left, right );
		top.setWidth( "100%" );
		top.setSpacing( true );

		componentsContainer.addComponent( top );
		FormLayout bottom = new FormLayout( row5, editUserContactos );
		bottom.setMargin( false );
		bottom.setWidth( "100%" );
		componentsContainer.addComponent( bottom );
		componentsContainer.addComponent( rowTwinCentrosUsuarios );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewUser";
	}

	@Override
	protected void defaultFocus()
	{
		editUserNombre.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			List<Integer> listaCentros = new ArrayList<Integer>();

			for ( Object twinSelectedItem : ( (Iterable) twnColSelUsuariosCentros.getValue() ) )
				listaCentros.add( (Integer) twinSelectedItem );

			newUsuario.setId( null );
			newUsuario.setCambio( Utils.getTodayAsInt() );
			newUsuario.setEstado( Usuario.PASS_OK );
			newUsuario.setIntentos( 0 );
			
			usuariosManager.createUsuario( context, newUsuario, listaCentros );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			UsuariosManager usuarioMan = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );

			List<Integer> listaCentros = new ArrayList<Integer>();

			for ( Object twinSelectedItem : ( (Iterable) twnColSelUsuariosCentros.getValue() ) )
			{
				listaCentros.add( (Integer) twinSelectedItem );
			}

			usuarioMan.updateUsuario( context, (Usuario) orgDto, newUsuario, listaCentros );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			UsuariosManager dispoMan = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );

			dispoMan.deleteUsuario( context, newUsuario );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void initializeTwinColSelUsuariosCentros()
	{
		try
		{
			CentroQuery centroQuery = new CentroQuery();
			
			if ( !context.hasRight( "configuracion.todo" ) )
				centroQuery.setUsuario( context.getUsuario().getId() );

			List centros = centrosManager.getRows( context, centroQuery );

			for ( Object centro : centros )
			{
				twnColSelUsuariosCentros.addItem( ( (Centro) centro ).getId() );
				twnColSelUsuariosCentros.setItemCaption( ( (Centro) centro ).getId(), ( (Centro) centro ).getNombre() );
			}

			// para las opciones de añadir y eliminar carga el panel derecho con
			// los centros del usuario
			switch ( operation )
			{
				case OP_DELETE:
				case OP_MODIFY:
					List listaCentrosUsuario = usuariosManager.getCentros( context, newUsuario );

					HashSet<Integer> preselected = new HashSet<Integer>();

					for ( Object centroUsuario : listaCentrosUsuario )
					{
						if ( twnColSelUsuariosCentros.getItem( ( (Centro) centroUsuario ).getId() ) != null )
						{
							preselected.add( ( (Centro) centroUsuario ).getId() );
						}
					}

					twnColSelUsuariosCentros.setValue( preselected );
					break;
				default:
					break;
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof ImedigException ) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
	}
	
	private void fillComboPerfiles()
	{
		SqlSession session = context.getSessionCloud();

		boolean finish = ( session == null );

		try
		{
			if ( finish )
				session = context.openSessionCloud();

			try
			{
				Perfil query = new Perfil();
				
				List<Perfil> perfiles = perfilesManager.getRows( context, query );

				for ( Perfil perfil : perfiles )
				{
					if ( perfil.getId().equals( 1 ) && !context.hasRight( "configuracion.todo" ) )
						continue;

					comboPerfiles.addItem( perfil.getId() );
					comboPerfiles.setItemCaption( perfil.getId(), perfil.getDescripcion() );
				}
			}
			catch ( ImedigException e )
			{
			}
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			if ( !( e instanceof ImedigException ) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					context.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	}
}
