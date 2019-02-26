package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.RecursosManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author hector.licea
 *
 */
@SuppressWarnings("rawtypes")
public class RecursoVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = -3824740673260255817L;

	public RecursoVtoFieldRef()
	{
		super();
	}

	public RecursoVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
	{
		super(fieldRefName, fieldRefNameClass, destinyFieldRefName, destinyFieldRefColName);
	}
	
	@Override
	public String getFieldRefName() {return fieldRefName;}
	@Override
	public void setFieldRefName(String fieldRefName) { this.fieldRefName = fieldRefName; }
	
	@Override
	public Class getFieldRefNameClass() { return this.fieldRefNameClass; }
	@Override
	public void setFieldRefNameClass(Class fieldRefNameClass) { this.fieldRefNameClass = fieldRefNameClass; }
	
	@Override
	public String getDestinyFieldRefName() {return destinyFieldRefName;}
	@Override
	public void setDestinyFieldRefName(String destinyFieldRefName) { this.destinyFieldRefName = destinyFieldRefName; }
	
	@Override
	public String getDestinyFieldRefColName() {return destinyFieldRefColName;}
	@Override
	public void setDestinyFieldRefColName(String destinyFieldRefColName){ this.destinyFieldRefColName = destinyFieldRefColName; }
	
	@Override
	public ImedigDto getFieldDto() { return new Recurso();};
	@Override
	public Query getFieldQuery() {return new Recurso();}
	@Override
	public Class getFieldManagerImp() {return RecursosManager.class;}
	
}
