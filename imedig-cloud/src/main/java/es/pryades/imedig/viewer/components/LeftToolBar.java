package es.pryades.imedig.viewer.components;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.FontIcoMoon;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.ImageResource;
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
import es.pryades.imedig.viewer.datas.ImageData;

public class LeftToolBar extends HorizontalLayout {
	
	private static final long serialVersionUID = 2868485281494302893L;
	
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
	
	private final ImageResource imageResource; 
	
	Integer studyCount = 0;
	
	public LeftToolBar(ImedigContext context, ImageResource imageResource){
		//setWidth("165px");
		setHeight("100%");
		//setSpacing( true );
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
				/**
				 * 
				 */
				private static final long serialVersionUID = 2246918318846892542L;

				public void buttonClick( ClickEvent event )
				{
					context.sendAction(new RequestReport(this));
				}
			} );
		}
		toolbox.addComponent( group );
		
		buttonOpen.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3957936980477046114L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction( new QueryStudies(this));
				selectedAction(buttonOpen);
			}
		});
		
		buttonClose.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3120316840820293620L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new CloseStudies(this));
				selectedAction(buttonClose);
			}
		});
		
		buttonDistance.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 275966713474694589L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new DistanceAction(this));
				selectedAction(buttonDistance);
			}
		});

		buttonAngle.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 827423384174902965L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new AngleAction(this));
				selectedAction(buttonAngle);
			}
		});
		
		buttonNone.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8852589294839504630L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new NoneAction(this));
				selectedAction(buttonNone);
			}
		});
		
		buttonZoom.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 126137635161307671L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new ZoomAction(this));
				selectedAction(buttonZoom);
			}
		});
		
		
		buttonRestore.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7513102943320764605L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new RestoreAction(this));
			}
		});

		buttonUndo.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6569945444218724024L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new UndoAction(this));
			}
		});
		
		buttonContrast.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 37524764587510210L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new ContrastAction(this));
				selectedAction(buttonContrast);
			}
		});
		
		buttonErase.addClickListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6892451358186939618L;

			@Override
			public void buttonClick(ClickEvent event) {
				context.sendAction(new EraseAction(this));
				buttonErase.setEnabled( false );
				//selectedAction(buttonErase);
			}
		});
		
		FileDownloader fileDownloaderCsv = new FileDownloader( getResource() );
        fileDownloaderCsv.extend( buttonDownload );
        
		allButtonsDisable();
		
		//toolbox.setWidth( "50px" );
		VerticalLayout container = new VerticalLayout(toolbox);
		container.setHeight( "100%" );
		container.addStyleName( ImedigTheme.TOOLBOX_CONTENT );
		
		addComponent( container );
		//setComponentAlignment( toolbox, Alignment.TOP_RIGHT );
		//inside.addComponent(grid);
	}
	
	private void buildPanel(){
		thumnails = new VerticalLayout();
		thumnails.setWidth("100%");
		thumnails.setSpacing(true);
		
		panelThumnails = new Panel(thumnails);
		panelThumnails.setHeight( "100%" );
		panelThumnails.setWidth( "105px" );
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
		removeStyleName( ImedigTheme.TOOLBOX_CONTENT );
	}
	
	public void addStudyPanel( StudyPanel panel){
		if (!panelThumnails.isVisible()) {
			panelThumnails.setVisible( true );
			addStyleName( ImedigTheme.TOOLBOX_CONTENT );
		}
		
		panel.addStyleName(ImedigTheme.BG_THUMNAIL+ (studyCount % 2));
		studyCount ++;
		thumnails.addComponent(panel);
	}
	
	public void removeStudyPanel( StudyPanel panel){
		if (panel == null) return;
		thumnails.removeComponent(panel);
		studyCount--;
		if (studyCount == 0){
			panelThumnails.setVisible( false );
			allButtonsDisable();
			removeStyleName( ImedigTheme.TOOLBOX_CONTENT );
		}
	}
	
	public void changeImageFrame( ImageData data){
		
		Iterator<Component> it = thumnails.iterator();
		
		while ( it.hasNext() ){
			StudyPanel panel = (StudyPanel)it.next();
			panel.changeImageFrame( data );
		}
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
