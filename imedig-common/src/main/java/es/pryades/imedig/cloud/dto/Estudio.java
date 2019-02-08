package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Estudio extends ImedigDto
{
	private static final long serialVersionUID = -8328086467606070724L;

	private Long fecha;
	private Long fechafin;
	private String uid;
	private Integer paciente;
    private Integer instalacion;
    private Integer tipo;
    private Integer referidor;
}
