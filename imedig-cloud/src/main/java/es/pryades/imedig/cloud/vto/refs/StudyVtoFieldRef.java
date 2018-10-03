package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.VtoFieldRef;
import es.pryades.imedig.pacs.dal.StudiesManager;
import es.pryades.imedig.pacs.dto.Study;
import es.pryades.imedig.pacs.dto.query.StudyQuery;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class StudyVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = -4318052862343770891L;
	
	public StudyVtoFieldRef()
	{
		super();
	}

	public StudyVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new Study();}
	@Override
	public Query getFieldQuery() {return new StudyQuery();}
	@Override
	public Class getFieldManagerImp() {return StudiesManager.class;}
	
}
