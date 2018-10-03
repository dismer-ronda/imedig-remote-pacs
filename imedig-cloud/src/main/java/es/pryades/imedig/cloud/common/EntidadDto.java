package es.pryades.imedig.cloud.common;

import java.io.Serializable;

import es.pryades.imedig.cloud.dto.DetalleCentro;
import lombok.Data;

@Data
public class EntidadDto implements Serializable 
{
	private static final long serialVersionUID = -6631796927708768251L;
	
	private DetalleCentro centro = null;

	public EntidadDto()
	{
	}

	public EntidadDto( DetalleCentro aCentro )
	{
		centro = aCentro;
	}

	public Integer getId()
	{
		if ( centro != null )
			return centro.getId();

		return null;
	}

	public String getNombre()
	{
		if ( centro != null )
			return centro.getNombre();

		return null;
	}

	public String getDescripcion()
	{
		if ( centro != null )
			return centro.getDescripcion();

		return null;
	}
}
