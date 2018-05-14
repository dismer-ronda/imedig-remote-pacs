package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class CentroVtoFieldRef extends VtoFieldRef 
{
	public CentroVtoFieldRef()
	{
		super();
	}

	public CentroVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new DetalleCentro();};
	@Override
	public Query getFieldQuery() {return new CentroQuery();}
	@Override
	public Class getFieldManagerImp() {return DetallesCentrosManager.class;}
	
}
