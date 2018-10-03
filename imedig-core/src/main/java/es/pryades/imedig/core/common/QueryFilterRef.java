package es.pryades.imedig.core.common;

import java.io.Serializable;

import es.pryades.imedig.cloud.dto.Query;

/**
 * 
 * @author Dismer Ronda
 *
 */
public class QueryFilterRef implements Serializable 
{
	private static final long serialVersionUID = -6348718307689901304L;
	
	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//

	protected Query queryObject;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	
	/**
	 * 
	 */
	public QueryFilterRef(Query queryObject)
	{
		this.queryObject = queryObject;
	}
	
	//###########################################################################################//
	//#											ACCESS METHOD(S)							   	#//
	//###########################################################################################//	

	/**
	 * 
	 * @return
	 */
	public Query getFilterQueryObject()
	{
		return this.queryObject;
	}
	
	/**
	 * 
	 * @param queryObject
	 */
	public void setFilterQueryObject(Query queryObject)
	{
		this.queryObject = queryObject;
	}


  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------------
	
}
