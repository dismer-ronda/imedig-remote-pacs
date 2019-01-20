package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class TipoEstudio extends ImedigDto
{
	private String nombre;
    private Integer duracion;
}
