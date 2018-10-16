package es.pryades.imedig.viewer.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.imedig.cloud.common.StudyUtils;
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
import es.pryades.imedig.viewer.actions.ChangeImageFrame;
import es.pryades.imedig.viewer.actions.CloseStudies;
import es.pryades.imedig.viewer.actions.ContrastAction;
import es.pryades.imedig.viewer.actions.DisableDistanceAction;
import es.pryades.imedig.viewer.actions.DistanceAction;
import es.pryades.imedig.viewer.actions.EnumActions;
import es.pryades.imedig.viewer.actions.EraseAction;
import es.pryades.imedig.viewer.actions.FontAction;
import es.pryades.imedig.viewer.actions.NoneAction;
import es.pryades.imedig.viewer.actions.NotFigures;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.actions.QueryStudies;
import es.pryades.imedig.viewer.actions.RequestReport;
import es.pryades.imedig.viewer.actions.RestoreAction;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;
import es.pryades.imedig.viewer.components.image.ImageCanvas;
import es.pryades.imedig.viewer.components.image.ImageSerieNavigator;
import es.pryades.imedig.viewer.components.query.FontsDlg;
import es.pryades.imedig.viewer.components.query.QueryDlg;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.query.QueryManager;

public class ViewerWnd extends CssLayout implements ListenerAction, ImageResource, ModalParent, ImageSerieNavigator {
	private static final long serialVersionUID = 9064212477269947546L;

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

	boolean replicate = false;
	boolean firstLine = true;

	private User user;

	private LeftToolBar leftToolBar;
	private ImageCanvas imageCanvas;
	private HorizontalLayout content;
	
	private final ImedigContext context;
	
	private QueryDlg queryDlg;
	
	private final boolean modeReport;
	
	private String currentSerie;
	private Integer currentIndex;
	private ArrayList<StudyTree> studies;
	private Map<String, List<ImageData>> seriesImages = new HashMap<>();
	
	public ViewerWnd(ImedigContext context, User user) {
		this( context, user, false );
	}
	
	public ViewerWnd(ImedigContext context, User user, boolean modeReport) {
		super();
		this.modeReport = modeReport;
		this.context = context;
		this.user = user;
		setSizeFull();
		buidComponent();
		
		init();
	}
	
	@Override
	public void attach(){
		super.attach();
		context.addListener( this );
	}
	
