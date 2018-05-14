package es.pryades.imedig.core.common;

import java.util.List;

import es.pryades.imedig.cloud.dto.ImedigDto;

/**
 * 
 * @author Dismer Ronda
 *
 */
public interface ImedigPaginator 
{
	public List<ImedigDto>  getPageNum(Integer pagNum) throws Throwable;
	public List<ImedigDto>  getFistPage() throws Throwable;
	public List<ImedigDto>  getLastPage() throws Throwable;
	public List<ImedigDto>  getCurrPage() throws Throwable;
	public List<ImedigDto>  getNextPage() throws Throwable;
	public List<ImedigDto>  getPreviousPage() throws Throwable;

	public Boolean isFirstPageEnable();
	public Boolean isLastPageEnable();
	public Boolean isNextPageEnable();
	public Boolean isPreviousPageEnable();
	
	public int getCurrPag();
	public int getTotalPag();
}
