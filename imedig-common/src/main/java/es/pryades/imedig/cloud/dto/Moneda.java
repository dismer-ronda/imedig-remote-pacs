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
public class Moneda extends ImedigDto
{
	private static final long serialVersionUID = -250950910417065536L;
	
	String nombre; 
    String codigo;
    String simbolo;
}
