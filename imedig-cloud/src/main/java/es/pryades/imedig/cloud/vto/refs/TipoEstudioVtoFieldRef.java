package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author hector.licea
 *
 */
@SuppressWarnings("rawtypes")
public class TipoEstudioVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = 7538429627443326174L;
	
	public TipoEstudioVtoFieldRef()
	{
		super();
	}

	public TipoEstudioVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new TipoEstudio();};
	@Override
	public Query getFieldQuery() {return new TipoEstudio();}
	@Override
	public Class getFieldManagerImp() {return TiposEstudiosManager.class;}
	
}
