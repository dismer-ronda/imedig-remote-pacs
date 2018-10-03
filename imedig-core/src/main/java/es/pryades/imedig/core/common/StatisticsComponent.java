package es.pryades.imedig.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class StatisticsComponent extends VerticalLayout {
	
	private static final long serialVersionUID = -6491945184152451784L;
	
	private List<Object> rows = new ArrayList<Object>();
	private Map<Object, String> rowCaptions = new HashMap<Object, String>();

	private List<Object> columns = new ArrayList<Object>();
	private Map<Object, String> columnCaptions = new HashMap<Object, String>();

	private List<Object> variables = new ArrayList<Object>();
	private Map<Object, String> variableCaptions = new HashMap<Object, String>();

	private Map<Object, Map<Object, StatisticsVariable>> statistics = new HashMap<Object, Map<Object, StatisticsVariable>>();

	private Map<Object, Statistics> statisticsOld = new HashMap<Object, Statistics>();
	
	private String caption;
	@Setter
	private boolean inlineCaption = true;

	public StatisticsComponent(String aCaption){
		setWidth("100%");
		setHeight("100%");
		
		caption = aCaption;
	}
	
	public void addRow(Object id, String caption){
		if (!rows.contains(id)){
			rows.add(id);
		}
		
		rowCaptions.put(id, caption);
	}
	
	public void addColumn(Object id, String caption){
		if (!columns.contains(id)){
			columns.add(id);
		}

		columnCaptions.put(id, caption);
	}

	public void clearLabels(){
		rows.clear();
		rowCaptions.clear();
	}
	
	public void addVariable(Object variable, String caption){
		variables.add(variable);
		variableCaptions.put(variable, caption);
	}
	
	public void addStatistics(Object column, Object variable, StatisticsVariable values){
		if (statistics.get(column)==null){
			statistics.put(column, new HashMap<Object, StatisticsVariable>());
		}
		
		statistics.get(column).put(variable, values);
	}
	
	public void clearStatistics(){
		columns.clear();
		columnCaptions.clear();
		statistics.clear();
		variables.clear();
		variableCaptions.clear();
	}
	
	public void clearStatistics(Object id){
		columns.remove(id);
		columnCaptions.remove(id);
		statistics.remove(id);
	}
	
	public void showStatistics(){
		removeAllComponents();
		GridLayout grid = null;
		if (variables.size()>1){
			grid = new GridLayout(columns.size()+1, 2*rows.size()+2);
		}else{
			grid = new GridLayout(columns.size()+1, rows.size()+2);
		}
		grid.setSpacing(false);
		grid.setImmediate(true);
		grid.setWidth("99%");
//		if (columns.size()>1){
//			grid.setWidth("99%");
//		}
		
		grid.setColumnExpandRatio(0, 1.0f);
		for (int i = 0; i < columns.size(); i++) {
			grid.setColumnExpandRatio(i+1, 1.0f);
		}
		//Caption
		Label label = getStatisticLabel(caption);
		grid.addComponent(label, 0, 0, columns.size(), 0);
		grid.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		settingLabels( grid );
		settingStatistics(grid);
		addComponent(grid);
		setComponentAlignment(grid, Alignment.TOP_CENTER);
		grid.markAsDirtyRecursive();
	}
	
	private void settingLabels( GridLayout grid ){
		int row = 2;

		for (Object key : rows) {
			Label label = getStatisticLabel(rowCaptions.get(key));
			grid.addComponent(label, 0, row);
			grid.setComponentAlignment(label, Alignment.TOP_LEFT);
			
			if (!variables.isEmpty() && variables.size() > 1){
				VerticalLayout layout = new VerticalLayout();
				row++;
				for (Object key1 : variables) {
					label = getStatisticLabel(variableCaptions.get(key1)+":");
					layout.addComponent(label);
					layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
				}
				grid.addComponent(layout, 0, row);
			}
			
			row++;
		}
	}
	
	private void settingStatistics( GridLayout grid ){
		int col = 1;
		for (Object colId : columns) {
			Component caption = getCaptionComponent(colId);
			grid.addComponent(caption, col, 1);
			grid.setComponentAlignment(caption, Alignment.TOP_RIGHT);
			
			int row = 2;
			for (Object rowId : rows) {
				
				if (variables.size() > 1){
					row++;
				}

				VerticalLayout layout = new VerticalLayout();
				layout.setWidth("100%");
				for (Object var : variables) {
					Label label = getStatisticLabel(getStatisticValue(colId, rowId, var));
					layout.addComponent( label );
					layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
				}
				grid.addComponent(layout, col, row);
				//grid.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
				row++;
			}
			
			col++;
		}
	}
	
	private Component getCaptionComponent(Object id){
		String caption = getCaptionStat(id);
		
		VerticalLayout inside = new VerticalLayout();
		inside.setWidth("100%");
		if (inlineCaption){
			inside.setHeight("36px");
			Label label = getStatisticLabel(caption);
			inside.addComponent(label);
			inside.setComponentAlignment(label, Alignment.TOP_RIGHT);
		}else{
			String[] values = caption.split(" ");
			for (String s : values) {
				Label label = getStatisticLabel(s);
				label.setWidth("-1px");
				inside.addComponent(label);
				inside.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
			}
		}
		
		return inside;
	}
	
	private String getStatisticValue(Object col, Object row, Object var ){
		if (statistics.get(col) == null){
			return "";
		}
		
		if (statistics.get(col).get(var) == null){
			return "";
		}
		
		String value = statistics.get(col).get(var).getValue(row);
		if (value == null){
			return "";
		}
		
		return value;
	}
	
/*	private String getStatisticValue(Object statId, Object id ){
		Statistics stat = statisticsOld.get(statId);
		
		if (stat == null){
			return "";
		}
		
		String value = stat.getStatistics().get(id);
		if (value == null){
			return "";
		}
		
		return value;
	}*/
	
	private String getCaptionStat(Object colId ){
		String caption = columnCaptions.get(colId);
		
		if (caption == null){
			return "";
		}
		
		return caption;
	}
	/*
		statisticsGridLayout.addComponent(label, COL_LABEL, ROW_TOTAL);*/
	
	private Label getStatisticLabel(String caption){
		Label label = new Label(caption);
		label.setStyleName("statisticlabel");
		label.setWidth("-1px");
		
		return label;
	}
	
	private Label getStatisticVariable(String aCaption){
		String caption = aCaption;
		if (caption==null){
			caption = "";
		}
		
		Label label = new Label(caption);
		label.setStyleName("statisticvalue");
		label.setWidth("-1px");
		
		return label;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private class Statistics{
		private String caption;
		private Map<Object, String> statistics;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private class StatisticsColumn{
		private String caption;
		private Map<Object, String> statistics;
	}
}
