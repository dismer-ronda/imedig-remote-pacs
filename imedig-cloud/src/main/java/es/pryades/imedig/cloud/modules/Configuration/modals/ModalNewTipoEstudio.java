package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.TiposEstudiosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.TipoEstudio;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public final class ModalNewTipoEstudio extends ModalWindowsCRUD
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5384562124198472933L;

	private static final Logger LOG = Logger.getLogger( ModalNewTipoEstudio.class );

	protected TipoEstudio newTipoEstudio;

	private TextField editNombre;
	private ComboBox comboBoxHoras;
	private ComboBox comboBoxMinutos;
	private OptionGroup groupTipo;
	private ComboBox comboBoxModalidad;
	
	private static final List<Integer> HOURS = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
	private List<Integer> MINUTES;

	private TiposEstudiosManager tiposEstudiosManager;

	public ModalNewTipoEstudio( ImedigContext ctx, Operation modalOperation, TipoEstudio tipoEstudio, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, tipoEstudio, right );
		
		setWidth( "600px" );
		
		tiposEstudiosManager = (TiposEstudiosManager) IOCManager.getInstanceOf( TiposEstudiosManager.class );
		
		buildMinutes();
		
		initComponents();
		
		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
		
	}

	private void buildMinutes()
	{
		int to = 60/5;
		MINUTES = new ArrayList<>();
		
		for ( int i = 0; i <= to; i++ )
		{
			MINUTES.add( i*5 );
			
		}
		
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newTipoEstudio = (TipoEstudio) Utils.clone( (TipoEstudio) orgDto );
		}
		catch ( Throwable e1 )
		{
			newTipoEstudio = new TipoEstudio();
		}

		bi = new BeanItem<ImedigDto>( newTipoEstudio );

		editNombre = new TextField( getContext().getString( "modalNewStudyType.lbNombre" ), bi.getItemProperty( "nombre" ) );
		editNombre.setWidth( "100%" );
		editNombre.setNullRepresentation( "" );
		editNombre.setRequired( true );
		
		groupTipo = new OptionGroup( getContext().getString( "modalNewStudyType.lbTipo" ) );
		groupTipo.addItem( 1 );
		groupTipo.setItemCaption( 1, getContext().getString( "resource.type.1" ) );
		groupTipo.setPropertyDataSource( bi.getItemProperty( "tipo" ) );
		groupTipo.setRequired( true );
		groupTipo.setValue( Constants.TYPE_IMAGING_DEVICE );
		groupTipo.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = 8964479749018558410L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if (!Constants.TYPE_IMAGING_DEVICE.equals( newTipoEstudio.getTipo()))
					comboBoxModalidad.setVisible( false );
				
			}
		} );
		
		comboBoxModalidad = new ComboBox( getContext().getString( "modalNewRecurso.lbModalidad" ) );
		comboBoxModalidad.setPropertyDataSource( bi.getItemProperty( "modalidad" ) );
		comboBoxModalidad.setWidth( "100%" );
		comboBoxModalidad.setNewItemsAllowed( false );
		comboBoxModalidad.setNullSelectionAllowed( true );
		comboBoxModalidad.setRequired( true );
		fillModalidad( comboBoxModalidad );

		comboBoxHoras = new ComboBox( getContext().getString( "words.hours" ) );
		comboBoxHoras.setWidth( "80px" );
		comboBoxHoras.setNullSelectionAllowed( false );
		comboBoxHoras.setNewItemsAllowed( false );
		fillHoras(comboBoxHoras);

		comboBoxMinutos = new ComboBox( getContext().getString( "words.minutes" ) );
		comboBoxMinutos.setWidth( "80px" );
		comboBoxMinutos.setNullSelectionAllowed( false );
		comboBoxMinutos.setNewItemsAllowed( false );
		fillMinutos( comboBoxMinutos);
		FormLayout hoursLayout = new FormLayout( comboBoxHoras );
		hoursLayout.setMargin( false );
		FormLayout minLayout = new FormLayout( comboBoxMinutos );
		minLayout.setMargin( false );
		
		Integer h = 0;
		Integer m = 0;
		if (orgDto != null){
			h = newTipoEstudio.getDuracion()/60;
			m = newTipoEstudio.getDuracion()%60;
			
		}
		comboBoxHoras.setValue( h );
		comboBoxMinutos.setValue( m );


		HorizontalLayout timeLayout = new HorizontalLayout(hoursLayout, minLayout);
		timeLayout.setSpacing( true );
		timeLayout.setCaption( getContext().getString( "modalNewStudyType.lbDuracion" ) );
		FormLayout layout = new FormLayout(editNombre, groupTipo, comboBoxModalidad, timeLayout);
		
		layout.setMargin( false );
		layout.setWidth( "100%" );
		layout.setSpacing( true );
		
		componentsContainer.addComponent( layout );
		
	}

	private void fillModalidad( ComboBox comboBox )
	{
		String modList = context.getString( "QueryForm.Modalities" );

		String modalities[] = modList.split( "," );

		for ( String modality : modalities )
		{
			comboBox.addItem( modality );
			comboBox.setItemCaption( modality, modality +" - "+getContext().getString( "words.modality."+modality ) );
		}
		
	}

	private void fillHoras( ComboBox comboBox )
	{
		for ( Integer n : HOURS )
		{
			comboBox.addItem( n );
		}
	}

	private void fillMinutos( ComboBox comboBox )
	{
		for ( Integer n : MINUTES )
		{
			comboBox.addItem( n );
		}
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewStudyType";
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
			newTipoEstudio.setDuracion( getMinutes() );
			tiposEstudiosManager.setRow( context, null, newTipoEstudio );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ));
		}

		return false;
	}

	protected boolean onModify()
	{
		try
		{
			newTipoEstudio.setDuracion( getMinutes() );
			tiposEstudiosManager.setRow( context, (TipoEstudio) orgDto, newTipoEstudio );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ));
		}

		return false;
	}
	
	private Integer getMinutes(){
		Integer h = (Integer)comboBoxHoras.getValue();
		Integer m = (Integer)comboBoxMinutos.getValue();
		
		if (h == 0 && m == 0) return null;
		
		return (h*60)+m;
	}

	protected boolean onDelete()
	{
		try
		{
			tiposEstudiosManager.delRow( context, newTipoEstudio );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( new ImedigException( e, LOG ));
		}

		return false;
	}
}
