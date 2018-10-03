package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class DetalleCentro extends Centro 
{
	private static final long serialVersionUID = 5462232357269493432L;
	
	String horario_nombre;
	String moneda_nombre;
	String moneda_codigo;
	String moneda_simbolo;
}
