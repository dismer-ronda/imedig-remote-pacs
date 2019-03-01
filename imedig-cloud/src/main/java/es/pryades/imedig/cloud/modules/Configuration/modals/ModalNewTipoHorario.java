                    package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.TipoHorarioManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.PlanificacionHorario;
import es.pryades.imedig.cloud.dto.TipoHorario;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public class ModalNewTipoHorario extends ModalWindowsCRUD
{
	private static final long serialVersionUID = 7094121561194740730L;

	private static final Logger LOG = Logger.getLogger( ModalNewTipoHorario.class );

	protected TipoHorario newTipoHorario;

	private TextField editNombre;
	//TODO: Agregar cuando existan otros tipos de recursos
	//1 - Equipos
	//private OptionGroup optionTipoRecurso;
	private OptionGroup optionTipo;
	private Panel panelWorkingPlan;
	private WorkingPlanComponent workingPlanComponent;

	private TipoHorarioManager tipoHorarioManager;

	public ModalNewTipoHorario( ImedigContext ctx, Operation modalOperation, TipoHorario tipoHorario, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, tipoHorario, right );

		setWidth( "1000px" );

		tipoHorarioManager = (TipoHorarioManager)IOCManager.getInstanceOf( TipoHorarioManager.class );

		initComponents();

		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		newTipoHorario = Utils.clone2( (TipoHorario)orgDto );
		if (newTipoHorario == null){
			newTipoHorario = new TipoHorario();
			newTipoHorario.setTipo_horario( Constants.SCHEDULER_ALL_EQUALS );
		}

		bi = new BeanItem<ImedigDto>( newTipoHorario );

		editNombre = new TextField( getContext().getString( "modalNewTipoHorario.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );

		optionTipo = new OptionGroup( getContext().getString( "modalNewTipoHorario.tipo" ) );
		optionTipo.addItem( Constants.SCHEDULER_ALL_EQUALS );
		optionTipo.setItemCaption( Constants.SCHEDULER_ALL_EQUALS, getContext().getString( "modalNewTipoHorario.tipo."+Constants.SCHEDULER_ALL_EQUALS ) );
		optionTipo.addItem( Constants.SCHEDULER_ALL_WEEK_DAYS );
		optionTipo.setItemCaption( Constants.SCHEDULER_ALL_WEEK_DAYS, getContext().getString( "modalNewTipoHorario.tipo."+Constants.SCHEDULER_ALL_WEEK_DAYS ) );
		optionTipo.addItem( Constants.SCHEDULER_CUSTOM );
		optionTipo.setItemCaption( Constants.SCHEDULER_CUSTOM, getContext().getString( "modalNewTipoHorario.tipo."+Constants.SCHEDULER_CUSTOM ) );
		optionTipo.addStyleName( ValoTheme.OPTIONGROUP_HORIZONTAL );
		
		panelWorkingPlan = new Panel( getContext().getString( "modalNewTipoHorario.horario" ) );
		panelWorkingPlan.setWidth( "100%" );
		panelWorkingPlan.setHeight( "425px" );
		
		FormLayout layout = new FormLayout( editNombre, optionTipo);
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );

		componentsContainer.addComponent( layout );
		List<DayPlan<String>> plan = null;
		PlanificacionHorario datos = getExtraInformationFromJson();
		if ( datos != null && datos.getDiaryPlan() != null )
		{
			plan = datos.getDiaryPlan();
		}
		workingPlanComponent = new WorkingPlanComponent( getContext(), plan );
		panelWorkingPlan.setContent( workingPlanComponent );
		componentsContainer.addComponent( panelWorkingPlan );
		
		optionTipo.setValue( Constants.SCHEDULER_ALL_EQUALS );
		optionTipo.addValueChangeListener( new ValueChangeListener()
		{
			
			private static final long serialVersionUID = 2651836522187963085L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				workingPlanComponent.select( (Integer)optionTipo.getValue() );
			}
		} );
		optionTipo.setPropertyDataSource( bi.getItemProperty( "tipo_horario" ) );
		
		editNombre.focus();
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewTipoHorario";
	}

	@Override
	protected void defaultFocus()
	{
		editNombre.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			if ( !isValid() ) return false;

			PlanificacionHorario datos = getInformacionHorario();
			newTipoHorario.setDatos( Utils.toJson( datos ) );
			newTipoHorario.setTipo_recurso( getTipoRecurso() );
			tipoHorarioManager.setRow( context, null, newTipoHorario );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private PlanificacionHorario getInformacionHorario()
	{
		PlanificacionHorario datos = new PlanificacionHorario();
		datos.setDiaryPlan( workingPlanComponent.getWeekPlan() );

		return datos;
	}

	private PlanificacionHorario getExtraInformationFromJson()
	{
		if ( StringUtils.isBlank( newTipoHorario.getDatos() ) )
			return null;

		try
		{
			return (PlanificacionHorario)Utils.toPojo( newTipoHorario.getDatos(), PlanificacionHorario.class, false );
		}
		catch ( Throwable e )
		{
			LOG.error( "Error", e );
		}
		return null;
	}

	protected Integer getTipoRecurso(){
		return Constants.TYPE_IMAGING_DEVICE;
	}
	
	private boolean isValid(){
		if ( !workingPlanComponent.isValid() )
		{
			Notification.show( getContext().getString( "modalNewRecurso.error.horario" ), Notification.Type.ERROR_MESSAGE );
			return false;
		}
		
		if ( !workingPlanComponent.isValidRanges() )
		{
			Notification.show( getContext().getString( "modalNewRecurso.error.breaks" ), Notification.Type.ERROR_MESSAGE );
			return false;
		}
		
		return true;
	}

	protected boolean onModify()
	{
		try
		{
			if ( !isValid() ) return false;

			PlanificacionHorario datos = getInformacionHorario();
			newTipoHorario.setDatos( Utils.toJson( datos ) );
			tipoHorarioManager.setRow( context, (TipoHorario)orgDto, newTipoHorario );

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
			tipoHorarioManager.delRow( context, newTipoHorario );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

}
