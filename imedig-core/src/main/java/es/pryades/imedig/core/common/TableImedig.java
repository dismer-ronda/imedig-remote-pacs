package es.pryades.imedig.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ColumnAccessor;
import es.pryades.imedig.cloud.common.ObjectMetaInfo;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class TableImedig extends VerticalLayout
{

	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//
	
	private static final Logger LOG = Logger.getLogger( TableImedig.class ); 
	
	// INI -- Vaading Components
	protected Table table = new Table();
	
	protected HorizontalLayout tableContainer;
	// FIN -- Vaading Components
	
	protected String tableTitle="";
	protected List<String> tableHeaders;
	protected List<String> visibleCols;	
	protected Map<String, ColumnAccessor> accessors;
	
	protected ObjectMetaInfo tableObjectMetaInfo;
	protected GenericVto  objVto;
	protected VtoFieldRef tableVtoDataRef;
	
	protected HashMap<Item,Object> rawTableContent;
	protected HashMap<String,String> tableHeadersNames;
	
	@Getter protected ImedigContext context;

	protected List objectListColection;
	
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//
	
	/**
	 * 
	 * @param tableObjectType
	 * @param objVto
	 * @param tableVtoDataRef
	 * @param ctx
	 * @param resource
	 * @throws Throwable
	 */
	public TableImedig(Class<?> tableObjectType, GenericVto objVto, VtoFieldRef tableVtoDataRef, ImedigContext ctx )
    {
    	this.tableObjectMetaInfo = new ObjectMetaInfo(tableObjectType);
    	this.objVto = objVto; 
    	this.rawTableContent = new HashMap<Item, Object>();
    	this.tableVtoDataRef = tableVtoDataRef;
    	this.context = ctx;
	
   		this.tableHeaders =  tableObjectMetaInfo.getAttributesNamesArratString();
    }
    
    /**
     * get
     * @return
     */
	public String getTableTitle() {
		return tableTitle;
	}

	/**
	 * set
	 * @param tableTitle
	 */
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	   	table.setCaption(this.tableTitle);
	}

