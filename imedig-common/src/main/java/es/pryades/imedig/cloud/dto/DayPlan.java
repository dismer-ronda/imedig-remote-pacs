package es.pryades.imedig.cloud.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DayPlan<T> implements Serializable
{
	//Coincide con los valores de Calendar (Calendar.MODAY)
	private Integer day;
	private TimeRange<T> workingTime;
	private List<TimeRange<T>> breaks;
}
