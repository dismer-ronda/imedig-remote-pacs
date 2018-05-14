package es.pryades.imedig.cloud.modules;

import java.util.List;

import lombok.Data;

import com.vaadin.ui.Layout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.OptionData;
import es.pryades.imedig.cloud.common.SubModuleListenerChange;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

@SuppressWarnings("rawtypes")
@Data
public abstract class ImedigSubModule 
{
	private ImedigContext context;
	private Layout mainLayout;
	private List<OptionData> options;
	
	private SubModuleListenerChange subModuleListenerChange;
	
	protected abstract List getSubModuleOptions() throws ImedigException;
	protected abstract String getSubModuleName();
	protected abstract void renderSubModule() throws ImedigException;
	public abstract void clicked( OptionData option ) throws ImedigException;
	
	public void render() throws ImedigException
	{
		options = getSubModuleOptions();
	
		renderSubModule();
	}

	public void addListener(SubModuleListenerChange listener)
	{
		subModuleListenerChange = listener;
	}
}
