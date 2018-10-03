package es.pryades.imedig.cloud.core.reports;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;

/**
 * @author dismer.ronda
 *
 */

@EqualsAndHashCode(callSuper=true)
@Data
public class PdfExport extends ExportBase
{
	private static final long serialVersionUID = -1132757592931917018L;

	private static final Logger LOG = Logger.getLogger( PdfExport.class );
	
	private DetalleInforme informe;
	private List<InformeImagen> imagenes;
	private String size;
	private String orientation;
	private Integer template;
	
	public VelocityContext createVelocityContext( ImedigContext context ) throws Throwable
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "pagesize", size ); 
		vcontext.put( "orientation", orientation ); 
		vcontext.put( "title", informe.getId() + " - " + informe.getPaciente_nombre() + " - " + informe.getEstudio_id() );
		vcontext.put( "date_time", Utils.getFormatedDate( informe.getFecha(), "dd/MM/yyyy HH:MM" ) );
		vcontext.put( "reporter", informe.getInformaNombreCompleto() );
		vcontext.put( "referrer", informe.getRefiereNombreCompleto() );
		vcontext.put( "patient_id", informe.getPaciente_id() );
		vcontext.put( "patient_name", informe.getPaciente_nombre() );
		vcontext.put( "study_id", informe.getEstudio_id() );
		vcontext.put( "study_accession", informe.getEstudio_acceso() );
		vcontext.put( "report", informe.getTextoAsXml() ); 
		vcontext.put( "code", informe.getIcd10cm() ); 
		vcontext.put( "center", informe.getCentro_nombre() );
		vcontext.put( "center_logo", informe.getCentroImagenUrl() );
		vcontext.put( "address", informe.getCentro_direccion() );
		vcontext.put( "contacts", informe.getCentro_contactos() );
		vcontext.put( "images", imagenes );
		
		return vcontext;
	}
	
	public boolean doExport( ImedigContext context, OutputStream resp ) throws Throwable
	{
		LOG.info( "report information " + this );
		
		try 
		{
			InputStream in = createStreamTemplate( context, template );
			
			StringWriter stWriter = new StringWriter();
	        Reader templateReader = new BufferedReader( new InputStreamReader( in ) );	
	        
			VelocityContext vcontext = createVelocityContext( context );
			
			Velocity.evaluate( vcontext, stWriter, getClass().getSimpleName(), templateReader );

	        ITextRenderer renderer = new ITextRenderer();
	        
	        renderer.setDocumentFromString( stWriter.toString() );
	        renderer.layout();
	        renderer.createPDF( resp );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
			throw e;
		}
		
		return true;
	}
}
