package es.pryades.imedig.cloud.common;

import java.util.ResourceBundle;

import com.vaadin.ui.CustomComponent;

import es.pryades.imedig.cloud.common.AppUtils;

public class BaseModule extends CustomComponent
{
	private static final long serialVersionUID = -5305330866624717747L;
	
	private ResourceBundle resources;
	
	private BaseDesktopWindow owner;
	
	public BaseModule( ResourceBundle resources, BaseDesktopWindow owner )
	{
		setResources( resources );
		
		setOwner( owner );
	}

	public ResourceBundle getResources() {
		return resources;
	}

	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

	public String getString( String key )
	{
		return AppUtils.getString( resources, key );
	}

	public void setOwner(BaseDesktopWindow owner) {
		this.owner = owner;
	}

	public BaseDesktopWindow getOwner() {
		return owner;
	}
}
