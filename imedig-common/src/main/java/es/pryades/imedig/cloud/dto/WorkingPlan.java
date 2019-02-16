package es.pryades.imedig.cloud.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class WorkingPlan implements Serializable
{
	private static final long serialVersionUID = -2954401982543784834L;
	
	private List<DayPlan<String>> diaryPlan;
}
