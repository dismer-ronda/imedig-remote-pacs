package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.InformesPlantillasManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.InformePlantilla;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.InformePlantillaQuery;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class InformePlantillaVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = -4722202618405220333L;
	
	public InformePlantillaVtoFieldRef()
	{
		super();
	}

	public InformePlantillaVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new InformePlantilla();}
	@Override
	public Query getFieldQuery() {return new InformePlantillaQuery();}
	@Override
	public Class getFieldManagerImp() {return InformesPlantillasManager.class;}

}
