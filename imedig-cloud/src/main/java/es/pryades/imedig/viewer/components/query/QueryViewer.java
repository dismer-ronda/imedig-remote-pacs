package es.pryades.imedig.viewer.components.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.components.PageTable;
import es.pryades.imedig.viewer.datas.QueryTableItem;

public class QueryViewer extends VerticalLayout implements PageTable.PaginatorListener{

	private static final Logger LOG = Logger.getLogger( QueryViewer.class );
	private static final long serialVersionUID = 2949836327715684727L;
	
	private TextField fieldName;
	private TextField filedId;
	private ComboBox type;
	private ComboBox bydate;
	private Table tableEstudies;
	private BeanItemContainer<QueryTableItem> container;

	private PageTable paginator;
	private QueryTableModel model;
	private final User user;
	
	private Button btnQuery;
	
	private final ImedigContext context;
	
	public static final Integer PAGE_SIZE = 25;
	
	public QueryViewer(ImedigContext context, User user){
		this.context = context;
		this.user = user;
		
		setSizeFull();
		setMargin( true );
		setSpacing( true );
		
		buildFiltros();
		buildTable();
		btnQuery.click();
	}

	private void buildFiltros() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);

		layout.addComponent(fieldName = new TextField(context.getString("QueryForm.PatientName")));
		fieldName.setWidth("150px");
		layout.addComponent(filedId = new TextField(context.getString("QueryForm.PatientId")));
		filedId.setWidth("150px");
		layout.addComponent(type = new ComboBox(context.getString("QueryForm.Modality")));
		type.setNewItemsAllowed(false);
		type.setFilteringMode(FilteringMode.CONTAINS);
		type.setWidth("100px");
		initTypeList();
		layout.addComponent(bydate = new ComboBox(context.getString("QueryForm.StudyDate")));
		bydate.setNewItemsAllowed(false);
		bydate.setFilteringMode(FilteringMode.CONTAINS);
		initByDateList();
		Component buttons = filterButtons();
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);
		addComponent(layout);
		setComponentAlignment(layout, Alignment.TOP_CENTER);
	}

	private void initTypeList() {
		String modList = context.getString("QueryForm.Modalities");

		String modalities[] = modList.split(",");

		for (String modality : modalities) {
			type.addItem(modality);
		}
	}

	private void initByDateList() {
		bydate.addItems(1, 2, 3, 4, 5);
		bydate.setItemCaption(1, context.getString("QueryForm.Today"));
		bydate.setItemCaption(2, context.getString("QueryForm.Yesterday"));
		bydate.setItemCaption(3, context.getString("QueryForm.LastWeek"));
		bydate.setItemCaption(4, context.getString("QueryForm.LastMonth"));
		bydate.setItemCaption(5, context.getString("QueryForm.LastYear"));
		bydate.setValue(user.getQuery());
	}

	private Component filterButtons() {
		btnQuery = new Button();
		btnQuery.setDescription(context.getString("QueryForm.QueryTip"));
		btnQuery.setImmediate(true);
		btnQuery.setIcon(FontAwesome.SEARCH);
		btnQuery.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		//btnQuery.addStyleName(ValoTheme.BUTTON_LARGE);
		btnQuery.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4888989195136715235L;

			@Override
			public void buttonClick(ClickEvent event) {
				onQuery();
			}
		});

		Button btnOpen = new Button();
		btnOpen.setDescription(context.getString("QueryForm.Open"));
		btnOpen.setImmediate(true);
		btnOpen.setIcon(FontAwesome.FOLDER_OPEN);
		btnOpen.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btnOpen.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -585302886377865803L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction( new OpenStudies(this, getSeletedStudies()));
			}
		});

		HorizontalLayout layout = new HorizontalLayout(btnQuery, btnOpen);
		layout.setSpacing(true);
		return layout;
	}

	public void onQuery() {
		paginator.setPage(0);
		paginator.setSize( PAGE_SIZE );

		model = new QueryTableModel(PAGE_SIZE);

		model.setPatientName("*" + fieldName.getValue() + "*");
		model.setPatientId(filedId.getValue());
		model.setReferringPhysicianName(user.getFilter());
		model.setStudyDate(convertDate((Integer) bydate.getValue()));
		model.setModalitiesInStudy((String) type.getValue());

		int size = model.doQuery();

		paginator.resetTotal(size);
		refreshTable(model.getCurrentPage());
	}

	private String convertDate(Integer index) {
		if (index == null)
			return "";

		switch (index) {
		case 1:
			return Utils.getTodayDate("yyyyMMdd");

		case 2:
			return Utils.getYesterdayDate("yyyyMMdd");

		case 3:
			return Utils.getLastWeekDate("yyyyMMdd") + "-";

		case 4:
			return Utils.getLastMonthDate("yyyyMMdd") + "-";

		case 5:
			return Utils.getLastYearDate("yyyyMMdd") + "-";

		default:
			return "";
		}
	}

	private void buildTable() {
		paginator = new PageTable(context);
		paginator.setListener( this );
		container = new BeanItemContainer<>(QueryTableItem.class);
		tableEstudies = new Table();
		tableEstudies.setContainerDataSource(container);
		tableEstudies.setSizeFull();
		tableEstudies.setVisibleColumns(new String[] { "selected", "studyReport", "studyDate", "modality", "patientId", "patientName","patientAge", "referringPhysicianName" });
		tableEstudies.setColumnHeaders(new String[] { "", "", 
				context.getString("QueryForm.StudyDate"),
				context.getString("QueryForm.Modality"), context.getString("QueryForm.PatientId"),
				context.getString("QueryForm.PatientName"), context.getString("QueryForm.Age"),
				context.getString("QueryForm.Referrer") });
		tableEstudies.setColumnAlignments( Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT);
		tableEstudies.setSelectable(true);
		tableEstudies.setMultiSelect(false);
		tableEstudies.setSortEnabled(false);
		tableEstudies.addItemClickListener( new ItemClickListener() {

			private static final long serialVersionUID = 7774918893866773038L;

			@Override
			public void itemClick( ItemClickEvent event )
			{
				if (event.isDoubleClick()){
					QueryTableItem item = (QueryTableItem)event.getItemId();
					
					if (item != null){
						context.sendAction(new OpenStudies(this, Arrays.asList(item.getStudy().getStudyInstanceUID())));
					}
				}
				
			}
		} );

		addComponents(tableEstudies, paginator);
		setExpandRatio(tableEstudies, 1.0f);
	}

	@Override
	public void onFirst() {
		model.firstPage();
		refreshTable(model.getCurrentPage());
	}

	@Override
	public void onPrevious() {
		model.previousPage();
		refreshTable(model.getCurrentPage());
	}

	@Override
	public void onNext() {
		model.nextPage();
		refreshTable(model.getCurrentPage());
	}

	@Override
	public void onLast() {
		model.lastPage();
		refreshTable(model.getCurrentPage());
	}
	
	private void refreshTable(List<Study> studies){
		container.removeAllItems();
		
		for (Study study : studies) {
			QueryTableItem item = new QueryTableItem(context, study);
			
			DetalleInforme informe = getStudyReport( study );
			
			if (informe != null){
				item.setStudyReport( AppUtils.getImgEstado( informe.getEstado() ) );
			}else{
				item.setStudyReport( getEmptyLabel() );
			}
			
			container.addItem(item);
		}
	}
	
	private static Component getEmptyLabel(){
		Label label = new Label();
		label.setWidth( "-1px" );
		
		return label;
	}
	
	private DetalleInforme getStudyReport(Study study){
		DetallesInformesManager informesManager = IOCManager.getInstanceOf( DetallesInformesManager.class );
		
		InformeQuery query = new InformeQuery();
		query.setEstudio_uid( study.getStudyInstanceUID() );
		
		try{
			//Siempre retorna listado ordenado por fecha descendentemente
			List<DetalleInforme> informes = informesManager.getRows( context, query );
			if (!informes.isEmpty()) return informes.get( 0 );
			
		}catch ( Throwable e ){
			LOG.error( "Error", e);
		}
		
		return null;
	}
	
	private List<String> getSeletedStudies(){
		List<String> result = new ArrayList<>();
		
		for (QueryTableItem item : container.getItemIds()) {
			if (!item.getSelected().getValue()) continue;
			
			result.add(item.getStudy().getStudyInstanceUID());
		}
		
		if (result.isEmpty()){
			QueryTableItem item = (QueryTableItem)tableEstudies.getValue();
			if (item != null){
				result.add( item.getStudy().getStudyInstanceUID() );
			}
		}
		
		return result;
	}
}
