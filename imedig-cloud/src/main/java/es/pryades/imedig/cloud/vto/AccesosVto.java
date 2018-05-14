package es.pryades.imedig.cloud.vto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.vto.controlers.AccesosControlerVto;
import es.pryades.imedig.cloud.vto.refs.UsuarioVtoFieldRef;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AccesosVto extends GenericVto
{

	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//
		
    Integer usuario; 
	String cuando;
	String nombreRefUsuario;	
		
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
			
	public AccesosVto()
	{
		
	}
	
	//###########################################################################################//
	//#											ACCESS METHOD(S)							   	#//
	//###########################################################################################//	

	public GenericControlerVto getControlerVto(ImedigContext ctx)
	{
		UsuarioVtoFieldRef usuarioVtoFieldRef = new UsuarioVtoFieldRef("usuario", Usuario.class, "nombre;ape1;ape2", "nombreRefUsuario");

		return new AccesosControlerVto(usuarioVtoFieldRef,ctx);
	}
	
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
		
}
