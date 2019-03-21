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
	
	//Tipo recursos
	public static final Integer TYPE_IMAGING_DEVICE = 1;
	
	//Perfiles de los usuarion
	public static final Integer PROFILE_ADMIN = 1;
	public static final Integer PROFILE_DOCTOR = 2;
	public static final Integer PROFILE_IMGDOCTOR = 3;
	public static final Integer PROFILE_CENTER_ADMIN = 4;
	public static final Integer PROFILE_STUDENT = 5;
	public static final Integer PROFILE_ADMINISTRATIVE = 6;
	
	//Tipo de horarios
	public static final int SCHEDULER_ALL_EQUALS = 0;
	public static final int SCHEDULER_ALL_WEEK_DAYS = 1;
	public static final int SCHEDULER_CUSTOM = 2;
	
	//Estado de las citas
	public static final int APPOINTMENT_STATUS_PLANING = 0; //Estado planificadas
	public static final int APPOINTMENT_STATUS_EXECUTING = 1;//En ejecución
	public static final int APPOINTMENT_STATUS_ENDED = 2;//Terminada
	
	//Derechos
	public static final String DERECHO_LOGIN = "login";
	public static final String DERECHO_CONFIG = "configuracion";
	public static final String DERECHO_CONFIG_TODO = "configuracion.todo";
	public static final String DERECHO_CONFIG_IMAGENES = "configuracion.imagenes";
	public static final String DERECHO_CONFIG_IMAGENES_ADD = "configuracion.imagenes.adicionar";
	public static final String DERECHO_CONFIG_IMAGENES_MOD = "configuracion.imagenes.modificar";
	public static final String DERECHO_CONFIG_IMAGENES_DEL = "configuracion.imagenes.borrar";
	public static final String DERECHO_CONFIG_CENTROS = "configuracion.centros";
	public static final String DERECHO_CONFIG_CENTROS_ADD = "configuracion.centros.adicionar";
	public static final String DERECHO_CONFIG_CENTROS_MOD = "configuracion.centros.modificar";
	public static final String DERECHO_CONFIG_CENTROS_DEL = "configuracion.centros.borrar";
	public static final String DERECHO_CONFIG_USUARIOS = "configuracion.usuarios";
	public static final String DERECHO_CONFIG_USUARIOS_ADD = "configuracion.usuarios.adicionar";
	public static final String DERECHO_CONFIG_USUARIOS_MOD = "configuracion.usuarios.modificar";
	public static final String DERECHO_CONFIG_USUARIOS_DEL = "configuracion.usuarios.borrar";
	public static final String DERECHO_ADMINISTRACION_ACCESO = "administracion.acceso";
	public static final String DERECHO_INFORMES_CREAR = "informes.crear";
	public static final String DERECHO_INFORMES_APROBAR = "informes.aprobar";
	public static final String DERECHO_INFORMES_SOLICITAR = "informes.solicitar";
	public static final String DERECHO_INFORMES_TERMINAR = "informes.terminar";
	public static final String DERECHO_CONFIG_INFORMES_PLATILLAS = "configuracion.informes.plantillas";
	public static final String DERECHO_CONFIG_INFORMES_PLATILLAS_ADD = "configuracion.informes.plantillas.adicionar";
	public static final String DERECHO_CONFIG_INFORMES_PLATILLAS_MOD = "configuracion.informes.plantillas.modificar";
	public static final String DERECHO_CONFIG_INFORMES_PLATILLAS_DEL = "configuracion.informes.plantillas.borrar";
	public static final String DERECHO_CONFIG_TIPOS_ESTUDIOS = "configuracion.tipos.estudios";
	public static final String DERECHO_CONFIG_TIPOS_ESTUDIOS_ADD = "configuracion.tipos.estudios.adicionar";
	public static final String DERECHO_CONFIG_TIPOS_ESTUDIOS_MOD = "configuracion.tipos.estudios.modificar";
	public static final String DERECHO_CONFIG_TIPOS_ESTUDIOS_DEL = "configuracion.tipos.estudios.borrar";
	public static final String DERECHO_CONFIG_TIPOS_RECURSOS = "configuracion.recursos";
	public static final String DERECHO_CONFIG_TIPOS_RECURSOS_ADD = "configuracion.recursos.adicionar";
	public static final String DERECHO_CONFIG_TIPOS_RECURSOS_MOD = "configuracion.recursos.modificar";
	public static final String DERECHO_CONFIG_TIPOS_RECURSOS_DEL = "configuracion.recursos.borrar";
	public static final String DERECHO_CONFIG_PACIENTES = "configuracion.pacientes";
	public static final String DERECHO_CONFIG_PACIENTES_ADD ="configuracion.pacientes.adicionar";
	public static final String DERECHO_CONFIG_PACIENTES_MOD = "configuracion.pacientes.modificar";
	public static final String DERECHO_CONFIG_PACIENTES_DEL = "configuracion.pacientes.borrar";
	public static final String DERECHO_CITAS = "administracion.citas";
	public static final String DERECHO_CITAS_ADD = "administracion.citas.adicionar";
	public static final String DERECHO_CITAS_MOD = "administracion.citas.modificar";
	public static final String DERECHO_CITAS_DEL = "administracion.citas.borrar";
	public static final String DERECHO_CONFIG_TIPOS_HORARIOS = "configuracion.tipos.horarios";
	public static final String DERECHO_CONFIG_TIPOS_HORARIOS_ADD = "configuracion.tipos.horarios.adicionar";
	public static final String DERECHO_CONFIG_TIPOS_HORARIOS_MOD = "configuracion.tipos.horarios.modificar";
	public static final String DERECHO_CONFIG_TIPOS_HORARIOS_DEL = "configuracion.tipos.horarios.borrar";
	public static final String DERECHO_CONFIG_RECURSOS = "configuracion.recursos";
	public static final String DERECHO_CONFIG_RECURSOS_ADD = "configuracion.recursos.adicionar";
	public static final String DERECHO_CONFIG_RECURSOS_MOD = "configuracion.recursos.modificar";
	public static final String DERECHO_CONFIG_RECURSOS_DEL =  "configuracion.recursos.borrar";

	
		
}
