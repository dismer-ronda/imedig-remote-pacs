package es.pryades.imedig.viewer.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.Action;
import es.pryades.imedig.cloud.core.action.ImageResource;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.Reports.ReportsDlg;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.Settings;
import es.pryades.imedig.viewer.actions.AddFigure;
import es.pryades.imedig.viewer.actions.AddToUndoAction;
import es.pryades.imedig.viewer.actions.AngleAction;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.ContrastAction;
import es.pryades.imedig.viewer.actions.DistanceAction;
import es.pryades.imedig.viewer.actions.EraseAction;
import es.pryades.imedig.viewer.actions.FontAction;
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.RequestReport;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;
import es.pryades.imedig.viewer.components.image.ImageCanvas;
import es.pryades.imedig.viewer.components.query.FontsDlg;
import es.pryades.imedig.viewer.components.query.QueryDlg;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.query.QueryManager;

public class ViewerWnd extends HorizontalLayout implements ListenerAction, ImageResource, ModalParent {
	private static final Logger LOG = LoggerFactory.getLogger(ViewerWnd.class);

	public static HashMap<String, ReportInfo> imagesInfo = new HashMap<String, ReportInfo>();
	
	public static final Integer OPERATION_NONE = 0;
	public static final Integer OPERATION_ZOOM = 1;
	public static final Integer OPERATION_PAN = 2;
	public static final Integer OPERATION_MEASURE_LINE = 3;
	public static final Integer OPERATION_MEASURE_ANGLE = 4;
	public static final Integer OPERATION_MEASURE_AREA = 5;
	public static final Integer OPERATION_WINDOW = 6;

	public static final int OPEN_STUDIES = 100;

	protected ResourceBundle resourceBundle;

	ArrayList<String> selectedStudies = null;
	private ArrayList<StudyTree> studies;

	boolean replicate = false;
	boolean firstLine = true;

	private User user;

	private LeftToolBar leftToolBar;
	private ImageCanvas imageCanvas;
	
	private final ImedigContext context;
	
	private QueryDlg queryDlg;
	
	private final boolean modeReport;
	
	public ViewerWnd(ImedigContext context, User user) {
		this( context, user, false );
	}
	
	public ViewerWnd(ImedigContext context, User user, boolean modeReport) {
		super();
		this.modeReport = modeReport;
		this.context = context;
		this.user = user;
		setSizeFull();
		setSpacing(true);
		//setMargin(true);
		buidComponent();
		
		init();
	}
	
	private void init(){
		studies = new ArrayList<>();
		if (modeReport){
			leftToolBar.buttonOpen.setEnabled( false );
			leftToolBar.buttonClose.setEnabled( false );
			leftToolBar.buttonDownload.setVisible( false );
			if (leftToolBar.buttonReport != null){
				leftToolBar.buttonReport.setVisible( false );
			}
		}
	}

	private void buidComponent() {
		leftToolBar = new LeftToolBar(context, this, this);
		addComponent(leftToolBar);
		setComponentAlignment(leftToolBar, Alignment.TOP_LEFT);
		imageCanvas = new ImageCanvas( context, user, this );
		addComponent(imageCanvas);
		setExpandRatio(imageCanvas, 1.0f);
	}

	@Override
	public void doAction(Action action) {
		if (action == null)
			return;

		if (action instanceof QueryStudies) {
			queryStudies();
		}else if (action instanceof FontAction) {
			queryFonts();
		}else if (action instanceof OpenStudies) {
			if (openStudies((List<String>)action.getData())){
				if (modeReport){
					leftToolBar.buttonClose.setEnabled( false );
				}else{
					leftToolBar.studiesOpen();
				}
			}
		}else if (action instanceof CloseStudies) {
			closeStudies();
			leftToolBar.allButtonsDisable();
		}else if (action instanceof OpenImage) {
			openImage((ImageData)action.getData());
			enabledButtons();
		}else if (action instanceof AngleAction) {
			imageCanvas.angleAction();
		}else if (action instanceof DistanceAction) {
			imageCanvas.distanceAction();
		}else if (action instanceof ZoomAction) {
			imageCanvas.zoomAction();
		}else if (action instanceof UndoAction) {
			if (!imageCanvas.undoAction()){
				leftToolBar.buttonUndo.setEnabled( false );
			}
		}else if (action instanceof NoneAction) {
			imageCanvas.noneAction();
		}else if (action instanceof ContrastAction) {
			imageCanvas.contrastAction();
		}else if (action instanceof AddToUndoAction) {
			leftToolBar.buttonUndo.setEnabled( true );
		}else if (action instanceof EraseAction) {
			imageCanvas.clearFigures();
		}else if (action instanceof AddFigure) {
			leftToolBar.buttonErase.setEnabled( true );
		}else if (action instanceof RequestReport) {
			requestReport();
		}
		
	}

