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
public class Parametro extends ImedigDto
{
	Integer centro;
	String codigo;
	String valor;
	String descripcion;
}
