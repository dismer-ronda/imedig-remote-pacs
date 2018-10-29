package es.pryades.imedig.viewer.components.query;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
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
import es.pryades.imedig.cloud.dto.viewer.Study;
import es.pryades.imedig.cloud.dto.viewer.User;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.Reports.ModalNewInforme;
import es.pryades.imedig.cloud.modules.Reports.ShowExternalUrlDlg;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.actions.OpenStudies;
import es.pryades.imedig.viewer.components.PageTable;
import es.pryades.imedig.viewer.datas.QueryTableItem;

public class QueryViewer extends VerticalLayout implements PageTable.PaginatorListener, SelectedStudyListener, ModalParent, Upload.SucceededListener, Upload.FailedListener, Upload.Receiver, Upload.StartedListener
{

	private static final Logger LOG = Logger.getLogger( QueryViewer.class );
	private static final long serialVersionUID = 2949836327715684727L;

	private TextField fieldName;
	private TextField filedId;
	private ComboBox type;
	private ComboBox bydate;
	private Table tableEstudies;
	private BeanItemContainer<QueryTableItem> container;

	private Button btnOpenStudy;
	private Button btnViewReport;
	private Button btnReport;
	private Upload btnUpload;

	private PageTable paginator;
	private QueryTableModel model;
	private final User user;

	private Button btnQuery;

	private final ImedigContext context;
	
	private QueryTableItem itemSelected = null;
	
	private ByteArrayOutputStream reportStream;

	public static final Integer PAGE_SIZE = 4;

	public QueryViewer( ImedigContext context, User user ){
		this.context = context;
		this.user = user;

		setSizeFull();
		setMargin( true );
		setSpacing( true );
		buildComponents();
		btnQuery.click();
	}

	private void buildComponents()
	{
		buildFiltros();
		buildTable();
		buildFooter();
	}