//------------------------------------------------------------------------------------------------------------	
	/**
	 * get
	 * @return
	 */
	public List<String> getTableHeaders() {
		return tableHeaders;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public ObjectMetaInfo getTableObjectMetaInfo() {
		return tableObjectMetaInfo;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public Table getTable() {
		return table;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public List<String> getVisibleCols() {
		return visibleCols;
	}

	/**
	 * 
	 * @param visibleCols
	 */
	public void setVisibleCols(List<String> visibles) {
		if (visibleCols == null){
			visibleCols = new ArrayList<String>();
		}
		
		if (accessors == null ){
			accessors = new HashMap<String, ColumnAccessor>();
		}
		visibleCols.clear();
		accessors.clear();
		
		for (String col : visibles) {
			visibleCols.add( col );
			ColumnAccessor column = new ColumnAccessor();
			column.setColumn(col);
			accessors.put(col, column);
		}
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public HashMap<Item, Object> getRawTableContent() {
		return rawTableContent;
	}

	/**
	 * set
	 * @param tableContent
	 */
	public void setRawTableContent(HashMap<Item, Object> rawTableContent) {
		this.rawTableContent = rawTableContent;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public HashMap<String, String> getTableHeadersNames() {
		return tableHeadersNames;
	}

	/**
	 * set
	 * @param tableHeadersNames
	 */
	public void setTableHeadersNames(HashMap<String, String> tableHeadersNames) {
		this.tableHeadersNames = tableHeadersNames;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public List getObjectListColection()
	{
		return objectListColection;
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void initializeTable()
	{
		this.setSizeFull();
    	
		// Assigning table title
		//this.table.setCaption(tableTitle);

        // size
		this.table.setWidth("100%");
		this.table.setHeight("100%");

        // selectable
        this.table.setSelectable(true);
        this.table.setImmediate(true); // react at once when something is selected
        this.table.setMultiSelect(false); // by default multiselect option is unavaible

        // turn on column reordering and collapsing
        this.table.setColumnReorderingAllowed(true);
        this.table.setColumnCollapsingAllowed(true);
        
        this.tableContainer = new HorizontalLayout();
        this.tableContainer.setWidth("100%");
        this.tableContainer.setHeight("100%");
        this.tableContainer.addComponent(table);
        this.tableContainer.setExpandRatio(table, 1.0f);
        
    	this.addComponent(tableContainer);
    	this.setExpandRatio(tableContainer, 1.0f);
    	
    	for (String keyCol : visibleCols) 
    	{
			try
			{
	    		Class clazz = accessors.get( keyCol ).getColumnClass( tableObjectMetaInfo.getObjClass() );
	    		
	    		table.addContainerProperty( keyCol, clazz, null, tableHeadersNames.get(keyCol), null, Align.LEFT );
			}
			catch ( Throwable e )
			{
				e.printStackTrace();
			}
    		
		}
	}

//------------------------------------------------------------------------------------------------------------	
	/**
	 * 
	 * @throws Throwable
	 */
	protected void fillTableContainer(List objectElemsList, String idFieldName, List tableElemsList) throws Throwable
	{
		this.clearTableData();
		objectListColection = objectElemsList;
		
		if(tableElemsList.size() > 0){
			
			int i=0;
			
	        for (Object rowObject: tableElemsList) 
	        {
	        	if(rowObject.getClass().equals(this.tableObjectMetaInfo.getObjClass()))
	        	{
	        		ColumnAccessor accessor = new ColumnAccessor();
	        		accessor.setColumn(idFieldName);
	        		Item item = table.addItem(accessor.getColumnObject(rowObject));
	        		this.rawTableContent.put(item, objectElemsList.get(i));
	        		
	        		for (String keyCol : visibleCols) {
	        			item.getItemProperty(keyCol).setValue(accessors.get(keyCol).getColumnObject(rowObject));
					}

	        		 /*ObjectMetaInfo rowObjectMetaInfo = new ObjectMetaInfo(rowObject.getClass());
	     			 rowObjectMetaInfo.addObjectvaluesToMetaInfo(rowObject);
	     			 
	     			 Item item = this.table.addItem(rowObjectMetaInfo.getAttributesValues().get(idFieldName));
	     			 this.rawTableContent.put(item, objectElemsList.get(i));
	     			 
	     			 for(String headerItem: this.tableHeaders){
	     				if(this.visibleCols.contains(headerItem))
	     				{
	     					
	        				 item.getItemProperty(tableHeadersNames.get(headerItem)).setValue(rowObjectMetaInfo.getAttributesValues().get(headerItem));
	        			}	 
	        		 }*/
	        	}else{
	        		throw new Exception( "TableImedig Initialization error! Expected:" + this.tableObjectMetaInfo.getObjClass().getName() +
	        												  ", received: " + rowObject.getClass().getName() );
	        	}
	        	
	        	i++;
	        }
		}
    }
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @throws Throwable
	 */
	public Object getRowValue(Integer rowId) throws Throwable
	{
		// instance the table objects Type
		Object rowObject = tableObjectMetaInfo.createObjectFromMetaInfo();
		
		// construct a new MetaObjectInfo from table objects Type 		
		ObjectMetaInfo rowObjectMetaInfo = new ObjectMetaInfo(tableObjectMetaInfo.getObjClass());
		
		// retrieve table row at index rowId
		Item selectedRowItem = this.table.getItem(rowId);
		
		// update metaInfoValues from selectedRowItem 
		for(String headerName: this.tableHeaders)
		{
			Property property = selectedRowItem.getItemProperty(headerName);
			
			if ( property != null )
				rowObjectMetaInfo.getAttributesValues().put(headerName, property.getValue());
		}
		
		// update rowObject from rowObjectMetaInfo
		rowObjectMetaInfo.updateObjectValuesFromMetaInfo(rowObject);

		return this.rawTableContent.get(selectedRowItem);
    }
	
//------------------------------------------------------------------------------------------------------------	
	/**
	 * 
	 * 
	 */
	public void clearTableData()
	{
		this.table.removeAllItems();
		this.table.markAsDirtyRecursive();
		
		if(this.objectListColection != null)
			this.objectListColection.clear();
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * Carga la lista completa de elementos de tipo this.tableVtoDataRef.FieldQuery.class de BBDD
	 * 
	 * @return
	 */
	private List retriveValuesFromInstanceDB() throws Throwable
	{
		List listaElementos = null;

		if(this.tableVtoDataRef != null || this.tableVtoDataRef.getFieldManagerImp() != null)
		{
			ImedigManager queryMan = (ImedigManager) IOCManager.getInstanceOf( this.tableVtoDataRef.getFieldManagerImp() );

			ImedigDto objQuery = (ImedigDto)this.tableVtoDataRef.getFieldQuery();
			listaElementos = queryMan.getRows(this.context, objQuery);
			return listaElementos;
		}
		else
		{
			throw new Exception("TableVtoDataRef tableVtoDataRef or tableVtoDataRef.FieldManagerImp cant not be null");
		}
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * Itera sobre la lista de elementos y crea una nueva lista de elementos de tipo GenericVto
	 * mapendando los valores de los campos necesarios 
	 * 
	 * @return
	 */
	protected List<GenericVto> loadTableValues(List<ImedigDto> objListElems) throws Throwable
	{
		ArrayList<GenericVto> tableValues = new ArrayList<GenericVto>();

		for(ImedigDto elemItem: objListElems)
		{
			GenericControlerVto elemItemControler = this.objVto.getControlerVto(context);
			tableValues.add((GenericVto) elemItemControler.generateVtoFromDto(elemItem));
		}

		return tableValues;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void refreshVisibleContent()
	{
		try
		{
			List listadoElementos = null;	
			List<GenericVto> listaElemsTabla = null;
			listadoElementos =  this.retriveValuesFromInstanceDB();     //Load values from DB 
			listaElemsTabla = this.loadTableValues(listadoElementos);   //Parse values from DB to clear id references fields
			GenericControlerVto elemItemControler = this.objVto.getControlerVto(context);
			
			// connect data source
	        this.fillTableContainer(listadoElementos, elemItemControler.getVtoIdFiledName(), listaElemsTabla);
		}
		catch (Throwable e) 
    	{
			LOG.error("Error cargando la lista de elementos");
		}

	}
 
	public void focus()
	{
		table.focus();
	}
	
	public int getTotalRows()
	{
		LOG.info( "getTotalRows " + table.size() );
		return table.size();
	}
}
