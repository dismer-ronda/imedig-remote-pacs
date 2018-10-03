package es.pryades.imedig.cloud.common;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
@Data 
public class Return implements Serializable 
{
	private static final long serialVersionUID = 6888024927362522271L;
	
	private int Code;
    private String Desc;
	
	public Return()
	{
		Code = 200;
		Desc = "";
	}
	
	static public Return getReturn( String text ) throws Throwable
	{
		return (Return) Utils.toPojo( text, Return.class, true );
	}
}
