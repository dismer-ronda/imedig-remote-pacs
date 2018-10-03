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
public class Perfil extends ImedigDto
{
	private static final long serialVersionUID = 8009827146244178086L;
	
	String descripcion;
}
