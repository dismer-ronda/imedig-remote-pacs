package es.pryades.imedig.cloud.vto;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.vto.controlers.InformeControlerVto;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class InformeVto extends GenericVto
{
	private static final long serialVersionUID = 1614728255098525760L;
	
	private Component imgEstado;
	private String fecha;
	private String estado;
	private String paciente_id;
	private String paciente_nombre;
	private String estudio_id;
	private String estudio_acceso;
	private String modalidad;
	private String centro_nombre;
	private String informa;
	private String icd10cm;
	private String refiere;
	
	public InformeVto()
	{
	}
	
	public GenericControlerVto getControlerVto( ImedigContext ctx )
	{
		return new InformeControlerVto( ctx );
	}
}
