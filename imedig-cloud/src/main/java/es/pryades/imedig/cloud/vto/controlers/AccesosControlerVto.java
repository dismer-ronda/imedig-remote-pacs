package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.ObjectMetaInfo;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Acceso;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.vto.AccesosVto;
import es.pryades.imedig.cloud.vto.refs.UsuarioVtoFieldRef;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class AccesosControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -2693585449604061093L;
	
	private static final String[] visibleCols = {"id","nombreRefUsuario","cuando"};
	
	public AccesosControlerVto(VtoFieldRef usuarioVtoFieldRef, ImedigContext ctx)
	{
		super(ctx);
		this.vtoFieldRef.put("nombreRefUsuario", usuarioVtoFieldRef);
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
	
	//###########################################################################################//
	//#											INHERIT METHOD(S)							    #//
	//###########################################################################################//	
	
	/**
	 * get
	 * @return
	 */
	public static String[] getVisibleCols()
	{
		return AccesosControlerVto.visibleCols;
	} 
	
	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return AccesosControlerVto.visibleCols;
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
		return Acceso.class;
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
		return AccesosVto.class;
	}
	
	//###########################################################################################//
	//#											OTHER METHOD(S)							   		#//
	//###########################################################################################//	
	
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
		AccesosVto result = null;

		if(dtoObj != null)
		{
			if(dtoObj.getClass().equals(Acceso.class))
			{
				/* ++++  Mapeando de dto.Acceso -> vto.AccesosVto  ++++ */
				
				result = new AccesosVto();
				
				// ++ ID
				Integer vtoId = ((Acceso)dtoObj).getId();
				result.setId(vtoId);
				// -- ID
				
				// ++ USUARIO
				Integer vtoUsuario = ((Acceso) dtoObj).getUsuario();
				result.setUsuario(vtoUsuario);
				// -- USUARI0
				
				// ++ NOMBRE_REF_USUARIO
				String vtoNombreRefUsuario = "";
				
				UsuarioVtoFieldRef usuarioVtoFieldRef = (UsuarioVtoFieldRef) this.vtoFieldRef.get("nombreRefUsuario");
				ObjectMetaInfo refDtoObjUsuarioMetaInfo = new ObjectMetaInfo(dtoObj.getClass());
				refDtoObjUsuarioMetaInfo.addObjectvaluesToMetaInfo(dtoObj);
				
				if(usuarioVtoFieldRef != null)
				{
					ImedigManager queryManUsuario = (ImedigManager) IOCManager.getInstanceOf(usuarioVtoFieldRef.getFieldManagerImp());
					Query objQueryResultUsuario = null; 
					
					if(usuarioVtoFieldRef != null && usuarioVtoFieldRef.getFieldRefName()!= null && refDtoObjUsuarioMetaInfo.getAttributesValues().get(usuarioVtoFieldRef.getFieldRefName())!= null)
					{			 
						objQueryResultUsuario = queryManUsuario.getRow(context, ((Integer) refDtoObjUsuarioMetaInfo.getAttributesValues().get(usuarioVtoFieldRef.getFieldRefName())).intValue());
					}

					if(objQueryResultUsuario != null)
					{
						ObjectMetaInfo objQueryResultMetaInfo = new ObjectMetaInfo(usuarioVtoFieldRef.getFieldRefNameClass());
						objQueryResultMetaInfo.addObjectvaluesToMetaInfo(objQueryResultUsuario);
						
						String destinyFieldList[] = usuarioVtoFieldRef.getDestinyFieldRefName().split(";");
						
						for (String item: destinyFieldList) 
						{
							vtoNombreRefUsuario += (String) objQueryResultMetaInfo.getAttributesValues().get(item) + " "; 
						}
					}
				}

				result.setNombreRefUsuario(vtoNombreRefUsuario);
				// -- NOMBRE_REF_USUARIO
				
				// ++ CUANDO
				String vtoCuando =  Utils.formatFecha(((Acceso) dtoObj).getCuando() != null ? String.valueOf(((Acceso) dtoObj).getCuando()) : "", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");	
				result.setCuando(vtoCuando);
				// -- ORDEN
				
				/* ----  Mapeando de dto.Acceso -> vto.AccesosVto  ---- */
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
