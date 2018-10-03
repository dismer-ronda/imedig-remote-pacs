package es.pryades.imedig.core.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import lombok.Getter;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 *
 */
public class ImedigPaginatorImp implements ImedigPaginatorFilter, Serializable
{
	private static final long serialVersionUID = -8786807407586446065L;

	private static final Logger LOG = Logger.getLogger( ImedigPaginatorImp.class ); 
	
	//###########################################################################################//
	//#										PROPIEDADES	 									   	#//
	//###########################################################################################//
	
	protected Integer pagSize;
	protected Integer currPag;
	protected Integer totalPag;
	protected Integer totalRows;
	
	protected VtoFieldRef VtoDataRef;
	
	protected QueryFilterRef queryFilterRef;

	@Getter protected ImedigContext context;
	
	boolean firstPageEnable;
	boolean lastPageEnable;
	boolean nextPageEnable;
	boolean previousPageEnable;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	
	/**
	 * 
	 * @param vtoDataRef
	 * @param ctx
	 * @param resource
	 */
	public ImedigPaginatorImp (VtoFieldRef vtoDataRef, ImedigContext ctx)
	{
		this.currPag = 1;
		this.pagSize = -1;
		this.totalPag = -1;
		this.VtoDataRef = vtoDataRef; 
		this.context = ctx;
		
		this.firstPageEnable = false;
		this.lastPageEnable = false;
		this.nextPageEnable = false;
		this.previousPageEnable = false;
	}
	
	/**
	 * 
	 * @param pagSize
	 * @param vtoDataRef
	 * @param queryfilterRef
	 * @param ctx
	 * @param resource
	 */
	public ImedigPaginatorImp (Integer pagSize, VtoFieldRef vtoDataRef, QueryFilterRef queryfilterRef, ImedigContext ctx)
	{
		this.currPag = 1;
		this.pagSize = pagSize;
		this.totalPag = -1;
		this.VtoDataRef = vtoDataRef;
		this.queryFilterRef = queryfilterRef;
		this.context = ctx;
		
		this.firstPageEnable = false;
		this.lastPageEnable = false;
		this.nextPageEnable = false;
		this.previousPageEnable = false;
	}
	
	//###########################################################################################//
	//#									 INHERIT METHODS 									   	#//
	//###########################################################################################//
	
