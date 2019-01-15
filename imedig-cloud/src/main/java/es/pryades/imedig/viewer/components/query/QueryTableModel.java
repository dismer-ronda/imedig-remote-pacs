package es.pryades.imedig.viewer.components.query;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.pacs.dal.StudiesSearchManager;
import es.pryades.imedig.pacs.dto.query.StudyQuery;
import lombok.Getter;
import lombok.Setter;

public class QueryTableModel implements Serializable
{
	private static final long serialVersionUID = -1958881715934013874L;

	private static final Logger LOG = LoggerFactory.getLogger( QueryTableModel.class );
	
	//private List<Study> studies;
	
	int page;
	int size;
	int totalSize;
	
	private StudiesSearchManager studiesSearchManager;
	private ImedigContext ctx;
	
	public int getPage() 
	{
		return page;
	}
	
	@Getter @Setter private String patientName; 
	@Getter @Setter	private String patientId;
	@Getter @Setter private Timestamp studyDateFrom;
	@Getter @Setter private Timestamp studyDateTo;
	@Getter @Setter	private String referringPhysicianName;
	@Getter @Setter	private String modalitiesInStudy;
	private StudyQuery query;
	
	public QueryTableModel( ImedigContext ctx, int size ) 
	{
		super();
		
		this.size = size;
		this.page = 0;
		this.totalSize = 0;
		
		studiesSearchManager = (StudiesSearchManager) IOCManager.getInstanceOf( StudiesSearchManager.class );
		this.ctx = ctx;
	}

	/**
	 * 
	 */
	/*public Object getValueAt( int col, int row ) 
	{
		return null;
	}*/

	/**
	 * Recuperar el número de filas a pintar
	 */
	/*public int getRowCount() 
	{
		if ( (page + 1) * size >= getTotalSize() )
			return getTotalSize() - page * size;
			
		return size;
	}*/

	public boolean firstPage()
	{
		if ( page != 0 )
		{
			page = 0;
			
			return true;
		}
		
		return false;
	}
	
	public boolean nextPage()
	{
		if ( (page + 1) * size < getTotalSize() )
		{
			page++;
			
			return true;
		}
		
		return false;
	}
	
	public boolean previousPage()
	{
		if ( (page - 1) * size >= 0 )
		{
			page--;
			
			return true;
		}
		
		return false;
	}

	public boolean lastPage()
	{
		int temp = page;
		
		if ( getTotalSize() == 0 )
			page = 0;
		else
			page = (getTotalSize() - 1) / size;
		
		return page != temp;
	}
	
	public void setPage( int newPage )
	{
		if ( newPage < 0 || getTotalSize() == 0 )
			page = 0;
		else if ( newPage * size < getTotalSize() )
			page = newPage;
		else
			page = 0;
	}

	public int getNumberOfPages()
	{
		if ( getTotalSize() == 0 )
			return 0;
		else
			return (getTotalSize() - 1) / size + 1;
	}
	
	public int getTotalSize() 
	{
		/*if ( studies == null )
			return 0;
		
		return studies.size();*/
		return totalSize;
	}

	/**
	 * Realizar búsqueda 
	 * 
	 * @param localizador
	 */
	
	public int doQuery() 
	{
		query = new StudyQuery();
		query.setRef_physician( referringPhysicianName );
		query.setPat_name( patientName );
		query.setMods_in_study( modalitiesInStudy );
		query.setFrom_date( studyDateFrom );
		query.setTo_date( studyDateTo );
		query.setPat_id( patientId );
		
	    try
		{
			totalSize = studiesSearchManager.getNumberOfRows( ctx, query );
		}
		catch ( Throwable e )
		{
			LOG.error( "Error", e );
			totalSize = 0;
		}
		
		return totalSize;
	}

	/*public Study getRow( int i ) 
	{
		if ( studies == null || studies.size() == 0 || page * size + i > getTotalSize() - 1 ) 
			return null;
		
		return studies.get( page * size + i );
	}*/
	
	public List<Study> getCurrentPage(){
		query.setPageSize( size );
		query.setPageNumber( page+1 );

		List<es.pryades.imedig.pacs.dto.Study> studies;
		try
		{
			studies = studiesSearchManager.getRows( ctx, query );
		}
		catch ( Throwable e )
		{
			LOG.error( "Error obteniendo listado", e );
			studies = new ArrayList<>();
		}
        return convertFrom( studies );
	}
	
	private List<Study> convertFrom(List<es.pryades.imedig.pacs.dto.Study> studies){
		List<Study> result = new ArrayList<>();
		SimpleDateFormat dateformat = new SimpleDateFormat( "dd/MM/yyyy" );
		SimpleDateFormat timeformat = new SimpleDateFormat( "HH:mm" );
		
		for ( es.pryades.imedig.pacs.dto.Study item : studies )
		{
			Study study = new Study();
			study.setStudyInstanceUID( item.getStudy_iuid() );
			study.setAccessionNumber( item.getAccession_no() );
			study.setReferringPhysicianName( item.getRef_physician() );
			study.setPatientName( item.getPat_name() );
			study.setPatientID( item.getPat_id() );
			study.setIssuerOfPatientID( item.getPat_id_issuer() );
			study.setPatientBirthDate( item.getPat_birthdate() );
			study.setPatientSex( item.getPat_sex() );
			study.setModalitiesInStudy( item.getMods_in_study() );
			study.setStudyDate( item.getStudy_datetime() == null ? "" : dateformat.format( item.getStudy_datetime() ) );
			study.setStudyTime( item.getStudy_datetime() == null ? "" : timeformat.format( item.getStudy_datetime() ) );
			study.setStudyID( item.getStudy_id() );
			
			result.add( study );
		}
		return result;
	}
	
}

