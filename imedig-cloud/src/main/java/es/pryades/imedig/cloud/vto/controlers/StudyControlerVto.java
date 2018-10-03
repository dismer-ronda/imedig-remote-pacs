package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.StudyVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import es.pryades.imedig.core.common.VtoFieldRef;
import es.pryades.imedig.pacs.dto.Study;

/**
 * 
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("rawtypes")
public class StudyControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -6873087829670738808L;
	
	private static final String[] visibleCols =	{ "study_datetime", "pat_name", "study_iuid", "num_instances" };

	public StudyControlerVto( ImedigContext ctx )
	{
		super( ctx );
	}

	/**
	 * 
	 * @param vtoObjRef
	 */
	public void setVtoObjRef( GenericVto vtoObjRef ) throws Throwable
	{
		this.dtoObjRef = this.generateDtoFromVto( vtoObjRef );
		super.setVtoObjRef( vtoObjRef );
	}

	/**
	 * get
	 * 
	 * @return
	 */
	public static String[] getVisibleCols()
	{
		return StudyControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return StudyControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return HashMap
	 */
	@Override
	public HashMap<String, VtoFieldRef> getVtoMapRefList()
	{
		return this.vtoFieldRef;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isCampoRef( String fieldName )
	{
		return (this.vtoFieldRef.get( fieldName ) != null);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String getVtoIdFiledName()
	{
		return "id";
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Object getDtoObject()
	{
		return this.dtoObjRef;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Class getDtoObjectClass()
	{
		return Study.class;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public GenericVto getVtoObject()
	{
		return this.vtoObjRef;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Class getVtoObjClass()
	{
		return Study.class;
	}

	/**
	 * 
	 * @return
	 */
	public Object generateDtoFromVto( GenericVto vtoObj ) throws Throwable
	{
		Object result = null;

		return result;
	}

	public GenericVto generateVtoFromDto( Object dtoObj ) throws Throwable
	{
		StudyVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( Study.class ) )
			{
				result = new StudyVto();

				result.setId( ((Study)dtoObj).getId() );
				result.setStudy_datetime( ((Study)dtoObj).getStudy_datetime().toString() );
				result.setStudy_iuid( ((Study)dtoObj).getStudy_iuid() );
				result.setStudy_desc( ((Study)dtoObj).getStudy_desc() );
				result.setPat_name( ((Study)dtoObj).getPat_name() );
				result.setNum_instances( ((Study)dtoObj).getNum_instances() );
			}
			else
			{
			}
		}

		return result;
	}
}
