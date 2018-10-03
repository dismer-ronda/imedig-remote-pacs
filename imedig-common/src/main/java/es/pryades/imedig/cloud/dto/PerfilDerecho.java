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
public class PerfilDerecho extends ImedigDto
{
	private static final long serialVersionUID = 6012363664363565173L;
	
	Integer perfil;
    Integer derecho;
}
