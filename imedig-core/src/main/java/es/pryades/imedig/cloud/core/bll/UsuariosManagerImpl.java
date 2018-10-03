package es.pryades.imedig.cloud.core.bll;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.UsuarioCentrosManager;
import es.pryades.imedig.cloud.core.dal.ibatis.UsuarioMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.UsuarioCentro;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */
@SuppressWarnings("rawtypes")
public class UsuariosManagerImpl extends ImedigManagerImpl implements UsuariosManager
{
	private static final long serialVersionUID = -2930737246566970181L;

	private static final Logger LOG = Logger.getLogger( UsuariosManagerImpl.class );

	@Inject
	UsuarioCentrosManager ucManager;

	public static ImedigManager build()
	{
		return new UsuariosManagerImpl();
	}

	public UsuariosManagerImpl()
	{
		super( UsuarioMapper.class, Usuario.class, LOG );
	}

	@SuppressWarnings("unchecked")
	public Usuario createUsuario( ImedigContext ctx, Usuario usuario, List centros ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			usuario.setPwd( Utils.MD5( usuario.getPwd() ) );

			setRow( ctx, null, usuario );

			updateListaCentros( ctx, usuario, centros );

			if ( finish )
				session.commit();

			return usuario;
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			if ( e instanceof Throwable )
				throw (Throwable)e;

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public int getNumberOfCentros( ImedigContext ctx, Usuario usuario ) throws Throwable
	{
		return ucManager.getNumberOfRows( ctx, usuario );
	}

	public List getCentros( ImedigContext ctx, Usuario usuario ) throws Throwable
	{
		return ucManager.getRows( ctx, usuario );
	}

	public void addCentro( ImedigContext ctx, UsuarioCentro usuario_centro ) throws Throwable
	{
		ucManager.setRow( ctx, null, usuario_centro );
	}

	public void delCentro( ImedigContext ctx, UsuarioCentro usuario_centro ) throws Throwable
	{
		ucManager.delRow( ctx, usuario_centro );
	}

	/**
	 * @author Dismer Ronda
	 * @since 1.1.2
	 * @param ctx
	 * @param usuario
	 * @param newlistaCentros
	 * @throws Throwable
	 */
	public void updateListaCentros( ImedigContext ctx, Usuario usuario, List<Integer> newlistaCentros ) throws Throwable
	{
		List listaCentros;
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			listaCentros = this.getCentros( ctx, usuario );

			// se itera sobre la lista de centros del usuario (tabla
			// susc_usuarios_centros)
			for ( Object centroItem : listaCentros )
			{
				// se verifica si el elemento i-esimo pertenece a la nueva lista
				if ( newlistaCentros != null && newlistaCentros.contains( ((Centro)centroItem).getId() ) )
				{
					// de ser el caso se elimina de la nueva lista
					newlistaCentros.remove( ((Centro)centroItem).getId() );
				}
				else
				{
					// de no perternecer se elimina de la base de datos (tabla
					// susc_usuarios_centros)
					UsuarioCentro usuarioCentroItemTmp = new UsuarioCentro();
					usuarioCentroItemTmp.setCentro( ((Centro)centroItem).getId() );
					usuarioCentroItemTmp.setUsuario( usuario.getId() );

					this.delCentro( ctx, usuarioCentroItemTmp );
				}
			}

			// cuando se termiana de comprobar cada uno de los centros del
			// usuario (ciclo anterior)
			// los elementos que no se hayan borrado se mantendran y ademas si
			// queda algun elemento en la
			// nueva lista de centro se insertara

			// se itera sobre la nueva lista para saber si queda algun elemento
			if ( newlistaCentros != null )
			{
				for ( Integer centroItem : newlistaCentros )
				{
					UsuarioCentro usuarioCentroItem = new UsuarioCentro();
					usuarioCentroItem.setCentro( centroItem );
					usuarioCentroItem.setUsuario( usuario.getId() );

					// se inserta el nuevo centro en la tabla
					// (susc_usuarios_centros)
					this.addCentro( ctx, usuarioCentroItem );
				}
			}

			if ( finish )
				session.commit();

		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}

	}

