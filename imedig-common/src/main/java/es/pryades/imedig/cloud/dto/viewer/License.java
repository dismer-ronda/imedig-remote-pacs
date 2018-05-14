package es.pryades.imedig.cloud.dto.viewer;

import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class License 
{
	private String Code;

	static public License getLicense( String text ) throws Throwable 
	{
		return (License)Utils.toPojo( text, License.class, true );
	}
}
