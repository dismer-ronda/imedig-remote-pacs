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
public class UsuarioCentro extends ImedigDto
{
	private static final long serialVersionUID = 277702148374771800L;
	
	Integer usuario;
    Integer centro;
}
