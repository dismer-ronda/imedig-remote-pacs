package es.pryades.imedig.viewer.components.appointments;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.LocalTime;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.KeyValue;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import lombok.Data;

@Data
public class AppointmentVo extends ImedigDto implements Serializable
{	
	private static final long serialVersionUID = -8776864293789319567L;
	
	private Paciente paciente;
	private Usuario referidor;
	private TipoEstudio tipo;
	private Date fecha;
	private KeyValue<LocalTime, Integer> fechainicio;
	private Integer duracion;
	private Integer estado;
}
