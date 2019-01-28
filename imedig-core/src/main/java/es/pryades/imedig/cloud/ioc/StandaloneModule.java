package es.pryades.imedig.cloud.ioc;

import java.io.Serializable;

import org.apache.tapestry5.ioc.ServiceBinder;

import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.bll.UsuariosManagerImpl;
import es.pryades.imedig.cloud.core.dal.AccesosManager;
import es.pryades.imedig.cloud.core.dal.AccesosManagerImpl;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.CentrosManagerImpl;
import es.pryades.imedig.cloud.core.dal.DerechosManager;
import es.pryades.imedig.cloud.core.dal.DerechosManagerImpl;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManagerImpl;
import es.pryades.imedig.cloud.core.dal.DetallesEstadisticasInformesManager;
import es.pryades.imedig.cloud.core.dal.DetallesEstadisticasInformesManagerImpl;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManagerImpl;
import es.pryades.imedig.cloud.core.dal.DocumentosManager;
import es.pryades.imedig.cloud.core.dal.DocumentosManagerImpl;
import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dal.InstalacionesManagerImpl;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.EstudiosManagerImpl;
import es.pryades.imedig.cloud.core.dal.HorariosManager;
import es.pryades.imedig.cloud.core.dal.HorariosManagerImpl;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dal.ImagenesManagerImpl;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManager;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManagerImpl;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dal.InformesManagerImpl;
import es.pryades.imedig.cloud.core.dal.InformesPlantillasManager;
import es.pryades.imedig.cloud.core.dal.InformesPlantillasManagerImpl;
import es.pryades.imedig.cloud.core.dal.MonedasManager;
import es.pryades.imedig.cloud.core.dal.MonedasManagerImpl;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dal.PacientesManagerImpl;
import es.pryades.imedig.cloud.core.dal.ParametrosManager;
import es.pryades.imedig.cloud.core.dal.ParametrosManagerImpl;
import es.pryades.imedig.cloud.core.dal.PerfilesDerechosManager;
import es.pryades.imedig.cloud.core.dal.PerfilesDerechosManagerImpl;
import es.pryades.imedig.cloud.core.dal.PerfilesManager;
import es.pryades.imedig.cloud.core.dal.PerfilesManagerImpl;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManagerImpl;
import es.pryades.imedig.cloud.core.dal.UsuarioCentrosManager;
import es.pryades.imedig.cloud.core.dal.UsuarioCentrosManagerImpl;
import es.pryades.imedig.pacs.dal.StudiesManager;
import es.pryades.imedig.pacs.dal.StudiesManagerImpl;
import es.pryades.imedig.pacs.dal.StudiesSearchManager;
import es.pryades.imedig.pacs.dal.StudiesSearchManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class StandaloneModule implements Serializable 
{
	private static final long serialVersionUID = -5481071877954040643L;

	/**
	 * Registra los servicios soportados mediante IOC
	 * 
	 * @param binder: Binder de servicios en el que estos se registrarán
	 *         
	 */
	public static void bind( ServiceBinder binder )
	{
		binder.bind( CentrosManager.class, CentrosManagerImpl.class );
		binder.bind( DetallesCentrosManager.class, DetallesCentrosManagerImpl.class );
		binder.bind( ParametrosManager.class, ParametrosManagerImpl.class );
		binder.bind( UsuariosManager.class, UsuariosManagerImpl.class );
		binder.bind( UsuarioCentrosManager.class, UsuarioCentrosManagerImpl.class );
		binder.bind( DerechosManager.class, DerechosManagerImpl.class);
		binder.bind( PerfilesDerechosManager.class, PerfilesDerechosManagerImpl.class);
		binder.bind( DocumentosManager.class, DocumentosManagerImpl.class );
		binder.bind( AccesosManager.class, AccesosManagerImpl.class );
		binder.bind( HorariosManager.class, HorariosManagerImpl.class );
		binder.bind( MonedasManager.class, MonedasManagerImpl.class );
		binder.bind( ImagenesManager.class, ImagenesManagerImpl.class );
		binder.bind( PerfilesManager.class, PerfilesManagerImpl.class );
		binder.bind( InformesManager.class, InformesManagerImpl.class );
		binder.bind( DetallesInformesManager.class, DetallesInformesManagerImpl.class );
		binder.bind( InformesPlantillasManager.class, InformesPlantillasManagerImpl.class );
		binder.bind( InformesImagenesManager.class, InformesImagenesManagerImpl.class );
		binder.bind( DetallesEstadisticasInformesManager.class, DetallesEstadisticasInformesManagerImpl.class );
		binder.bind( InstalacionesManager.class, InstalacionesManagerImpl.class );
		binder.bind( EstudiosManager.class, EstudiosManagerImpl.class );
		binder.bind( PacientesManager.class, PacientesManagerImpl.class );
		binder.bind( TiposEstudiosManager.class, TiposEstudiosManagerImpl.class );

		binder.bind( StudiesManager.class, StudiesManagerImpl.class );
		binder.bind( StudiesSearchManager.class, StudiesSearchManagerImpl.class );
	}
}
