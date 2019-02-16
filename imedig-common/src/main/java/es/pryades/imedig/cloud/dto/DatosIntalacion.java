package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class DatosIntalacion implements Serializable
{
	private static final long serialVersionUID = 1762438409142977468L;

	private Integer tiempominimo;
	private WorkingPlan workingPlan;
}
