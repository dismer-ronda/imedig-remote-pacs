package es.pryades.imedig.cloud.dto.query;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Usuario;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class UsuarioQuery extends Usuario 
{
	Integer centro;
	List<DetalleCentro> centros;
}
