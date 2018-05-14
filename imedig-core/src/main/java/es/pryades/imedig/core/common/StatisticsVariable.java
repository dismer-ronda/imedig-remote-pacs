package es.pryades.imedig.core.common;

import java.util.HashMap;
import java.util.Map;

public class StatisticsVariable {
	private Map<Object, String> values = new HashMap<Object, String>();
	
	public void addValue(Object id, String value){
		values.put(id, value);
	}
	
	public String getValue(Object id){
		String value = values.get(id);
		
		if ( value == null ){
			return "";
		}
		
		return value;
	}
}
