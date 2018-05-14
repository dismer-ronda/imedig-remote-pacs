package es.pryades.imedig.cloud.dto.viewer;

import lombok.Data;

import es.pryades.imedig.cloud.common.Utils;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

@Data 
public class Token
{
	private String Id;
	private int Type;
	private long Expiration;

	static public Token getToken( String text ) throws Throwable
	{
		return (Token)Utils.toPojo( text, Token.class, true );
	}
}