	private void buildFiltros()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );

		layout.addComponent( fieldName = new TextField( context.getString( "QueryForm.PatientName" ) ) );
		fieldName.setWidth( "150px" );
		layout.addComponent( filedId = new TextField( context.getString( "QueryForm.PatientId" ) ) );
		filedId.setWidth( "150px" );
		layout.addComponent( type = new ComboBox( context.getString( "QueryForm.Modality" ) ) );
		type.setNewItemsAllowed( false );
		type.setFilteringMode( FilteringMode.CONTAINS );
		type.setWidth( "100px" );
		initTypeList();
		layout.addComponent( bydate = new ComboBox( context.getString( "QueryForm.StudyDate" ) ) );
		bydate.setNewItemsAllowed( false );
		bydate.setFilteringMode( FilteringMode.CONTAINS );
		initByDateList();
		Component buttons = filterButtons();
		layout.addComponent( buttons );
		layout.setComponentAlignment( buttons, Alignment.BOTTOM_CENTER );
		addComponent( layout );
		setComponentAlignment( layout, Alignment.TOP_CENTER );
	}

	private void initTypeList()
	{
		String modList = context.getString( "QueryForm.Modalities" );

		String modalities[] = modList.split( "," );

		for ( String modality : modalities )
		{
			type.addItem( modality );
		}
	}

	private void initByDateList()
	{
		bydate.addItems( 1, 2, 3, 4, 5 );
		bydate.setItemCaption( 1, context.getString( "QueryForm.Today" ) );
		bydate.setItemCaption( 2, context.getString( "QueryForm.Yesterday" ) );
		bydate.setItemCaption( 3, context.getString( "QueryForm.LastWeek" ) );
		bydate.setItemCaption( 4, context.getString( "QueryForm.LastMonth" ) );
		bydate.setItemCaption( 5, context.getString( "QueryForm.LastYear" ) );
		bydate.setValue( user.getQuery() );
	}

	private Component filterButtons()
	{
		btnQuery = new Button();
		btnQuery.setDescription( context.getString( "QueryForm.QueryTip" ) );
		btnQuery.setImmediate( true );
		btnQuery.setIcon( FontAwesome.SEARCH );
		btnQuery.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		// btnQuery.addStyleName(ValoTheme.BUTTON_LARGE);
		btnQuery.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 4888989195136715235L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				onQuery();
			}
		} );

		HorizontalLayout layout = new HorizontalLayout( btnQuery );
		layout.setSpacing( true );
		return layout;
	}

	public void onQuery()
	{
		paginator.setPage( paginator.getPage() );
		paginator.setSize( PAGE_SIZE );

		model = new QueryTableModel( PAGE_SIZE );

		model.setPatientName( "*" + fieldName.getValue() + "*" );
		model.setPatientId( filedId.getValue() );
		model.setReferringPhysicianName( user.getFilter() );
		model.setStudyDate( convertDate( (Integer)bydate.getValue() ) );
		model.setModalitiesInStudy( (String)type.getValue() );

		int size = model.doQuery();

		paginator.resetTotal( size );
		refreshTable( model.getCurrentPage() );
	}

	private String convertDate( Integer index )
	{
		if ( index == null )
			return "";

		switch ( index )
		{
			case 1:
				return Utils.getTodayDate( "yyyyMMdd" );

			case 2:
				return Utils.getYesterdayDate( "yyyyMMdd" );

			case 3:
				return Utils.getLastWeekDate( "yyyyMMdd" ) + "-";

			case 4:
				return Utils.getLastMonthDate( "yyyyMMdd" ) + "-";

			case 5:
				return Utils.getLastYearDate( "yyyyMMdd" ) + "-";

			default:
				return "";
		}
	}

	private void buildTable()
	{
		container = new BeanItemContainer<>( QueryTableItem.class );
		tableEstudies = new Table();
		tableEstudies.setContainerDataSource( container );
		tableEstudies.setSizeFull();
		tableEstudies.setVisibleColumns( new String[]
		{ "selected", "studyReport", "studyDate", "modality", "patientId", "patientName", "patientAge", "referringPhysicianName" } );
		tableEstudies.setColumnHeaders( new String[]
		{ "", "", context.getString( "QueryForm.StudyDate" ), context.getString( "QueryForm.Modality" ), context.getString( "QueryForm.PatientId" ), context.getString( "QueryForm.PatientName" ), context.getString( "QueryForm.Age" ), context.getString( "QueryForm.Referrer" ) } );
		tableEstudies.setColumnAlignments( Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT );
		tableEstudies.setSelectable( true );
		tableEstudies.setMultiSelect( false );
		tableEstudies.setSortEnabled( false );
		tableEstudies.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -8937046990158446607L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				btnOpenStudy.setEnabled( hasStudiesSelected() );
				
				itemSelected = (QueryTableItem)tableEstudies.getValue();
				if ( tableEstudies.getValue() == null ){
					btnViewReport.setEnabled( false );
					btnReport.setEnabled( false );
					btnUpload.setEnabled( false );
				} else {
					btnViewReport.setEnabled( ((QueryTableItem)tableEstudies.getValue()).isReport() );
					btnReport.setEnabled( !((QueryTableItem)tableEstudies.getValue()).isReport() );
					btnUpload.setEnabled( !((QueryTableItem)tableEstudies.getValue()).isReport() );
				}
			}
		} );

		tableEstudies.addItemClickListener( new ItemClickListener(){

			private static final long serialVersionUID = 7774918893866773038L;

			@Override
			public void itemClick( ItemClickEvent event ){
				if ( !event.isDoubleClick() )
					return;

				QueryTableItem item = (QueryTableItem)event.getItemId();

				if ( item != null ){
					context.sendAction( new OpenStudies( this, Arrays.asList( item.getStudy().getStudyInstanceUID() ) ) );
				}
			}
		} );

		addComponents( tableEstudies );
		setExpandRatio( tableEstudies, 1.0f );
	}

	private boolean hasStudiesSelected()
	{
		return tableEstudies.getValue() != null || !getSeletedStudies().isEmpty();
	}

	@Override
	public void onFirst(){
		model.firstPage();
		itemSelected = null;
		refreshTable( model.getCurrentPage() );
	}

	@Override
	public void onPrevious(){
		model.previousPage();
		itemSelected = null;
		refreshTable( model.getCurrentPage() );
	}

	@Override
	public void onNext(){
		model.nextPage();
		itemSelected = null;
		refreshTable( model.getCurrentPage() );
	}

	@Override
	public void onLast(){
		model.lastPage();
		itemSelected = null;
		refreshTable( model.getCurrentPage() );
	}

	private void buildFooter(){
		btnOpenStudy = buildButton( context.getString( "words.open" ) );
		btnOpenStudy.addClickListener( new ClickListener() {
			private static final long serialVersionUID = -585302886377865803L;

			@Override
			public void buttonClick( ClickEvent event ){
				context.sendAction( new OpenStudies( this, getSeletedStudies() ) );
			}
		} );

		btnViewReport = buildButton( context.getString( "modalNewReport.wndCaption.view" ) );
		btnViewReport.addClickListener( new ClickListener(){
			private static final long serialVersionUID = 3390872787511885394L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				QueryTableItem item = (QueryTableItem)tableEstudies.getValue();
				DetalleInforme informe = getStudyReport( item.getStudy().getStudyInstanceUID() );

				viewReportSelectedStudy( informe );
			}
		} );

		btnReport = buildButton( context.getString( "words.do.report" ) );
		btnReport.addClickListener( new ClickListener(){
			private static final long serialVersionUID = 1204288685379729587L;

			@Override
			public void buttonClick( ClickEvent event )	{
				QueryTableItem item = (QueryTableItem)tableEstudies.getValue();
				if (item != null) reportRequest( item );
			}
		} );

		btnUpload = new Upload( null, this );
		btnUpload.setImmediate( true );
		btnUpload.setButtonCaption( context.getString( "QueryForm.Upload.Report" ) );
		btnUpload.setEnabled( false );
		btnUpload.addStartedListener( this );
		btnUpload.addSucceededListener( this );
		btnUpload.addFailedListener( this );
		HorizontalLayout left = buildHorizontalLayout();
		left.addComponents( btnOpenStudy, btnViewReport, btnReport, btnUpload );
		HorizontalLayout right = buildHorizontalLayout();

		paginator = new PageTable( context );
		paginator.setListener( this );
		HorizontalLayout center = buildHorizontalLayout();
		center.addComponent( paginator );
		HorizontalLayout footer = new HorizontalLayout( left, center, right );
		footer.setComponentAlignment( left, Alignment.MIDDLE_LEFT );
		footer.setComponentAlignment( center, Alignment.MIDDLE_CENTER );
		footer.setComponentAlignment( right, Alignment.MIDDLE_RIGHT );
		footer.setMargin( false );
		footer.setWidth( "100%" );

		addComponent( footer );
	}

	private void viewReportSelectedStudy( final DetalleInforme informe ){
		if ( informe.aprobado() || informe.terminado() ){
			onDownloadReport( informe, 0, "", "", false );
		}else{
			InformesImagenesManager manager = IOCManager.getInstanceOf( InformesImagenesManager.class );
			InformeImagen query = new InformeImagen();
			query.setInforme( informe.getId() );

			String right = context.hasRight( "informes.crear" ) ? "informes.crear" : "informes.solicitar";

			List<InformeImagen> images;

			try	{
				images = manager.getRows( context, query );
			}catch ( Throwable e )	{
				e.printStackTrace();

				images = new ArrayList<InformeImagen>();
			}

			new ModalNewInforme( context, Operation.OP_MODIFY, informe, images, null, right ).showModalWindow();
		}
	}

	private void reportRequest( final QueryTableItem item ){
		DetallesCentrosManager centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );

		DetalleCentro detalleCentro = null;
		try{
			detalleCentro = (DetalleCentro)centrosManager.getRow( context, 1 );
		}catch ( Throwable e ){
			e.printStackTrace();
			return;
		}

		List<InformeImagen> imagenes = new ArrayList<InformeImagen>();
		

		DetalleInforme informe = new DetalleInforme();

		informe.setCentro( detalleCentro.getId() );
		informe.setEstudio_acceso( item.getStudy().getAccessionNumber() );
		informe.setEstudio_id( item.getStudy().getStudyID() );
		informe.setEstudio_uid( item.getStudy().getStudyInstanceUID() );
		informe.setModalidad( item.getStudy().getModalitiesInStudy() );
		informe.setPaciente_id( item.getStudy().getPatientID() );
		informe.setPaciente_nombre( item.getStudy().getPatientName() );
		informe.setCentro_ip( detalleCentro.getIp() );
		informe.setCentro_puerto( detalleCentro.getPuerto() );

		informe.setHorario_nombre( detalleCentro.getHorario_nombre() );

		informe.setEstado( 0 );
		informe.setProtegido( 0 );

		final ModalNewInforme report = new ModalNewInforme( context, Operation.OP_ADD, informe, imagenes, this, "informes.solicitar" );

		report.addCloseListener( new Window.CloseListener()	{
			private static final long serialVersionUID = -7551376683736330872L;

			@Override
			public void windowClose( CloseEvent e )	{
				if ( report.isAdded() )
					refreshVisibleContent();
			}
		} );
		report.showModalWindow();
	}

	private void onDownloadReport( DetalleInforme informe, Integer template, String orientation, String size, Boolean images ){
		long ts = new Date().getTime();

		String extra = "ts=" + ts + "&id=" + informe.getId() + "&orientation=" + orientation + "&size=" + size + "&template=" + template + "&images=" + images;
		String token = "token=" + Utils.getTokenString( informe.getId() + "" + ts, Settings.TrustKey );
		String code = "code=" + Utils.encrypt( extra, Settings.TrustKey );

		LOG.debug( "extra " + extra );

		String url = "/imedig-services/report/" + informe.getId() + "?" + token + "&" + code;
		String title = informe.getId() + "-" + informe.getPaciente_nombre();

		new ShowExternalUrlDlg( context, title, url ).showModalWindow();
	}

	private void showNewReport( Study study ){
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

		DetalleInforme informe = new DetalleInforme();

		informe.setCentro( detalleCentro.getId() );
		informe.setEstudio_acceso( study.getAccessionNumber() );
		informe.setEstudio_id( study.getStudyID() );
		informe.setEstudio_uid( study.getStudyInstanceUID() );
		informe.setModalidad( study.getModalitiesInStudy() );
		informe.setPaciente_id( study.getPatientID() );
		informe.setPaciente_nombre( study.getPatientName() );
		informe.setCentro_ip( detalleCentro.getIp() );
		informe.setCentro_puerto( detalleCentro.getPuerto() );

		informe.setHorario_nombre( detalleCentro.getHorario_nombre() );

		informe.setEstado( 0 );
		informe.setProtegido( 0 );

		final ModalNewInforme report = new ModalNewInforme( context, Operation.OP_ADD, informe, imagenes, this, "informes.solicitar" );

		report.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = -7551376683736330872L;

			@Override
			public void windowClose( CloseEvent e )
			{
				if ( report.isAdded() )
				{
					// showReports();
				}
			}
		} );
		report.showModalWindow();
	}

	private HorizontalLayout buildHorizontalLayout()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );
		layout.setMargin( false );

		return layout;
	}

	private Button buildButton( String caption )
	{
		Button button = new Button( caption );
		button.setEnabled( false );

		return button;
	}

	private void refreshTable( List<Study> studies )
	{
		container.removeAllItems();

		for ( Study study : studies )
		{
			QueryTableItem item = new QueryTableItem( context, study );

			DetalleInforme informe = getStudyReport( study.getStudyInstanceUID() );
			item.setReport( informe != null );
			if ( informe != null )
			{
				item.setStudyReport( AppUtils.getImgEstado( informe.getEstado() ) );
			}
			else
			{
				item.setStudyReport( getEmptyLabel() );
			}
			item.setListener( this );
			container.addItem( item );
		}
		if (itemSelected != null && studies.contains( itemSelected.getStudy() )){
			tableEstudies.setValue( itemSelected );
		}else{
			tableEstudies.setValue( null );
		}
		btnOpenStudy.setEnabled( hasStudiesSelected() );
	}

	private static Component getEmptyLabel()
	{
		Label label = new Label();
		label.setWidth( "-1px" );

		return label;
	}

	private DetalleInforme getStudyReport( String studyUID )
	{
		DetallesInformesManager informesManager = IOCManager.getInstanceOf( DetallesInformesManager.class );

		InformeQuery query = new InformeQuery();
		query.setEstudio_uid( studyUID );

		try
		{
			// Siempre retorna listado ordenado por fecha descendentemente
			List<DetalleInforme> informes = informesManager.getRows( context, query );
			if ( !informes.isEmpty() )
				return informes.get( 0 );

		}
		catch ( Throwable e )
		{
			LOG.error( "Error", e );
		}

		return null;
	}

	private List<String> getSeletedStudies(){
		List<String> result = getMultipleSeletedStudies();

		if ( result.isEmpty() ){
			QueryTableItem item = (QueryTableItem)tableEstudies.getValue();
			if ( item != null )
			{
				result.add( item.getStudy().getStudyInstanceUID() );
			}
		}

		return result;
	}

	private List<String> getMultipleSeletedStudies(){
		List<String> result = new ArrayList<>();

		for ( QueryTableItem item : container.getItemIds() ){
			if ( !item.getSelected().getValue() )
				continue;

			result.add( item.getStudy().getStudyInstanceUID() );
		}

		return result;
	}

	
	@Override
	public void selectStudy()
	{
		btnOpenStudy.setEnabled( hasStudiesSelected() );
	}

	@Override
	public void refreshVisibleContent(){
		int size = model.doQuery();
		int page = paginator.getPage();  	
		paginator.resetTotal( size );
		paginator.setPage( page );
		paginator.update();
		refreshTable( model.getCurrentPage() );
	}

	@Override
	public void uploadStarted( StartedEvent event )
	{
		if (!event.getMIMEType().contains( "pdf" )){
			btnUpload.interruptUpload();
			Notification.show( context.getString( "QueryForm.Error.Upload.Report.pdf" ), Notification.Type.ERROR_MESSAGE );
		}
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		reportStream = new ByteArrayOutputStream(); 
		
		return reportStream;
	}

	@Override
	public void uploadFailed( FailedEvent event )
	{
		LOG.error( "Upload Fail -> File: " + event.getFilename() );
		
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )	{
		
		//Item seleccionado
		QueryTableItem selected = (QueryTableItem)tableEstudies.getValue();
		
		//Obtener el centro
		DetallesCentrosManager centrosManager = (DetallesCentrosManager)IOCManager.getInstanceOf( DetallesCentrosManager.class );

		DetalleCentro detalleCentro = new DetalleCentro();
		try{
			detalleCentro = (DetalleCentro)centrosManager.getRow( context, 1 );
			reportStream.close();
		}catch ( Throwable e ){
			e.printStackTrace();
			return;
		}
		Informe informe = new Informe();
		informe.setFecha( Utils.getTodayAsLong( detalleCentro.getHorario_nombre() ) );
		informe.setPaciente_id( selected.getStudy().getPatientID() );
		informe.setPaciente_nombre( selected.getStudy().getPatientName() );
		informe.setEstudio_id( selected.getStudy().getStudyID() );
		informe.setEstudio_uid( selected.getStudy().getStudyInstanceUID() );
		informe.setModalidad( selected.getStudy().getModalitiesInStudy() );
		informe.setModalidad( selected.getStudy().getModalitiesInStudy() );
		informe.setCentro( detalleCentro.getId() );
		informe.setInforma( context.getUsuario().getId() );
		informe.setEstado( Informe.STATUS_APROVED );
		informe.setPdf( reportStream.toByteArray() );
		informe.setProtegido( 0 );
		
		
		InformesManager informesManager = (InformesManager) IOCManager.getInstanceOf( InformesManager.class );
		try{
			informesManager.setRow( context, null, informe );
			//informe = (Informe)informesManager.getRow( context, informe.getId() );
			//Informe clone = (Informe)Utils.clone( informe );
			//clone.setPdf( reportStream.toByteArray() );
			//informesManager.setRow( context, informe,  clone);
		}catch ( Throwable e ){
			e.printStackTrace();
			return;
		}

		btnUpload.setEnabled( false );
		btnReport.setEnabled( false );

		refreshVisibleContent();
	}
}
