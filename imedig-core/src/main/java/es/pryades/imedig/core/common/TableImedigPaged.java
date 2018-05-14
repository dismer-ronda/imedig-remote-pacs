package es.pryades.imedig.core.common;

import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("serial")
public class TableImedigPaged extends TableImedig implements ImedigPaginator
{
	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//
	
	// INI -- Vaading Components
	protected Button pageButtonIni;
	protected Button pageButtonPrevious;

	protected Label lbCurrPage;
	protected Label lbPageCantOf;
	protected Label lbTotalPage;
	
	protected Button pageButtonEnd;
	protected Button pageButtonNext;

	protected HorizontalLayout tablePageOppContainerBack;
	protected HorizontalLayout tablePageOppContainerNext;
	protected HorizontalLayout tablePageOppContainerInfo;
	protected HorizontalLayout tablePageOppContainer;
	// FIN -- Vaading Components
	
	protected ImedigPaginator imedigPaginator;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	 
	/**
	 * 
	 * @param tableObjectType
	 * @param objVto
	 * @param tableVtoDataRef
	 * @param queryfilterRef
	 * @param ctx
	 * @param resource
	 * @param pageSize
	 * @throws Throwable
	 */
    public TableImedigPaged(Class<?> tableObjectType, GenericVto objVto, VtoFieldRef tableVtoDataRef, QueryFilterRef queryfilterRef, ImedigContext ctx, Integer pageSize)
    {
    	super(tableObjectType,objVto,tableVtoDataRef,ctx);

    	this.imedigPaginator = new ImedigPaginatorImp(pageSize, tableVtoDataRef, queryfilterRef, ctx);
    }
    
    public TableImedigPaged(Class<?> tableObjectType, GenericVto objVto, VtoFieldRef tableVtoDataRef, ImedigPaginator aPaginator, ImedigContext ctx )throws Throwable
    {
    	super(tableObjectType,objVto,tableVtoDataRef,ctx);

    	imedigPaginator = aPaginator;
    }
    
	//###########################################################################################//
	//#										  EVENT METHODS									   	#//
	//###########################################################################################//

