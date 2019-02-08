package es.pryades.imedig.viewer.components.citations;

import java.io.Serializable;
import java.util.Date;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import lombok.Data;

@Data
public class CitationVo extends ImedigDto implements Serializable
{	
	private static final long serialVersionUID = -8776864293789319567L;
	
	private Paciente paciente;
	private Usuario referidor;
	private TipoEstudio tipo;
	private Date fecha;
	private Date fechainicio;
	private Date fechafin;
}
