package es.pryades.imedig.cloud.common;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */
public class ImedigException extends Exception
{
	private static final long serialVersionUID = -4650650424063622583L;

	int imedigError;
	String detailedMessage;

	public static final int UNKNOWN = -1000;

	public static final int SETTINGS_NOT_FOUND = -1001;
	public static final int SETTINGS_READ_ERROR = -1002;
	public static final int SETTINGS_NOT_INITIALIZED = -1003;

	public static final int INSTANCE_NOT_INITIALIZED = -1010;
	public static final int GLOBAL_NOT_INITIALIZED = -1011;

	public static final int CONNECTION = -1020;

	public static final int KEY_DUPLICATE = -1030;
	public static final int UNIQUE_VIOLATION = -1031;
	public static final int NULL_RETURN = -1032;
	public static final int INVALID_ROW_ID = -1033;
	public static final int ZERO_SUBSCRIPTIONS = -1034;
	public static final int KEY_NULL = -1035;
	public static final int FOREIGN_KEY_VIOLATION = -1036;
	public static final int NOT_NULL_VIOLATION = -1037;
	
	public static final int FORMAT_ERROR_INT = -1050;
	public static final int FORMAT_ERROR_LONG = -1051;
	public static final int FORMAT_ERROR_DOUBLE = -1052;
	public static final int FORMAT_ERROR_DATE = -1053;

	public static final int ENCRYPT_ERROR = -1060;
	public static final int DECRYPT_ERROR = -1061;

	public static final int ENCODE_ERROR = -1070;
	public static final int DECODE_ERROR = -1071;

	public static final int MAIL_ADDRESS_INVALID = -1080;
	public static final int MAIL_SEND_ERROR = -1081;

	public static final int UPDATE_WITHOUT_LAST = -1100;
	public static final int UPDATE_WITHOUT_NEW = -1101;
	public static final int UPDATE_CLASSES_MISSMATCH = -1102;

	public static final int REFLECTION_ERROR = -1110;
	public static final int FILE_NOT_FOUND = -1111;
	public static final int IO_ERROR = -1112;
	public static final int METHOD_CALL_ILEGAL = -1113;
	public static final int INTERRUPTED_ERROR = -1114;
	public static final int EXCEED_MAX_QUANTITY_FOUND = -1115;

	public static final int STRING_RESOURCE_ERROR = -1120;

	public static final int JSON_STRING_ERROR = -1140;
	public static final int STRING_JSON_ERROR = -1141;

	public static final int JOB_NOT_READY = -1150;

	public static final int CAN_NOT_DELETE_USER = -1160;
	public static final int CAN_NOT_CREATE_USER = -1161;
	public static final int CAN_NOT_MODIFY_USER = -1162;
	public static final int ERROR_ENTER_NAME_SURENAME = -1163;
	public static final int ERROR_ENTER_EMAIL = -1164;
	public static final int ERROR_ENTER_CENTER = -1165;

	public static final int INITIALIZATION_ERROR = -1200;

	public static final int COMPATIBILITY_ERROR = -1210;

	public static final int RESTRICTION_VIOLATION_ERROR = -1240;

	public static final int INPUT_VALUE_NUMBER_ERROR = -1250;
	public static final int INPUT_VALUE_ERROR = -1251;

	public static final int DAYS_INTERCEPTION = -1301;

	public static final int LOGIN_FAIL = -1500;
	public static final int LOGIN_BLOCKED = -1501;
	public static final int LOGIN_PASSWORD_CHANGED = -1502;

	public ImedigException( Throwable e, Logger LOG){
		this( e, LOG, UNKNOWN );
	}
	
	public ImedigException( Throwable e, Logger LOG, int imedigError )
	{
		super();

		setImedigError( imedigError != 0 ? imedigError : UNKNOWN );

		if ( e != null )
		{
			if ( e instanceof PersistenceException )
			{
				if ( e.getCause() instanceof PSQLException )
				{
					PSQLException s1 = (PSQLException)e.getCause();

					String state = s1.getSQLState();
					
					if ( "23503".equals(  state ) )
						setImedigError( FOREIGN_KEY_VIOLATION );
					else if ( "23505".equals(  state ) )
						setImedigError( UNIQUE_VIOLATION );
					else if ( "23502".equals(  state ) )
						setImedigError( NOT_NULL_VIOLATION );
				}
			}
			else
			{
				this.detailedMessage = e.getLocalizedMessage();
			}

			if ( e instanceof AddressException )
				setImedigError( MAIL_ADDRESS_INVALID );

			if ( e instanceof MessagingException )
				setImedigError( MAIL_SEND_ERROR );
		}
	}

	public void setImedigError( int imedigError )
	{
		this.imedigError = imedigError;
	}

	public int getImedigError()
	{
		return imedigError;
	}

	public String getDetailedMessage()
	{
		return this.detailedMessage;
	}
}
