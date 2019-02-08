package es.pryades.imedig.cloud.common;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;

public class FiltrerAddSelect extends CustomField<Object>
{
	private static final long serialVersionUID = 8119967193870518018L;

	@Getter
	private Button buttonAdd;
	@Getter
	private ComboBox comboBox;

	private HorizontalLayout layout;

	public FiltrerAddSelect( String caption )
	{
		super();
		setCaption( caption );
		buildComponents();
	}

	private void buildComponents()
	{
		comboBox = new ComboBox();
		comboBox.setWidth( "100%" );
		comboBox.setFilteringMode( FilteringMode.CONTAINS );
		comboBox.setNewItemsAllowed( false );

		buttonAdd = new Button( FontAwesome.PLUS_CIRCLE );
		buttonAdd.addStyleName( ValoTheme.BUTTON_ICON_ONLY );

		layout = new HorizontalLayout( comboBox, buttonAdd );
		layout.addStyleName( ValoTheme.LAYOUT_COMPONENT_GROUP );
		layout.setWidth( "100%" );
		layout.setExpandRatio( comboBox, 1.0f );

	}

	@Override
	public void setTabIndex( int tabIndex )
	{
		comboBox.setTabIndex( tabIndex );
		buttonAdd.setTabIndex( tabIndex );
	}

	@Override
	public void setReadOnly( boolean readOnly )
	{
		comboBox.setReadOnly( readOnly );
		buttonAdd.setReadOnly( readOnly );
		super.setReadOnly( readOnly );
	}

	@Override
	protected Component initContent()
	{
		return layout;
	}

	@Override
	public Class<? extends Object> getType()
	{
		return Object.class;
	}

	public void addClickListener( ClickListener listener )
	{
		buttonAdd.addClickListener( listener );
	}

	public void addValueChangeListener( Property.ValueChangeListener listener )
	{
		comboBox.addValueChangeListener( listener );
	}

	public void setVisibleAdd( boolean visible )
	{
		buttonAdd.setVisible( visible );
	}
	
	@Override
	public void setPropertyDataSource(Property newDataSource){
		comboBox.setPropertyDataSource( newDataSource );
	}
	
	public void setItemCaptionPropertyId(Object propertyId){
		comboBox.setItemCaptionPropertyId( propertyId );
	}
	
	public void setContainerDataSource(Container newDataSource) {
		comboBox.setContainerDataSource(newDataSource);
	}
}
