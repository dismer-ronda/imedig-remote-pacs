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
public class Derecho extends ImedigDto
{
	private static final long serialVersionUID = -1290947278785330962L;
	
	String codigo;
	String descripcion;
}
