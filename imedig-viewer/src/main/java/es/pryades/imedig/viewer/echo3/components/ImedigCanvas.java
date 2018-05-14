package es.pryades.imedig.viewer.echo3.components;

import java.util.EventListener;

import org.apache.log4j.Logger;

import nextapp.echo.app.Component;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

public class ImedigCanvas extends Component
{
	private static final Logger LOG = Logger.getLogger( ImedigCanvas.class );

	private static final long serialVersionUID = 1L;

	public static final String MODE_NONE = "none";
	public static final String MODE_LINE = "line";
	public static final String MODE_RECT = "rect";
	public static final String MODE_ELLIPSE = "ellipse";
	public static final String MODE_POLYGON = "polygon";

	public static final String PROPERTY_WIDTH = "width";
	public static final String PROPERTY_HEIGHT = "height";
	
	public static final String PROPERTY_ACTION_COMMAND = "actionCommand";
	public static final String PROPERTY_ACTION_EVENT = "actionEvent";
	
	public static final String PROPERTY_SELECTION_MODE = "selectionMode";
	public static final String PROPERTY_SHOW_LINE = "showLine";
	
	public static final String PROPERTY_ANNOTATIONS = "annotations";
	public static final String PROPERTY_OVERLAYS = "overlays";

	public static final String ACTION_LISTENERS_PROPERTY = "actionListeners";
	public static final String INPUT_ACTION = "action";
    public static final String PROPERTY_IMAGE = "image";
    public static final String PROPERTY_CURSOR = "cursor";
    public static final String PROPERTY_REFRESH = "refresh";
	
    public static final String PROPERTY_ACTIVE = "active";
    public static final String PROPERTY_ACTIVE_COLOR = "activeColor";
    public static final String PROPERTY_INACTIVE_COLOR = "inactiveColor";

    public static final String REFWIDTH_PROPERTY = "refWidth";
	public static final String REFHEIGHT_PROPERTY = "refHeight";
	
	public static final String ROIX1_PROPERTY = "roiX1";
	public static final String ROIY1_PROPERTY = "roiY1";
	public static final String ROIX2_PROPERTY = "roiX2";
	public static final String ROIY2_PROPERTY = "roiY2";

	public static final String CURSOROFFSETX_PROPERTY = "cursorOffsetX";
	public static final String CURSOROFFSETY_PROPERTY = "cursorOffsetY";

	private int refWidth;
	private int refHeight;
	
	private int roiX1;
	private int roiY1;
	private int roiX2;
	private int roiY2;
	
	private int wheel;
	
	private int actionEvent;
	
	private Boolean refresh;

	public static final String WHEEL_PROPERTY = "wheel";
	
	public int getRoiX1() {
		return roiX1;
	}
	public void setRoiX1(int roiX1) {
		this.roiX1 = roiX1;
	}
	public int getRoiY1() {
		return roiY1;
	}
	public void setRoiY1(int roiY1) {
		this.roiY1 = roiY1;
	}
	public int getRoiX2() {
		return roiX2;
	}
	public void setRoiX2(int roiX2) {
		this.roiX2 = roiX2;
	}
	public int getRoiY2() {
		return roiY2;
	}
	public void setRoiY2(int roiY2) {
		this.roiY2 = roiY2;
	}

	public int getRefWidth() {
		return refWidth;
	}
	public void setRefWidth(int refWidth) {
		this.refWidth = refWidth;
	}

	public int getRefHeight() {
		return refHeight;
	}
	public void setRefHeight(int refHeight) {
		this.refHeight = refHeight;
	}

	public int getWheel() {
		return wheel;
	}
	public void setWheel(int wheel) {
		this.wheel= wheel;
	}

	public int getActionEvent() {
		return actionEvent;
	}
	public void setActionEvent(int actionEvent) {
		this.actionEvent = actionEvent;
	}

	
	public ImedigCanvas() {
		super();
	}

	public int getWidth() {
		return (Integer) get(PROPERTY_WIDTH);
	}
	
	public void setWidth(int w) {
        set(PROPERTY_WIDTH, w);
	}
	
	public int getHeight() {
		return (Integer) get(PROPERTY_HEIGHT);
	}
	
	public void setHeight(int h) {
        set(PROPERTY_HEIGHT, h);
	}
	
	public String getActionCommand() {
		return (String) get(PROPERTY_ACTION_COMMAND);
	}
	
	public void setActionCommand(String ac) {
        set(PROPERTY_ACTION_COMMAND, ac);
	}
	
