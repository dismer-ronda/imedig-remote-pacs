package es.pryades.imedig.viewer.echo3.components;

import java.util.EventListener;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

public class ContentPaneEx extends ContentPane {

	private static final long serialVersionUID = 1L;

	public static final String WIDTH_PROPERTY = "width";
	public static final String HEIGHT_PROPERTY = "height";
	public static final String ACTION_LISTENERS_PROPERTY = "actionListeners";
	public static final String ACTION_COMMAND_PROPERTY = "actionCommand";
	public static final String INPUT_ACTION = "action";

	private int width;
	private int height;
	private String actionCommand;
	
	
	public ContentPaneEx() {
		super();
	}

	
	public int getWidth() {
		return width;
	}

	
	private void setWidth(int w) {
		width=w;
	}
	
	
	public int getHeight() {
		return height;
	}

	
	private void setHeight(int h) {
		height=h;
	}
	

	public String getActionCommand() {
		return actionCommand;
	}

	
	public void setActionCommand(String newac) {
		String oldac=actionCommand;
		actionCommand=newac;
		firePropertyChange(ACTION_COMMAND_PROPERTY, oldac, newac);
	}
	
	
	public void fireAction() {
		EventListener[] actionListeners = getEventListenerList().getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this,getActionCommand());
		for (int i=0;i<actionListeners.length;i++) {
			((ActionListener) actionListeners[i]).actionPerformed(e);
		}
		
	}
	
	
	public void processInput(String inputName, Object inputValue) {
        super.processInput(inputName, inputValue);
        if (WIDTH_PROPERTY.equals(inputName)) {
            setWidth((Integer) inputValue);
        } else
        if (HEIGHT_PROPERTY.equals(inputName)) {
            setHeight((Integer) inputValue);
        } else
        if (INPUT_ACTION.equals(inputName)) {
        	fireAction();
        }
    }

	
    public void addActionListener(ActionListener l) {
    	getEventListenerList().addListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_PROPERTY, null, l);    	
    }

    
    public void removeActionListener(ActionListener l) {
    	getEventListenerList().addListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_PROPERTY, l, null);    	
    }
    
    
    public boolean hasActionListeners() {
    	return hasEventListenerList() && getEventListenerList().getListenerCount(ActionListener.class) > 0;
    }

    
}

