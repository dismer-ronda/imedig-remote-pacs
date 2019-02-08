package es.pryades.imedig.cloud.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class TipoEstudio extends ImedigDto
{
	private static final long serialVersionUID = -9100957956151592784L;
	
	private String nombre;
    private Integer duracion;
    private Integer tipo;
    
    @Override
    public String toString(){
    	return nombre;
    }
}
