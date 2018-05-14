package es.pryades.imedig.cloud.submodules;

import lombok.Getter;
import lombok.Setter;

import es.pryades.imedig.cloud.common.CenterListenerChange;
import es.pryades.imedig.cloud.modules.ImedigSubModule;

public abstract class AbstractCentersSubModule extends ImedigSubModule 
{
	@Setter 
	@Getter
	private CenterListenerChange centerListenerChange;
	
	protected void changeToCenter( Integer centerid )
	{
		if ( centerListenerChange != null )
			centerListenerChange.changeToCenter(centerid);
	}
}
