package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.Data;

/**
*
* @author Dismer Ronda 
* @since 1.0.0.0
*/

@Data
public class Query implements Serializable
{
	private static final long serialVersionUID = -2479919425411331061L;
	
	Integer pageSize;
	Integer pageNumber;
}
