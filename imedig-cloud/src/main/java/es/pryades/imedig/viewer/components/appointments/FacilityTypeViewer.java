package es.pryades.imedig.viewer.components.appointments;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.core.dal.RecursosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.cloud.ioc.IOCManager;
import lombok.Getter;

public class FacilityTypeViewer extends VerticalLayout
{
	private static final long serialVersionUID = 979303127906951967L;
	
	private static final Logger LOG = Logger.getLogger( FacilityTypeViewer.class );
	
	private ImedigContext ctx;
	
	@Getter
	private Integer type;
	
	private TabSheet tabSheet;
	
	private RecursosManager recursosManager;
	
	public FacilityTypeViewer( ImedigContext ctx, Integer type)
	{
		this.ctx = ctx;
		this.type = type;
		
		recursosManager = (RecursosManager) IOCManager.getInstanceOf( RecursosManager.class );
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}

	private void buildComponents()
	{
		try
		{
			buildFacilities();
		}
		catch ( Throwable e )
		{
			LOG.error( "Error creando componente", e );
		}
		
	}
	
	private void buildFacilities() throws Throwable
	{
		
		List<AppointmentSchedulerViewer> viewers = new ArrayList<>();
		Recurso query = new Recurso();
		query.setTipo( type );
		int counter = recursosManager.getNumberOfRows( ctx, query );
		
		List<Recurso> recursos = recursosManager.getRows( ctx, query );
		
		for ( Recurso recurso : recursos )
		{
			viewers.add( new AppointmentSchedulerViewer( ctx, recurso ) );
		}
		
		if (counter == 0) return;
		if (counter == 1) {
			addFacilities(viewers.get( 0 ));
		}else{
			addFacilities(viewers);
		}
		
	}

	private void addFacilities( AppointmentSchedulerViewer facilityTypeViewer )
	{
		addComponent( facilityTypeViewer );
		
	}

	private void addFacilities( List<AppointmentSchedulerViewer> viewers )
	{
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		for ( AppointmentSchedulerViewer viewer : viewers )
		{
			tabSheet.addTab( viewer,  viewer.getRecurso().getNombre()+" ("+viewer.getRecurso().getModalidad()+")");
		}
		addComponent( tabSheet );
	}

}
