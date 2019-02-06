package es.pryades.imedig.cloud.dto.query;

import java.util.List;

import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Usuario;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class UsuarioQuery extends Usuario 
{
	private static final long serialVersionUID = 6152958189551941988L;
	
	Integer centro;
	List<DetalleCentro> centros;
	List<Integer> perfiles;
}
