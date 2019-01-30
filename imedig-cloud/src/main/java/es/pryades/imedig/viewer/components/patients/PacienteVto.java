package es.pryades.imedig.viewer.components.patients;

import com.vaadin.ui.Component;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.GenericControlerVto;
import es.pryades.imedig.core.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author hector.licea
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class PacienteVto extends GenericVto
{
	private static final long serialVersionUID = 2273060583389786111L;

	private String identificador;
	private String nombre;
	private String sexo;
	private String edad;
	private Component citas;
		
	public PacienteVto()
	{
	}
	
	public GenericControlerVto getControlerVto( ImedigContext ctx )
	{
		return new PacienteControlerVto( ctx );
	}
}
