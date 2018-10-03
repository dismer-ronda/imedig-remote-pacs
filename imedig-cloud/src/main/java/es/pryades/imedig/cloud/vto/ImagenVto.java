package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.ImagenControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;

/**
 * 
 * @author Dismer Ronda Bernardo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ImagenVto extends GenericVto
{
	private static final long serialVersionUID = 6731592344050580428L;
	
	private String nombre;
	
	public ImagenVto()
	{
	}

	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		return new ImagenControlerVto(ctx);
	}
}
