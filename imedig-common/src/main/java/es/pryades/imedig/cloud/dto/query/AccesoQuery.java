package es.pryades.imedig.cloud.dto.query;

import es.pryades.imedig.cloud.dto.ImedigDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class AccesoQuery extends ImedigDto 
{
	private static final long serialVersionUID = -8996334686000220436L;
	
	Long desde;
	Long hasta;
	Integer usuario;
}
