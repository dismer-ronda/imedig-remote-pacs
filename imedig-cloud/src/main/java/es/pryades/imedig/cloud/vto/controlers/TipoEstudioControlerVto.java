package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.vto.TipoEstudioVto;
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
public class TipoEstudioControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -6978872843411881934L;
	
	private static final String[] visibleCols =
	{ "nombre", "duracion" };

	public TipoEstudioControlerVto( ImedigContext ctx)
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
		return TipoEstudioControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return TipoEstudioControlerVto.visibleCols;
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
		return TipoEstudio.class;
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
		return TipoEstudio.class;
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
		TipoEstudioVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( TipoEstudio.class ) )
			{
				/* ++++ Mapeando de dto.DetalleCentro -> CentroVto ++++ */

				result = new TipoEstudioVto();

				// ++ ID
				Integer vtoId = ((TipoEstudio)dtoObj).getId();
				result.setId( vtoId );
				// -- ID

				// ++ NOMBRE
				String vtoNombre = ((TipoEstudio)dtoObj).getNombre();
				result.setNombre( vtoNombre );
				// -- NOMBRE

				// ++ DURACION
				Integer vtoDuracion = ((TipoEstudio)dtoObj).getDuracion();
				result.setDuracion( vtoDuracion );
				// -- DURACION
			}
			else
			{
				// throw ImedigException();
			}

		}

		return result;
	}
}
