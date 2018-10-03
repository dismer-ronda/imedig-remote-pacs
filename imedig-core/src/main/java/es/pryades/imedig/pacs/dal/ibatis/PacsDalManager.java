package es.pryades.imedig.pacs.dal.ibatis;

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

public class PacsDalManager implements Serializable  
{
	private static final long serialVersionUID = 5772027264652829438L;

	private static final Logger LOG = Logger.getLogger( PacsDalManager.class );

    static PacsDalManager instance = null;

    SqlSessionFactory sessionFactory;
	
	String dbDriver;
	String dbUrl;
	String dbUser;
	String dbPassword;
	
	public PacsDalManager( String dbDriver, String dbUrl, String dbUser, String dbPassword ) 
	{
		super();
		
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
						"<mapper resource='es/pryades/imedig/pacs/dal/StudyMapper.xml'/>" +
					"</mappers>"+
				"</configuration>";
				
		StringReader reader = new StringReader( xml.replace('\'', '"') );
		
		sessionFactory = new SqlSessionFactoryBuilder().build( reader );
		
		LOG.info( "data manager initialized" );
	}
	
	public static void Init( String dbDriver, String dbUrl, String dbUser, String dbPassword ) throws ImedigException
	{
		if ( instance == null )
		{
			instance = new PacsDalManager( dbDriver, dbUrl, dbUser, dbPassword );
			
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
	
	public static PacsDalManager getInstance() throws ImedigException
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
