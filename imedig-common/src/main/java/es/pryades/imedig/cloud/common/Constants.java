package es.pryades.imedig.cloud.common;

import java.io.Serializable;

public class Constants implements Serializable 
{
	private static final long serialVersionUID = 8273713292118118400L;
	
	public static final String COLOR_NAME_RED 		= "red";
	public static final String COLOR_NAME_BLUE 		= "blue";
	public static final String COLOR_NAME_GREEN		= "green";
	public static final String COLOR_NAME_YELLOW 	= "yellow";
	public static final String COLOR_NAME_PINK 		= "pink";
	public static final String COLOR_NAME_AQUA 		= "aqua";
	public static final String COLOR_NAME_ORANGE 	= "orange";
	public static final String COLOR_NAME_BROWN 	= "brown";
	public static final String COLOR_NAME_BLUE2 	= "blue2";
	public static final String COLOR_NAME_GREEN2	= "green2";
	public static final String COLOR_NAME_BLUE3		= "blue-munsell";
	public static final String COLOR_NAME_ROSE		= "american-rose";
	public static final String COLOR_NAME_LEMON		= "lemon";

	public static final int TRANSPORT_CLIENT_TCP	= 102;
	
	public static final int PROTOCOL_MODBUS			= 202;

	public static final int CONECTION_DIRECT		= 301;
	public static final int CONECTION_PASSTHROW		= 302;
	
	public static final int ID_PROFILE_ROOT		= 1;
	public static final int ID_PROFILE_REGULAR	= 2;
	public static final int ID_PROFILE_SAW		= 3;
	public static final int ID_IMAGE_EMPTY		= 1;

	public static final int DEFAULT_CONNECTION_COLLECTOR		= 1;
	public static final int DEFAULT_CONNECTION_IDDLE			= 30;
	
	public static final int DEFAULT_DEVICE_ADDRESS			= 1;
	public static final int DEFAULT_DEVICE_IDDLE				= 1;
	public static final int DEFAULT_DEVICE_TYPE				= 1;
	public static final int DEFAULT_DEVICE_CONSUMPTION		= 1;
	
	public static final int DEFAULT_PAGE_SIZE = 25; 
	
	public static final String CENTER_NODE 		= "CENTER.NODE";
	public static final String SHOW_PERIOD 		= "SHOW.PERIOD";
	
	public static final int CLASS_TYPE_DEVICE			= 1;
	public static final int CLASS_TYPE_TRANSPORT		= 2;
	public static final int CLASS_TYPE_CONNECTION		= 4;
	public static final int CLASS_TYPE_ACTION			= 5;
	public static final int CLASS_TYPE_REPORT_BAND		= 6;
	public static final int CLASS_TYPE_TASK 			= 7;
	public static final int CLASS_TYPE_WIDGET			= 8;
	
	public static final String WIDTH_LABEL				= "200px";

	public static final String SEX_MALE = "M";
	public static final String SEX_FEMALE = "F";
	public static final String SEX_OTHER = "O";
	
	//Tipo instalaciones
	public static final Integer TYPE_IMAGING_DEVICE = 1;
	
	//Perfiles de los usuarion
	public static final Integer PROFILE_ADMIN = 1;
	public static final Integer PROFILE_DOCTOR = 2;
	public static final Integer PROFILE_IMGDOCTOR = 3;
	public static final Integer PROFILE_CENTER_ADMIN = 4;
	public static final Integer PROFILE_STUDENT = 5;
	public static final Integer PROFILE_ADMINISTRATIVE = 6;
}
