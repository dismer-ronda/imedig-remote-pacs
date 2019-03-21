package es.pryades.imedig.viewer.components;

import static es.pryades.imedig.cloud.common.Constants.DERECHO_INFORMES_CREAR;
import static es.pryades.imedig.cloud.common.Constants.DERECHO_INFORMES_SOLICITAR;

import java.util.ArrayList;
import java.util.Date;
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

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.MessageBoxListener;
import es.pryades.imedig.cloud.common.MessageBoxUtils;
import es.pryades.imedig.cloud.common.StudyUtils;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.action.Action;
import es.pryades.imedig.cloud.core.action.ImageResource;
import es.pryades.imedig.cloud.core.action.ListenerAction;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.DetallesInformesManager;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.Reports.ShowExternalUrlDlg;
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
import es.pryades.imedig.viewer.actions.ReportRequest;
import es.pryades.imedig.viewer.actions.RestoreAction;
import es.pryades.imedig.viewer.actions.UndoAction;
import es.pryades.imedig.viewer.actions.ZoomAction;
import es.pryades.imedig.viewer.components.image.ImageCanvas;
import es.pryades.imedig.viewer.components.image.ImageSerieNavigator;
import es.pryades.imedig.viewer.components.query.FontsDlg;
import es.pryades.imedig.viewer.datas.ImageData;
import es.pryades.imedig.wado.query.QueryManager;

public class ViewerWnd extends CssLayout implements ListenerAction, ImageResource, ModalParent, ImageSerieNavigator, MessageBoxListener {
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
		
