package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class TipoHorario extends ImedigDto
{
	private static final long serialVersionUID = -419112283256810423L;
	
	private String nombre;
    private Integer tipo_instalacion;
    private Integer tipo_horario;
    private String datos;
}
