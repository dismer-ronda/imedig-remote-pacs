package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.vaadin.inputmask.InputMask;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DatosIntalacion;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.dto.WorkingPlan;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public abstract class ModalNewInstalacion extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -3703048869668565205L;

	private static final Logger LOG = Logger.getLogger( ModalNewInstalacion.class );

	protected Instalacion newInstalacion;

	private TextField editNombre;
	private TextField editAEtitle;
	private TextField editTiempoMin;
	private ComboBox comboBoxModalidad;
	private WorkingPlanComponent workingPlanComponent;

	private InstalacionesManager instalacionesManager;

	public ModalNewInstalacion( ImedigContext ctx, Operation modalOperation, Instalacion instalacion, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, instalacion, right );

		setWidth( "750px" );

		instalacionesManager = (InstalacionesManager)IOCManager.getInstanceOf( InstalacionesManager.class );

		initComponents();

		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newInstalacion = (Instalacion)Utils.clone( (Instalacion)orgDto );
		}
		catch ( Throwable e1 )
		{
			newInstalacion = new Instalacion();
		}

		bi = new BeanItem<ImedigDto>( newInstalacion );

		editNombre = new TextField( getContext().getString( "modalNewInst.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );

		editAEtitle = new TextField( getContext().getString( "modalNewInst.lbAEtitle" ), bi.getItemProperty( "aetitle" ) );
		editAEtitle.setWidth( "100%" );
		editAEtitle.setNullRepresentation( "" );

		comboBoxModalidad = new ComboBox( getContext().getString( "modalNewInst.lbModalidad" ) );
		comboBoxModalidad.setPropertyDataSource( bi.getItemProperty( "modalidad" ) );
		comboBoxModalidad.setWidth( "100%" );
		comboBoxModalidad.setNewItemsAllowed( false );
		comboBoxModalidad.setNullSelectionAllowed( true );
		fillModalidad( comboBoxModalidad );

		editTiempoMin = new TextField( getContext().getString( "modalNewInst.lbTiempoMinimo" ) );
		editTiempoMin.setWidth( "60px" );

		// editTiempoMin.setMaxLength( 3 );
		InputMask mask = new InputMask( "[0-9]{0,3}" );
		mask.setRegexMask( true );
		mask.setPlaceholder( " " );
		mask.extend( editTiempoMin );
		editTiempoMin.setValue( "30" );

		FormLayout layout = new FormLayout( editNombre, editAEtitle, comboBoxModalidad, editTiempoMin );
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );

		componentsContainer.addComponent( layout );

		Panel panel = new Panel( getContext().getString( "modalNewInst.lbHorario" ) );
		panel.setWidth( "100%" );
		panel.setHeight( "425px" );

		List<DayPlan<String>> plan = null;
		DatosIntalacion datos = getExtraInformationFromJson();
		if ( datos != null && datos.getWorkingPlan() != null )
		{
			plan = datos.getWorkingPlan().getDiaryPlan();
		}
		workingPlanComponent = new WorkingPlanComponent( getContext(), plan );
		workingPlanComponent.setWidth( "100%" );
		panel.setContent( workingPlanComponent );
		// panel.addStyleName( ValoTheme.PANEL_BORDERLESS );
		componentsContainer.addComponent( panel );

	}

	protected abstract void fillModalidad( ComboBox comboBox );

	@Override
	protected String getWindowResourceKey()
	{
		return getMainResourceKey();
	}

	protected abstract String getMainResourceKey();

	@Override
	protected void defaultFocus()
	{
		editNombre.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			if ( !workingPlanComponent.isValid() )
			{
				Notification.show( getContext().getString( "modalNewInst.error.horario" ), Notification.Type.ERROR_MESSAGE );
				return false;
			}

			DatosIntalacion datos = getExtraInformation();
			newInstalacion.setDatos( Utils.toJson( datos ) );
			newInstalacion.setTipo( getTipo() );
			instalacionesManager.setRow( context, null, newInstalacion );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private DatosIntalacion getExtraInformation()
	{
		DatosIntalacion datos = new DatosIntalacion();
		WorkingPlan workingPlan = new WorkingPlan();
		workingPlan.setDiaryPlan( workingPlanComponent.getWeekPlan() );
		datos.setWorkingPlan( workingPlan );

		if ( StringUtils.isEmpty( editTiempoMin.getValue() ) )
		{
			datos.setTiempominimo( 60 );
		}
		else
		{
			datos.setTiempominimo( Integer.valueOf( editTiempoMin.getValue() ) );
		}

		return datos;
	}

	private DatosIntalacion getExtraInformationFromJson()
	{
		if ( StringUtils.isBlank( newInstalacion.getDatos() ) )
			return null;

		try
		{
			return (DatosIntalacion)Utils.toPojo( newInstalacion.getDatos(), DatosIntalacion.class, false );
		}
		catch ( Throwable e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected abstract Integer getTipo();

	protected boolean onModify()
	{
		try
		{
			if ( !workingPlanComponent.isValid() )
			{
				Notification.show( getContext().getString( "modalNewInst.error.horario" ), Notification.Type.ERROR_MESSAGE );
				return false;
			}

			DatosIntalacion datos = getExtraInformation();
			newInstalacion.setDatos( Utils.toJson( datos ) );
			newInstalacion.setTipo( getTipo() );
			instalacionesManager.setRow( context, (Instalacion)orgDto, newInstalacion );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onDelete()
	{
		try
		{
			instalacionesManager.delRow( context, newInstalacion );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

}
