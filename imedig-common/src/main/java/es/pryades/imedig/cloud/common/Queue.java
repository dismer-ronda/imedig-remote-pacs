package es.pryades.imedig.cloud.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes","unchecked"})
public class Queue
{
	List queue;

	public Queue()
	{
		queue = new ArrayList();
	}
	
	public void push( Object obj, boolean notify  )
	{
		synchronized ( this )
		{
			queue.add( obj );
			
			if ( notify )
				notify();
		}
	}

	public Object pop()
	{
		synchronized ( this )
		{
			if ( queue.size() > 0 )
			{
				Object obj = queue.get( 0 );
				
				queue.remove( 0 );
				
				return obj;
			}
		}
		
		return null;
	}
}
