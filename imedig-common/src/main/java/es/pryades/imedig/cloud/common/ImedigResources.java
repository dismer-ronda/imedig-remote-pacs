package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.util.ResourceBundle;

public class ImedigResources implements Serializable
{
	private static final long serialVersionUID = -604168217294054524L;
	
	private ResourceBundle resources;
	
	private ImedigResources() 
	{
	}

    public String getString(String key) 
    {
        try 
        {
            return resources.getString(key);
        } 
        catch ( Throwable e ) 
        {
            return  '!'+key+'!';
        }
    }
}
