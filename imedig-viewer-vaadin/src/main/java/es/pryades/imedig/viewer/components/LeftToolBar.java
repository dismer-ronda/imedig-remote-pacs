package es.pryades.imedig.viewer.components;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.viewer.ListenerAction;
import es.pryades.imedig.viewer.actions.AngleAction;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.DistanceAction;
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;
import es.pryades.imedig.viewer.application.ViewerApplicationUI;

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
	
	private final ListenerAction listenerAction;
	
	private static final List<String> BG_COLORS = Arrays.asList(ViewerTheme.BG_BLUE,
			ViewerTheme.BG_GREEN,
			ViewerTheme.BG_BROWN,
			ViewerTheme.BG_ORANGE,
			ViewerTheme.BG_RED);
	
	private Integer studyCount = 0;
	
	public LeftToolBar(ListenerAction listenerAction){
		setWidth("120px");
		setHeight("100%");
		this.listenerAction = listenerAction;
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
		grid.addComponent( buttonOpen = getButton( "open", "ViewerWnd.Open" ));
		grid.addComponent( buttonClose = getButton( "close", "ViewerWnd.Close" ));
		grid.addComponent( buttonNone = getButton( "cursor-green", "ViewerWnd.NoOperation" ));
		grid.addComponent( buttonZoom = getButton( "magnifier", "ViewerWnd.Zoom" ));
		grid.addComponent( buttonContrast = getButton( "contrast", "ViewerWnd.Contrast" ));
		grid.addComponent( buttonUndo = getButton( "undo", "ViewerWnd.Undo" ));
		grid.addComponent( buttonDistance = getButton( "distance", "ViewerWnd.Distance" ));
		grid.addComponent( buttonAngle = getButton( "angle", "ViewerWnd.Angle" ));
		buttonOpen.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonClose.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonNone.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonDistance.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonAngle.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonZoom.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonContrast.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		buttonUndo.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
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

		//grid.setEnabled(false);
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

	
	private static Button getButton(String name, String keytooltip){
		
		Button btn = new Button( );
		btn.setId("btn.tool."+name);
		btn.setIcon( new ThemeResource( "img/"+name+".png" ) );
		btn.setDescription( ViewerApplicationUI.getText( keytooltip ) );
		
		return btn;
	}
	
	public void clearThumnails(){
		thumnails.removeAllComponents();
		studyCount = 0;
	}
	
	public void addStudyPanel( StudyPanel panel){
		panel.addStyleName(BG_COLORS.get(studyCount % 5));
		studyCount ++;
		thumnails.addComponent(panel);
	}
	
}
