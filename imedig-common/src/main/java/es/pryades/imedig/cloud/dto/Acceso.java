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
public class Acceso extends ImedigDto
{
	private static final long serialVersionUID = 3376951520064497465L;
	
	Integer usuario; 
	Long cuando;
}
