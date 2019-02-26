package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KeyValue<K,V> implements Serializable
{
	private static final long serialVersionUID = -4432776075968001778L;

	private K key;
	private V value;
	
	public KeyValue(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

}
