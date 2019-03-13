package es.pryades.imedig.cloud.dto.query;

import java.util.List;

import es.pryades.imedig.cloud.dto.Cita;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitaQuery extends Cita
{
	private static final long serialVersionUID = -1862899224852314602L;
	
	private Long fecha_desde;
	private Long fecha_hasta;
	private List<Integer> estados;
}
