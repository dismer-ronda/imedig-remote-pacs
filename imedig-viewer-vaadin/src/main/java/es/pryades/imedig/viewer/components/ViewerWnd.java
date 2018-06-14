package es.pryades.imedig.viewer.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.canvas.dom.client.ImageData;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.Action;
import es.pryades.imedig.viewer.ListenerAction;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.components.query.QueryDlg;
import es.pryades.imedig.wado.query.QueryManager;

public class ViewerWnd extends VerticalLayout implements ListenerAction {
	private static final Logger LOG = LoggerFactory.getLogger(ViewerWnd.class);

	public static HashMap<String, ReportInfo> imagesInfo = new HashMap<String, ReportInfo>();
	
	private class Status {
		int ix1;
		int iy1;
		int ix2;
		int iy2;

		double windowWidth;
		double windowCenter;

		int frame;

		public Status(int ix1, int iy1, int ix2, int iy2, double windowCenter, double windowWidth, int frame) {
			this.ix1 = ix1;
			this.iy1 = iy1;
			this.ix2 = ix2;
			this.iy2 = iy2;

			this.windowCenter = windowCenter;
			this.windowWidth = windowWidth;

			this.frame = frame;
		}
	}

	public static final Integer OPERATION_NONE = 0;
	public static final Integer OPERATION_ZOOM = 1;
	public static final Integer OPERATION_PAN = 2;
	public static final Integer OPERATION_MEASURE_LINE = 3;
	public static final Integer OPERATION_MEASURE_ANGLE = 4;
	public static final Integer OPERATION_MEASURE_AREA = 5;
	public static final Integer OPERATION_WINDOW = 6;

	public static final int OPEN_STUDIES = 100;

	protected ResourceBundle resourceBundle;

	private int buttonSize;

	// private QueryDlg queryDlg = null;

	ArrayList<String> selectedStudies = null;
	private ArrayList<StudyTree> studies;

	private String currentSelMode;
	private boolean currentShowLine;
	private int currentOperation;
	private int currentCursorOffsetX;
	private int currentCursorOffsetY;

	private String currentCursor;

	boolean replicate = false;
	// ImedigCanvas activeCanvas = null;
	// private ArrayList<ImedigCanvas> canvasImages;

	boolean firstLine = true;

	private double aix1;
	private double aiy1;
	private double aix2;
	private double aiy2;

	// private ImedigApplication app;

	private User user;

	private Panel imagesThumbnail;
	// private ContentPane contentPaneGrid;
	// private Grid opGrid;

	private boolean firstTime = true;

	private LeftToolBar leftToolBar;

	public ViewerWnd(User user) {
		super();
		this.user = user;
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buidComponent();
		
		init();
	}
	
	private void init(){
		studies = new ArrayList<>();
	}

	private void buidComponent() {
		leftToolBar = new LeftToolBar(this);
		addComponent(leftToolBar);
		setComponentAlignment(leftToolBar, Alignment.TOP_LEFT);
	}

	@Override
	public void doAction(Action action) {
		if (action == null)
			return;

		if (action instanceof QueryStudies) {
			queryStudies();
		}else if (action instanceof OpenStudies) {
			openStudies((List<String>)action.getData());
		}else if (action instanceof CloseStudies) {
			closeStudies();
		}else if (action instanceof OpenImage) {
			openImage((ImageData)action.getData());
		}
	}

	private void openImage(ImageData data) {
		// TODO Auto-generated method stub
		
	}

	private void queryStudies() {
		QueryDlg dlg = new QueryDlg(user);
		dlg.setListener(this);
		UI.getCurrent().addWindow(dlg);
	}
	
	private void closeStudies()	{
		leftToolBar.clearThumnails();
		
		studies.clear();
	}
	
	private void openStudies(List<String> studies) {
		for ( String uid : studies ){
			LOG.info( "selectedStudies uid " + uid );
			
			String AETitle = Settings.PACS_AETitle;
			String Host = Settings.PACS_Host;
			int Port = Settings.PACS_Port;
				
			StudyTree study = QueryManager.getInstance().getStudyTree( AETitle, Host, Port, Settings.IMEDIG_AETitle, uid, new HashMap<String,String>() );

			if ( study != null ){
				this.studies.add( study );
			}
		}
		setStudyThumbnails();
	}

	private void setStudyThumbnails() {
		leftToolBar.clearThumnails();
		
		for ( StudyTree study : studies ){
			if ( study.getSeriesList().size() == 0 ) continue;
			
			leftToolBar.addStudyPanel(new StudyPanel(study, this));
		}
	}

}
