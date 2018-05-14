package es.pryades.imedig.cloud.common;

import com.vaadin.ui.PopupView;

public class SelectDatePopupView extends PopupView {

	private static final long serialVersionUID = 6123560679021048232L;
	
	private SelectDateLayout dateLayout;
	
	public SelectDatePopupView(String small, SelectDateLayout aDateLayout) {
		super(small, aDateLayout);
		
		dateLayout = aDateLayout;
		
		setHideOnMouseOut(false);
	}

	public SelectDateLayout getSelectDateLayout(){
		return dateLayout;
	}
}
