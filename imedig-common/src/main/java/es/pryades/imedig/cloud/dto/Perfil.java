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
public class Perfil extends ImedigDto
{
	private static final long serialVersionUID = 8009827146244178086L;
	
	public static final int PROFILE_ADMIN = 1;
	public static final int PROFILE_DOCTOR = 2;
	public static final int PROFILE_RADIOLOGIST = 3;
	public static final int PROFILE_CENTER_ADMIN = 4;
	public static final int PROFILE_STUDENT = 5;
	public static final int PROFILE_ADMINISTRATIVE = 6;
	
	String descripcion;
}
