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
public class Imagen extends ImedigDto 
{
	private static final long serialVersionUID = 8729717003091335266L;
	
	byte[] datos;
	Integer centro;
	String nombre;
}
