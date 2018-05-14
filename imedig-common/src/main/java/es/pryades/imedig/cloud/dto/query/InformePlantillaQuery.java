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
	Integer usuario;
}
