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
public class Documento extends ImedigDto 
{
	public static final int TYPE_REPORT = 1;

	String descripcion;
	String nombre;
	byte[] datos;
	Integer tipo;
	Integer centro;
	Integer imagen;
}
