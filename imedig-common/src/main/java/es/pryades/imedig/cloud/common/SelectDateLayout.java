package es.pryades.imedig.cloud.common;

import java.util.Date;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SelectDateLayout extends VerticalLayout {

	private static final long serialVersionUID = 5012458457809826010L;

	@Setter
	private PopupView popupView;
	private DateField dateField;
	
	@Getter @Setter
	private Date date;
	private ResourceBundle resource;
	private String horario;
	
	public SelectDateLayout(ResourceBundle aResource, String aHorario){
		
		resource 	= aResource;
		horario		= aHorario;
		
		setSpacing(true);
		//setMargin(true);
		setStyleName("selectedate");
		setWidth("120px");
		Component component = buildSelectedComponent();
		addComponent(component);
		setComponentAlignment(component, Alignment.TOP_CENTER);
		
		component = buildButtonComponent();
		addComponent(component);
		setComponentAlignment(component, Alignment.BOTTOM_CENTER);
	}
	
	public void setInitDate(Date aDate){
		dateField.setValue(aDate);
		date = null;
	}
	
	private Component buildSelectedComponent(){
		HorizontalLayout layout = new HorizontalLayout();
		
		dateField = new PopupDateField(AppUtils.getString( resource, "words.select.date"));
		dateField.setResolution( Resolution.DAY );
		dateField.setDateFormat( "dd/MM/yyyy" );
		dateField.setWidth("110px");
		dateField.setParseErrorMessage(AppUtils.getString( resource, "error.date.format"));
		dateField.setImmediate(true);
		dateField.addValueChangeListener( new ValueChangeListener()  {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				Date today = Utils.getCurrentDate(horario);
				
				Date select = (Date)dateField.getValue();
				if (select == null){
					return;
				}
				/*if (select.getTime()>today.getTime()){
					dateField.setValue(today);
					getParent().getWindow().showNotification(AppUtils.getResource( resource, "error.date.after.today"),Notification.TYPE_ERROR_MESSAGE);
				}*/
			}
		});
		layout.addComponent(dateField);
		
		return layout;
	}
	
	private Component buildButtonComponent(){
		HorizontalLayout layout = new HorizontalLayout();

		layout.setSpacing( true );
		layout.setWidth("100%");
		
		Button button = new Button();
		button.setStyleName( "miniborderless" );
		button.setIcon( new ThemeResource( "images/accept.png" ) );
		button.setDescription(AppUtils.getString( resource, "words.ok"));
		button.addClickListener( new Button.ClickListener() {

			private static final long serialVersionUID = 5012459568910937121L;

			@Override
			public void buttonClick(ClickEvent event) {
				setDate((Date)dateField.getValue());
				popupView.setPopupVisible( false );
			}
		});
		layout.addComponent(button);
		layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		
		button = new Button();
		button.setStyleName( "miniborderless" );
		button.setIcon( new ThemeResource( "images/cancel.png" ) );
		button.setDescription(AppUtils.getString( resource, "words.cancel"));
		button.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -5012459568910937121L;

			@Override
			public void buttonClick(ClickEvent event) {
				setDate(null);
				popupView.setPopupVisible(false);
			}
		});
		layout.addComponent(button);
		layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		
		return layout;
	}
}
