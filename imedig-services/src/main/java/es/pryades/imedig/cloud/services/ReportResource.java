package es.pryades.imedig.cloud.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import es.pryades.imedig.cloud.common.ImedigResource;
import es.pryades.imedig.cloud.common.Return;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.core.reports.PdfExport;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class ReportResource extends ImedigResource 
{
    private static final Logger LOG = Logger.getLogger( ReportResource.class );

    String id;

	private DetallesInformesManager informesManager;
	private InformesImagenesManager imagenesManager;

	public ReportResource() 
	{
		super();
    	
    	informesManager = (DetallesInformesManager) IOCManager.getInstanceOf( DetallesInformesManager.class );
		imagenesManager = (InformesImagenesManager) IOCManager.getInstanceOf( InformesImagenesManager.class );
	}

    @Override
    protected void doInit() throws ResourceException 
    {
    	id = (String)getRequest().getAttributes().get( "id" );

        setExisting( id != null );
    }
	
	/**
	 * GET
	 */
	@Get("pdf")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
		
		String token = params.get( "token" );
		String code = Utils.decrypt( params.get( "code" ), Settings.TrustKey );
		
		params.clear();
		
        Utils.getParameters( code, params );
        
        String ts = params.get( "ts" );
        final String orientation = params.get( "orientation" );
        final String size = params.get( "size" );
        final String template = params.get( "template" );
        final String images = params.get( "images" );
        
		Return ret = new Return();
	    
		try
		{
			if ( Utils.isValidRequest( token, id + ts, ts, Settings.TrustKey, 0 ) ) 
			{
				rep = new OutputRepresentation(MediaType.APPLICATION_PDF) 
				{
					@Override
					public void write( OutputStream arg0 ) throws IOException
					{
						try
						{
					    	ImedigContext ctx = new ImedigContext();
					    	
							PdfExport export = new PdfExport();
							
							DetalleInforme informe = (DetalleInforme)informesManager.getRow( ctx, Integer.parseInt( id ) );
							
							if ( informe.getPdf() == null )
							{
								InformeImagen query = new InformeImagen();
								query.setInforme( informe.getId() );

								if ( "true".equals( images ) )
								{
									List<InformeImagen> imagenes = imagenesManager.getRows( ctx, query );
	
									for ( InformeImagen imagen : imagenes )
										imagen.setUrl( Utils.getEnviroment( "CLOUD_URL" ) + imagen.getUrl() );
										//imagen.setUrl( Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-viewer" +  imagen.getUrl() );

									export.setImagenes( imagenes );
								}

								export.setInforme( informe );
								export.setOrientation( orientation );
								export.setSize( size );
								export.setTemplate( Integer.parseInt( template ) );
							
								LOG.info( "generating PDF ..." );
								export.doExport( ctx, arg0 );
							}
							else
							{
								LOG.info( "taking PDF from database ..." );
								arg0.write( informe.getPdf() );
							}
						}
						catch ( Throwable e )
						{
							Utils.logException( e, LOG );
						}
					}
				};				
			}
			else
			{
				ret.setCode( ReturnFactory.STATUS_4XX_FORBIDDEN );
				ret.setDesc( "Access denied" );
	
				rep = new StringRepresentation( ret.getDesc() );
			}
		}
		catch( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
			ret.setDesc( "Resource not found" );

			rep = new StringRepresentation( ret.getDesc() ); 
			
			Utils.logException( e, LOG );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
