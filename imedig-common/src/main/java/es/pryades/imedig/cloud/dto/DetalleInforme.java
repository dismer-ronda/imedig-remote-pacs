package es.pryades.imedig.cloud.dto;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;

import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class DetalleInforme extends Informe
{
	String centro_nombre;
	String centro_direccion;
	String centro_contactos;
	Integer centro_imagen;
	String centro_ip;
	Integer centro_puerto;

	String informa_titulo;
	String informa_nombre;
	String informa_ape1;
	String informa_ape2;

	String refiere_titulo;
	String refiere_nombre;
	String refiere_ape1;
	String refiere_ape2;

	String horario_nombre;
	String moneda_nombre;
	String moneda_codigo;
	String moneda_simbolo;
	
	public String getInformaNombreCompleto()
	{
		if ( informa != null )
			return (informa_titulo.isEmpty() ? "" : informa_titulo + " ") + informa_nombre + " " + informa_ape1 + (informa_ape2.isEmpty() ? "" : " " + informa_ape2);
		
		return "";
	}
	
	public String getRefiereNombreCompleto()
	{
		return (refiere_titulo.isEmpty() ? "" : refiere_titulo + " ") + refiere_nombre + " " + refiere_ape1 + (refiere_ape2.isEmpty() ? "" : " " + refiere_ape2);
	}
	
	public String getInformaNombreAbreviado()
	{
		if ( informa != null )
			return (informa_titulo.isEmpty() ? "" : informa_titulo + " ") + informa_ape1;
		
		return "";
	}
	
	public String getRefiereNombreAbreviado()
	{
		return (refiere_titulo.isEmpty() ? "" : refiere_titulo + " ") + refiere_ape1;
	}
	
	public String getCentroImagenUrl()
	{
		long ts = new Date().getTime();
		
		String extra = "ts=" + ts + "&id=" + centro_imagen;
		String token = "token=" + Utils.getTokenString( centro_imagen + "" + ts, Settings.TrustKey );
		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ) ;
		
		return StringEscapeUtils.escapeXml( "imedig-services/image/" + centro_imagen + "?" + token + "&" + code );
	}
}
