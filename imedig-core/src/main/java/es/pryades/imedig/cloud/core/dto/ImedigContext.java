package es.pryades.imedig.cloud.core.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.Action;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dal.ibatis.ClientDalManager;
import es.pryades.imedig.cloud.dto.Derecho;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Parametros;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.pacs.dal.ibatis.PacsDalManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Dismer Ronda
 *
 */
@Data
@SuppressWarnings({"rawtypes"})
public class ImedigContext implements Serializable  
{
	private static final long serialVersionUID = 56996704384120017L;

	static final Logger LOG = Logger.getLogger( ImedigContext.class );
	
	String instanceName;

	SqlSession sessionCloud;
	SqlSession sessionPacs;
	
	ResourceBundle resources;	

	Usuario usuario;
	
	List<DetalleCentro> centros;
	List derechos;
	
	Parametros parametros;
	
	HashMap<String,Object> extra;
	
	String orientation;
	String pagesize;
	Integer template;
	Boolean images;
	
	/*@Getter @Setter
	String cloudUrl;*/
	
	private Set<ListenerAction> listeners;
	
	public ImedigContext()
	{
		instanceName = null;
		sessionCloud = null;

		sessionPacs = null;
		
		extra = new HashMap<String,Object>();
		
		orientation = "portrait";
		pagesize = "A4";
		template = 0;
		images = Boolean.TRUE;
		
		listeners = new HashSet<>();
	}
	
	protected void finalize() throws Throwable 
	{
		closeSessionCloud();
		closeSessionPacs();
		listeners.clear();
	}

	public SqlSession openSessionCloud() throws ImedigException
	{
		if ( getSessionCloud() != null )
			throw new ImedigException( new Exception( "Cloud SQL session MUST be closed previously" ), LOG, ImedigException.CONNECTION );
			
		if ( getSessionCloud() == null )
		{
			setSessionCloud( ClientDalManager.openSession() );
		}
		
		return getSessionCloud();
	}
	
	public void closeSessionCloud() throws ImedigException
	{
		if ( getSessionCloud() != null )
		{
			sessionCloud.close();
			
			setSessionCloud( null );
		}
	}

	public SqlSession openSessionPacs() throws ImedigException
	{
		if ( getSessionPacs() != null )
			throw new ImedigException( new Exception( "PACS SQL session MUST be closed previously" ), LOG, ImedigException.CONNECTION );
			
		if ( getSessionPacs() == null )
			setSessionPacs( PacsDalManager.openSession() );
		
		return getSessionPacs();
	}
	
	public void closeSessionPacs() throws ImedigException
	{
		if ( getSessionPacs() != null )
		{
			sessionPacs.close();
			
			setSessionPacs( null );
		}
	}

	/**
	 * @return True si la contraseña del usuario correspondiente al contexto ha expirado 
	 * @throws ImedigException
	 */
	public boolean isAccountExpired() throws ImedigException
    {
		return Utils.isExpired( getUsuario().getCambio(), Settings.PWD_expiration );
    }
	
	public void addData( String key, Object value )
	{
		extra.put( key, value );
	}
	
	public Object getData( String key )
	{
		return extra.get( key );
	}
	
	public void removeData( String key )
	{
		extra.remove( key );
	}
	
	public void clearData()
	{
		extra.clear();
	}
	
	public boolean hasRight( String derecho )
	{
		for ( Object obj : derechos )
			if ( derecho.equals( ((Derecho)obj).getCodigo() ) )
				return true;

		return false;
	}
	
	
	public boolean hasProfile( Integer profile )	{
		if (usuario == null) return false;
		
		return profile.equals( usuario.getPerfil() ); 
	}	

	public String getString( String key )
	{
		try 
		{
			return resources.getString( key );
		}
		catch ( Throwable e )
		{
			return key;
		}
	}	
	
	public void sendAction(Action action){
		if (action == null) return;
		
		synchronized (listeners) {
			for ( ListenerAction listener : listeners )
				listener.doAction( action );
		}
	}
	
	
	public void addListener(ListenerAction listener){
		
		if (listener == null) return;
		
		synchronized (listeners) {
			listeners.add( listener );
		}
		
	}

	public void removeListener(ListenerAction listener){
		if (listener == null) return;
		
		synchronized (listeners) {
			listeners.remove( listener );
		}
		
	}
	
	public void removeAllListener( ){
		listeners.clear();
	}
	
	

}
