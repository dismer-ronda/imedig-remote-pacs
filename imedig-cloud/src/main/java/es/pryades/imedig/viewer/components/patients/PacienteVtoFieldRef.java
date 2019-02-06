package es.pryades.imedig.viewer.components.patients;

import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.PacienteQuery;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author hector.licea
 * 
 */
@SuppressWarnings("rawtypes")
public class PacienteVtoFieldRef extends VtoFieldRef 
{
	private static final long serialVersionUID = -8715775551331850648L;
	
	public PacienteVtoFieldRef()
	{
		super();
	}

	public PacienteVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new Paciente();}
	@Override
	public Query getFieldQuery() {return new PacienteQuery();}
	@Override
	public Class getFieldManagerImp() {return PacientesManager.class;}
	
}
