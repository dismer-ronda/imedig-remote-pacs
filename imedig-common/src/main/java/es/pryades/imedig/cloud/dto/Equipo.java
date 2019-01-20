package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Equipo extends ImedigDto
{
	private String nombre;
    private String aetitle;
    private String modalidad;	
}
