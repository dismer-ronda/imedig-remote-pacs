package es.pryades.imedig.cloud.common;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public final class ReturnFactory 
{
	public static final int STATUS_2XX_OK 								= 200;
	public static final int STATUS_2XX_CREATED 							= 201;
	public static final int STATUS_2XX_ACCEPTED 						= 202;
	public static final int STATUS_2XX_NO_CONTENT 						= 204;
	public static final int STATUS_2XX_RESET_CONTENT 					= 205;
	public static final int STATUS_2XX_PARTIAL_CONTENT 					= 206;

	public static final int STATUS_3XX_MULTIPLE_CHOICES 				= 300;
	public static final int STATUS_3XX_MOVED_PERMANENTLY 				= 301;
	public static final int STATUS_3XX_FOUND 							= 302;
	public static final int STATUS_3XX_SEE_OTHER 						= 303;
	public static final int STATUS_3XX_NOT_MODIFIED 					= 304;
	public static final int STATUS_3XX_USE_PROXY 						= 305;
	public static final int STATUS_3XX_TEMPORARY_REDIRECT 				= 307;

	public static final int STATUS_4XX_BAD_REQUEST 						= 400;
	public static final int STATUS_4XX_UNAUTHORIZED 					= 401;
	public static final int STATUS_4XX_PAYMENT_REQUIRED 				= 402;
	public static final int STATUS_4XX_FORBIDDEN 						= 403;
	public static final int STATUS_4XX_NOT_FOUND 						= 404;
	public static final int STATUS_4XX_METHOD_NOT_ALLOWED 				= 405;
	public static final int STATUS_4XX_NOT_ACCEPTABLE 					= 406;
	public static final int STATUS_4XX_PROXY_AUTHENTICATION_REQUIRED 	= 407;
	public static final int STATUS_4XX_REQUEST_TIMEOUT 					= 408;
	public static final int STATUS_4XX_CONFLICT 						= 409;
	public static final int STATUS_4XX_GONE 							= 410;
	public static final int STATUS_4XX_LENGTH_REQUIRED 					= 411;
	public static final int STATUS_4XX_PRECONDITION_FAILED 				= 412;
	public static final int STATUS_4XX_REQUEST_ENTITY_TOO_LARGE 		= 413;
	public static final int STATUS_4XX_REQUEST_URI_TOO_LONG 			= 414;
	public static final int STATUS_4XX_UNSUPORTED_MEDIA_TYPE 			= 415;
	public static final int STATUS_4XX_REQUEST_RANGE_NOT_SATISFIABLE 	= 416;
	public static final int STATUS_4XX_EXPECTATION_FAILED 				= 417;
	public static final int STATUS_4XX_TOO_MANY_CONNECTIONS 			= 421;
	public static final int STATUS_4XX_UPGRADE_REQUIRED 				= 426;

	public static final int STATUS_5XX_INTERNAL_SERVER_ERROR 			= 500;
	public static final int STATUS_5XX_NOT_IMPLEMENTED 					= 501;
	public static final int STATUS_5XX_BAD_GATEWAY 						= 502;
	public static final int STATUS_5XX_SERVICE_UNAVAILABLE 				= 503;
	public static final int STATUS_5XX_GATEWAY_TIMEOUT 					= 504;
	public static final int STATUS_5XX_HTTP_VERSION_NOT_SUPPORTED 		= 505;
	public static final int STATUS_5XX_USER_ACCESS_DENIED 				= 530;
	
	private ReturnFactory()
	{
		super();
	}
	
	static public Representation getRepresentation( Return ret ) throws Throwable
	{
		return new StringRepresentation( Utils.toJson( ret ), MediaType.APPLICATION_JSON );
	}
}
