package es.pryades.imedig.cloud.dto.query;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Informe;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class InformeQuery extends Informe 
{
	private static final long serialVersionUID = -6308105161975673041L;
	
	Long desde;
	Long hasta;
	List<DetalleCentro> centros;
	List<String> list_claves;
	List<Integer> estados;
}