		context.addListener( this );
	}
	
	@Override
	protected void finalize() throws Throwable{
		context.removeListener( this );
	}
	
	@Override
	public void detach(){
		super.detach();
		if (modeReport) context.removeListener( this );
	}
	
	
	
	private void init(){
		studies = new ArrayList<>();
		if (modeReport){
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
		
		if (action instanceof FontAction) {
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
		}else if (action instanceof ReportRequest) {
			reportRequest();
		}else if (action instanceof ChangeImageFrame) {
			leftToolBar.changeImageFrame( ((ChangeImageFrame)action).getData());
		}
		
	}

	private void enabledButtons()
	{
		leftToolBar.studyInViewer();
		if (modeReport){
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
		if (seriesImages.get( currentSerie ) == null) return;
		
		currentIndex = seriesImages.get( currentSerie ).indexOf( data );
		
		imageCanvas.openImage(data);
	}
	
	private void queryFonts() {
		FontsDlg dlg = new FontsDlg();
		UI.getCurrent().addWindow(dlg);
	}
	
	private void closeStudies(CloseStudies closeStudies)	{
		if (closeStudies.getData() == null){
			closeAllStudies();
			context.sendAction( new QueryStudies( this ) );
			return;
		}
		
		leftToolBar.removeStudyPanel( (StudyPanel)closeStudies.getSource() );
		StudyTree study = closeStudies.getData();
		studies.remove( study );
		if (studies.isEmpty()) context.sendAction( new QueryStudies( this ) );
		
		if (imageCanvas.getImageData()!= null && study == imageCanvas.getImageData().getStudy()){
			imageCanvas.clear();
			leftToolBar.allButtonsDisable();
			
			if (leftToolBar.studyCount > 0){
				leftToolBar.studiesOpen();
			}
		}
	}
	
	private void closeAllStudies(){
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

			if ( study != null && study.getSeriesList().size() != 0 && !this.studies.contains( study )){
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
	
	private void reportRequest(){
		
		if (countStudyReport()>0){
			showConfirmationNewReport();
		}else{
			showNewReport();
		}
	}
	
	private int countStudyReport(){
		InformesManager informesManager = IOCManager.getInstanceOf( InformesManager.class );
		
		ReportInfo reportInfo = getReportInfo();
		
		InformeQuery query = new InformeQuery();
		query.setEstudio_uid( reportInfo.getHeader().getStudyInstanceUID() );
		
		try
		{
			return informesManager.getNumberOfRows( context, query );
		}
		catch ( Throwable e ){
			LOG.error( "Error", e);
		}
		
		return 0;
	}
	
	private DetalleInforme getStudyReport(){
		DetallesInformesManager informesManager = IOCManager.getInstanceOf( DetallesInformesManager.class );
		
		ReportInfo reportInfo = getReportInfo();
		
		InformeQuery query = new InformeQuery();
		query.setEstudio_uid( reportInfo.getHeader().getStudyInstanceUID() );
		
		try{
			//Siempre retorna listado ordenado por fecha descendentemente
			List<DetalleInforme> informes = informesManager.getRows( context, query );
			return informes.get( 0 );
			
		}catch ( Throwable e ){
			LOG.error( "Error", e);
		}
		
		return null;
	}
	
	private static boolean isToShow(Informe informe){
		return informe.aprobado() || informe.terminado();
	}
	
	private void showNewReport(){
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
		//context.sendAction( new ShowReportsListAction( this ) );
	}

	@Override
	public String currentImageUrl()
	{
		ReportInfo reportInfo = getReportInfo();
		
		String imageUrl = reportInfo != null ? context.getData( "Url") + reportInfo.getUrl() : null;
		
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
	
	private void showConfirmationNewReport(){
		MessageBox messageBox = MessageBoxUtils.showMessageBox( context.getResources(), 
				Icon.NONE,
				context.getString( "words.confirm" ), 
				context.getString( "ViewerWnd.confirm.study.report.exist" ), this, ButtonId.CUSTOM_1, ButtonId.CUSTOM_2, ButtonId.CUSTOM_3);
		messageBox.getButton( ButtonId.CUSTOM_1 ).setCaption( context.getString( "ViewerWnd.open.exists" ) );
		messageBox.getButton( ButtonId.CUSTOM_2 ).setCaption( context.getString( "ViewerWnd.request.new" ) );
		messageBox.getButton( ButtonId.CUSTOM_3 ).setCaption( context.getString( "words.cancel" ) );
	}

	@Override
	public void buttonClicked( ButtonId buttonId )
	{
		if (buttonId == ButtonId.CUSTOM_1){
			DetalleInforme informe = getStudyReport();
			if (informe == null) return;
				
			if (isToShow( informe )){
				showAprovedReport( informe, 0, "", "", false );
			}else{
				modifyReport( informe );
			}
			
			
		}else if (buttonId == ButtonId.CUSTOM_2){
			showNewReport();
		}
	}

	private void showAprovedReport( Informe informe, Integer template, String orientation, String size, Boolean images ){
		long ts = new Date().getTime();
		
		String extra = "ts=" + ts + 
						"&id=" + informe.getId() + 
						"&orientation=" + orientation + 
						"&size=" + size + 
						"&template=" + template +
						"&images=" + images;
		String token = "token=" + Utils.getTokenString( informe.getId() + "" + ts, Settings.TrustKey );
		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey ) ;
		
		LOG.debug( "extra " +  extra );
		
		String url =  "/imedig-services/report/" + informe.getId() + "?" + token + "&" + code;
		String title = informe.getId() + "-" + informe.getPaciente_nombre();
		
		new ShowExternalUrlDlg( context, title, url ).showModalWindow();
	}
	
	private void modifyReport( DetalleInforme informe  ){
		
		InformesImagenesManager manager = IOCManager.getInstanceOf( InformesImagenesManager.class );
		InformeImagen query = new InformeImagen();
		query.setInforme( informe.getId() );
		
		String right = context.hasRight( DERECHO_INFORMES_CREAR ) ? DERECHO_INFORMES_CREAR : DERECHO_INFORMES_SOLICITAR;
		
		List<InformeImagen> images;
		
		try
		{
			images = manager.getRows( context, query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			images = new ArrayList<InformeImagen>();
		}
		
		new ModalNewInforme( context, Operation.OP_MODIFY, informe, images, null, right ).showModalWindow();
	}
}
