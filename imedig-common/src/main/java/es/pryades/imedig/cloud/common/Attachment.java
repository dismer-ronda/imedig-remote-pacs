package es.pryades.imedig.cloud.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attachment implements Serializable
{
	private static final long serialVersionUID = 5752750160820594785L;
	
	private String name;
	private String type;
	private byte content[];
}
