package es.pryades.imedig.viewer.actions;

import es.pryades.imedig.cloud.core.action.Action;

public abstract class AbstractAction<Object, D> implements Action<Object, D> {
	
	private final Object source;
	private final D data;
	
	public AbstractAction(Object source){
		this( source, null );
	}
	
	public AbstractAction(Object source, D data){
		this.source = source;
		this.data	= data;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public D getData() {
		return data;
	}

}
