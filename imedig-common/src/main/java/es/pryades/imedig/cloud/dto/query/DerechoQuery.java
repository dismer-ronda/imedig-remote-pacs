package es.pryades.imedig.cloud.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.ImedigDto;
/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data 
public class DerechoQuery extends ImedigDto
{
	private static final long serialVersionUID = -1449932850626811661L;
	
	Integer autorizacion;
    String derecho;
}
