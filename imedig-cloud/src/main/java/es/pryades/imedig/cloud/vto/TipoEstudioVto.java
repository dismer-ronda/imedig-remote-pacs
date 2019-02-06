package es.pryades.imedig.cloud.vto;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.TipoEstudioControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 
 * @author hector.licea
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class TipoEstudioVto extends GenericVto
{
	private static final long serialVersionUID = 1284069443520322772L;
	
	private String  nombre;
	private Integer duracion;
	private Integer tipo;
	
	public TipoEstudioVto()
	{
	}
	
	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		return new TipoEstudioControlerVto(ctx);
	}
}
