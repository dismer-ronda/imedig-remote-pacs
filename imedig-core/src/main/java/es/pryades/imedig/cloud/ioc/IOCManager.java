package es.pryades.imedig.cloud.ioc;

import java.io.IOException;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@SuppressWarnings( {"unchecked","rawtypes"}) 
public class IOCManager  
{
	static IOCManager instance = null;
	
	private RegistryBuilder builder;
	private Registry registry;
	
	public IOCManager() 
	{
		super();
		
		builder = new RegistryBuilder();
		builder.add( StandaloneModule.class );

		registry = builder.build();
		registry.performRegistryStartup();
	}

	/**
	 * Obtiene la instancia del manager de IOC
	 * 
	 * @return <code>IOCManager</code> Devuelve la instancia global del manager de IOC
	 *         
	 */
	public static IOCManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Inicializa el manager de IOC 
	 * 
	 * @return <code>IOCManager</code> Devuelve una instancia del manager global de IOC
	 *         
	 */
	public static void Init() throws IOException
	{
		if ( instance == null )
			instance = new IOCManager();
	}
	
	/**
	 * Obtiene una instancia de un servicio
	 * 
	 * @param clazz: Interfaz del servicio solicitado.
	 * @return <code>Object</code> Devuelve una instancia de la interfaz del servicio solicitado
	 *         
	 */
	public static Object getInstanceOf( Class clazz )
	{
		return getInstance().registry.getService( clazz );
	}
}
