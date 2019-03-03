package es.pryades.imedig.viewer.components.appointments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.core.dal.RecursosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Recurso;
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
	
	private RecursosManager recursosManager;
	
	private static final String COMBO_WIDTH = "200px";
	private static final String TEXT_WIDTH = "200px";
	
	private List<Integer> types = Arrays.asList( Constants.TYPE_IMAGING_DEVICE );
			

	/**
	 * 
	 * @param ctx
	 */
	public AppointmentsViewer( ImedigContext ctx )
	{
		this.ctx = ctx;
		recursosManager = (RecursosManager) IOCManager.getInstanceOf( RecursosManager.class );
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}
	
	private void buildComponents(){
		try
		{
			buildResourcesType();
		}
		catch ( Throwable e )
		{
			LOG.error( "Error creando componente", e );
		}
	}

	private void buildResourcesType() throws Throwable
	{
		int counter = 0;
		
		List<ResourceTypeViewer> viewers = new ArrayList<>();
		Recurso query = new Recurso();
		
		for ( Integer type : types )
		{
			query.setTipo( type );
			int falicities = recursosManager.getNumberOfRows( ctx, query );
			
			if (falicities == 0 ) continue;
			counter++;
			
			viewers.add( new ResourceTypeViewer( ctx, type ) );
		}
		
		if (counter == 0) return;
		if (counter == 1) {
			addResourcesType(viewers.get( 0 ));
		}else{
			addResourcesType(viewers);
		}
		
	}

	private void addResourcesType( ResourceTypeViewer resourceTypeViewer )
	{
		addComponent( resourceTypeViewer );
		
	}

	private void addResourcesType( List<ResourceTypeViewer> viewers )
	{
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		for ( ResourceTypeViewer viewer : viewers )
		{
			tabSheet.addTab( viewer, ctx.getString( "resource.type."+viewer.getType() ) );
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
