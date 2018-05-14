package es.pryades.imedig.core.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings( {"unchecked","rawtypes", "unused"} )
@Data
public abstract class ImedigManagerImpl implements ImedigManager
{
	Class mapperClass;
	Class dtoClass;
	Logger logger;
	
	public ImedigManagerImpl( Class mapperClass, Class dtoClass, Logger logger )
	{
		setMapperClass( mapperClass );
		setLogger( logger );
		setDtoClass( dtoClass );
	}
	
	@Override
	public boolean hasUniqueId( ImedigContext ctx ) 
	{
		return true;
	}
	
	@Override
	public boolean hasBlob() 
	{
		return false;
	}
	
	public void setRow( ImedigContext ctx, ImedigDto lastRow, ImedigDto newRow ) throws Throwable
	{
		if ( lastRow != null && newRow.equals( lastRow ) )
		{
			getLogger().info( "nothing to update in the row" );
			
			return;
		}
		
		SqlSession session = getDatabaseSession( ctx ); 
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );

		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );

			Utils.emptyToNull( newRow, newRow.getClass() );
			
			if ( !hasUniqueId( ctx ) || newRow.getId() == null )
			{
				if ( !hasBlob() )
					getLogger().info( "inserting row " + newRow );
				
				mapper.addRow( newRow );
				
				if ( hasUniqueId( ctx ) && newRow.getId() == null )
					throw new Exception( "Row id was not set. Check SQL script" );
			}
			else
			{
				if ( !hasBlob() )
					getLogger().info( "updating row " + newRow );

				if ( lastRow == null )
					throw new Exception( "Update row without the last row value" );
				
				if ( !lastRow.getClass().equals( newRow.getClass() ) )
					throw new Exception( "Update missmatch classes" );
				
				mapper.setRow( newRow );
			}

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			Utils.logException( e, getLogger() );
			
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
	}

	public void delRow( ImedigContext ctx, ImedigDto row ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			if ( !hasBlob() )
				getLogger().info( "deleting row " + row );

			mapper.delRow( row );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			Utils.logException( e, getLogger() );
			
			throw e;
		}
		finally
		{
			if ( finish )
				closeDatabaseSession( ctx );
		}
	}

	public int getNumberOfRows( ImedigContext ctx, Query query ) throws Throwable
    {
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		int count = 0;
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			count = mapper.getNumberOfRows( query ); 
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
		
		return count;
    }
    
	public List getRows( ImedigContext ctx, Query query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		ArrayList<ImedigDto> rows = null;
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			boolean paged = (query != null) && (query.getPageSize() != null) && (query.getPageSize() != -1);  
			
			if ( paged )
				rows = mapper.getPage( query );
			else
				rows = mapper.getRows( query );
			
			for ( ImedigDto dto : rows )
				Utils.nullToEmpty( dto, dto.getClass() );
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

		if ( rows == null )
			throw new Exception( "Null return" );
		
		return rows;
	}
	
	public ImedigDto newDto() throws Throwable
	{
		try 
		{
			return (ImedigDto) dtoClass.newInstance();
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, getLogger() );
			
			throw e;
		} 
	}

	public ImedigDto getRow( ImedigContext ctx, int id ) throws Throwable
	{
		ImedigDto dto = newDto();
		
		dto.setId( id );
		
		return getRow( ctx, dto );
	}

	public ImedigDto getLastRow( ImedigContext ctx, ImedigDto query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		ImedigDto row = null;
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			row = mapper.getLastRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
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
		
		return row;
	}
	
	public ImedigDto getRow( ImedigContext ctx, ImedigDto dto ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		ImedigDto row = null;
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			row = mapper.getRow( dto ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
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
		
		return row;
	}
	
	public ImedigDto getNextRow( ImedigContext ctx, ImedigDto query ) throws Throwable
	{
		SqlSession session = getDatabaseSession( ctx );
		
		boolean finish = session == null;		
		
		if ( finish )
			session = openDatabaseSession( ctx );
		
		ImedigDto row = null;
		
		try 
		{
			ImedigMapper mapper = (ImedigMapper)session.getMapper( mapperClass );
			
			row = mapper.getNextRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
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
		
		return row;
	}
}
