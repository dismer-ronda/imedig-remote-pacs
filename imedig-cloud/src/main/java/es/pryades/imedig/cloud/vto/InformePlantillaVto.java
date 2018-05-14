package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.InformePlantillaControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;

/**
 * 
 * @author Dismer Ronda 
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InformePlantillaVto extends GenericVto
{
	private String datos;
	private String nombre;

	public InformePlantillaVto()
	{
	}

	public GenericControlerVto getControlerVto( ImedigContext ctx )
	{
		return new InformePlantillaControlerVto( ctx );
	}
}
