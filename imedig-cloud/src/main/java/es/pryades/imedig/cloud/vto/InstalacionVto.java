package es.pryades.imedig.cloud.vto;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.InstalacionControlerVto;
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
public class InstalacionVto extends GenericVto
{
	private static final long serialVersionUID = -2070157367731279575L;

	private String  nombre;
	private String  aetitle;
	private String  modalidad;
	private Integer tipo;
	
	public InstalacionVto()
	{
	}
	
	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		return new InstalacionControlerVto(ctx);
	}
}
