package es.pryades.imedig.cloud.core.dal.ibatis;

import java.io.Serializable;
import java.io.StringReader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class ClientDalManager implements Serializable 
{
	private static final long serialVersionUID = -4683969245615613143L;

	private static final Logger LOG = Logger.getLogger( ClientDalManager.class );

    static ClientDalManager instance = null;

    SqlSessionFactory sessionFactory;
	
    String engine;
	
	String dbDriver;
	String dbUrl;
	String dbUser;
	String dbPassword;
	
	public ClientDalManager( String engine, String dbDriver, String dbUrl, String dbUser, String dbPassword ) 
	{
		super();
		
		this.engine = engine;

		this.dbDriver = dbDriver;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}
	
	public void Init()
	{
		String xml = 
			"<?xml version='1.0' encoding='UTF-8'?>" + 
				"<!DOCTYPE configuration PUBLIC '-//mybatis.org//DTD Config 3.0//EN ' 'http://mybatis.org/dtd/mybatis-3-config.dtd'>" +
				"<configuration>" +
					"<environments default='desarrollo'>" +
						"<environment id='desarrollo'>" +
							"<transactionManager type='JDBC' />" + 
							"<dataSource type='POOLED'>" +
								"<property name='driver' value='" + dbDriver + "' />" +
								"<property name='url' value='" + dbUrl + "'/>" +
								"<property name='username' value='" + dbUser + "' />" +
								"<property name='password' value='" + dbPassword + "' />" + 
							"</dataSource>" +
						"</environment>" +
					"</environments>" +
					"<mappers>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/CentroMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/ParametroMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/DerechoMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/PerfilMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/PerfilDerechoMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/DetalleCentroMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/UsuarioMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/UsuarioCentrosMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/DocumentoMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/AccesoMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/HorarioMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/MonedaMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/ImagenMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/InformeMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/DetalleInformeMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/InformePlantillaMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/InformeImagenMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/DetalleEstadisticaInformeMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/InstalacionMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/PacienteMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/CitaMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/TipoEstudioMapper.xml'/>" +
						"<mapper resource='es/pryades/imedig/cloud/core/dal/" + engine + "/TipoHorarioMapper.xml'/>" +
					"</mappers>"+
				"</configuration>";
				
		StringReader reader = new StringReader( xml.replace('\'', '"') );
		
		sessionFactory = new SqlSessionFactoryBuilder().build( reader );
		
		LOG.info( "data manager initialized" );
	}
	
	public static void Init( String engine, String dbDriver, String dbUrl, String dbUser, String dbPassword ) throws ImedigException
	{
		if ( instance == null )
		{
			instance = new ClientDalManager( engine, dbDriver, dbUrl, dbUser, dbPassword );
			
			try 
			{
				instance.Init();
			} 
			catch ( Throwable e ) 
			{
				throw new ImedigException( e, LOG, 0 );
			}
		}
	}
	
	public static ClientDalManager getInstance() throws ImedigException
	{
		if ( instance == null )
			throw new ImedigException( new Exception( "database not initialized" ), LOG, ImedigException.INSTANCE_NOT_INITIALIZED );

		return instance;
	}

	public static SqlSession openSession() throws ImedigException
	{
		for ( int i = 0; i < 3; i++ )
		{
			try 
			{
				return instance.sessionFactory.openSession();	
			}
			catch ( Throwable e )
			{
				LOG.error( e.getMessage() + ". Retrying " + (3 - i - 1) + " more times" );
				
				if ( i == 2 )
					throw new ImedigException( e, LOG, 0 );	
			}
		}
		
		// This code should not be reached
		return null;
	}
}
