package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes","unchecked"})
public class Stack implements Serializable 
{
	private static final long serialVersionUID = -3022494350874794130L;
	
	private List stack;

	public Stack()
	{
		stack = new ArrayList();
	}
	
	public void push( Object obj, boolean notify  )
	{
		synchronized ( this )
		{
			stack.add( 0, obj );
			
			if ( notify )
				notify();
		}
	}

	public Object pop()
	{
		synchronized ( this )
		{
			if ( stack.size() > 0 )
			{
				Object obj = stack.get( 0 );
				
				stack.remove( 0 );
				
				return obj;
			}
		}
		
		return null;
	}
	
	public int size()
	{
		return stack.size();
	}
	
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}
}
