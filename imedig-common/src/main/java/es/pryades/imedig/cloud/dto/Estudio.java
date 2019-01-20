package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Estudio extends ImedigDto
{

	private Long fecha;
	private String uid;
	private Integer paciente;
    private Integer equipo;
    private Integer tipo;
    private Integer referidor;
    private Integer duracion;		
}
