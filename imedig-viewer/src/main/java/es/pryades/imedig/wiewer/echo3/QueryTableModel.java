package es.pryades.imedig.wiewer.echo3;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import nextapp.echo.app.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.wado.query.QueryManager;

public class QueryTableModel extends AbstractTableModel 
{
	private static final Logger LOG = LoggerFactory.getLogger( QueryTableModel.class );
	
	private static final long serialVersionUID = 1L;
	
	private List<Study> studies;
	
	int page;
	int size;
	
	public int getPage() 
	{
		return page;
	}
	
	@Getter @Setter private String patientName; 
	@Getter @Setter	private String patientId;
	@Getter @Setter private String studyDate;
	@Getter @Setter	private String referringPhysicianName;
	@Getter @Setter	private String modalitiesInStudy;
	
	public QueryTableModel( int size ) 
	{
		super();
		
		this.size = size;
		this.page = 0;
	}

	/**
	 * 
	 */
	public Object getValueAt( int col, int row ) 
	{
		return null;
	}

	/**
	 * Recuperar el número de filas a pintar
	 */
	public int getRowCount() 
	{
		if ( (page + 1) * size >= getTotalSize() )
			return getTotalSize() - page * size;
			
		return size;
	}

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
	
	/**
	 * Recuperar el número de columnas
	 */
	public int getColumnCount() 
	{
		return 7;
	}
	
	public int getTotalSize() 
	{
		if ( studies == null )
			return 0;
		
		return studies.size();
	}

	/**
	 * Realizar búsqueda 
	 * 
	 * @param localizador
	 */
	
	public int doQuery() 
	{
		HashMap<String,String> parameters = new HashMap<String,String>();
		
		parameters.put( "ReferringPhysicianName", referringPhysicianName );
		parameters.put( "PatientName", patientName );
		parameters.put( "PatientID", patientId );
		parameters.put( "StudyDate", studyDate );
		parameters.put( "ModalitiesInStudy", modalitiesInStudy );
		
		String AETitle = Settings.PACS_AETitle;
		String Host = Settings.PACS_Host;
		int Port = Settings.PACS_Port;
			
	    studies = QueryManager.getInstance().findStudies( AETitle, Host, Port, Settings.IMEDIG_AETitle, parameters );
		
		fireTableDataChanged();
		
		return studies.size();
	}

	public Study getRow( int i ) 
	{
		if ( studies == null || studies.size() == 0 || page * size + i > getTotalSize() - 1 ) 
			return null;
		
		return studies.get( page * size + i );
	}
	
}

