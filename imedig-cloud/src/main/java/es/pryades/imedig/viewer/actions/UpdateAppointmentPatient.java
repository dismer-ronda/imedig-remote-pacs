package es.pryades.imedig.viewer.actions;

import es.pryades.imedig.cloud.dto.Recurso;

public class UpdateAppointmentPatient extends AbstractAction<Object, Recurso>
{
	private static final long serialVersionUID = -4030379510920152769L;

	public UpdateAppointmentPatient(Object source, Recurso data) {
		super(source, data);
	}
}
