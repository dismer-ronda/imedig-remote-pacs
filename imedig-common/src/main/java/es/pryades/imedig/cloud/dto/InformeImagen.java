package es.pryades.imedig.cloud.dto;

import java.io.UnsupportedEncodingException;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang.StringEscapeUtils;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class InformeImagen extends ImedigDto 
{
	private static final long serialVersionUID = -3242856491950080582L;
	
	private Integer informe;
	private String url;
	private String icon;
	
	public String getEscapedUrl() throws UnsupportedEncodingException
	{
		return StringEscapeUtils.escapeXml( url );
	}
}
