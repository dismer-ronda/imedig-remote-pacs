package es.pryades.imedig.viewer.components.citations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.core.dal.InstalacionesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Instalacion;
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
	
	private InstalacionesManager instalacionesManager;
	
	public FacilityTypeViewer( ImedigContext ctx, Integer type)
	{
		this.ctx = ctx;
		this.type = type;
		
		instalacionesManager = (InstalacionesManager) IOCManager.getInstanceOf( InstalacionesManager.class );
		
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
		
		List<CitationSchedulerViewer> viewers = new ArrayList<>();
		Instalacion query = new Instalacion();
		query.setTipo( type );
		int counter = instalacionesManager.getNumberOfRows( ctx, query );
		
		List<Instalacion> instalaciones = instalacionesManager.getRows( ctx, query );
		
		for ( Instalacion instalacion : instalaciones )
		{
			viewers.add( new CitationSchedulerViewer( ctx, instalacion ) );
		}
		
		if (counter == 0) return;
		if (counter == 1) {
			addFacilities(viewers.get( 0 ));
		}else{
			addFacilities(viewers);
		}
		
	}

	private void addFacilities( CitationSchedulerViewer facilityTypeViewer )
	{
		addComponent( facilityTypeViewer );
		
	}

	private void addFacilities( List<CitationSchedulerViewer> viewers )
	{
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		for ( CitationSchedulerViewer viewer : viewers )
		{
			tabSheet.addTab( viewer,  viewer.getInstalacion().getNombre()+" ("+viewer.getInstalacion().getModalidad()+")");
		}
		addComponent( tabSheet );
	}

}
