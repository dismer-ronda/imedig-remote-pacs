package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
@EqualsAndHashCode(callSuper=true)
public class DetalleEstadisticaInforme extends ImedigDto 
{
	Integer fecha;
	Integer centro;
	String centro_nombre;
	Integer cantidad;
}
