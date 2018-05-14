package es.pryades.imedig.cloud.vto.refs;

import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.query.ImagenQuery;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("rawtypes")
public class ImagenVtoFieldRef extends VtoFieldRef 
{
	public ImagenVtoFieldRef()
	{
		super();
	}

	public ImagenVtoFieldRef(String fieldRefName,Class fieldRefNameClass, String destinyFieldRefName,String destinyFieldRefColName)
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
	public ImedigDto getFieldDto() { return new Imagen();};
	@Override
	public Query getFieldQuery() {return new ImagenQuery();}
	@Override
	public Class getFieldManagerImp() {return ImagenesManager.class;}

}
