package es.pryades.imedig.cloud.core.bll;

import java.util.List;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.UsuarioCentro;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.core.common.ImedigManager;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */
@SuppressWarnings("rawtypes")
public interface UsuariosManager extends ImedigManager
{
	public void setPassword( ImedigContext ctx, Usuario usuario, String subject, String text, boolean mail ) throws Throwable;

	public void sendNewPassword( ImedigContext ctx, String email, String subject, String text, boolean mail ) throws Throwable;

	public void validateLocalUser( ImedigContext ctx, String login, String password, String subject, String text, boolean mail ) throws Throwable;
	public void validateLdapUser( ImedigContext ctx, String login, String password, String subject, String text, boolean mail ) throws Throwable;
	
	public void validateUser( ImedigContext ctx, String login, String password, String subject, String text, boolean mail ) throws Throwable;

	public Usuario createUsuario( ImedigContext ctx, Usuario usuario, List centros ) throws Throwable;

	public void deleteUsuario( ImedigContext ctx, Usuario usuario ) throws Throwable;

	public void updateUsuario( ImedigContext ctx, Usuario usuario, Usuario newUsuario, List centros ) throws Throwable;

	public int getNumberOfCentros( ImedigContext ctx, Usuario usuario ) throws Throwable;

	public List getCentros( ImedigContext ctx, Usuario usuario ) throws Throwable;

	public void addCentro( ImedigContext ctx, UsuarioCentro usuario_centro ) throws Throwable;

	public void delCentro( ImedigContext ctx, UsuarioCentro usuario_centro ) throws Throwable;

	public void updateListaCentros( ImedigContext ctx, Usuario usuario, List<Integer> newlistaCentros ) throws Throwable;

	public void remoteLogin(ImedigContext ctx, String login, String password) throws Throwable;
	
	List getPageLazy(ImedigContext ctx, UsuarioQuery query) throws Throwable;
}
