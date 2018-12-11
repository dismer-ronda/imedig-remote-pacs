package es.pryades.imedig.cloud.modules.Reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBoxListener;
import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.MessageBoxUtils;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.core.reports.PdfExport;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.InformeQuery;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD.Operation;
import es.pryades.imedig.cloud.vto.InformeVto;
import es.pryades.imedig.cloud.vto.controlers.InformeControlerVto;
import es.pryades.imedig.cloud.vto.refs.InformeVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings({"unchecked"})
public class ReportsViewer extends FilteredContent implements ModalParent, Property.ValueChangeListener, MessageBoxListener
{
	private static final long serialVersionUID = -3588907063210926036L;

	private static final Logger LOG = Logger.getLogger( ReportsViewer.class );

	@Setter @Getter private Integer fecha;
	@Setter @Getter private String paciente;
	@Setter @Getter private String estudio;
	@Setter @Getter private Integer centro;
	@Setter @Getter private Integer estado;
	@Setter @Getter private String icd10cm;
	@Setter @Getter private String claves;
	@Setter @Getter private Integer refiere;
	@Setter @Getter private Integer informa;
	@Setter @Getter private String modalidad;
	
	private BeanItem<ReportsViewer> bi;
	
	private Button btnView;
	private Button btnApprove;
	private Button btnFinish;
	private ComboBox comboRefiere;
	private ComboBox comboStatus;

	private InformesManager informesManager;
	private InformesImagenesManager informeImagenesManager;
	
	private static final String COMBO_WIDTH = "200px";
	private static final String TEXT_WIDTH = "200px";
	
	private static final Integer PERFIL_IMAGENOLOGO = 3;
	private static final Integer PERFIL_USUARIO = 2;
	
	private boolean defaultSearch;
	private boolean showMessage;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public ReportsViewer( ImedigContext ctx, boolean defaultSearch )
	{
		super( ctx );
		
		informesManager = (InformesManager) IOCManager.getInstanceOf( InformesManager.class );
		informeImagenesManager = (InformesImagenesManager) IOCManager.getInstanceOf( InformesImagenesManager.class );
		
		this.defaultSearch = defaultSearch;
		this.showMessage = defaultSearch;
		
		setSizeFull();
		setMargin( true );
		initComponents();
		
		if (showMessage){
			showNotification();
		}
	}
	
