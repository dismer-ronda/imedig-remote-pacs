package es.pryades.imedig.core.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StatisticsVariable implements Serializable 
{
	private static final long serialVersionUID = -8566405265085156022L;
	
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
