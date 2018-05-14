package es.pryades.imedig.cloud.core.reports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.Data;

import org.apache.velocity.VelocityContext;
import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.core.dal.InformesPlantillasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.InformePlantilla;
import es.pryades.imedig.cloud.ioc.IOCManager;

/**
 * @author dismer.ronda
 *
 */

@Data
public abstract class ExportBase 
{
	private static final Logger LOG = Logger.getLogger( ExportBase.class );
	
	private static InformesPlantillasManager plantillasManager = null;

	public InputStream createStreamTemplate( ImedigContext ctx, int id) throws Throwable
	{
		if (plantillasManager == null)
			plantillasManager = (InformesPlantillasManager) IOCManager.getInstanceOf(InformesPlantillasManager.class);

		return new ByteArrayInputStream( ((InformePlantilla) plantillasManager.getRow(ctx, id)).getDatos().getBytes( "UTF-8" ) );
	}
	
	public VelocityContext createVelocityContext() throws Throwable
	{
		return new VelocityContext();
	}
}
