package es.pryades.imedig.viewer.components.citations;

import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Estudio;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.components.converters.DateToIntegerConverter;

public class ModalCitationDlg extends ModalWindowsCRUD
{

	private static final long serialVersionUID = -831617884634887703L;

	private Instalacion instalacion;
	private Estudio newEstudio;
	
	private TextField editPaciente;
	private TextField editInstalacion;
	private ComboBox comboTipo;
	private TextField editReferidor;
	private DateField dateFieldFecha;
	private TimeField timeInicio;
	private TimeField timeFin;
	
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

		editPaciente = new TextField();
		editPaciente.setWidth( "100%" );
		editPaciente.setNullRepresentation( "" );
		//editPaciente.setRequired( true );
		Button search = new Button( FontAwesome.SEARCH );
		search.addStyleName( ValoTheme.BUTTON_ICON_ONLY );

		HorizontalLayout paciente = new HorizontalLayout(editPaciente, search);
		paciente.addStyleName( ValoTheme.LAYOUT_COMPONENT_GROUP );
		paciente.setCaption( getContext().getString( "modalCitationDlg.lbPaciente" ) );
		paciente.setWidth( "100%" );
		paciente.setExpandRatio( editPaciente, 1.0f );
		
		if (Constants.TYPE_IMAGING_DEVICE.equals( instalacion.getTipo() )){
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.equipo" ));
		}else{
			editInstalacion = new TextField( getContext().getString( "modalCitationDlg.lbInstalacion.consulta" ));
		}
		editInstalacion.setValue( instalacion.getNombre() );
		editInstalacion.setReadOnly( true );
		editInstalacion.setWidth( "100%" );
		
		editReferidor = new TextField( );
		editReferidor.setWidth( "100%" );
		editReferidor.setNullRepresentation( "" );
		
		search = new Button( FontAwesome.SEARCH );
		search.addStyleName( ValoTheme.BUTTON_ICON_ONLY );

		HorizontalLayout referidor = new HorizontalLayout(editReferidor, search);
		referidor.addStyleName( ValoTheme.LAYOUT_COMPONENT_GROUP );
		referidor.setCaption(getContext().getString( "modalCitationDlg.lbReferidor" ));
		referidor.setWidth( "100%" );
		referidor.setExpandRatio( editReferidor, 1.0f );

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
		
		FormLayout layout = new FormLayout( paciente, editInstalacion, referidor, comboTipo, dateFieldFecha, timeInicio, timeFin );
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
