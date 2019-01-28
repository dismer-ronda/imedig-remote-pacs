package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author hector.licea
 *
 */
@SuppressWarnings("rawtypes")
public class InstalacionVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = -3824740673260255817L;

	public InstalacionVtoFieldRef()
	{
		super();
	}

	public InstalacionVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new Instalacion();};
	@Override
	public Query getFieldQuery() {return new Instalacion();}
	@Override
	public Class getFieldManagerImp() {return InstalacionesManager.class;}
	
}
