package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Instalacion extends ImedigDto
{
	private static final long serialVersionUID = 7328448028613778209L;
	
	private String nombre;
    private String aetitle;
    private String modalidad;
    private Integer tipo;
    private String datos;
}