	@Override
	public void detach(){
		super.detach();
		context.removeListener( this );
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
		addComponent( content = new HorizontalLayout() );
		content.setSizeFull();
		
		leftToolBar = new LeftToolBar(context, this);
		content.addComponent(leftToolBar);
		content.setComponentAlignment(leftToolBar, Alignment.TOP_LEFT);
		imageCanvas = new ImageCanvas( context, user, this, this, !modeReport );
		content.addComponent(imageCanvas);
		content.setExpandRatio(imageCanvas, 1.0f);
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
			if (openStudies(((OpenStudies)action).getData())){
				if (modeReport){
					leftToolBar.buttonClose.setEnabled( false );
				}else{
					leftToolBar.studiesOpen();
				}
			}
		}else if (action instanceof CloseStudies) {
			closeStudies((CloseStudies)action);
		}else if (action instanceof OpenImage) {
			openImage(((OpenImage)action).getData());
		}else if (action instanceof AngleAction) {
			imageCanvas.settingAction( EnumActions.ANGLE );
		}else if (action instanceof DisableDistanceAction) {
			if (imageCanvas.getCurrentAction().equals( EnumActions.DISTANCE )){
				imageCanvas.settingAction( EnumActions.NONE );
				leftToolBar.selectedAction( leftToolBar.buttonNone );
			}
			leftToolBar.buttonDistance.setEnabled( false );
		}else if (action instanceof DistanceAction) {
			imageCanvas.settingAction( EnumActions.DISTANCE );
		}else if (action instanceof ZoomAction) {
			imageCanvas.settingAction( EnumActions.ZOOM );
		}else if (action instanceof UndoAction) {
			if (!imageCanvas.undoAction()){
				leftToolBar.buttonUndo.setEnabled( false );
				leftToolBar.buttonRestore.setEnabled( false );
			}
		}else if (action instanceof RestoreAction) {
			imageCanvas.restoreAction();
			leftToolBar.buttonUndo.setEnabled( false );
			leftToolBar.buttonRestore.setEnabled( false );
		}else if (action instanceof NoneAction) {
			imageCanvas.settingAction( EnumActions.NONE );
		}else if (action instanceof ContrastAction) {
			imageCanvas.settingAction( EnumActions.CONTRAST );
		}else if (action instanceof AddToUndoAction) {
			leftToolBar.buttonUndo.setEnabled( true );
			leftToolBar.buttonRestore.setEnabled( true );
		}else if (action instanceof EraseAction) {
			imageCanvas.clearFigures();
		}else if (action instanceof AddFigure) {
			leftToolBar.buttonErase.setEnabled( true );
		}else if (action instanceof NotFigures) {
			leftToolBar.buttonErase.setEnabled( false );
		}else if (action instanceof RequestReport) {
			requestReport();
		}else if (action instanceof ChangeImageFrame) {
			leftToolBar.changeImageFrame( ((ChangeImageFrame)action).getData());
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
		boolean enabled = leftToolBar.buttonUndo.isEnabled();
		enabledButtons();
		leftToolBar.buttonUndo.setEnabled( enabled );
		leftToolBar.buttonRestore.setEnabled( enabled );
		
		currentSerie = data.getSeries().getSeriesData().getSeriesInstanceUID();
		currentIndex = seriesImages.get( currentSerie ).indexOf( data );
		
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
	
	private void closeStudies(CloseStudies closeStudies)	{
		if (closeStudies.getData() == null){
			closeAllStydies();
			return;
		}
		
		leftToolBar.removeStudyPanel( (StudyPanel)closeStudies.getSource() );
		StudyTree study = closeStudies.getData();
		studies.remove( study );
		if (imageCanvas.getImageData()!= null && study == imageCanvas.getImageData().getStudy()){
			imageCanvas.clear();
			leftToolBar.allButtonsDisable();
			
			if (leftToolBar.studyCount > 0){
				leftToolBar.studiesOpen();
			}
		}
	}
	
	private void closeAllStydies(){
		leftToolBar.clearThumnails();
		leftToolBar.allButtonsDisable();
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

			if ( study != null && study.getSeriesList().size() != 0 ){
				this.studies.add( study );
				for ( String serieUID : StudyUtils.getSeriesUID( study ) )
				{
					seriesImages.put( serieUID, StudyUtils.readSeriesImageData( serieUID,  study ) );
				}
			}
		}
		setStudyThumbnails();
		return true;
	}

	private void setStudyThumbnails() {
		leftToolBar.clearThumnails();
		
		for ( StudyTree study : studies ){
			StudyPanel panel = new StudyPanel(context);
			Map<String, List<ImageData>> datas = new HashMap<>();
			for ( String serieUID : StudyUtils.getSeriesUID( study ) )
			{
				datas.put( serieUID, seriesImages.get( serieUID ) );
			}
			
			panel.setStudy( study, datas );
			
			leftToolBar.addStudyPanel(panel);
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
		
		String imageUrl = reportInfo != null ? Utils.getEnviroment( "CLOUD_URL" ) + reportInfo.getUrl() : null;
		
		LOG.info(  "imageUrl " + imageUrl );
		
		return imageUrl;
	}

	@Override
	public void refreshVisibleContent()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ImageData getFirstImageSerie(){
		currentIndex = 0;
		
		return seriesImages.get( currentSerie ).get( currentIndex );
	}

	@Override
	public ImageData getPreviousImageSerie(){
		if (currentIndex == -1) return null;
		
		if (currentIndex == 0) return null;
		
		--currentIndex;
		
		return seriesImages.get( currentSerie ).get( currentIndex );
	}

	@Override
	public ImageData getNextImageSerie(){
		if (currentIndex == -1) return null;

		if (currentIndex == (seriesImages.get( currentSerie ).size()-1)) return null;
		
		++currentIndex;
		
		return seriesImages.get( currentSerie ).get( currentIndex );
	}


	@Override
	public ImageData getLastImageSerie(){
		currentIndex = seriesImages.get( currentSerie ).size()-1;
		
		return seriesImages.get( currentSerie ).get( currentIndex );
	}

	@Override
	public boolean containImagesSeries()
	{
		if (currentIndex == -1) return false;
		
		return !((currentIndex == 0) && (currentIndex == seriesImages.get( currentSerie ).size()-1));
	}


	@Override
	public boolean hasPriorImageSerie()
	{
		return currentIndex != 0;
	}


	@Override
	public boolean hasNextImageSerie(){
		return currentIndex != seriesImages.get( currentSerie ).size()-1;
	}

}
