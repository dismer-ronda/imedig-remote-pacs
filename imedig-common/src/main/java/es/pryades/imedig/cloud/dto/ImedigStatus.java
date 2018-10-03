package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.Data;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
public class ImedigStatus implements Serializable
{
 	private static final long serialVersionUID = -40688294152828212L;
 	
	private Integer numberStudies; 
    private Integer numberReports;
    private Integer numberImages;
    
    private Double averagePerStudy;
    
	private Long sizeMain;
	private Long usedMain;
	
	private Long sizeNAS = 0L;
	private Long usedNAS = 0L;
}
