package es.pryades.imedig.viewer.components.appointments;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.viewer.components.patients.ModalNewPaciente;

public class PacienteAppointmentDlg extends ModalNewPaciente
{
	private static final long serialVersionUID = -2893547659934956931L;
	
	private final ModalAppointmentDlg appointmentDlg;

	public PacienteAppointmentDlg( ImedigContext ctx, Operation modalOperation, Paciente orgPaciente, ModalParent parentWindow, String right )
	{
		super( ctx, modalOperation, orgPaciente, parentWindow, right );
		appointmentDlg = (ModalAppointmentDlg)parentWindow;
	}
	
	@Override
	protected boolean onAdd()
	{
		if(!super.onAdd()) return false;
		
		appointmentDlg.updatePaciente( newPaciente );
		
		return true;
	}

	@Override
	protected boolean onModify()
	{
		return false;
	}

	@Override
	protected boolean onDelete()
	{
		return false;
	}


}
