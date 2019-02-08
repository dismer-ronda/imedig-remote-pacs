package es.pryades.imedig.cloud.dto;

import org.apache.commons.lang3.StringUtils;

import es.pryades.imedig.cloud.common.AppUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class Paciente extends ImedigDto
{
 	private static final long serialVersionUID = 3025909671511166747L;

 	private String uid;
    private String email;
    private String nombre;
    private String apellido1;
    private String apellido2;
	private Integer fecha_nacimiento;
	private String sexo;			
	private String telefono;			
	private String movil;		
	
	public String getNombreCompleto() {
		return AppUtils.getNombreNAA( this );
	}
	
	public String getNombreCompletoConIdentificador() {
		if (StringUtils.isNotBlank(uid)) {
			return uid +" - "+ getNombreCompleto();
		}
		return getNombreCompleto();
	}
}
