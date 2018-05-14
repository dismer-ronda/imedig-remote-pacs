package es.pryades.imedig.cloud.dto.viewer;

import lombok.Data;

@Data 
public class User
{
	private String login;
	private String filter;
	private Integer query;
	private String compression;
	private String uid;
}
