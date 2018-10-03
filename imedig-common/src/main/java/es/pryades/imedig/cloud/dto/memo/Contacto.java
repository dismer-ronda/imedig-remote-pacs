package es.pryades.imedig.cloud.dto.memo;

import java.io.Serializable;

import lombok.Data;

@Data
public class Contacto implements Serializable
{
	private static final long serialVersionUID = 1007061903482521223L;
	
	String tipo; // Teléfono, Móvil, Email
	String contacto;
}
