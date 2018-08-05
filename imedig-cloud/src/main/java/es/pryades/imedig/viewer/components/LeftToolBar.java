package es.pryades.imedig.viewer.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.FontIcoMoon;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.viewer.actions.AngleAction;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.ContrastAction;
import es.pryades.imedig.viewer.actions.DistanceAction;
import es.pryades.imedig.viewer.actions.EraseAction;
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;

public class LeftToolBar extends VerticalLayout {
	
	private VerticalLayout inside;
	private Panel panelThumnails;
	private VerticalLayout thumnails;
	
	Button buttonOpen = null;
	Button buttonClose = null;
	Button buttonNone = null;
	Button buttonDistance = null;
	Button buttonAngle = null;
	Button buttonZoom = null;
	Button buttonContrast = null;
	Button buttonUndo = null;
	Button buttonErase = null;
	Button buttonDownload = null;
	Button buttonReport = null;
	
	private final ImedigContext context;
	private Button lastAction = null;
	
	private final ListenerAction listenerAction;
	
	private Integer studyCount = 0;
	
	public LeftToolBar(ImedigContext context, ListenerAction listenerAction){
		setWidth("130px");
		setHeight("100%");
		this.listenerAction = listenerAction;
		this.context = context;
		buidInsideLayout();
	}


	private void buidInsideLayout() {
		inside = new VerticalLayout();
		inside.setSpacing(true);
		inside.setHeight("100%");
		
		buildButtons();
		buildPanel();
		addComponent(inside);
	}

	private void buildButtons() {
		GridLayout grid = new GridLayout(3, 3);
		grid.addComponent( buttonOpen = getButton( FontAwesome.FOLDER_OPEN, "open", "ViewerWnd.Open" ));
		grid.addComponent( buttonClose = getButton( FontAwesome.CLOSE, "close", "ViewerWnd.Close" ));
		grid.addComponent( buttonNone = getButton( FontAwesome.MOUSE_POINTER, "cursor-green", "ViewerWnd.NoOperation" ));
		grid.addComponent( buttonZoom = getButton( FontAwesome.SEARCH_PLUS, "magnifier", "ViewerWnd.Zoom" ));
		grid.addComponent( buttonContrast = getButton( FontAwesome.ADJUST, "contrast", "ViewerWnd.Contrast" ));
		grid.addComponent( buttonUndo = getButton( FontAwesome.UNDO, "undo", "ViewerWnd.Undo" ));
		grid.addComponent( buttonDistance = getButton( FontIcoMoon.RULE, "distance", "ViewerWnd.Distance" ));
		grid.addComponent( buttonAngle = getButton( FontIcoMoon.PROTRACTOR, "angle", "ViewerWnd.Angle" ));
		grid.addComponent( buttonErase = getButton( FontAwesome.ERASER, "eraser", "ViewerWnd.ClearAnnotations" ));
		grid.addComponent( buttonDownload = getButton( FontAwesome.DOWNLOAD, "download", "words.download" ));
		grid.addComponent( buttonReport = getButton( FontAwesome.FILE_PDF_O, "report", "words.report.request" ));
		
		buttonOpen.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new QueryStudies(this, null));
				selectedAction(buttonOpen);
			}
		});
		
		buttonClose.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new CloseStudies(this, null));
				selectedAction(buttonClose);
			}
		});
		
		buttonDistance.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new DistanceAction(this, null));
				selectedAction(buttonDistance);
			}
		});

		buttonAngle.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new AngleAction(this, null));
				selectedAction(buttonAngle);
			}
		});
		
		buttonNone.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new NoneAction(this, null));
				selectedAction(buttonNone);
			}
		});
		
		buttonZoom.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new ZoomAction(this, null));
				selectedAction(buttonZoom);
			}
		});
		
		buttonUndo.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new UndoAction(this, null));
				selectedAction(buttonUndo);
			}
		});
		
		buttonContrast.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new ContrastAction(this, null));
				selectedAction(buttonContrast);
			}
		});
		
		buttonErase.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new EraseAction(this, null));
				buttonErase.setEnabled( false );
				//selectedAction(buttonErase);
			}
		});
		
		allButtonsDisable();
		
		inside.addComponent(grid);
	}
	
	private void buildPanel(){
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		thumnails.setSpacing(true);
		
		panelThumnails = new Panel(thumnails);
		panelThumnails.setSizeFull();
		panelThumnails.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		inside.addComponent(panelThumnails);
		inside.setExpandRatio(panelThumnails, 1.0f);
	}
	
	private Button getButton(FontIcon font, String name, String keytooltip){
		
		Button btn = new Button( );
		btn.setId("btn.tool."+name);
		btn.setIcon( font );
		btn.setDescription( context.getString( keytooltip ) );
		btn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btn.addStyleName(ValoTheme.BUTTON_LARGE);
		btn.addStyleName(ImedigTheme.BUTTON_TOOLBOX);
		
		return btn;
	}
	
	public void clearThumnails(){
		thumnails.removeAllComponents();
		studyCount = 0;
	}
	
	public void addStudyPanel( StudyPanel panel){
		panel.addStyleName(ImedigTheme.BG_THUMNAIL+ (studyCount % 2));
		studyCount ++;
		thumnails.addComponent(panel);
	}
	
	
	public void allButtonsDisable(){
		buttonClose.setEnabled( false );
		buttonNone.setEnabled( false );
		buttonDistance.setEnabled( false );
		buttonAngle.setEnabled( false );
		buttonZoom.setEnabled( false );
		buttonContrast.setEnabled( false );
		buttonUndo.setEnabled( false );
		buttonErase.setEnabled( false );
		
		if (lastAction != null){
			lastAction.removeStyleName( ImedigTheme.BUTTON_SELECTED );
		}
	}

	public void studiesOpen(){
		buttonClose.setEnabled( true );
	}

	public void studyInViewer(){
		buttonNone.setEnabled( true );
		buttonDistance.setEnabled( true );
		buttonAngle.setEnabled( true );
		buttonZoom.setEnabled( true );
		buttonContrast.setEnabled( true );
		buttonUndo.setEnabled( false );
		if (lastAction != null){
			lastAction.removeStyleName( ImedigTheme.BUTTON_SELECTED );
		}
	}
	
	private void selectedAction(Button button){
		if (lastAction != null){
			lastAction.removeStyleName( ImedigTheme.BUTTON_SELECTED );
		}
		if (button == buttonOpen || 
				button == buttonClose || 
				button == buttonErase ||
				button == buttonUndo) return;
		
		button.addStyleName( ImedigTheme.BUTTON_SELECTED );
		lastAction = button;
	}
}
