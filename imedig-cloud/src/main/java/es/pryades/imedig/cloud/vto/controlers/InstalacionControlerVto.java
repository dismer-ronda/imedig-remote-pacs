package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.vto.InstalacionVto;
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
public class InstalacionControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 6481184312348308369L;
	
	private static final String[] visibleCols =
	{ "nombre", "aetitle", "modalidad"};

	public InstalacionControlerVto( ImedigContext ctx)
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
		return InstalacionControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return InstalacionControlerVto.visibleCols;
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
		return Instalacion.class; 
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
		return InstalacionVto.class;
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
		InstalacionVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( Instalacion.class ) )
			{
				result = new InstalacionVto();

				// ID
				result.setId( ((Instalacion)dtoObj).getId() );
				// NOMBRE
				result.setNombre( ((Instalacion)dtoObj).getNombre() );
				// AETITLE
				result.setAetitle( ((Instalacion)dtoObj).getAetitle() );
				// MODALIDAD
				result.setModalidad( ((Instalacion)dtoObj).getModalidad() );
				// TIPO
				result.setTipo( ((Instalacion)dtoObj).getTipo() );
			}
			else
			{
				// throw ImedigException();
			}

		}

		return result;
	}
}
