package es.pryades.imedig.cloud.modules.Reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContent;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Settings;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.InformesImagenesManager;
import es.pryades.imedig.cloud.core.dal.InformesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.core.reports.PdfExport;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.DetalleInforme;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
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
@SuppressWarnings({"serial","unchecked"})
public class ReportsViewer extends FilteredContent implements ModalParent, Property.ValueChangeListener
{
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

	private InformesManager informesManager;
	private InformesImagenesManager imagenesManager;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public ReportsViewer( ImedigContext ctx )
	{
		super( ctx );
		
		informesManager = (InformesManager) IOCManager.getInstanceOf( InformesManager.class );
		imagenesManager = (InformesImagenesManager) IOCManager.getInstanceOf( InformesImagenesManager.class );
		
		setSizeFull();
		setMargin( true );
		initComponents();
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
		
		btnView = new Button();
		btnView.setCaption( getContext().getString( "words.view" ) );
		btnView.setEnabled( false );
		bttnViewReportListener();
		extras.add( btnView );
	
		btnApprove = new Button();
		btnApprove.setCaption( getContext().getString( "words.approve" ) );
		btnApprove.setEnabled( false );
		bttnApproveReportListener();
		extras.add( btnApprove );
	
		btnFinish = new Button();
		btnFinish.setCaption( getContext().getString( "words.finish" ) );
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
		return informe.aprobado();
	}

	private void bttnViewReportListener()
	{
		btnView.addClickListener( new Button.ClickListener()
		{
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
			images = imagenesManager.getRows( getContext(), query );
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
			images = imagenesManager.getRows( getContext(), query );
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
				imagenes = imagenesManager.getRows( ctx, query );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
	
			for ( InformeImagen imagen : imagenes )
				imagen.setUrl( Utils.getEnviroment( "CLOUD_URL" ) + imagen.getUrl() );
		
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
			public void buttonClick( ClickEvent event )
			{
				final DetalleInforme informe = (DetalleInforme)getSelectedRow();
	
				final ReportConfigDlg dlg = new ReportConfigDlg( getContext(), getContext().getString( "ReportConfigDlg.title.approve" ), informe );
				
				dlg.addCloseListener
				( 
					new Window.CloseListener() 
					{
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
		);
	}

	private void bttnFinishReportListener()
	{
		btnFinish.addClickListener( new Button.ClickListener()
		{
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
	
	public Component getQueryFecha()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.date" ));
		combo.setWidth( "120px" );
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

	public Component getQueryPaciente()
	{
		TextField text = new TextField( getContext().getString( "words.patient" ), bi.getItemProperty( "paciente" ) );
		text.setNullRepresentation( "" );
		
		return getRow( text );
	}

	public Component getQueryEstudio()
	{
		TextField text = new TextField( getContext().getString( "words.study" ), bi.getItemProperty( "estudio" ) );
		text.setNullRepresentation( "" );
		
		return getRow( text );
	}

	private void fillComboCentro( ComboBox comboCentro )
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

	public Component getQueryCentro()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.center" ));
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "centro" ) );
		combo.setWidth( "200px" );

		fillComboCentro( combo );
		
		return getRow( combo );
	}

	public Component getQueryEstado()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.status" )); 
		combo.setWidth( "120px" );
		combo.setNullSelectionAllowed( false );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "estado" ) );
		
		combo.addItem( -1 );
		combo.setItemCaption( -1, getContext().getString( "words.all" ) );
		combo.addItem( Informe.STATUS_REQUESTED );
		combo.setItemCaption( Informe.STATUS_REQUESTED, getContext().getString( "words.requested" ) );
		combo.addItem( Informe.STATUS_INFORMED );
		combo.setItemCaption( Informe.STATUS_INFORMED, getContext().getString( "words.not.approved" ) );
		combo.addItem( Informe.STATUS_APROVED );
		combo.setItemCaption( Informe.STATUS_APROVED, getContext().getString( "words.approved" ) );		
		combo.addItem( Informe.STATUS_FINISHED );
		combo.setItemCaption( Informe.STATUS_FINISHED, getContext().getString( "words.finished" ) );
		
		return getRow( combo );
	}

	public Component getQueryCodigo()
	{
		TextField text = new TextField( getContext().getString( "words.icd10cm" ), bi.getItemProperty( "icd10cm" ) );
		text.setNullRepresentation( "" );
		
		return getRow( text );
	}

	public Component getQueryClaves()
	{
		TextField text = new TextField( getContext().getString( "words.keywords" ), bi.getItemProperty( "claves" ) );
		text.setNullRepresentation( "" );
		
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
		ComboBox combo = new ComboBox(getContext().getString( "words.refer" ));
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "refiere" ) );
		combo.setWidth( "200px" );
		
		fillComboMedicos( combo );
		
		return getRow( combo );
	}

	public Component getQueryInforma()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.report" ));
		combo.setNullSelectionAllowed( true );
		combo.setTextInputAllowed( false );
		combo.setPropertyDataSource( bi.getItemProperty( "informa" ) );
		combo.setWidth( "200px" );
		
		fillComboMedicos( combo );

		return getRow( combo );
	}

	public Component getQueryModalidad()
	{
		ComboBox combo = new ComboBox(getContext().getString( "words.modality" )); 
		combo.setWidth( "120px" );
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

	
	public Component getQueryComponent()
	{
		fecha = 4;
		paciente = "";
		estudio = "";
		estado = -1;
		icd10cm = "";
		claves = "";
		
		bi = new BeanItem<ReportsViewer>( this );
		
		VerticalLayout column = new VerticalLayout();
		column.setMargin( false );
		column.setSpacing( true );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing( true );
		row1.setMargin( false );
		row1.addComponent( getQueryFecha() );
		row1.addComponent( getQueryEstado() );
		row1.addComponent( getQueryRefiere() );
		row1.addComponent( getQueryInforma() );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setSpacing( true );
		row2.setMargin( false );
		row2.addComponent( getQueryCentro() );
		row2.addComponent( getQueryModalidad() );
		row2.addComponent( getQueryEstudio() );
		row2.addComponent( getQueryPaciente() );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setSpacing( true );
		row3.setMargin( false );
		row3.addComponent( getQueryCodigo() );
		row3.addComponent( getQueryClaves() ); 

		column.addComponent( row1 );
		column.addComponent( row2 );
		column.addComponent( row3 );

		return column;
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
		
		return layout;
	}
}
