package es.pryades.imedig.cloud.common;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageConfiguration implements Serializable
{
	private static final long serialVersionUID = 6162668949409626027L;
	
	private String minimunFreeDiskSpace; 
	
	private Boolean enableExternalStorage; 
	
	private String externalStorageDirectory; 
	private String externalStorageFilesystem; 
	private String moveStudyIfNotAccessedFor; 
	private Integer keepStudyMax; 
}
