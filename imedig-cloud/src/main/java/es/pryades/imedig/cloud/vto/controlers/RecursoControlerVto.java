package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.vto.RecursoVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("rawtypes")
public class RecursoControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 6481184312348308369L;
	
	private static final String[] visibleCols =
	{ "nombre", "aetitle", "modalidad"};

	public RecursoControlerVto( ImedigContext ctx)
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
		return RecursoControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return RecursoControlerVto.visibleCols;
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
		return Recurso.class; 
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
		return RecursoVto.class;
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

	/**
	 * 
	 * @return
	 */
	public GenericVto generateVtoFromDto( Object dtoObj ) throws Throwable
	{
		RecursoVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( Recurso.class ) )
			{
				result = new RecursoVto();

				// ID
				result.setId( ((Recurso)dtoObj).getId() );
				// NOMBRE
				result.setNombre( ((Recurso)dtoObj).getNombre() );
				// AETITLE
				result.setAetitle( ((Recurso)dtoObj).getAetitle() );
				// MODALIDAD
				result.setModalidad( ((Recurso)dtoObj).getModalidad() );
				// TIPO
				result.setTipo( ((Recurso)dtoObj).getTipo() );
			}
			else
			{
				// throw ImedigException();
			}

		}

		return result;
	}
}
