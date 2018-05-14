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
	String horario_nombre;
	String moneda_nombre;
	String moneda_codigo;
	String moneda_simbolo;
}