	/**
	 * @author Dismer Ronda
	 * @since 1.1.2
	 * @param ctx
	 * @param usuario
	 * @param newlistaCentros
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public void updateUsuario( ImedigContext ctx, Usuario usuario, Usuario newUsuario, List centros ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{

			if ( usuario != null && newUsuario != null )
			{
				this.updateListaCentros( ctx, usuario, centros );

				if ( !newUsuario.getPwd().equals( usuario.getPwd() ) )
					newUsuario.setPwd( Utils.MD5( newUsuario.getPwd() ) );

				setRow( ctx, usuario, newUsuario );
			}

			if ( finish )
				session.commit();

		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	/**
	 * @author Dismer Ronda
	 * @since 1.1.2
	 * @param ctx
	 * @param usuario
	 * @throws Throwable
	 */
	@Override
	public void deleteUsuario( ImedigContext ctx, Usuario usuario ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			// Se eliminan todas las filas de la relacion del usuario con los
			// centros
			this.updateListaCentros( ctx, usuario, null );

			// Se elimina el usuario
			delRow( ctx, usuario );

			if ( finish )
				session.commit();

		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	private void loadUsuario( ImedigContext ctx, String login ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			UsuarioQuery query = new UsuarioQuery();

			query.setLogin( login );

			List rows = getRows( ctx, query );

			if ( rows.size() != 1 )
				throw new Exception( "Null return" );

			Usuario autorizacion = (Usuario)rows.get( 0 );

			if ( autorizacion.getEstado() == Usuario.PASS_BLOCKED )
				throw new Exception( "Account " + login + " blocked" );

			ctx.setUsuario( autorizacion );
		}
		catch ( Throwable e )
		{
			if ( e instanceof Throwable )
				throw (Throwable)e;

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void setIntentos( ImedigContext ctx, Usuario autorizacion ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			UsuarioMapper mapper = session.getMapper( UsuarioMapper.class );

			mapper.setIntentos( autorizacion );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void validateUser( ImedigContext ctx, String login, String password, String subject, String text, boolean mail ) throws Throwable
	{
		boolean rollback = true;

		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			loadUsuario( ctx, login );

			Usuario usuario = ctx.getUsuario();

			if ( Utils.MD5( password ).equalsIgnoreCase( usuario.getPwd() ) )
			{
				usuario.setIntentos( 0 );

				setIntentos( ctx, usuario );

				if ( finish )
					session.commit();

				LOG.info( "user " + usuario.getEmail() + " logged" );

				return;
			}

			if ( usuario.getIntentos() == Settings.PWD_fails_change )
			{
				usuario.setIntentos( usuario.getIntentos() + 1 );
				usuario.setEstado( Usuario.PASS_CHANGED );
				usuario.setPwd( (String)Utils.getRandomPassword( Settings.PWD_min_size ) );

				setPassword( ctx, usuario, subject, text, mail );

				if ( finish )
					session.commit();
				
				rollback = false;

				throw new Exception( "Password for " + login + " changed" );
			}
			else if ( usuario.getIntentos() == Settings.PWD_fails_block )
			{
				usuario.setEstado( Usuario.PASS_BLOCKED );

				setEstado( ctx, usuario );

				if ( finish )
					session.commit();
				rollback = false;

				throw new Exception( "Account " + login + " blocked" );
			}
			else
			{
				usuario.setIntentos( usuario.getIntentos() + 1 );

				setIntentos( ctx, usuario );

				if ( finish )
					session.commit();
			}

			rollback = false;

			if ( usuario.getEstado() == Usuario.PASS_CHANGED )
				throw new Exception( "Password for " + login + " changed" );

			throw new Exception( "Login as " + login + " failed " );
		}
		catch ( Throwable e )
		{
			if ( finish && rollback )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void setPassword( ImedigContext ctx, Usuario usuario, String subject, String text, boolean mail ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			UsuarioMapper mapper = session.getMapper( UsuarioMapper.class );

			text = Utils.replaceWildcards( text, usuario );

			usuario.setPwd( Utils.MD5( usuario.getPwd() ) );
			usuario.setCambio( Utils.getTodayAsInt() );

			mapper.setPassword( usuario );

			if ( mail )
				Utils.sendMail( Settings.MAIL_from, usuario.getEmail(), subject, Settings.MAIL_host, Settings.MAIL_user, Settings.MAIL_password, text, null );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void setEstado( ImedigContext ctx, Usuario usuario ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			UsuarioMapper mapper = session.getMapper( UsuarioMapper.class );

			mapper.setEstado( usuario );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void sendNewPassword( ImedigContext ctx, String email, String subject, String text, boolean mail ) throws Throwable
	{
		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			loadUsuario( ctx, email );

			Usuario usuario = ctx.getUsuario();

			String password = (String)Utils.getRandomPassword( Settings.PWD_min_size );

			usuario.setPwd( password );
			usuario.setEstado( Usuario.PASS_FORGET );
			usuario.setIntentos( 0 );

			setPassword( ctx, usuario, subject, text, mail );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void remoteLogin(ImedigContext ctx, String login, String password) throws Throwable
	{
		boolean rollback = true;

		SqlSession session = ctx.getSessionCloud();

		boolean finish = session == null;

		if ( finish )
			session = ctx.openSessionCloud();

		try
		{
			loadUsuario( ctx, login );

			Usuario usuario = ctx.getUsuario();

			if ( Utils.MD5( password ).equalsIgnoreCase( usuario.getPwd() ) )
			{
				usuario.setIntentos( 0 );

				setIntentos( ctx, usuario );

				if ( finish )
					session.commit();

				LOG.info( "user " + usuario.getEmail() + " logged" );

				return;
			}

			if ( usuario.getIntentos() == Settings.PWD_fails_block )
			{
				usuario.setEstado( Usuario.PASS_BLOCKED );

				setEstado( ctx, usuario );

				if ( finish )
					session.commit();
				rollback = false;

				throw new Exception( "Account " + login + " blocked" );
			}
			else
			{
				usuario.setIntentos( usuario.getIntentos() + 1 );

				setIntentos( ctx, usuario );

				if ( finish )
					session.commit();
			}

			rollback = false;

			if ( usuario.getEstado() == Usuario.PASS_CHANGED )
				throw new Exception( "Password for " + login + " changed" );

			throw new Exception( "Login as " + login + " failed " );
		}
		catch ( Throwable e )
		{
			if ( finish && rollback )
				session.rollback();

			Utils.logException( e, LOG );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	@Override
	public SqlSession getDatabaseSession( ImedigContext ctx )
	{
		return ctx.getSessionCloud();
	}

	@Override
	public SqlSession openDatabaseSession( ImedigContext ctx ) throws Throwable
	{
		return ctx.openSessionCloud();
	}

	@Override
	public void closeDatabaseSession( ImedigContext ctx ) throws Throwable
	{
		ctx.closeSessionCloud();
	}
}
