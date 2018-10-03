package es.pryades.imedig.cloud.dto.memo;

import java.io.Serializable;

import lombok.Data;

@Data
public class Direccion implements Serializable
{
	private static final long serialVersionUID = 9014948215071470949L;
	
	String tipo;	// Personal, Trabajo
	String calle;
	String localidad;
	String provincia;
	String cp;
	String pais;
}
