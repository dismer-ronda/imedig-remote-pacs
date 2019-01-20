package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Paciente extends ImedigDto
{
    private String uid;
    private String email;
    private String nombre;
	private Integer fecha_nacimiento;
	private String sexo;			
}
