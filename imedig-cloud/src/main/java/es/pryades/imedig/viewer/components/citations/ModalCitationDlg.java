package es.pryades.imedig.viewer.components.citations;

import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FiltrerAddSelect;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.common.lazy.LazyContainer;
import es.pryades.imedig.cloud.common.lazy.PacienteLazyProvider;
import es.pryades.imedig.cloud.common.lazy.ReferidorLazyProvider;
import es.pryades.imedig.cloud.common.lazy.SearchCriteria;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.components.converters.DateToIntegerConverter;

public class ModalCitationDlg extends ModalWindowsCRUD
{

	private static final long serialVersionUID = -831617884634887703L;

	private Instalacion instalacion;
	private Estudio newEstudio;
	
	private FiltrerAddSelect selectPaciente;
	private FiltrerAddSelect selectReferidor;
	private TextField editInstalacion;
	private ComboBox comboTipo;
	private DateField dateFieldFecha;
	private TimeField timeInicio;
	private TimeField timeFin;
	
	private PacienteLazyProvider pacienteLazyProvider;
	private ReferidorLazyProvider referidorLazyProvider;
	
	public ModalCitationDlg(ImedigContext ctx, Operation modalOperation, Instalacion instalacion, Estudio oldEstudio, ModalParent parentWindow, String right){
		super( ctx, parentWindow, modalOperation, oldEstudio, right );
		
		this.instalacion = instalacion;
		
		setWidth( "800px" );
		initComponents();
	}
	
	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newEstudio = (Estudio) Utils.clone( (Estudio) orgDto );
		}
		catch ( Throwable e1 )
		{
			newEstudio = new Estudio();
		}

		bi = new BeanItem<ImedigDto>( newEstudio );

		selectPaciente = new FiltrerAddSelect( getContext().getString( "modalCitationDlg.lbPaciente" ) );
		selectPaciente.setWidth( "100%" );
		selectPaciente.setRequired( true );
		
		pacienteLazyProvider = new PacienteLazyProvider( getContext() );
		LazyContainer dataSourcePaciente = new LazyContainer(Paciente.class, pacienteLazyProvider, new SearchCriteria());
		dataSourcePaciente.setMinFilterLength(0);
		selectPaciente.getComboBox().setItemCaptionPropertyId("nombreCompletoConIdentificador");
		selectPaciente.getComboBox().setContainerDataSource(dataSourcePaciente);
				
		if (Constants.TYPE_IMAGING_DEVICE.equals( instalacion.getTipo() )){
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.equipo" ));
		}else{
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.consulta" ));
		}
		editInstalacion.setValue( instalacion.getNombre() );
		editInstalacion.setReadOnly( true );
		editInstalacion.setWidth( "100%" );
		
		selectReferidor = new FiltrerAddSelect( getContext().getString( "modalCitationDlg.lbReferidor" ) );
		selectReferidor.setWidth( "100%" );
		selectReferidor.setRequired( true );
		selectReferidor.getButtonAdd().setVisible( false );
		
		referidorLazyProvider = new ReferidorLazyProvider( getContext() );
		LazyContainer dataSourceReferidor = new LazyContainer(Usuario.class, referidorLazyProvider, new SearchCriteria());
		dataSourceReferidor.setMinFilterLength(0);
		selectReferidor.getComboBox().setItemCaptionPropertyId("nombreCompleto");
		selectReferidor.getComboBox().setContainerDataSource(dataSourceReferidor);
		
		comboTipo = new ComboBox( getContext().getString( "modalCitationDlg.lbTipo" ) );
		comboTipo.setWidth( "70%" );
		comboTipo.setFilteringMode( FilteringMode.CONTAINS );
		comboTipo.setNullSelectionAllowed( false );
		comboTipo.setNewItemsAllowed( false );
		comboTipo.setRequired( true );
		fillTipoEstudio();
		
		dateFieldFecha = new DateField( getContext().getString( "modalCitationDlg.lbFecha" ));
		dateFieldFecha.setConverter( new DateToIntegerConverter() );
		dateFieldFecha.setDateFormat( "dd/MM/yyyy" );
		dateFieldFecha.setResolution( Resolution.DAY );
		dateFieldFecha.setRequired( true );
		
		timeInicio = new TimeField( getContext().getString( "modalCitationDlg.lbHoraInicio" ) );
		timeInicio.set24HourFormat( true );
		timeInicio.setRequired( true );
		
		timeFin = new TimeField( getContext().getString( "modalCitationDlg.lbHoraFin" ) );
		timeFin.set24HourFormat( true );
		timeFin.setRequired( true );
		
		FiltrerAddSelect select = new FiltrerAddSelect( getContext().getString( "modalCitationDlg.lbFecha" ) );
				
		FormLayout layout = new FormLayout( selectPaciente, editInstalacion, selectReferidor, comboTipo, dateFieldFecha, timeInicio, timeFin );
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setWidth( "100%" );
	
		componentsContainer.addComponent( layout );
	}

	private void fillTipoEstudio()
	{
		TipoEstudio query = new TipoEstudio();
		TiposEstudiosManager manager = (TiposEstudiosManager) IOCManager.getInstanceOf( TiposEstudiosManager.class );
		
		try
		{
			for ( TipoEstudio item : (List<TipoEstudio>)manager.getRows( getContext(), query ) )
			{
				comboTipo.addItem( item.getId() );
				comboTipo.setItemCaption( item.getId(), item.getNombre() );
				
			}
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected boolean onAdd()
	{
		return true;
	}

	@Override
	protected boolean onModify()
	{
		return true;
	}

	@Override
	protected boolean onDelete()
	{
		return false;
	}

	@Override
	protected void defaultFocus()
	{
		// TODO Auto-generated method stub
		
	}
	
	public void setDate(Date date){
		dateFieldFecha.setValue( date );
		timeInicio.setValue( date );
		timeFin.setValue( date );
	}
	
	@Override
	protected String getWindowResourceKey()
	{
		return "modalCitationDlg";
	}
}