	private void enabledButtons()
	{
		leftToolBar.studyInViewer();
		if (modeReport){
			leftToolBar.buttonOpen.setEnabled( false );
			leftToolBar.buttonClose.setEnabled( false );
		}
	}

	public ReportInfo getReportInfo() {
		return imageCanvas.getReportInfo();
	}

	private void openImage(ImageData data) {
		imageCanvas.openImage(data);
	}

	private void queryStudies() {
		if (queryDlg == null){
			queryDlg = new QueryDlg(context, user);
			queryDlg.setListener(this);
		}else{
			queryDlg.onQuery();
		}
		UI.getCurrent().addWindow(queryDlg);
	}
	
	private void queryFonts() {
		FontsDlg dlg = new FontsDlg();
		UI.getCurrent().addWindow(dlg);
	}
	
	private void closeStudies()	{
		leftToolBar.clearThumnails();
		
		studies.clear();
		imageCanvas.clear();
	}
	
	private boolean openStudies(List<String> studies) {
		if (studies == null || studies.isEmpty()) return false;
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
		return true;
	}

	private void setStudyThumbnails() {
		leftToolBar.clearThumnails();
		
		for ( StudyTree study : studies ){
			if ( study.getSeriesList().size() == 0 ) continue;
			
			leftToolBar.addStudyPanel(new StudyPanel(study, this));
		}
	}
	
	private void requestReport(){
		ReportInfo reportInfo = getReportInfo();
		if ( reportInfo == null ) return;
		
		DetallesCentrosManager centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );
		
		DetalleCentro detalleCentro = null; 
		try
		{
			detalleCentro = (DetalleCentro)centrosManager.getRow( context, 1 );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			return;
		}

		List<InformeImagen> imagenes = new ArrayList<InformeImagen>();
		InformeImagen imagen = new InformeImagen();
		imagen.setUrl( reportInfo.getUrl() );
		imagen.setIcon( reportInfo.getIcon() );
		imagenes.add( imagen );
		
		DetalleInforme informe = new DetalleInforme();

		informe.setCentro( detalleCentro.getId() );
		informe.setEstudio_acceso( reportInfo.getHeader().getAccessionNumber() );
		informe.setEstudio_id( reportInfo.getHeader().getStudyID() );
		informe.setEstudio_uid( reportInfo.getHeader().getStudyInstanceUID() );
		informe.setModalidad( reportInfo.getHeader().getModality() );
		informe.setPaciente_id( reportInfo.getHeader().getPatientID() );
		informe.setPaciente_nombre( reportInfo.getHeader().getPatientName() );
		informe.setCentro_ip( detalleCentro.getIp() );
		informe.setCentro_puerto( detalleCentro.getPuerto() );

		informe.setHorario_nombre( detalleCentro.getHorario_nombre() );
		
		informe.setEstado( 0 );
		informe.setProtegido( 0 );

		final ModalNewInforme report = new ModalNewInforme( context, Operation.OP_ADD, informe, imagenes, this, "informes.solicitar" );
		
		report.addCloseListener(
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -7551376683736330872L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					if ( report.isAdded() )
						showReports();
			    }
			}
		);
		report.showModalWindow();
	}
	
	private void showReports(){
		ReportsDlg dlg = new ReportsDlg( context );

		getUI().addWindow( dlg );
	}

	@Override
	public String currentImageUrl()
	{
		ReportInfo reportInfo = getReportInfo();
		
		String imageUrl = reportInfo != null ? Utils.getEnviroment( "CLOUD_URL" ) + "/imedig-cloud" + reportInfo.getUrl() : null;
		
		LOG.info(  "imageUrl " + imageUrl );
		
		return imageUrl;
	}

	@Override
	public void refreshVisibleContent()
	{
		// TODO Auto-generated method stub
		
	}

}
