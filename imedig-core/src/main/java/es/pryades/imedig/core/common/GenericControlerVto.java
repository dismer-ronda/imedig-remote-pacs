package es.pryades.imedig.core.common;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Getter;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public abstract class GenericControlerVto implements VtoControler, Serializable
{	
	private static final long serialVersionUID = -4772066040180968742L;

	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//
	
	@Getter protected ImedigContext context;
	
	protected HashMap<String,VtoFieldRef> vtoFieldRef= null;
	protected GenericVto vtoObjRef;
	protected Object dtoObjRef;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	
	public GenericControlerVto(ImedigContext ctx)
	{
		this.context = ctx;
		this.vtoFieldRef = new HashMap<String, VtoFieldRef>();
	}

	//###########################################################################################//
	//#											ACCESS METHOD(S)							   	#//
	//###########################################################################################//	

	/**
	 * 
	 * @return
	 */
	public HashMap<String, VtoFieldRef> getVtoFieldRef() 
	{
		return vtoFieldRef;
	}

	/**
	 * 
	 * @param vtoFieldRef
	 */
	public void setVtoFieldRef(HashMap<String, VtoFieldRef> vtoFieldRef) 
	{
		this.vtoFieldRef = vtoFieldRef;
	}

	/**
	 * 
	 * @return
	 */
	public GenericVto getVtoObjRef() 
	{
		return vtoObjRef;
	}

	/**
	 * 
	 * @param vtoObjRef
	 */
	public void setVtoObjRef(GenericVto vtoObjRef) throws Throwable 
	{
		this.vtoObjRef = vtoObjRef;
	}
	
	/**
	 * get
	 * @return
	 */
	public static String[] getVisibleCols()
	{
		return null;
	} 
	
	//###########################################################################################//
	//#											INHERIT METHOD(S)							    #//
	//###########################################################################################//	
	
	@Override
	public abstract HashMap<String, VtoFieldRef> getVtoMapRefList();
	@Override
	public abstract Boolean isCampoRef(String fieldName);
	@Override
	public abstract String[] getStaticVisibleCols();
	@Override
	public abstract String getVtoIdFiledName();
	@Override
	public abstract Object getDtoObject();	
	@Override
	public abstract Class getDtoObjectClass();	
	@Override
	public abstract GenericVto getVtoObject();	
	@Override
	public abstract Class getVtoObjClass();
	
	//###########################################################################################//
	//#										  OTHER METHODS									   	#//
	//###########################################################################################//
	
	public abstract Object generateDtoFromVto(GenericVto vtoObj) throws Throwable;
	public abstract GenericVto generateVtoFromDto(Object dtoObj) throws Throwable;
	
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
}
