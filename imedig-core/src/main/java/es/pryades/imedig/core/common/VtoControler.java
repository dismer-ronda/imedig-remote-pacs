package es.pryades.imedig.core.common;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.ImedigException;

/**
 * Interfaz con los metodos para mapear de un objeto Dto a Vto y viceversa
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public interface VtoControler 
{	
	public String getVtoIdFiledName();
	
	public String[] getStaticVisibleCols();
	
	public HashMap<String, VtoFieldRef> getVtoMapRefList();
	public Boolean isCampoRef(String fieldName);

	public Object getDtoObject();
	public Class getDtoObjectClass();
	public Object getVtoObject();
	public Class getVtoObjClass();

	public Object generateDtoFromVto(GenericVto vtoObj) throws Throwable;
	public GenericVto generateVtoFromDto(Object dtoObj) throws Throwable;
}
