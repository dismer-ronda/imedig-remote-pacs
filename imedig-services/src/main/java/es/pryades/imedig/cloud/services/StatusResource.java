package es.pryades.imedig.cloud.services;

import java.sql.Timestamp;
import java.util.HashMap;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigResource;
import es.pryades.imedig.cloud.common.ReturnFactory;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.StorageConfiguration;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigStatus;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.pacs.dal.StudiesManager;
import es.pryades.imedig.pacs.dto.query.StudyQuery;

public class StatusResource extends ImedigResource 
{
	private static final long serialVersionUID = 1004820386029507404L;

	private static final Logger LOG = Logger.getLogger( StatusResource.class );

	private DetallesInformesManager informesManager;
	private StudiesManager studiesManager;
	
	private String queryDate;

	public StatusResource() 
	{
		super();

		informesManager = (DetallesInformesManager) IOCManager.getInstanceOf( DetallesInformesManager.class );
		studiesManager = (StudiesManager) IOCManager.getInstanceOf( StudiesManager.class );
	}

    @Override
    protected void doInit() throws ResourceException 
    {
    	queryDate = (String)getRequest().getAttributes().get( "queryDate" );

        setExisting( queryDate != null );
    }
	
	public void setDateFilter( InformeQuery queryObj, int queryDate )
	{
		switch ( queryDate )
		{
			case 1:
				queryObj.setDesde( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 2:
				queryObj.setDesde( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 3:
				queryObj.setDesde( Long.parseLong( Utils.getLastWeekDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 4:
				queryObj.setDesde( Long.parseLong( Utils.getLastMonthDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 5:
				queryObj.setDesde( Long.parseLong( Utils.getLastYearDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			default:
			break;
		}
	}
	
	public void setDateFilter( StudyQuery queryObj, int queryDate )
	{
		switch ( queryDate )
		{
			case 1:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 2:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 00:00:00" )  );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 3:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastWeekDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 4:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastMonthDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 5:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastYearDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			default:
				queryObj.setFrom_date( null );
				queryObj.setTo_date( null );
			break;
		}
	}
	
	private int getNumberReportsDone( ImedigContext ctx, int queryDate )
	{
		InformeQuery queryObj = new InformeQuery();

		setDateFilter( queryObj, queryDate );
		queryObj.setEstado( Informe.STATUS_FINISHED );
		
		try
		{
			return informesManager.getNumberOfRows( ctx, queryObj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return 0;
	}

	private int getNumberStudiesDone( ImedigContext ctx, int queryDate )
	{
		StudyQuery queryObj = new StudyQuery();

		setDateFilter( queryObj, queryDate );
		
		try
		{
			return studiesManager.getNumberOfRows( ctx, queryObj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return 0;
	}

	private int getNumberImagesDone( ImedigContext ctx, int queryDate )
	{
		StudyQuery queryObj = new StudyQuery();

		setDateFilter( queryObj, queryDate );
		
		try
		{
			return studiesManager.getTotalImages( ctx, queryObj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );		
		}
		
		return 0;
	}
    
	private StorageConfiguration readStorageConfiguration() throws Exception
	{
		StorageConfiguration storageConfiguration = (StorageConfiguration)Utils.readObject( Settings.HOME_dir + "/conf/storage.conf" );
		
		if ( storageConfiguration == null )
		{
			storageConfiguration = new StorageConfiguration();
			
			storageConfiguration.setEnableExternalStorage( false );
			storageConfiguration.setExternalStorageDirectory( "192.168.254.254:/Public" ); 
			storageConfiguration.setExternalStorageFilesystem( "nfs" ); 
			storageConfiguration.setKeepStudyMax( 60 ); 
		}
		
		return storageConfiguration;
	}

	private void readStorageUse( ImedigStatus status ) throws Exception
	{
		String res = Utils.cmdExec( "df | awk {'print $6 \" \" $2 \" \" $3 \" \" $4'}" );
		
		String lines[] = res.split( "\n" );
		
		StorageConfiguration storageConfiguration = readStorageConfiguration();
		
    	status.setSizeNAS( 0L );
    	status.setUsedNAS( 0L );

    	for ( String line : lines )
		{
			String fields[] = line.split( " " );
			
			if ( fields.length == 4 )
			{
				if ( fields[0].equals( "/" ) )
				{
			    	status.setSizeMain( Long.parseLong( fields[1] ) / (1024 * 1024) );
			    	status.setUsedMain( Long.parseLong( fields[2] ) / (1024 * 1024) );
				}
				else if ( fields[0].equals( "/home/imedig/" + Utils.extractLastDir( storageConfiguration.getExternalStorageDirectory() ) ) )
				{
			    	status.setSizeNAS( Long.parseLong( fields[1] ) / (1024 * 1024) );
			    	status.setUsedNAS( Long.parseLong( fields[2] ) / (1024 * 1024) );
				}
			}
		}
	}
	
	/**
	 * GET
	 */
	@Get("text")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		try
		{
			HashMap<String,String> params = new HashMap<String,String>();
			
			Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
			
	        Integer date = Integer.parseInt( queryDate );

	    	ImedigContext ctx = new ImedigContext();
	    	
	    	ImedigStatus status = new ImedigStatus();
	    	status.setNumberStudies( getNumberStudiesDone( ctx, date ) );
	    	status.setNumberReports( getNumberReportsDone( ctx, date ) );
	    	status.setNumberImages( getNumberImagesDone( ctx, date ) );
	    	
			double average = status.getNumberStudies() != 0 ? (status.getNumberImages() / status.getNumberStudies().doubleValue()) : 0;
	    	status.setAveragePerStudy( Utils.roundDouble( average, 1 ) );
	    	
	    	readStorageUse( status );
	  	
	    	rep = new StringRepresentation( Utils.toJson( status ) );
			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
		}
		catch( Throwable e )
		{
			e.printStackTrace();
			
			rep = new StringRepresentation( "Not found" );
			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_NOT_FOUND ) );
		}	
		
		return rep;
    }
}