	private void addButtnIniListener()
	{
		this.pageButtonIni.addClickListener(new Button.ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getFistPage());
				} 
				catch (Throwable e) 
				{

				}
			}
		});
	}
	
	private void addButtnNextListener()
	{
		this.pageButtonNext.addClickListener(new Button.ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getNextPage());
				} 
				catch (Throwable e) 
				{

				}
			}
		});
	}
	
	private void addButtnPreviousListener()
	{
		this.pageButtonPrevious.addClickListener(new Button.ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				try 
				{
					refreshVisibleContent(getPreviousPage());
				} 
				catch (Throwable e) 
				{

				}
			}
		});
	}
	
	private void addButtnEndListener()
	{
		this.pageButtonEnd.addClickListener(new Button.ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getLastPage());
				} 
				catch (Throwable e) 
				{

				}
			}
		});
	}


	/**
	 * 
	 * @return
	 */
	public ImedigPaginatorFilter getImedigPaginatorFilter()
	{
		return (ImedigPaginatorFilter)imedigPaginator;
	}
	
	public ImedigPaginator getImedigPaginator()
	{
		return imedigPaginator;
	}

	//###########################################################################################//
	//#										  OTHER METHODS									   	#//
	//###########################################################################################//

	/**
	 * 
	 * @throws Throwable
	 */
	@Override
	public void initializeTable()
	{
		super.initializeTable();
    
    	lbCurrPage =  new Label();
    	lbCurrPage.setValue(" ");

    	lbPageCantOf = new Label(getContext().getString( "tableImedig.lbPageCantOf"));
    	
    	lbTotalPage =  new Label();
    	lbTotalPage.setValue(" ");
    	
    	pageButtonIni = new Button();
		pageButtonIni.setStyleName( "borderless icon-on-top" );
		pageButtonIni.setDescription(getContext().getString( "tableImedig.pageButtonIni"));
		pageButtonIni.setIcon( new ThemeResource( "images/first.png" ) );
		pageButtonIni.setEnabled(false);
		addButtnIniListener();
		
    	pageButtonPrevious = new Button();
		pageButtonPrevious.setStyleName( "borderless icon-on-top" );
		pageButtonPrevious.setDescription(getContext().getString( "tableImedig.pageButtonPrevious"));
		pageButtonPrevious.setIcon( new ThemeResource( "images/left.png" ) );
		pageButtonPrevious.setEnabled(false);
		addButtnPreviousListener();
		
    	pageButtonNext = new Button();
		pageButtonNext.setStyleName( "borderless icon-on-top" );
		pageButtonNext.setDescription(getContext().getString( "tableImedig.pageButtonNext"));
		pageButtonNext.setIcon( new ThemeResource( "images/right.png" ) );
		pageButtonNext.setEnabled(false);
		addButtnNextListener();
		
		pageButtonEnd = new Button();
		pageButtonEnd.setStyleName( "borderless icon-on-top" );
		pageButtonEnd.setDescription(getContext().getString( "tableImedig.pageButtonEnd"));
		pageButtonEnd.setIcon( new ThemeResource( "images/last.png" ) );
		pageButtonEnd.setEnabled(false);
		addButtnEndListener();
		
		tablePageOppContainerBack = new HorizontalLayout();
    	tablePageOppContainerBack.addComponent(pageButtonIni);
    	tablePageOppContainerBack.addComponent(pageButtonPrevious);
    	tablePageOppContainerBack.setVisible(false);
    	
		tablePageOppContainerInfo = new HorizontalLayout();
    	tablePageOppContainerInfo.setSpacing( true );
    	tablePageOppContainerInfo.addComponent(lbCurrPage);
    	tablePageOppContainerInfo.addComponent(lbPageCantOf);
    	tablePageOppContainerInfo.addComponent(lbTotalPage);
    	
		tablePageOppContainerNext = new HorizontalLayout();
    	tablePageOppContainerNext.addComponent(pageButtonNext);
    	tablePageOppContainerNext.addComponent(pageButtonEnd);
    	tablePageOppContainerNext.setVisible(false);
    	
    	tablePageOppContainer = new HorizontalLayout();
    	tablePageOppContainer.setSpacing( true );
    	tablePageOppContainer.addComponent(tablePageOppContainerBack);
    	tablePageOppContainer.addComponent(tablePageOppContainerInfo);
    	tablePageOppContainer.addComponent(tablePageOppContainerNext);	
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	private void updateNavButtons()
	{
		//actualizar botonera de paginacion
		this.pageButtonIni.setEnabled(this.isFirstPageEnable());
		this.pageButtonPrevious.setEnabled(this.isPreviousPageEnable());
		this.pageButtonEnd.setEnabled(this.isLastPageEnable());
		this.pageButtonNext.setEnabled(this.isNextPageEnable());
		
		
		if(this.getCurrPag() > 0)
		{
			this.lbCurrPage.setValue(Integer.toString( this.getCurrPag()));
		}
		
		if(this.getTotalPag() > 0)
		{
			this.lbTotalPage.setValue(Integer.toString( this.getTotalPag()));
		}
		
		if(this.getTotalPag() > 1)
		{
			this.tablePageOppContainerBack.setVisible(true);
			this.tablePageOppContainerInfo.setVisible(true);
			this.tablePageOppContainerNext.setVisible(true);
		}
		else
		{
			this.tablePageOppContainerBack.setVisible(false);
			this.tablePageOppContainerInfo.setVisible(false);
			this.tablePageOppContainerNext.setVisible(false);
		}
	}
	
	//###########################################################################################//
	//#									 INHERIT METHODS 									   	#//
	//###########################################################################################//
	
	/**
	 * 
	 * 
	 */
	public void refreshVisibleContent(List<ImedigDto> listadoElementos)
	{
		try
		{
			List<GenericVto> listaElemsTabla = null;
			listaElemsTabla = this.loadTableValues(listadoElementos);   //Parse values from DB to clear id references fields
			GenericControlerVto elemItemControler = this.objVto.getControlerVto(context);
			
			// connect data source
	        this.fillTableContainer(listadoElementos, elemItemControler.getVtoIdFiledName(), listaElemsTabla);
		}
		catch (Throwable e) 
    	{
			e.printStackTrace();
		}

	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	@Override
	public List<ImedigDto>  getPageNum(Integer pagNum) throws Throwable {
		return null;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getFistPage() throws Throwable 
	{
		List<ImedigDto>  result = imedigPaginator.getFistPage();
		this.updateNavButtons();
		
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
		List<ImedigDto>  result = imedigPaginator.getLastPage();
		this.updateNavButtons();
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getCurrPage() throws Throwable 
	{
		List<ImedigDto>  result = imedigPaginator.getCurrPage();
		this.updateNavButtons();
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto>  getNextPage() throws Throwable
	{
		List<ImedigDto>  result = imedigPaginator.getNextPage();
		this.updateNavButtons();
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<ImedigDto> getPreviousPage() throws Throwable 
	{
		List<ImedigDto> result = imedigPaginator.getPreviousPage();
		this.updateNavButtons();
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isFirstPageEnable() 
	{
		return this.imedigPaginator.isFirstPageEnable();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isLastPageEnable()
	{
		return this.imedigPaginator.isLastPageEnable();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isNextPageEnable() 
	{
		return this.imedigPaginator.isNextPageEnable();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isPreviousPageEnable()
	{
		return this.imedigPaginator.isPreviousPageEnable();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public int getCurrPag() 
	{
		return this.imedigPaginator.getCurrPag();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public int getTotalPag() 
	{
		return this.imedigPaginator.getTotalPag();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public HorizontalLayout getTablePageOppContainer() {
		return tablePageOppContainer;
	}
	/**
	 * @param tablePageOppContainer
	 */
	public void setTablePageOppContainer(HorizontalLayout tablePageOppContainer) {
		this.tablePageOppContainer = tablePageOppContainer;
	}

//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------

}
