package es.pryades.imedig.cloud.dto;

import lombok.Data;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
public class ImedigStatus
{
    private Integer numberStudies; 
    private Integer numberReports;
    private Integer numberImages;
    
    private Double averagePerStudy;
    
	private Long sizeMain;
	private Long usedMain;
	
	private Long sizeNAS = 0L;
	private Long usedNAS = 0L;
}
