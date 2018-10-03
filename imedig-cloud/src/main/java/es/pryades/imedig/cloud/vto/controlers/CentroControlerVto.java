package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.vto.CentroVto;
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
public class CentroControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 3295415699699647927L;
	
	private static final String[] visibleCols =
	{ "id", "nombre", "descripcion" };

	public CentroControlerVto( ImedigContext ctx)
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
		return CentroControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return CentroControlerVto.visibleCols;
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
		return DetalleCentro.class;
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
		return DetalleCentro.class;
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
		CentroVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( DetalleCentro.class ) )
			{
				/* ++++ Mapeando de dto.DetalleCentro -> CentroVto ++++ */

				result = new CentroVto();

				// ++ ID
				Integer vtoId = ((DetalleCentro)dtoObj).getId();
				result.setId( vtoId );
				// -- ID

				// ++ NOMBRE
				String vtoNombre = ((DetalleCentro)dtoObj).getNombre();
				result.setNombre( vtoNombre );
				// -- NOMBRE

				// ++ ORDEN
				Integer vtoOrden = ((DetalleCentro)dtoObj).getOrden();
				result.setOrden( vtoOrden );
				// -- ORDEN

				// ++ DESCRIPCION
				String vtoDescripcion = ((DetalleCentro)dtoObj).getDescripcion();
				result.setDescripcion( vtoDescripcion );
				// -- DESCRIPCION

				// ++ DIRECCION
				String vtoDireccion = ((DetalleCentro)dtoObj).getDireccion();
				result.setDireccion( vtoDireccion );
				// -- DIRECCION

				// ++ CONTACTOS
				String vtoContactos = ((DetalleCentro)dtoObj).getContactos();
				result.setContactos( vtoContactos );
				// -- CONTACTOS

				// ++ COORDENADAS
				String vtoCoordenadas = ((DetalleCentro)dtoObj).getCoordenadas();
				result.setCoordenadas( vtoCoordenadas );
				// -- COORDENADAS

			}
			else
			{
				// throw ImedigException();
			}

		}

		return result;
	}
}
