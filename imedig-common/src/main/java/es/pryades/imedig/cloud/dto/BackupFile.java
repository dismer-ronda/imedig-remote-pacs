package es.pryades.imedig.cloud.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
@AllArgsConstructor
public class BackupFile implements Serializable
{
	private static final long serialVersionUID = 8631675119774120013L;
	
	private String fileName;
    private Long size;
    private Long modified;
}
