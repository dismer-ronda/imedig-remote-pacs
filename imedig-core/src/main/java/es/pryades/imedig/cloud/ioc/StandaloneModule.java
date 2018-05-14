package es.pryades.imedig.cloud.ioc;

import org.apache.tapestry5.ioc.ServiceBinder;

import es.pryades.imedig.cloud.core.bll.*;
import es.pryades.imedig.cloud.core.dal.*;
import es.pryades.imedig.pacs.dal.StudiesManager;
import es.pryades.imedig.pacs.dal.StudiesManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class StandaloneModule 
{
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

		binder.bind( StudiesManager.class, StudiesManagerImpl.class );
	}
}
