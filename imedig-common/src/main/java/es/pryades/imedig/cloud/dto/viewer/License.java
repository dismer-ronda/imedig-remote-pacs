package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;

import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class License implements Serializable 
{
	private static final long serialVersionUID = 6002663981953413598L;
	
	private String Code;

	static public License getLicense( String text ) throws Throwable 
	{
		return (License)Utils.toPojo( text, License.class, true );
	}
}
