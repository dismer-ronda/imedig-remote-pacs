package es.pryades.imedig.cloud.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.Centro;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class CentroQuery extends Centro 
{
	private static final long serialVersionUID = 5077358113525137451L;
	
	Integer usuario;
}
