package es.pryades.imedig.core.common;

/**
 * 
 * @author Dismer Ronda
 *
 */
public interface ImedigPaginatorFilter extends ImedigPaginator
{
	//###########################################################################################//
	//#										  METHODS	 									   	#//
	//###########################################################################################//

	public QueryFilterRef getPaginatorQueryFilterRef();
	public void setPaginatorQueryFilterRef(QueryFilterRef queryFilteRef);
	
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
	
}
