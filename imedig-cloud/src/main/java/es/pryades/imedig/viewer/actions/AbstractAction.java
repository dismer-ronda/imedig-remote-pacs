package es.pryades.imedig.viewer.actions;

import es.pryades.imedig.cloud.core.action.Action;

public abstract class AbstractAction implements Action {
	
	private final Object source;
	private final Object data;
	
	public AbstractAction(Object source){
		this( source, null );
	}
	
	public AbstractAction(Object source, Object data){
		this.source = source;
		this.data	= data;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public Object getData() {
		return data;
	}

}
