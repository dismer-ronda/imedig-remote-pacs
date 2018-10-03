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
public class ReportInfo implements Serializable 
{
	private static final long serialVersionUID = -3231098734699798373L;
	
	private ImageHeader header;
	private String url;
	private String icon;
}
