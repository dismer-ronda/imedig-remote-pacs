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
public class InformePlantilla extends ImedigDto 
{
	private static final long serialVersionUID = 6914821765347696445L;

	private String nombre;
	private String datos;
	private Integer centro;
}
