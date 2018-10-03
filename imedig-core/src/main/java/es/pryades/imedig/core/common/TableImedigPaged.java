package es.pryades.imedig.core.common;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;

/**
 * 
 * @author Dismer Ronda
 *
 */
public class TableImedigPaged extends TableImedig implements ImedigPaginator
{
	private static final long serialVersionUID = 499034172926866445L;

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

//	protected HorizontalLayout tablePageOppContainerBack;
//	protected HorizontalLayout tablePageOppContainerNext;
//	protected HorizontalLayout tablePageOppContainerInfo;
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
			private static final long serialVersionUID = 5352879870171194031L;

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
			private static final long serialVersionUID = 4359193104762728586L;

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
			private static final long serialVersionUID = 8126241051042111504L;

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
			private static final long serialVersionUID = -8041453546296309988L;

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

    	lbPageCantOf = new Label(getContext().getString( "/"));
    	
    	lbTotalPage =  new Label();
    	lbTotalPage.setValue(" ");
    	
    	pageButtonIni = getButton("tableImedig.pageButtonIni", FontAwesome.ANGLE_DOUBLE_LEFT);
		pageButtonIni.setEnabled(false);
		addButtnIniListener();
		
    	pageButtonPrevious = getButton("tableImedig.pageButtonPrevious", FontAwesome.ANGLE_LEFT);
		pageButtonPrevious.setEnabled(false);
		addButtnPreviousListener();
		
    	pageButtonNext = getButton("tableImedig.pageButtonNext", FontAwesome.ANGLE_RIGHT);
		pageButtonNext.setEnabled(false);
		addButtnNextListener();
		
		pageButtonEnd = getButton("tableImedig.pageButtonEnd", FontAwesome.ANGLE_DOUBLE_RIGHT);
		pageButtonEnd.setEnabled(false);
		addButtnEndListener();
		
//		tablePageOppContainerBack = new HorizontalLayout();
//		tablePageOppContainerBack.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
//		tablePageOppContainerBack.addComponents( pageButtonIni, pageButtonPrevious );
//    	tablePageOppContainerBack.setVisible(false);
    	
//		tablePageOppContainerInfo = new HorizontalLayout();
//		tablePageOppContainerInfo.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
//		tablePageOppContainerInfo.addComponents( lbCurrPage, lbPageCantOf, lbTotalPage );
    	//tablePageOppContainerInfo.setSpacing( true );
    	
//		tablePageOppContainerNext = new HorizontalLayout();
//		tablePageOppContainerNext.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
//		tablePageOppContainerNext.addComponents( pageButtonNext, pageButtonEnd );
//    	tablePageOppContainerNext.setVisible(false);
    	
    	tablePageOppContainer = new HorizontalLayout();
    	tablePageOppContainer.setWidthUndefined();
    	tablePageOppContainer.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
    	tablePageOppContainer.addComponents( pageButtonIni, pageButtonPrevious, lbCurrPage, lbPageCantOf, lbTotalPage, pageButtonNext, pageButtonEnd );
    	tablePageOppContainer.setVisible(false);
	}
	
	private Button getButton(String tooltip, FontAwesome icon) {

		Button btn = new Button(icon);
		btn.setDescription(context.getString(tooltip));
		btn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btn.addStyleName(ValoTheme.BUTTON_BORDERLESS);

		return btn;
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
//			this.tablePageOppContainerBack.setVisible(true);
//			this.tablePageOppContainerInfo.setVisible(true);
//			this.tablePageOppContainerNext.setVisible(true);
			tablePageOppContainer.setVisible( true );
		}
		else
		{
//			this.tablePageOppContainerBack.setVisible(false);
//			this.tablePageOppContainerInfo.setVisible(false);
//			this.tablePageOppContainerNext.setVisible(false);
			tablePageOppContainer.setVisible( false );
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
