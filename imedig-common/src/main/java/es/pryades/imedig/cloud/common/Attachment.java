package es.pryades.imedig.cloud.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attachment 
{
	private String name;
	private String type;
	private byte content[];
}
