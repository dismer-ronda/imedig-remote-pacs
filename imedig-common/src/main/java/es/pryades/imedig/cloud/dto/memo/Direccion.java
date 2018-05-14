package es.pryades.imedig.cloud.dto.memo;

import lombok.Data;

@Data
public class Direccion
{
	String tipo;	// Personal, Trabajo
	String calle;
	String localidad;
	String provincia;
	String cp;
	String pais;
}