	public String getSelectionMode() {
		return (String) get(PROPERTY_SELECTION_MODE);
	}
	
	public void setSelectionMode(String ac) {
        set(PROPERTY_SELECTION_MODE, ac);
	}

	public Boolean getShowLine() {
		return (Boolean) get(PROPERTY_SHOW_LINE);
	}
	
	public void setShowLine(Boolean ac) {
        set(PROPERTY_SHOW_LINE, ac);
	}

	public Boolean getActive() {
		return (Boolean) get(PROPERTY_ACTIVE);
	}
	
	public void setActive(Boolean ac) {
        set(PROPERTY_ACTIVE, ac);
	}

	public String getActiveColor() {
		return (String) get(PROPERTY_ACTIVE_COLOR);
	}
	
	public void setActiveColor(String color) {
        set(PROPERTY_ACTIVE_COLOR, color);
	}

	public String getInactiveColor() {
		return (String) get(PROPERTY_INACTIVE_COLOR);
	}
	
	public void setInactiveColor(String color) {
        set(PROPERTY_INACTIVE_COLOR, color);
	}

	public String getAnnotations() {
		return (String) get( PROPERTY_ANNOTATIONS );
	}
    
	public void setAnnotations(String annotations) {
		set(PROPERTY_ANNOTATIONS, annotations );
	}
    
    public String getOverlays() {
		return (String) get( PROPERTY_OVERLAYS );
	}
    
	public void setOverlays(String overlays ) {
		set(PROPERTY_OVERLAYS, overlays );
	}

	public ImageReference getImage() {
        return (ImageReference) get(PROPERTY_IMAGE);
    }
    
    public void setImage(ImageReference img) {
        set(PROPERTY_IMAGE, img);
    }
    
	public String getCursor() {
        return (String) get(PROPERTY_CURSOR);
    }
    
    public void setCursor(String cursor) {
        set(PROPERTY_CURSOR, cursor);
    }

	public Boolean getRefresh() {
		return (Boolean) get(PROPERTY_REFRESH);
	}
	
	public void setRefresh(Boolean refresh) {
        set(PROPERTY_REFRESH, refresh);
	}
	

    public void fireAction() {
		EventListener[] actionListeners = getEventListenerList().getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this,getActionCommand());
		for (int i=0;i<actionListeners.length;i++) {
			((ActionListener) actionListeners[i]).actionPerformed(e);
		}
	}
	
	public void processInput(String inputName, Object inputValue) 
	{
        super.processInput(inputName, inputValue);
    
        if (INPUT_ACTION.equals(inputName)) 
        {
        	fireAction();
        }
        else if ( inputValue != null )
        {
	        LOG.debug( "inputName = " + inputName + " inputValue = "+ inputValue );
	        
	        if (REFWIDTH_PROPERTY.equals(inputName)) 
	        {
	            setRefWidth((Integer) inputValue);
	        } 
	        else if (REFHEIGHT_PROPERTY.equals(inputName)) 
	        {
	            setRefHeight((Integer) inputValue);
	        } 
	        else if (ROIX1_PROPERTY.equals(inputName)) 
	        {
	        	setRoiX1((Integer) inputValue);
	        } 
	        else if (ROIY1_PROPERTY.equals(inputName)) 
	        {
	        	setRoiY1((Integer) inputValue);
	        } 
	        else if (ROIX2_PROPERTY.equals(inputName)) 
	        {
	        	setRoiX2((Integer) inputValue);
	        } 
	        else if (ROIY2_PROPERTY.equals(inputName)) 
	        {
	        	setRoiY2((Integer) inputValue);
	        } 
	        else if (WHEEL_PROPERTY.equals(inputName)) 
	        {
	        	setWheel((Integer) inputValue);
	        } 
	        else if (PROPERTY_ACTION_EVENT.equals(inputName)) 
	        {
	        	setActionEvent((Integer) inputValue);
	        } 
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
    
    
    public boolean isValidChild(Component component) {
        return false;
    }

	public int getCursorOffsetX() {
		return (Integer) get(CURSOROFFSETX_PROPERTY);
	}
	
	public void setCursorOffsetX(int w) {
        set(CURSOROFFSETX_PROPERTY, w);
	}

	public int getCursorOffsetY() {
		return (Integer) get(CURSOROFFSETY_PROPERTY);
	}
	
	public void setCursorOffsetY(int w) {
        set(CURSOROFFSETY_PROPERTY, w);
	}
}
