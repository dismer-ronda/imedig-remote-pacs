package es.pryades.imedig.core.common;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class GenericVto implements Serializable  
{
	private static final long serialVersionUID = -1906698955582393058L;
	
	protected Integer id;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	
	public GenericVto()
	{
		
	}
	
	//###########################################################################################//
	//#											ACCESS METHOD(S)							   	#//
	//###########################################################################################//	
	
	public abstract GenericControlerVto getControlerVto(ImedigContext ctx);
	
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------

}
