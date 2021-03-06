package es.pryades.imedig.cloud.vto.controlers;

import java.util.HashMap;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.vto.InformeVto;
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
public class InformeControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -5276816955887400532L;
	
	private static final String[] visibleCols =	{ "imgEstado", "fecha", "estado", "paciente_id", "paciente_nombre", "refiere", "informa"};

	public InformeControlerVto( ImedigContext ctx )
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
		return InformeControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return InformeControlerVto.visibleCols;
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
		return DetalleInforme.class;
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
		return DetalleInforme.class;
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

	private String getEstado( Integer estado )
	{
		switch ( estado.intValue() )
		{
			case Informe.STATUS_INFORMED:
				return getContext().getString( "words.not.approved" );
				
			case Informe.STATUS_APROVED:
				return getContext().getString( "words.approved" );

			case Informe.STATUS_FINISHED:
				return getContext().getString( "words.finished" );

			default:
				return getContext().getString( "words.requested" );
		}
	}

	public GenericVto generateVtoFromDto( Object dtoObj ) throws Throwable
	{
		InformeVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( DetalleInforme.class ) )
			{
				result = new InformeVto();

				result.setId( ((DetalleInforme)dtoObj).getId() );
				result.setImgEstado( AppUtils.getImgEstado( ((DetalleInforme)dtoObj).getEstado() ) );
				result.setEstado( getEstado( ((DetalleInforme)dtoObj).getEstado() ) );
				result.setFecha( Utils.getFormatedDate( ((DetalleInforme)dtoObj).getFecha(), "dd/MM/yyyy HH:mm" ) );
				result.setCentro_nombre( ((DetalleInforme)dtoObj).getCentro_nombre() );
				result.setEstudio_id( ((DetalleInforme)dtoObj).getEstudio_id() );
				result.setEstudio_acceso( ((DetalleInforme)dtoObj).getEstudio_acceso() );
				result.setPaciente_id( ((DetalleInforme)dtoObj).getPaciente_id() );
				result.setPaciente_nombre( ((DetalleInforme)dtoObj).getPaciente_nombre() );
				result.setModalidad( ((DetalleInforme)dtoObj).getModalidad() );
				result.setInforma( ((DetalleInforme)dtoObj).getInformaNombreAbreviado() );
				result.setIcd10cm( ((DetalleInforme)dtoObj).getIcd10cm() );
				result.setRefiere( ((DetalleInforme)dtoObj).getRefiereNombreAbreviado() );
			}
			else
			{
			}
		}

		return result;
	}
}
