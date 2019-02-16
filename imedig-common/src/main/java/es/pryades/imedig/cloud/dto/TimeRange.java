package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class TimeRange<T> implements Serializable
{
	private static final long serialVersionUID = 1752435559881148070L;
	
	private T start;
	private T end;
	
	public TimeRange(T start, T end){
		this.start = start;
		this.end = end;
	}
}
