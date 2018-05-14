package es.pryades.imedig.cloud.common;

import java.util.ResourceBundle;

public class ImedigResources 
{
	ResourceBundle resources;
	String bundle;
	
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
