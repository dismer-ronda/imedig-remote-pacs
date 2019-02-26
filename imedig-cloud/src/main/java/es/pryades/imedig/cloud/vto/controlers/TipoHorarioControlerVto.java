package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.vto.TipoHorarioVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import es.pryades.imedig.core.common.VtoFieldRef;

/**
 * 
 * 
 * @author hector.licea
 * 
 */
@SuppressWarnings("rawtypes")
public class TipoHorarioControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -8082703046594849640L;
	
	private static final String[] visibleCols =	{ "nombre", "tiporecurso", "tipohorario" };
	
	public TipoHorarioControlerVto( ImedigContext ctx)
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
		return TipoHorarioControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return TipoHorarioControlerVto.visibleCols;
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
		return TipoHorario.class;
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
		return TipoHorarioVto.class;
	}

	/**
	 * 
	 * @return
	 */
	public Object generateDtoFromVto( GenericVto vtoObj ) throws Throwable
	{
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public GenericVto generateVtoFromDto( Object dtoObj ) throws Throwable
	{
		TipoHorarioVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( TipoHorario.class ) )
			{
				result = new TipoHorarioVto();
				
				TipoHorario dto = (TipoHorario)dtoObj; 

				result.setId( dto.getId() );
				result.setNombre( dto.getNombre() );
				result.setTiporecurso( getContext().getString( "resource.type."+dto.getTipo_recurso()));
				result.setTipohorario(getContext().getString( "modalNewTipoHorario.tipo."+dto.getTipo_horario()));
			}
			else
			{
				// throw ImedigException();
			}

		}

		return result;
	}
}
