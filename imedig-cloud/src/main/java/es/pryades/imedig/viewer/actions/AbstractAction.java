package es.pryades.imedig.viewer.actions;

import java.io.Serializable;

import es.pryades.imedig.cloud.core.action.Action;

public abstract class AbstractAction<Object, D> implements Action<Object, D>, Serializable {
	
	private static final long serialVersionUID = 6357859301201104217L;
	
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
