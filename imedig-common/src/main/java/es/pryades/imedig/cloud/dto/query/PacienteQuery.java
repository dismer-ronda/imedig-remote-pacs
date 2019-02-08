package es.pryades.imedig.cloud.dto.query;

import es.pryades.imedig.cloud.dto.Paciente;
import lombok.Data;

@Data
public class PacienteQuery extends Paciente
{
	private static final long serialVersionUID = 2491414147375695598L;
	
	private String filtro;

}