	public String[] getVisibleCols()
	{
		return InformeControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "ReportsDlg.tableReports";
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( InformeVto.class, new InformeVto(), new InformeVtoFieldRef(), new QueryFilterRef( new InformeQuery() ), getContext(), Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String getTabTitle()
	{
		return getContext().getString( "words.reports" );
	}
	
	public List<Component> getExtraOperations()
	{
		List<Component> extras = new ArrayList<Component>();
		
		btnView = new Button(getContext().getString( "words.view" ));
		btnView.setEnabled( false );
		bttnViewReportListener();
		extras.add( btnView );
	
		btnApprove = new Button(getContext().getString( "words.approve" ));
		btnApprove.setEnabled( false );
		bttnApproveReportListener();
		extras.add( btnApprove );
	
		btnFinish = new Button(getContext().getString( "words.finish" ));
		btnFinish.setEnabled( false );
		bttnFinishReportListener();
		extras.add( btnFinish );

		return extras;
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		if ( row != null )
		{
			DetalleInforme informe = (DetalleInforme)row;

			setEnabledModify( canModifyReport( informe ) );
			setEnabledDelete( canModifyReport( informe ) );

			btnView.setEnabled( canViewReport( informe ) );
			btnApprove.setEnabled( canApproveReport( informe ) );
			btnFinish.setEnabled( canFinishReport( informe ) );
		}
		else
		{
			setEnabledModify( false );
			setEnabledDelete( false );
			
			btnView.setEnabled( false );
			btnApprove.setEnabled( false );
			btnFinish.setEnabled( false );
		}
	}

	@Override
	public Query getQueryObject()
	{
		InformeQuery query = null;
		if (defaultSearch){
			query = defaultQuery();
		}else{
			query = getQuery();
		}
		defaultSearch = false;
		
		return query;
	}
	
	private InformeQuery getQuery(){
		InformeQuery queryObj = new InformeQuery();

		setDateFilter( queryObj );
		
		if ( centro != null )
			queryObj.setCentro( centro );
		else
			queryObj.setCentros( getContext().getCentros() );
		
		if ( !paciente.isEmpty() )
			queryObj.setPaciente_id( paciente );
		if ( !estudio.isEmpty() )
			queryObj.setEstudio_id( estudio );
		if ( !icd10cm.isEmpty() )
			queryObj.setIcd10cm( icd10cm );
		
		List<String> listClaves = getClavesAsList( this.claves );
		
		if ( listClaves != null )
			queryObj.setList_claves( listClaves );
		
		if ( estado != null )
			queryObj.setEstado( estado == -1 ? null : estado );

		if ( refiere != null  )
			queryObj.setRefiere( refiere );
		if ( informa != null  )
			queryObj.setInforma( informa );

		if ( modalidad != null  )
			queryObj.setModalidad( modalidad );

		return queryObj;
	}
	
	private InformeQuery defaultQuery(){
		InformeQuery query = new InformeQuery();
		setDateFilter( query );
		
		if (getContext().hasProfile( PERFIL_IMAGENOLOGO )){
			query.setEstados( Arrays.asList( Informe.STATUS_INFORMED, Informe.STATUS_REQUESTED ) );
		}else if (getContext().hasProfile( PERFIL_USUARIO )){
			query.setEstados( Arrays.asList( Informe.STATUS_APROVED ) );
			query.setRefiere( getContext().getUsuario().getId() );
			comboRefiere.setValue( getContext().getUsuario().getId() );
			comboStatus.setValue( Informe.STATUS_APROVED );
		}else{
			query = getQuery();
		}
		
		return query;
	}

	private boolean canViewReport( DetalleInforme informe )
	{
		if ( informe.solicitado() && getContext().hasRight( "informes.solicitar" ) )
			return true;

		if ( getContext().hasRight( "informes.aprobar" ) )
			return true;

		if ( informe.aprobado() || informe.terminado() )
		{
			if ( informe.getProtegido() == 0 )
				return true;
			
			return informe.getRefiere() == null || getContext().getUsuario().getId().equals( informe.getRefiere() ) || getContext().getUsuario().getId().equals( informe.getInforma() );
		}

		return getContext().getUsuario().getId().equals( informe.getInforma() );
	}

	private boolean canModifyReport( DetalleInforme informe )
	{
		if ( informe.aprobado() || informe.terminado() )
			return false;
		
		if ( informe.solicitado() && getContext().hasRight( "informes.solicitar" ) )
			return true;
		
		return informe.getInforma().equals( getContext().getUsuario().getId() ) || getContext().hasRight( "informes.aprobar" );
	}

	private boolean canApproveReport( DetalleInforme informe )
	{
		return !informe.aprobado() && !informe.terminado() && getContext().hasRight( "informes.aprobar" );
	}

	private boolean canFinishReport( DetalleInforme informe )
	{
		return informe.aprobado() && getContext().hasRight( "informes.terminar" );
	}

	private void bttnViewReportListener()
	{
		btnView.addClickListener( new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 5008648404760810318L;

			public void buttonClick( ClickEvent event )
			{
				DetalleInforme informe = (DetalleInforme)getSelectedRow();
				
				if ( informe != null )
					showReportAsPdf( informe );
			}
		} );
	}

	public void onModifyRow( Object row )
	{
		DetalleInforme informe = (DetalleInforme)row;
		
		InformeImagen query = new InformeImagen();
		query.setInforme( informe.getId() );
		
		String right = getContext().hasRight( "informes.crear" ) ? "informes.crear" : "informes.solicitar";
		
		List<InformeImagen> images;
		
		try
		{
			images = informeImagenesManager.getRows( getContext(), query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			images = new ArrayList<InformeImagen>();
		}
		
		new ModalNewInforme( getContext(), Operation.OP_MODIFY, informe, images, ReportsViewer.this, right ).showModalWindow();
	}

	public void onDeleteRow( Object row )
	{
		DetalleInforme informe = (DetalleInforme)row;
		
		InformeImagen query = new InformeImagen();
		query.setInforme( informe.getId() );
		
		List<InformeImagen> images;
		
		try
		{
			images = informeImagenesManager.getRows( getContext(), query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			
			images = new ArrayList<InformeImagen>();
		}

		new ModalNewInforme( getContext(), Operation.OP_DELETE, informe, images, ReportsViewer.this, "informes.crear" ).showModalWindow();
	}
	
	private void onDownloadReport( DetalleInforme informe, Integer template, String orientation, String size, Boolean images )
	{
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
		
		new ShowExternalUrlDlg( getContext(), title, url ).showModalWindow();
	}

	private void showReportAsPdf( final DetalleInforme informe )
	{
		if ( informe.aprobado() || informe.terminado() )
		{
			onDownloadReport( informe, 0, "", "", false );
		}
		else
		{
			final ReportConfigDlg dlg = new ReportConfigDlg( getContext(), getContext().getString( "ReportConfigDlg.title.show" ), informe );
			
			dlg.addCloseListener
			( 
				new Window.CloseListener() 
				{
					/**
					 * 
					 */
					private static final long serialVersionUID = 5694973426557182244L;

					@Override
				    public void windowClose( CloseEvent e ) 
				    {
						if ( dlg.isAccepted() )
							onDownloadReport( informe, dlg.getTemplate(), dlg.getOrientation(), dlg.getPagesize(), dlg.getImages() );
				    }
				}
			);
			
			dlg.showModalWindow();
		}
	}

	private byte[] getReportPdf( DetalleInforme informe, Integer template, String orientation, String size, Boolean images )
	{
		ImedigContext ctx = new ImedigContext();
    	
		PdfExport export = new PdfExport();
		
		if ( images.booleanValue() )
		{
			InformeImagen query = new InformeImagen();
			query.setInforme( informe.getId() );
			
			List<InformeImagen> imagenes = null;
			
			try
			{
				imagenes = informeImagenesManager.getRows( ctx, query );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
	
			for ( InformeImagen imagen : imagenes )
				imagen.setUrl( context.getData( "Url") + imagen.getUrl() );
		
			export.setImagenes( imagenes );
		}
		
		export.setInforme( informe );
		export.setOrientation( orientation );
		export.setSize( size );
		export.setTemplate( template );
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	
		try
		{
			export.doExport( ctx, os );

			os.close();
			
			informe.setPdf( os.toByteArray() );
			
			return os.toByteArray();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}
	
	private void bttnApproveReportListener()
	{
		btnApprove.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -7204444384289336421L;

			public void buttonClick( ClickEvent event ){
				confirmarAprobar();
			}
		});
	}
	
	private void confirmarAprobar(){
		MessageBoxUtils.showMessageBox( getContext().getResources(), 
				Icon.NONE,
				getContext().getString( "words.confirm" ), 
				getContext().getString( "ReportsDlg.confirm.approve" ), this, ButtonId.YES, ButtonId.NO );
	}

	private void bttnFinishReportListener()
	{
		btnFinish.addClickListener( new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -6691221645866218646L;

			public void buttonClick( ClickEvent event )
			{
				try
				{
					DetalleInforme informe = (DetalleInforme)getSelectedRow();

					DetalleInforme clone = new DetalleInforme();

					clone.setId( informe.getId() );
					clone.setFecha( informe.getFecha() );
					clone.setPaciente_id( informe.getPaciente_id() );
					clone.setPaciente_nombre( informe.getPaciente_nombre() );
					clone.setEstudio_id( informe.getEstudio_id() );
					clone.setEstudio_uid( informe.getEstudio_uid() );
					clone.setEstudio_acceso( informe.getEstudio_acceso() );
					clone.setModalidad( informe.getModalidad() );
					clone.setClaves( informe.getClaves() );
					clone.setTexto( informe.getTexto() );
					clone.setCentro( informe.getCentro() );
					clone.setInforma( informe.getInforma() );
					clone.setIcd10cm( informe.getIcd10cm() );
					clone.setCentro( informe.getCentro() );
					clone.setRefiere( informe.getRefiere() );
					clone.setEstado( Informe.STATUS_FINISHED );
					clone.setProtegido( informe.getProtegido() );
					
					informesManager.setRow( getContext(), informe, clone );
					
					refreshVisibleContent();
				}
				catch ( Throwable e )
				{
					Utils.logException( e, LOG );
				}
			}
		} );
	}
	
	public void setDateFilter( InformeQuery queryObj )
	{
		switch ( fecha )
		{
			case 1:
				queryObj.setDesde( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 2:
				queryObj.setDesde( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getYesterdayDate("yyyyMMdd") + "235959" ) );
			break;
			
			case 3:
				queryObj.setDesde( Long.parseLong( Utils.getLastWeekDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 4:
				queryObj.setDesde( Long.parseLong( Utils.getLastMonthDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;

			case 5:
				queryObj.setDesde( Long.parseLong( Utils.getLastYearDate("yyyyMMdd") + "000000" ) );
				queryObj.setHasta( Long.parseLong( Utils.getTodayDate("yyyyMMdd") + "235959" ) );
			break;
			
			default:
			break;
		}
	}
	
	private List<String> getClavesAsList( String claves )
	{
		StringTokenizer tokenizer = new StringTokenizer( claves, " " );
		
		if ( tokenizer.countTokens() > 0 )
		{
			ArrayList<String> ret = new ArrayList<String>();
			while ( tokenizer.hasMoreElements() ) 
				ret.add( tokenizer.nextToken() );
			
			return ret;
		}
		
		return null;
	}
	
	private Component getQueryFecha()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.date" ));
		combo.setWidth( COMBO_WIDTH );
		combo.setNullSelectionAllowed( false );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "fecha" ) );
		combo.addItem( 0 );
		combo.setItemCaption( 0, getContext().getString( "words.all" ) );
		combo.addItem( 1 );
		combo.setItemCaption( 1, getContext().getString( "words.today" ) );
		combo.addItem( 2 );
		combo.setItemCaption( 2, getContext().getString( "words.yesterday" ) );
		combo.addItem( 3 );
		combo.setItemCaption( 3, getContext().getString( "words.lastweek" ) );
		combo.addItem( 4 );
		combo.setItemCaption( 4, getContext().getString( "words.lastmonth" ) );
		combo.addItem( 5 );
		combo.setItemCaption( 5, getContext().getString( "words.lastyear" ) );
		
		return getRow( combo );
	}

	private Component getQueryPaciente()
	{
		TextField text = new TextField( getContext().getString( "words.patient" ), bi.getItemProperty( "paciente" ) );
		text.setNullRepresentation( "" );
		text.setWidth( TEXT_WIDTH );
		return getRow( text );
	}

	private Component getQueryEstudio()
	{
		TextField text = new TextField( getContext().getString( "words.study" ), bi.getItemProperty( "estudio" ) );
		text.setNullRepresentation( "" );
		text.setWidth( TEXT_WIDTH );
		return getRow( text );
	}

	/*private void fillComboCentro( ComboBox comboCentro )
	{
		DetallesCentrosManager centrosManager = (DetallesCentrosManager) IOCManager.getInstanceOf( DetallesCentrosManager.class );

		SqlSession session = getContext().getSessionCloud();

		boolean finish = ( session == null );

		try
		{
			if ( finish )
				session = getContext().openSessionCloud();

			try
			{
				CentroQuery query = new CentroQuery();
				
				query.setUsuario( getContext().getUsuario().getId() );
				
				List<DetalleCentro> centros = centrosManager.getRows( getContext(), query );

				for ( DetalleCentro centro : centros )
				{
					comboCentro.addItem( centro.getId() );
					comboCentro.setItemCaption( centro.getId(), centro.getNombre() );
				}
			}
			catch ( ImedigException e )
			{
			}
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					getContext().closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	}

	private Component getQueryCentro()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.center" ));
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "centro" ) );
		combo.setWidth( COMBO_WIDTH );

		fillComboCentro( combo );
		
		return getRow( combo );
	}*/

	private Component getQueryEstado()
	{
		comboStatus = new ComboBox(getContext().getString( "words.status" )); 
		comboStatus.setWidth( COMBO_WIDTH );
		comboStatus.setNullSelectionAllowed( false );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setPropertyDataSource( bi.getItemProperty( "estado" ) );
		
		comboStatus.addItem( -1 );
		comboStatus.setItemCaption( -1, getContext().getString( "words.all" ) );
		comboStatus.addItem( Informe.STATUS_REQUESTED );
		comboStatus.setItemCaption( Informe.STATUS_REQUESTED, getContext().getString( "words.requested" ) );
		comboStatus.addItem( Informe.STATUS_INFORMED );
		comboStatus.setItemCaption( Informe.STATUS_INFORMED, getContext().getString( "words.not.approved" ) );
		comboStatus.addItem( Informe.STATUS_APROVED );
		comboStatus.setItemCaption( Informe.STATUS_APROVED, getContext().getString( "words.approved" ) );		
		comboStatus.addItem( Informe.STATUS_FINISHED );
		comboStatus.setItemCaption( Informe.STATUS_FINISHED, getContext().getString( "words.finished" ) );
		
		return getRow( comboStatus );
	}

	public Component getQueryCodigo()
	{
		TextField text = new TextField( getContext().getString( "words.icd10cm" ), bi.getItemProperty( "icd10cm" ) );
		text.setNullRepresentation( "" );
		text.setWidth( TEXT_WIDTH );
		return getRow( text );
	}

	public Component getQueryClaves()
	{
		TextField text = new TextField( getContext().getString( "words.keywords" ), bi.getItemProperty( "claves" ) );
		text.setNullRepresentation( "" );
		text.setWidth( TEXT_WIDTH );
		return getRow( text );
	}

	private void fillComboMedicos( ComboBox comboCentro )
	{
		UsuariosManager usuariosManager = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );

		SqlSession session = getContext().getSessionCloud();

		boolean finish = ( session == null );

		try
		{
			if ( finish )
				session = getContext().openSessionCloud();

			try
			{
				UsuarioQuery query = new UsuarioQuery();
				
				query.setCentros( getContext().getCentros() );
				
				List<Usuario> usuarios = usuariosManager.getRows( getContext(), query );

				for ( Usuario usuario : usuarios )
				{
					comboCentro.addItem( usuario.getId() );
					comboCentro.setItemCaption( usuario.getId(), usuario.getNombreCompleto() );
				}
			}
			catch ( ImedigException e )
			{
			}
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			Utils.logException( e, LOG );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					getContext().closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	}

	public Component getQueryRefiere()
	{
		comboRefiere = new ComboBox(getContext().getString( "words.refer" ));
		comboRefiere.setNullSelectionAllowed( true );
		comboRefiere.setTextInputAllowed( false );
		comboRefiere.setPropertyDataSource( bi.getItemProperty( "refiere" ) );
		comboRefiere.setWidth( COMBO_WIDTH );
		
		fillComboMedicos( comboRefiere );
		
		return getRow( comboRefiere );
	}

	public Component getQueryInforma()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.report" ));
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "informa" ) );
		combo.setWidth( COMBO_WIDTH );
		
		fillComboMedicos( combo );

		return getRow( combo );
	}

