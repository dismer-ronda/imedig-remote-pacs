package es.pryades.imedig.cloud.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.vaadin.server.StreamResource;

import es.pryades.imedig.cloud.dto.Imagen;

public class DatabaseImageSource implements StreamResource.StreamSource 
{
	private static final long serialVersionUID = 4020715925399092004L;

	Imagen imagen;
	
	public DatabaseImageSource( Imagen imagen )
	{
		this.imagen = imagen;
	}
	
	public InputStream getStream () 
	{
		return new ByteArrayInputStream( imagen.getDatos() );
	}
}