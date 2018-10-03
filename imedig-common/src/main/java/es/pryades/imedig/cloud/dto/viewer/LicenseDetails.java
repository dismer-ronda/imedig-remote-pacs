package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class LicenseDetails implements Serializable 
{
	private static final long serialVersionUID = -3980657846133583248L;
	
	private long id;
	private long rights;
	private long expiration;
}