	/**
	 * 
	 * @return
	 */
	@Override
	public QueryFilterRef getPaginatorQueryFilterRef()
	{
		return this.queryFilterRef;
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public void setPaginatorQueryFilterRef(QueryFilterRef queryFilteRef)
	{
		this.queryFilterRef = queryFilteRef;
		this.currPag = 1;
		this.totalPag =  this.getTotalPages();
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public List<ImedigDto>  getPageNum(Integer pagNum) throws Throwable 
	{
		if(pagNum == 1){
			return this.getFistPage();
		}
		else if(pagNum == this.totalPag)
		{
			return this.getLastPage(); 
		}
		else
		{
			this.currPag = pagNum;
			return this.getCurrPage();
		}
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getFistPage() throws Throwable
	{
		List<ImedigDto>  result = null; 
		
		this.currPag = 1;
		Query queryObject = this.queryFilterRef.getFilterQueryObject();
		queryObject.setPageNumber(this.currPag);
		queryObject.setPageSize(this.pagSize);
		result = this.retriveValuesFromInstanceDB(1);	
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getLastPage() throws Throwable
	{
		Query queryObject = this.queryFilterRef.getFilterQueryObject();
		this.currPag = this.totalPag;
		queryObject.setPageNumber(this.currPag);
		queryObject.setPageSize(this.pagSize);
		
		if(this.currPag == 1)
			return this.retriveValuesFromInstanceDB(1); //code currPage number
		else
			return this.retriveValuesFromInstanceDB(3); //code lastPage
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getCurrPage() throws Throwable 
	{
		Query queryObject = this.queryFilterRef.getFilterQueryObject();
		queryObject.setPageNumber(this.currPag);
		queryObject.setPageSize(this.pagSize);
		
		if(this.currPag == 1)
			return this.retriveValuesFromInstanceDB(1); //code firstPage
		else
			return this.retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getNextPage() throws Throwable 
	{
		this.currPag++;
		Query queryObject = this.queryFilterRef.getFilterQueryObject();
		queryObject.setPageNumber(this.currPag);
		queryObject.setPageSize(this.pagSize);
		
		if(this.currPag == 1)
			return this.retriveValuesFromInstanceDB(1); //code firstPage
		else if(this.currPag == this.totalPag)
			return this.retriveValuesFromInstanceDB(3); //code last number
		else
			return this.retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getPreviousPage() throws Throwable 
	{
		this.currPag--;
		Query queryObject = this.queryFilterRef.getFilterQueryObject();
		queryObject.setPageNumber(this.currPag);
		queryObject.setPageSize(this.pagSize);
		
		if(this.currPag == 1)
			return this.retriveValuesFromInstanceDB(1); //code firstPage
		else if(this.currPag == this.totalPag)
			return this.retriveValuesFromInstanceDB(3); //code last number
		else
			return this.retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isFirstPageEnable() 
	{
		return this.firstPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isLastPageEnable() 
	{
		return this.lastPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isNextPageEnable()
	{
		return this.nextPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isPreviousPageEnable() 
	{
		return this.previousPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public int getCurrPag() 
	{
		return (this.totalPag > 0 ? currPag.intValue() : 0);
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public int getTotalPag() {
		return totalPag.intValue();
	}

	//###########################################################################################//
	//#										  METHODS	 									   	#//
	//###########################################################################################//

	/**
	 * 
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	private List<ImedigDto> retriveValuesFromInstanceDB(Integer pageCode) throws Throwable
	{
		if(this.VtoDataRef != null || this.VtoDataRef.getFieldManagerImp() != null)
		{
			
			List<ImedigDto>  listaElementos = null;
			ImedigManager queryMan = (ImedigManager) IOCManager.getInstanceOf( this.VtoDataRef.getFieldManagerImp() );
			
			this.queryFilterRef.getFilterQueryObject().setPageSize(this.pagSize);
			
			if(this.totalPag > 0) // conozco el total de paginas
			{
				switch(pageCode)
				{
				
					case 1: // firstPage
							this.queryFilterRef.getFilterQueryObject().setPageNumber(1);
							listaElementos = queryMan.getRows(this.context, this.queryFilterRef.getFilterQueryObject());
		
							this.updatePageAvaibility();	
							
							if(listaElementos == null || listaElementos.size()==0)
							{
								this.firstPageEnable = false;
								this.lastPageEnable = false;
								this.nextPageEnable = false;
								this.previousPageEnable = false;
							}
						break;
					case 2: // page Number
							this.queryFilterRef.getFilterQueryObject().setPageNumber(this.currPag);
							listaElementos = queryMan.getRows(this.context, this.queryFilterRef.getFilterQueryObject());
		
							this.updatePageAvaibility();	
							
							if(listaElementos == null || listaElementos.size()==0)
							{
								this.firstPageEnable = false;
								this.lastPageEnable = false;
								this.nextPageEnable = false;
								this.previousPageEnable = false;
							}
						break;
					case 3: // last page
							this.queryFilterRef.getFilterQueryObject().setPageNumber(this.totalPag);
							listaElementos = queryMan.getRows(this.context, this.queryFilterRef.getFilterQueryObject());
		
							this.updatePageAvaibility();	
							
							if(listaElementos == null || listaElementos.size()==0)
							{
								this.firstPageEnable = false;
								this.lastPageEnable = false;
								this.nextPageEnable = false;
								this.previousPageEnable = false;
							}
						break;
				}
			}
			else // no conozco el total de paginas
			{
				switch(pageCode)
				{
					case 1: // firstPage
						this.queryFilterRef.getFilterQueryObject().setPageNumber(1);
						listaElementos = queryMan.getRows(this.context, this.queryFilterRef.getFilterQueryObject());
		
						this.updatePageAvaibility();	
						
						if(listaElementos == null || listaElementos.size()==0)
						{
							this.firstPageEnable = false;
							this.lastPageEnable = false;
							this.nextPageEnable = false;
							this.previousPageEnable = false;
						}
					break;
						case 2: // page Number
							this.queryFilterRef.getFilterQueryObject().setPageNumber(this.currPag);
							listaElementos = queryMan.getRows(this.context, this.queryFilterRef.getFilterQueryObject());
			
							this.updatePageAvaibility();	
							
							if(listaElementos == null || listaElementos.size()==0)
							{
								this.firstPageEnable = false;
								this.lastPageEnable = false;
								this.nextPageEnable = false;
								this.previousPageEnable = false;
							}
						break;
				}
			}
			
			return (listaElementos == null ? new ArrayList<ImedigDto>() : listaElementos);
		}
		else
		{
			throw new Exception("ImedigPaginator VtoDataRef or VtoDataRef.FieldManagerImp cant not be null");
		}
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	private void updatePageAvaibility()
	{
		if(this.pagSize < 1 )
		{
			this.firstPageEnable = false;
			this.lastPageEnable = false;
			this.nextPageEnable = false;
			this.previousPageEnable = false;
		}
		else
		{
			if(currPag > 1)
			{
				this.firstPageEnable = true;
				this.previousPageEnable = true;
			}
			else
			{
				this.firstPageEnable = false;
				this.previousPageEnable = false;
			}
			
			
			if(currPag == totalPag)
			{
				lastPageEnable = false;
				nextPageEnable = false;
			}
			else
			{
				lastPageEnable = true;
				nextPageEnable = true;
			}
		}
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	private Integer getTotalPages()
	{
		Integer result = 0;
		
		try 
		{
			ImedigManager queryMan = (ImedigManager) IOCManager.getInstanceOf( this.VtoDataRef.getFieldManagerImp() );	
			this.queryFilterRef.getFilterQueryObject().setPageSize(this.pagSize);
			totalRows = queryMan.getNumberOfRows(this.context, this.queryFilterRef.getFilterQueryObject());
			
			if(totalRows > 0)
			{
				if(this.pagSize > 0)
				{
					Integer div = totalRows/this.pagSize;
					Integer divMod = totalRows % this.pagSize;
					
					if(divMod != 0)
						div++;
					
					result = div;
				}
				else
				{
					result = 0;
				}
			}
			else
			{
				result = 0;
			}
		} 
		catch (Throwable e)
		{
			this.totalPag = -1;
		}
		
		return result;
	}
	
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
	
}
