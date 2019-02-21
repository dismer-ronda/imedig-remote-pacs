package es.pryades.imedig.cloud.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PlanificacionHorario implements Serializable
{
	private static final long serialVersionUID = -3143638781653607562L;
	
	private List<DayPlan<String>> diaryPlan;
}
