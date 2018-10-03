package es.pryades.imedig.core.common;

import java.io.Serializable;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public abstract class VtoFieldRef implements Serializable
{
	private static final long serialVersionUID = -6604489156474887009L;
	
	protected String fieldRefName = "";
	protected Class  fieldRefNameClass = null;
	protected String destinyFieldRefName = "";
	protected String destinyFieldRefColName = "";
	
	public VtoFieldRef()
	{
	}
	
	/**
	 * 
	 * @param fieldRefName				--> Nombre de la propiedad del Dto a referenciar
	 * @param fieldRefNameClass			--> Clase del Dto referenciado
	 * @param destinyFieldRefName   	--> Nombre de la propiedad del Dto referenciada
	 * @param destinyFieldRefColName	--> Nombre de la propiedad dentro del Vto Referenciada
	 */
	public VtoFieldRef(String fieldRefName, Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
	{
		this.fieldRefName = fieldRefName;
		this.fieldRefNameClass= fieldRefNameClass; 
		this.destinyFieldRefName = destinyFieldRefName;
		this.destinyFieldRefColName = destinyFieldRefColName;
	}

	public abstract String getFieldRefName();
	public abstract Class getFieldRefNameClass();
	public abstract String getDestinyFieldRefName();
	public abstract String getDestinyFieldRefColName();
	
	public abstract void setFieldRefName(String fieldRefName);
	public abstract void setFieldRefNameClass(Class fieldRefNameClass);
	public abstract void setDestinyFieldRefName(String destinyFieldRefName) ;
	public abstract void setDestinyFieldRefColName(String destinyFieldRefColName);
	
	public Query getFieldQuery() { return null; };
	public ImedigDto getFieldDto() { return null; };
	public Class getFieldManagerImp() { return null; };
}