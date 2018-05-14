package es.pryades.imedig.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@Data
@AllArgsConstructor
public class BackupFile
{
    private String fileName;
    private Long size;
    private Long modified;
}
