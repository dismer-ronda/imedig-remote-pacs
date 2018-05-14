package es.pryades.imedig.core.common;

import java.util.List;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import org.apache.ibatis.session.SqlSession;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings("rawtypes")
public interface ImedigManager 
{	
	public SqlSession getDatabaseSession( ImedigContext ctx );
	public SqlSession openDatabaseSession( ImedigContext ctx ) throws Throwable;
	public void closeDatabaseSession( ImedigContext ctx ) throws Throwable;

	public boolean hasUniqueId( ImedigContext ctx );

	public boolean hasBlob();

	public void setRow( ImedigContext ctx, ImedigDto lastRow, ImedigDto newRow ) throws Throwable;
	public void delRow( ImedigContext ctx, ImedigDto row ) throws Throwable;

    public int getNumberOfRows( ImedigContext ctx, Query query ) throws Throwable;
	public List getRows( ImedigContext ctx, Query query ) throws Throwable;
	public ImedigDto getRow( ImedigContext ctx, int id ) throws Throwable;

	public ImedigDto getLastRow( ImedigContext ctx, ImedigDto query ) throws Throwable;
	public ImedigDto getRow( ImedigContext ctx, ImedigDto dto ) throws Throwable;
	public ImedigDto getNextRow( ImedigContext ctx, ImedigDto query ) throws Throwable;
}
