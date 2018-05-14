package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.CentroControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;


/**
 * 
 * @author Dismer Ronda
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class CentroVto extends GenericVto
{
	private String  nombre;
	private Integer orden;
	private String  descripcion;
	private String  direccion;
	private String  contactos;
	private String  coordenadas;
	
	public CentroVto()
	{
	}
	
	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		return new CentroControlerVto(ctx);
	}
}
