package es.pryades.imedig.cloud.dto.memo;

import java.io.Serializable;

import lombok.Data;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
public class ResourceResponse implements Serializable
{
	private static final long serialVersionUID = -6606210302225551613L;
	
	Object representation; 
	Integer code;	
}
