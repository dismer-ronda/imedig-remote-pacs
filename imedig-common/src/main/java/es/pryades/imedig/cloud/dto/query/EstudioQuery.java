package es.pryades.imedig.cloud.dto.query;

import es.pryades.imedig.cloud.dto.Estudio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudioQuery extends Estudio
{
	private static final long serialVersionUID = -1862899224852314602L;
	
	private Long fecha_desde;
	private Long fecha_hasta;
}
