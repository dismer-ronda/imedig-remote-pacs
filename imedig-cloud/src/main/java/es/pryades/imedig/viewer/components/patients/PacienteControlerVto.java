package es.pryades.imedig.viewer.components.patients;

import java.util.Date;
import java.util.HashMap;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.Paciente;
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
public class PacienteControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -6601043762758420586L;

	private static final String[] visibleCols =	{ "identificador", "nombre", "sexo", "edad", "citas"};

	public PacienteControlerVto( ImedigContext ctx )
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
		return PacienteControlerVto.visibleCols;
	}

	/**
	 * 
	 * @return String[]
	 */
	@Override
	public String[] getStaticVisibleCols()
	{
		return PacienteControlerVto.visibleCols;
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
		return Paciente.class;
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
		return PacienteVto.class;
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
	
	private String getSexo( String sexo )
	{
		switch ( sexo )
		{
			case Constants.SEX_FEMALE:
				return getContext().getString( "words.sex.female" );
				
			case Constants.SEX_MALE:
				return getContext().getString( "words.sex.male" );

			case Constants.SEX_OTHER:
				return getContext().getString( "words.sex.other" );

			default:
				return "";
		}
	}

	public GenericVto generateVtoFromDto( Object dtoObj ) throws Throwable
	{
		PacienteVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals( Paciente.class ) )
			{
				result = new PacienteVto();

				result.setId( ((Paciente)dtoObj).getId() );
				result.setIdentificador(  ((Paciente)dtoObj).getUid() );
				result.setNombre( AppUtils.getNombreAAN( (Paciente)dtoObj ) );
				result.setSexo( getSexo( ((Paciente)dtoObj).getSexo() ) );
				result.setEdad( calcAge( (Paciente)dtoObj ) );
				result.setCitas( getCitas() );
			}
			else
			{
			}
		}

		return result;
	}

	private String calcAge(Paciente paciente) {
		if (paciente.getFecha_nacimiento() == null) return "";

		String age = "";
		Date dob = Utils.getDateFromInt( paciente.getFecha_nacimiento() );
		
		if (dob != null)
			age = Utils.getAge(dob, 
					context.getString("Generic.Month"),
					context.getString("Generic.Year"));
		
		return age;
	}

	private Component getCitas()
	{
		return new HorizontalLayout();
	}
}
