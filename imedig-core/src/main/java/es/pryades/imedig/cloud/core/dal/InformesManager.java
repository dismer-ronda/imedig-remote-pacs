package es.pryades.imedig.cloud.core.dal;

import java.util.List;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Informe;
import es.pryades.imedig.cloud.dto.InformeImagen;
import es.pryades.imedig.cloud.dto.viewer.ReportInfo;
import es.pryades.imedig.core.common.ImedigManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface InformesManager extends ImedigManager
{
	public ReportInfo getReportInfo( ImedigContext ctx, DetalleCentro centro );

	public void addReport( ImedigContext ctx, Informe informe, List<InformeImagen> imagenes ) throws Throwable;
	public void modifyReport( ImedigContext ctx, Informe oldInforme, Informe newInforme, List<InformeImagen> imagenes ) throws Throwable;
	public void deleteReport( ImedigContext ctx, Informe informe ) throws Throwable;
}
