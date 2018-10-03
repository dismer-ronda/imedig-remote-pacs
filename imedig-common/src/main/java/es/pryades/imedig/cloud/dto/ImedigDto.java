package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ImedigDto extends Query
{
	private static final long serialVersionUID = 2760159010432046647L;
	
	Integer id;
}