	public Component getQueryModalidad()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.modality" )); 
		combo.setWidth( COMBO_WIDTH );
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "modalidad" ) );
		
		String modalities[] = {"DX","CR","MR","CT","US","NM","XA","ES","PT","MG","RF","OT"};
		
		for ( String modality : modalities )
		{
			combo.addItem( modality );
			combo.setItemCaption( modality, getContext().getString( "words.modality." + modality ) );
		}
		
		return getRow( combo );
	}


	@Override
	public Component getQueryComponent()
	{
		fecha = 4;
		paciente = "";
		estudio = "";
		estado = -1;
		icd10cm = "";
		claves = "";
		
		bi = new BeanItem<ReportsViewer>( this );
	
		CssLayout query = new CssLayout();
		query.setWidth( "100%" );
		query.addComponents( getQueryFecha(), getQueryEstado(), getQueryRefiere(), 
				getQueryInforma(), /*getQueryCentro(),*/ getQueryModalidad(),
				getQueryEstudio(), getQueryPaciente(), getQueryCodigo(), getQueryClaves());
		return query;
		
	}

	@Override
	public void onAddRow()
	{
	}

	@Override
	public boolean isAddAvailable()
	{
		return false;
	}

	@Override
	public boolean isModifyAvailable()
	{
		return true;
	}
	
	@Override
	public boolean isDeleteAvailable()
	{
		return true;
	}

	@Override
	public boolean isExtrasAvailable()
	{
		return true;
	}
	
	private static final Component getRow(Component component){
		FormLayout layout = new FormLayout( component );
		layout.setMargin( new MarginInfo( false, true, false, false ) );
		layout.setSpacing( false );
		layout.setWidthUndefined();
		layout.addStyleName( ImedigTheme.FILTER_MARGIN );
		return layout;
	}
	
	private void showNotification(){
		Notification notification = new Notification( null, /*getContext().getString( "words.information")*/ 
				getContext().getString( "ReportsDlg.message.reports.attention" ), 
				Notification.Type.HUMANIZED_MESSAGE );
		notification.setDelayMsec(Notification.DELAY_FOREVER);
		notification.setStyleName( "info" );
		notification.show( Page.getCurrent() );
	}

	@Override
	public void buttonClicked( ButtonId buttonId )
	{
		if (buttonId == ButtonId.NO) return;
		
		final DetalleInforme informe = (DetalleInforme)getSelectedRow();
		
		final ReportConfigDlg dlg = new ReportConfigDlg( getContext(), getContext().getString( "ReportConfigDlg.title.approve" ), informe );
		
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				
				private static final long serialVersionUID = -6381257314235637218L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					if ( dlg.isAccepted() )
					{
						byte[] pdf = getReportPdf( informe, dlg.getTemplate(), dlg.getOrientation(), dlg.getPagesize(), dlg.getImages() );
						
						if ( pdf != null )
						{
							try
							{
								Informe clone = (Informe)Utils.clone( informe );
								
								clone.setEstado( 2 );
								clone.setInforma( getContext().getUsuario().getId() );
								clone.setFecha( Utils.getTodayAsLong( informe.getHorario_nombre() ) );
								clone.setPdf( pdf );
								
								informesManager.setRow( getContext(), informe, clone );
								
								refreshVisibleContent();
							}
							catch ( Throwable e1 )
							{
								Utils.logException( e1, LOG );
							}
						}
					}
			    }
			}
		);
	
		dlg.showModalWindow();
	}
}
