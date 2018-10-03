package es.pryades.imedig.cloud.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.InformePlantilla;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class InformePlantillaQuery extends InformePlantilla 
{
	private static final long serialVersionUID = -1619619064635517799L;
	
	Integer usuario;
}
