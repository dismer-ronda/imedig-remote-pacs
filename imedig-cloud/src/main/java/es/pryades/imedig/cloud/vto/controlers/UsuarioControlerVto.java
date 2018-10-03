package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.vto.UsuarioVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class UsuarioControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -8704612978161902686L;
	
	private static final String[] visibleCols = {"id","nombre", "ape1", "ape2"};
	
	public UsuarioControlerVto(ImedigContext ctx)
	{
		super(ctx);
	}

	/**
	 * 
	 * @param vtoObjRef
	 */
	public void setVtoObjRef(GenericVto vtoObjRef) throws Throwable 
	{
		this.dtoObjRef = this.generateDtoFromVto(vtoObjRef);
		super.setVtoObjRef(vtoObjRef);
	}
	
	/**
	 * get
	 * @return
	 */
	public static String[] getVisibleCols()
	{
		return UsuarioControlerVto.visibleCols;
	} 
	
	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return UsuarioControlerVto.visibleCols;
	}
	
	/**
	 * 
	 * @return HashMap
	 */
	@Override
	public HashMap<String, VtoFieldRef> getVtoMapRefList()
	{
		return this.vtoFieldRef;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Boolean isCampoRef(String fieldName)
	{
		return(this.vtoFieldRef.get(fieldName) != null);
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public String getVtoIdFiledName()
	{
		return "id";
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Object getDtoObject()
	{
		return this.dtoObjRef;
	}
	
	/**
	 * 
	 * @return 
	 */
	@Override
	public Class getDtoObjectClass()	
	{
		return Usuario.class;
	}
	
	/**
	 * 
	 * @return 
	 */
	@Override
	public GenericVto getVtoObject()
	{
		return this.vtoObjRef;
	}
	
	/**
	 * 
	 * @return 
	 */
	@Override
	public Class getVtoObjClass()
	{
		return UsuarioVto.class;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object generateDtoFromVto(GenericVto vtoObj) throws Throwable
	{
		Object result = null;

		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public GenericVto generateVtoFromDto(Object dtoObj) throws Throwable
	{
		UsuarioVto result = null;

		if(dtoObj != null)
		{
			if(dtoObj.getClass().equals(Usuario.class))
			{
				/* ++++  Mapeando de dto.Usuario -> vto.UsuarioVto  ++++ */
				
				result = new UsuarioVto();
				
				// ++ ID
				Integer vtoId = ((Usuario) dtoObj).getId();
				result.setId(vtoId);
				// -- ID
				
				// ++ NOMBRE
				String vtoNombre = ((Usuario) dtoObj).getNombre();
				result.setNombre(vtoNombre);
				// -- NOMBRE

				// ++ APE1
				String vtoApe1 = ((Usuario) dtoObj).getApe1();
				result.setApe1(vtoApe1);
				// -- APE1

				// ++ APE2
				String vtoApe2 = ((Usuario) dtoObj).getApe2();
				result.setApe2(vtoApe2);
				// -- APE2
				
				// ++ CONTACTOS
				String vtoContactos = ((Usuario) dtoObj).getContactos();
				result.setContactos(vtoContactos);
				// -- CONTACTOS
				
				/* ----  Mapeando de dto.Usuario -> vto.UsuarioVto  ---- */
			}
			else
			{
				// throw ImedigException();
			}
			
		}


		return result;
	}
	
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
}
