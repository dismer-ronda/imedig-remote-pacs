package es.pryades.imedig.cloud.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.dto.Imagen;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ImagenQuery extends Imagen 
{
	private static final long serialVersionUID = 1768458130421025748L;
	
	Integer usuario;
}
