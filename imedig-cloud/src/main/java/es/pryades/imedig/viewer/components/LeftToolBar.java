package es.pryades.imedig.viewer.components;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.FontIcoMoon;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.ImageResource;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.viewer.actions.AngleAction;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.ContrastAction;
import es.pryades.imedig.viewer.actions.DistanceAction;
import es.pryades.imedig.viewer.actions.EraseAction;
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.RequestReport;
import es.pryades.imedig.viewer.actions.RestoreAction;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;

public class LeftToolBar extends HorizontalLayout {
	
	private Panel panelThumnails;
	private VerticalLayout thumnails;
	
	Button buttonOpen = null;
	Button buttonClose = null;
	Button buttonNone = null;
	Button buttonDistance = null;
	Button buttonAngle = null;
	Button buttonZoom = null;
	Button buttonContrast = null;
	Button buttonRestore = null;
	Button buttonUndo = null;
	Button buttonErase = null;
	Button buttonDownload = null;
	Button buttonReport = null;
	
	private final ImedigContext context;
	private Button lastAction = null;
	
	private final ListenerAction listenerAction;
	private final ImageResource imageResource; 
	
	private Integer studyCount = 0;
	
	public LeftToolBar(ImedigContext context, ListenerAction listenerAction, ImageResource imageResource){
		setWidth("165px");
		setHeight("100%");
		setSpacing( true );
		this.listenerAction = listenerAction;
		this.imageResource = imageResource;
		this.context = context;
		buidInsideLayout();
	}


	private void buidInsideLayout() {
//		inside = new VerticalLayout();
//		inside.setSpacing(true);
//		inside.setHeight("100%");
		
		buildButtons();
		buildPanel();
//		addComponent(inside);
	}

	private void buildButtons() {
		VerticalLayout toolbox = new VerticalLayout();
		toolbox.setSpacing( true );
		
		VerticalLayout group = new VerticalLayout();
		group.addComponent( buttonOpen = getButton( FontAwesome.FOLDER_OPEN, "open", "ViewerWnd.Open" ));
		group.addComponent( buttonClose = getButton( FontAwesome.CLOSE, "close", "ViewerWnd.Close" ));
		toolbox.addComponent( group );
		
		group = new VerticalLayout();
		group.addComponent( buttonUndo = getButton( FontAwesome.UNDO, "undo", "ViewerWnd.Undo" ));
		group.addComponent( buttonRestore = getButton( FontAwesome.HISTORY, "restore", "ViewerWnd.Restore" ));
		group.addComponent( buttonErase = getButton( FontAwesome.ERASER, "eraser", "ViewerWnd.ClearAnnotations" ));
		toolbox.addComponent( group );
		
		group = new VerticalLayout();
		group.addComponent( buttonNone = getButton( FontAwesome.MOUSE_POINTER, "cursor-green", "ViewerWnd.NoOperation" ));
		group.addComponent( buttonZoom = getButton( FontAwesome.SEARCH_PLUS, "magnifier", "ViewerWnd.Zoom" ));
		group.addComponent( buttonContrast = getButton( FontAwesome.ADJUST, "contrast", "ViewerWnd.Contrast" ));
		group.addComponent( buttonDistance = getButton( FontIcoMoon.RULE, "distance", "ViewerWnd.Distance" ));
		group.addComponent( buttonAngle = getButton( FontIcoMoon.PROTRACTOR, "angle", "ViewerWnd.Angle" ));
		toolbox.addComponent( group );
		
		group = new VerticalLayout();
		group.addComponent( buttonDownload = getButton( FontAwesome.DOWNLOAD, "download", "words.download" ));
		
		if ( context.hasRight( "informes.crear" ) )
		{
			group.addComponent( buttonReport = getButton( FontAwesome.FILE_PDF_O, "report", "words.report.request" ));
			
			buttonReport.addClickListener( new Button.ClickListener()
			{
				public void buttonClick( ClickEvent event )
				{
					listenerAction.doAction(new RequestReport(this, null));
				}
			} );
		}
		toolbox.addComponent( group );
		
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
		
		
		buttonRestore.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listenerAction.doAction(new RestoreAction(this, null));
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
		
		FileDownloader fileDownloaderCsv = new FileDownloader( getResource() );
        fileDownloaderCsv.extend( buttonDownload );
        
		allButtonsDisable();
		
		toolbox.setWidth( "46px" );
		addComponent( toolbox );
		//setComponentAlignment( toolbox, Alignment.TOP_RIGHT );
		//inside.addComponent(grid);
	}
	
	private void buildPanel(){
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		thumnails.setSpacing(true);
		
		panelThumnails = new Panel(thumnails);
		panelThumnails.setHeight( "100%" );
		panelThumnails.setWidth( "100%" );
		panelThumnails.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		addComponent( panelThumnails );
		setExpandRatio( panelThumnails, 1.0f );
		panelThumnails.setVisible( false );
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
		panelThumnails.setVisible( false );
	}
	
	public void addStudyPanel( StudyPanel panel){
		if (!panelThumnails.isVisible()) panelThumnails.setVisible( true );
		
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
		buttonRestore.setEnabled( false );
		buttonErase.setEnabled( false );
		buttonDownload.setEnabled( false );
		
		if (buttonReport != null){
			buttonReport.setEnabled( false );
		}
		
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
		buttonRestore.setEnabled( false );
		buttonDownload.setEnabled( true );
		
		if (buttonReport != null){
			buttonReport.setEnabled( true );
		}
//		if (lastAction != null){
//			lastAction.removeStyleName( ImedigTheme.BUTTON_SELECTED );
//		}
	}
	
	public void selectedAction(Button button){
		if (lastAction != null){
			lastAction.removeStyleName( ImedigTheme.BUTTON_SELECTED );
		}
		if (button == buttonOpen || 
				button == buttonClose || 
				button == buttonErase ||
				button == buttonUndo  ||
				button == buttonRestore ||
				button == buttonDownload ||
				button == buttonReport) return;
		
		button.addStyleName( ImedigTheme.BUTTON_SELECTED );
		lastAction = button;
	}
	
	private StreamResource getResource()
	{
		StreamResource resource = new StreamResource( 
				new StreamSource() 
				{
					private static final long serialVersionUID = 3171380160730576267L;

					@Override
		            public InputStream getStream() 
		            {
		                try 
		                {
		                	return new URL( imageResource.currentImageUrl()).openStream();
		                } 
		                catch ( IOException e ) 
		                {
		                    e.printStackTrace();
		                    return null;
		                }
		
		            }
		        }, 
		        Utils.getUUID() + ".png" );
		
		resource.setCacheTime( 0 );
		
		return resource;
	}
}
