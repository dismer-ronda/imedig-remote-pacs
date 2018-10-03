package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;

import lombok.Data;

@Data 
public class User implements Serializable
{
	private static final long serialVersionUID = 3415972996648221736L;
	
	private String login;
	private String filter;
	private Integer query;
	private String compression;
	private String uid;
}
