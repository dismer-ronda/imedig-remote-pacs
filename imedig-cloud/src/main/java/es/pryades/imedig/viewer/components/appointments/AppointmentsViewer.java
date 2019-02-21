package es.pryades.imedig.viewer.components.appointments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author hector.licea
 * 
 */
@SuppressWarnings({"unchecked"})
public class AppointmentsViewer extends VerticalLayout implements ModalParent, Property.ValueChangeListener
{

	private static final long serialVersionUID = 486852411457445182L;

	private static final Logger LOG = Logger.getLogger( AppointmentsViewer.class );

	@Setter @Getter private String nombre;
	@Setter @Getter private String identificador;
	@Setter @Getter private Integer fecha;
	
	private ImedigContext ctx;
	private BeanItem<AppointmentsViewer> bi;
	
	private TabSheet tabSheet;
	
	private TextField textNombre;
	private TextField textIdentificador;
	private ComboBox comboFecha;

	private InstalacionesManager instalacionesManager;
	private EstudiosManager estudiosManager;
	
	private static final String COMBO_WIDTH = "200px";
	private static final String TEXT_WIDTH = "200px";
	
	//private static final Integer PERFIL_IMAGENOLOGO = 3;
	//private static final Integer PERFIL_USUARIO = 2;
	
	//private boolean defaultSearch;
	//private boolean showMessage;
	private List<Integer> types = Arrays.asList( Constants.TYPE_IMAGING_DEVICE );
			

	/**
	 * 
	 * @param ctx
	 */
	public AppointmentsViewer( ImedigContext ctx )
	{
		this.ctx = ctx;
		instalacionesManager = (InstalacionesManager) IOCManager.getInstanceOf( InstalacionesManager.class );
		estudiosManager = (EstudiosManager) IOCManager.getInstanceOf( EstudiosManager.class );
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}
	
	private void buildComponents(){
		try
		{
			buildFacilityType();
		}
		catch ( Throwable e )
		{
			LOG.error( "Error creando componente", e );
		}
	}

	private void buildFacilityType() throws Throwable
	{
		int counter = 0;
		
		List<FacilityTypeViewer> viewers = new ArrayList<>();
		Instalacion query = new Instalacion();
		
		for ( Integer type : types )
		{
			query.setTipo( type );
			int falicities = instalacionesManager.getNumberOfRows( ctx, query );
			
			if (falicities == 0 ) continue;
			counter++;
			
			viewers.add( new FacilityTypeViewer( ctx, type ) );
		}
		
		if (counter == 0) return;
		if (counter == 1) {
			addFacilitiesType(viewers.get( 0 ));
		}else{
			addFacilitiesType(viewers);
		}
		
	}

	private void addFacilitiesType( FacilityTypeViewer facilityTypeViewer )
	{
		addComponent( facilityTypeViewer );
		
	}

	private void addFacilitiesType( List<FacilityTypeViewer> viewers )
	{
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		for ( FacilityTypeViewer viewer : viewers )
		{
			tabSheet.addTab( viewer, ctx.getString( "facility.type."+viewer.getType() ) );
		}
		
		
	}


	@Override
	public void valueChange( ValueChangeEvent event )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshVisibleContent()
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
}
