package es.pryades.imedig.cloud.core.dal;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.WebServiceRequest;
import es.pryades.imedig.cloud.core.dal.ibatis.InformeMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.ImedigManagerImpl;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InformesManagerImpl extends ImedigManagerImpl implements InformesManager
{
    private static final Logger LOG = Logger.getLogger( InformesManagerImpl.class );

    @Inject
    private InformesImagenesManager imagenesManager;
    
    @Inject
	private DetallesCentrosManager centrosManager;
    
	public static ImedigManager build()
	{
		return new InformesManagerImpl();
	}

	public InformesManagerImpl()
	{
		super( InformeMapper.class, Informe.class, LOG );
	}
	
	@Override
	public boolean hasBlob() 
	{
		return true;
	}

	public ReportInfo getReportInfo( ImedigContext ctx, DetalleCentro centro )
	{
		SqlSession session = null;
		
		try
		{
			session = ctx.openSessionCloud();
			
	    	DetalleCentro temp = (DetalleCentro)centrosManager.getRow( ctx, centro.getId() );
	    	
    		long ts = new Date().getTime();
    		
    		String extra = "ts=" + ts;
    		String token = "token=" + Utils.getTokenString( "IMEDIG" + ts, Settings.TrustKey );
    		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ); 
    		
			//String url =  Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer/services/report/" + ctx.getUsuario().getLogin() + "?" + token + "&" + code;
    		String url =  Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-cloud/services/report/" + ctx.getUsuario().getLogin() + "?" + token + "&" + code;

    		LOG.info( "url " + url  );
			
			WebServiceRequest req = new WebServiceRequest( url, 200, "GET", "", "", new HashMap<String, String>() );

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			try
			{
				boolean ret = req.Execute( null, os, null );
				
				LOG.info( "response " + os.toString() );

				if ( ret )
					return (ReportInfo) Utils.toPojo( os.toString(), ReportInfo.class, true );
				
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		finally
		{
			if ( session != null )
			{
				try
				{
					ctx.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}

		return null;
	}
	
	public void addReport( ImedigContext ctx, Informe informe, List<InformeImagen> imagenes ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSessionCloud();

		try 
		{
			setRow( ctx, null, informe );
			
			for ( InformeImagen imagen : imagenes )
			{
				imagen.setId( null );
				imagen.setInforme( informe.getId() );

				imagenesManager.setRow( ctx, null, imagen );
			}
				
			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			Utils.logException( e, getLogger() );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void modifyReport( ImedigContext ctx, Informe oldInforme, Informe newInforme, List<InformeImagen> imagenes ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSessionCloud();

		try 
		{
			setRow( ctx, oldInforme, newInforme );
			
			InformeImagen query = new InformeImagen();
			query.setInforme( newInforme.getId() );
			
			imagenesManager.delRow( ctx, query );
			
			for ( InformeImagen imagen : imagenes )
			{
				imagen.setId( null );
				imagen.setInforme( newInforme.getId() );

				imagenesManager.setRow( ctx, null, imagen );
			}
				
			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			Utils.logException( e, getLogger() );
			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSessionCloud();
		}
	}

	public void deleteReport( ImedigContext ctx, Informe informe ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSessionCloud();

		try 
		{
			InformeImagen query = new InformeImagen();
			query.setInforme( informe.getId() );
			
			imagenesManager.delRow( ctx, query );
			
			delRow( ctx, informe );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			Utils.logException( e, getLogger() );
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
