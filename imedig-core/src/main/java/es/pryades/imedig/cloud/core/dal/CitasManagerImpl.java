package es.pryades.imedig.cloud.core.dal;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.ibatis.CitaMapper;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Cita;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CitaQuery;
import es.pryades.imedig.core.common.ImedigManager;

import org.apache.tapestry5.ioc.annotations.Inject;


/**
*
* @author hector.licea 
* 
*/
public class CitasManagerImpl extends ImedigCloudManagerImpl implements CitasManager
{
	private static final long serialVersionUID = -6213712981966757378L;

	private static final Logger LOG = Logger.getLogger( CitasManagerImpl.class );
	
	@Inject
	private RecursosManager recursosManager;
	
	@Inject
	private UsuariosManager usuariosManager;
	
	@Inject
	private PacientesManager pacientesManager;
	
	@Inject
	private TiposEstudiosManager tiposEstudiosManager;
	
	public static ImedigManager build()
	{
		return new CitasManagerImpl();
	}

	public CitasManagerImpl()
	{
		super( CitaMapper.class, Cita.class, LOG );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long getLastDate( ImedigContext ctx, CitaQuery query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		try 
		{
			CitaMapper mapper = (CitaMapper)session.getMapper( getMapperClass() );
			
			return mapper.getLastDate( query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, getLogger() );
			
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Cita> getTodayAppointments( ImedigContext ctx, Recurso resource ) throws Throwable
	{
		CitaQuery query = new CitaQuery();
		query.setRecurso( resource.getId() );
		query.setFecha_desde( Utils.getTodayFirstSecondAsLong() );
		query.setFecha_hasta( Utils.getTomorrowFirstSecondAsLong() );

		return (List<Cita>)getRows( ctx, query );
	}

	@SuppressWarnings("unchecked")
	private List<Cita> getNextHourAppointments( ImedigContext ctx, Recurso resource ) throws Throwable
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.add( Calendar.HOUR_OF_DAY, 1 );
		
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		long from = Utils.getCalendarTimeAsLong( calendar );

		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 999 );
		long to = Utils.getCalendarTimeAsLong( calendar );

		CitaQuery query = new CitaQuery();
		query.setRecurso( resource.getId() );
		query.setFecha_desde( from );
		query.setFecha_hasta( to );

		return (List<Cita>)getRows( ctx, query );
	}

	@SuppressWarnings("unchecked")
	private List<Recurso> getResources( ImedigContext ctx ) throws Throwable
	{
		Recurso query = new Recurso();
		query.setTipo( 1 );
		return recursosManager.getRows( ctx, query );
	}
	
	private Paciente getPaciente( ImedigContext ctx, int id ) throws Throwable
	{
		return (Paciente)pacientesManager.getRow( ctx, id );
	}
	
	private Usuario getUsuario( ImedigContext ctx, int id ) throws Throwable
	{
		return (Usuario)usuariosManager.getRow( ctx, id );
	}
	
	private TipoEstudio getTipoEstudio( ImedigContext ctx, int id ) throws Throwable
	{
		return (TipoEstudio)tiposEstudiosManager.getRow( ctx, id );
	}
	
	public void transferTodayWorklist( ImedigContext ctx ) throws Throwable
	{
		List<Recurso> recursos = getResources( ctx );

		for ( Recurso recurso : recursos )
		{
			List<Cita> citas = getTodayAppointments( ctx, recurso );
			
			for ( Cita cita : citas )
			{
				transferAppointmentWorklist( ctx, cita, recurso );
			}
		}
	}
	
	public void transferNextHourWorklist( ImedigContext ctx ) throws Throwable
	{
		List<Recurso> recursos = getResources( ctx );

		for ( Recurso recurso : recursos )
		{
			List<Cita> citas = getNextHourAppointments( ctx, recurso );
			
			for ( Cita cita : citas )
			{
				transferAppointmentWorklist( ctx, cita, recurso );
			}
		}
	}

	public void transferAppointmentWorklist( ImedigContext ctx, Cita cita, Recurso recurso ) throws Throwable
	{
		/*
		MSH|^~\&|%CALLING_AE%||%CALLED_AE%||%CURRENT_DATE%||ORM^O01^ORM_O01|168715|P|2.5
		PID||1|%PATIENT_ID%||%PATIENT_SURNAME1%^%PATIENT_NAME%||%PATIENT_BIRTH%|%PATIENT_SEX%|
		PV1||RAD|||||%REF_PHYS_ID%^%REF_PHYS_FIRST%^%REF_PHYS_LAST%|^ReferringPhysLast^ReferringPhysFirst
		ORC|NW|1|||||^^^%STUDY_DATETIME%
		OBR|1|1|2|1100|||||||||||||||%REQ_PROCID%|%SPS_ID%||||%MODALITY%||||||||||%PERFOMING_TECH%
		ZDS|%STUDY_UID%^%EXECUTING_AE%^%EXECUTING_NAME%
		*/
		
		Calendar now = Utils.getCalendarNow();
		
		if ( cita.getId() == null )
			cita.setId( Utils.getCalendarTimeAsInt( now ) );

		Paciente paciente = getPaciente( ctx, cita.getPaciente() );
		Usuario referidor = getUsuario( ctx, cita.getReferidor() );
		TipoEstudio tipo = getTipoEstudio( ctx, cita.getTipo() );

		URL url = Thread.currentThread().getContextClassLoader().getResource( "worklist.hl7" );
			
		String template = Utils.readFile( url.getPath() );
		
		String hl7 = template.
		replaceAll( "%CALLING_AE%", "IMEDIG_VIEWER" ).
		replaceAll( "%CALLED_AE%", "IMEDIG" ).
		replaceAll( "%CURRENT_DATE%", Utils.getFormatedDate( now.getTime(), "yyyyMMddhhmmss" ) ).
		replaceAll( "%PATIENT_ID%", paciente.getUid() ).
		replaceAll( "%PATIENT_SURNAME1%", paciente.getApellido1() + " " + paciente.getApellido2() ).
		replaceAll( "%PATIENT_NAME%", paciente.getNombre() ).
		replaceAll( "%PATIENT_BIRTH%", Integer.toString( paciente.getFecha_nacimiento() ) ).
		replaceAll( "%PATIENT_SEX%", paciente.getSexo() ).
		replaceAll( "%REF_PHYS_ID%", Integer.toString( referidor.getId() ) ).
		replaceAll( "%REF_PHYS_FIRST%", referidor.getNombre() ).
		replaceAll( "%REF_PHYS_LAST%", referidor.getApe1() ).
		replaceAll( "%STUDY_DATETIME%", Long.toString( cita.getFecha() ) ).
		replaceAll( "%REQ_PROCID%", tipo.getNombre() ).
		replaceAll( "%SPS_ID%", "" ).
		replaceAll( "%MODALITY%", recurso.getModalidad() ).
		replaceAll( "%PERFOMING_TECH%", "" ).
		replaceAll( "%STUDY_UID%", paciente.getId() + "." + referidor.getId() + "." + tipo.getId() + "." + recurso.getId() + "." + cita.getId() + "." + System.currentTimeMillis() ).
		replaceAll( "%EXECUTING_AE%", recurso.getAetitle() ).
		replaceAll( "%EXECUTING_NAME%", recurso.getNombre() );
		
		String fileName = "/tmp/" + Utils.getUUID();
		Utils.writeFile( fileName, hl7, "ISO-8859-1" );
		
		LOG.info( "***************" );
		LOG.info( hl7 );
		LOG.info( "***************" );
		
		Utils.cmdExec( "/opt/dcm4chee/utils/bin/hl7snd -c localhost:2575 " + fileName ); 
		
        Utils.DeleteFile( fileName );
	}
}
