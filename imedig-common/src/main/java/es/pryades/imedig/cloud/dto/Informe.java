package es.pryades.imedig.cloud.dto;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Informe extends ImedigDto 
{
	private static final long serialVersionUID = -3665486029886807515L;
	
	static public final int STATUS_REQUESTED 	= 0;
	static public final int STATUS_INFORMED 	= 1;
	static public final int STATUS_APROVED 		= 2;
	static public final int STATUS_FINISHED 	= 3;
	
    Long fecha;
	String paciente_id;
	String paciente_nombre;
	String estudio_id;
	String estudio_uid;
	String estudio_acceso;
	String modalidad;
	String claves;
	String texto;
	Integer centro;
	Integer informa;
	String icd10cm;
	Integer refiere;
	Integer estado;
	byte[] pdf;
	Integer protegido;
	Integer refpaciente;
	
	public String getTextoAsXml()
	{
		CleanerProperties props = new CleanerProperties();
		 
		props.setOmitHtmlEnvelope( true );
		props.setOmitXmlDeclaration( true );
		props.setOmitDoctypeDeclaration( true );
		props.setAllowMultiWordAttributes( true );
		props.setAllowHtmlInsideAttributes( true );
		
		TagNode node = new HtmlCleaner(props).clean( texto );
		
		return new PrettyXmlSerializer(props).getAsString( node, "UTF-8" );
	}
	
	public boolean solicitado()
	{
		return estado == STATUS_REQUESTED;
	}

	public boolean noaprobado()
	{
		return estado == STATUS_INFORMED;
	}
	
	public boolean aprobado()
	{
		return estado == STATUS_APROVED;
	}

	public boolean terminado()
	{
		return estado == STATUS_FINISHED;
	}
}
