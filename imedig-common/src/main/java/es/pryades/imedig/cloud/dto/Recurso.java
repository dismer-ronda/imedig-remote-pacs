package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Recurso extends ImedigDto
{
	private static final long serialVersionUID = 7328448028613778209L;
	
	private String nombre;
    private String aetitle;
    private String modalidad;
    private Integer tipo;
    private Integer tiempominimo;
    private Integer tipo_horario;
}
