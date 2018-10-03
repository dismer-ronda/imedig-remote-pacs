package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.UsuarioControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;

/**
 * 
 * @author Dismer Ronda Bernardo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UsuarioVto extends GenericVto
{
	private static final long serialVersionUID = 8982638321607979010L;
	
	private String nombre;
	private String ape1;
	private String ape2;
	private String contactos; 
	private Integer autorizacion;
	
	public UsuarioVto()
	{
	}

	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		return new UsuarioControlerVto(ctx);
	}
}
