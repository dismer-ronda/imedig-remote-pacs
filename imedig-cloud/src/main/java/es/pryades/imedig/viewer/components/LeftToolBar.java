package es.pryades.imedig.viewer.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
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
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;

public class LeftToolBar extends VerticalLayout {
	
	private VerticalLayout inside;
	private Panel panelThumnails;
	private VerticalLayout thumnails;
	
	private Button buttonOpen = null;
	private Button buttonClose = null;
	private Button buttonNone = null;
	private Button buttonDistance = null;
	private Button buttonAngle = null;
	private Button buttonZoom = null;
	private Button buttonContrast = null;
	private Button buttonUndo = null;
	
	private final ImedigContext context;
	
	private final ListenerAction listenerAction;
	
//	private static final List<String> BG_COLORS = Arrays.asList(ImedigTheme.BG_BLUE,
//			ImedigTheme.BG_GREEN,
//			ImedigTheme.BG_BROWN,
//			ImedigTheme.BG_ORANGE,
//			ImedigTheme.BG_RED);
	
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
		
		buttonOpen.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new QueryStudies(this, null));
			}
		});
		
		buttonClose.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new CloseStudies(this, null));
			}
		});
		
		buttonDistance.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new DistanceAction(this, null));
			}
		});

		buttonAngle.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new AngleAction(this, null));
			}
		});
		
		buttonNone.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new NoneAction(this, null));
			}
		});
		
		buttonZoom.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new ZoomAction(this, null));
			}
		});
		
		buttonUndo.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new UndoAction(this, null));
			}
		});
		
		buttonContrast.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new ContrastAction(this, null));
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

	
	private Button getButton(String name, String keytooltip){
		
		Button btn = new Button( );
		btn.setId("btn.tool."+name);
		btn.setIcon( new ThemeResource( "images/"+name+".png" ) );
		btn.setDescription( context.getString( keytooltip ) );
		
		return btn;
	}
	
	private Button getButton(FontIcon font, String name, String keytooltip){
		
		Button btn = new Button( );
		btn.setId("btn.tool."+name);
		btn.setIcon( font );
		btn.setDescription( context.getString( keytooltip ) );
		btn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btn.addStyleName(ValoTheme.BUTTON_LARGE);
		btn.addStyleName(ImedigTheme.BUTTON_TOOLBOX);
		//btn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		
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
	}
	
	public void enableUndo(boolean enabled){
		buttonUndo.setEnabled( enabled );
	}

}
