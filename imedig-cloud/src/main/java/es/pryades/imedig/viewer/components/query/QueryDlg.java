package es.pryades.imedig.viewer.components.query;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.components.PageTable;
import es.pryades.imedig.viewer.datas.QueryTableItem;
import lombok.Setter;

public class QueryDlg extends Window implements PageTable.PaginatorListener{

	private VerticalLayout content;

	private TextField fieldName;
	private TextField filedId;
	private ComboBox type;
	private ComboBox bydate;
	private Table tableEstudies;
	private BeanItemContainer<QueryTableItem> container;

	private PageTable paginator;
	private QueryTableModel model;
	private User user;
	@Setter
	private ListenerAction listener; 
	
	private Button btnQuery;
	
	private final ImedigContext context;

	public QueryDlg(ImedigContext context, User user) {
		super(context.getString("QueryForm.Title"));
		
		this.context = context;
		this.user = user;

		setModal(true);
		setResizable(false);
		//setClosable(false);
		setWidth("900px");
		setHeight("480px");
		content = new VerticalLayout();
		content.setSizeFull();
		content.setSpacing(true);
		content.setMargin(true);

		setContent(content);

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
		// TODO agregar los botones y sus funcionalidades
		content.addComponent(layout);
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
		//btnOpen.addStyleName(ValoTheme.BUTTON_LARGE);
		btnOpen.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				close();
				if (listener == null) return;
				listener.doAction(new OpenStudies(this, getSeletedStudies()));
			}
		});

		HorizontalLayout layout = new HorizontalLayout(btnQuery, btnOpen);
		layout.setSpacing(true);
		return layout;
	}

	public void onQuery() {
		paginator.setPage(0);

		model = new QueryTableModel(10);

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
		content.addComponent(paginator = new PageTable(context));
		container = new BeanItemContainer<>(QueryTableItem.class);
		tableEstudies = new Table();
		tableEstudies.setContainerDataSource(container);
		tableEstudies.setSizeFull();
		tableEstudies.setVisibleColumns(new String[] { "selected", "studyDate", "modality", "patientId", "patientName",
				"patientAge", "referringPhysicianName" });
		tableEstudies.setColumnHeaders(new String[] { "", context.getString("QueryForm.StudyDate"),
				context.getString("QueryForm.Modality"), context.getString("QueryForm.PatientId"),
				context.getString("QueryForm.PatientName"), context.getString("QueryForm.Age"),
				context.getString("QueryForm.Referrer") });
		tableEstudies.setSelectable(true);
		tableEstudies.setMultiSelect(false);
		tableEstudies.setSortEnabled(false);

		content.addComponent(tableEstudies);
		content.setExpandRatio(tableEstudies, 1.0f);
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
			container.addItem(new QueryTableItem(context, study));
		}
	}
	
	private List<String> getSeletedStudies(){
		List<String> result = new ArrayList<>();
		
		for (QueryTableItem item : container.getItemIds()) {
			if (!item.getSelected().getValue()) continue;
			
			result.add(item.getStudy().getStudyInstanceUID());
		}
		
		return result;
	}
}
