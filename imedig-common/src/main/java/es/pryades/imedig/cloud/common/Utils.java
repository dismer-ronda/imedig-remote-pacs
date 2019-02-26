package es.pryades.imedig.cloud.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.pryades.imedig.cloud.dto.Usuario;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class Utils implements Serializable
{
	private static final long serialVersionUID = 98385869705172646L;

	private static final Logger LOG = Logger.getLogger( Utils.class );

	public static final long ONE_MB = 1024 * 1024;

	public static final long ONE_SECOND = 1000;
	public static final long ONE_MINUTE = 60 * ONE_SECOND;
	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	public static final String TIME_FORMAT = "HH:mm";
	public static final DateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );

	public static String getSelector( String s, int length )
	{
		StringTokenizer tokens = new StringTokenizer( Normalizer.normalize( s.toUpperCase(), Normalizer.Form.NFD ).replaceAll( "[^\\p{ASCII}]", "" ) );

		String r = "";

		while ( tokens.hasMoreTokens() )
		{
			String token = tokens.nextToken();

			if ( !token.equals( "DE" ) && !token.equals( "DEL" ) && !token.equals( "LA" ) && !token.equals( "LAS" ) && !token.equals( "LOS" ) && !token.equals( "SAN" ) )
				r += token + " ";
		}

		String ret = r.trim();

		if ( ret.length() > length )
			ret = ret.substring( 0, length );

		return ret;
	}

	public static String convertToHex( byte value )
	{
		String table = "0123456789ABCDEF";

		byte lo = (byte)(value & 0x0F);
		byte hi = (byte)((value & 0xF0) >> 4);

		return "" + table.charAt( hi ) + table.charAt( lo );
	}

	public static String convertToHex( byte[] data )
	{
		StringBuffer buf = new StringBuffer();

		for ( int i = 0; i < data.length; i++ )
			buf.append( convertToHex( data[i] ) );

		return buf.toString();
	}

	public static byte convertFromHex( byte hi, byte lo )
	{
		String table = "0123456789ABCDEF";

		return (byte)(table.indexOf( lo ) | (table.indexOf( hi ) << 4));
	}

	public static byte[] convertFromHex( byte[] data )
	{
		byte buffer[] = new byte[data.length / 2];

		for ( int i = 0; i < data.length; i += 2 )
			buffer[i / 2] = convertFromHex( data[i], data[i + 1] );

		return buffer;
	}

	public static String MD5( String text ) throws Throwable
	{
		MessageDigest md;

		byte[] md5hash = new byte[32];

		try
		{
			md = MessageDigest.getInstance( "MD5" );

			md.update( text.getBytes( "iso-8859-1" ), 0, text.length() );

			md5hash = md.digest();

			return convertToHex( md5hash );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static String SHA1( String text ) throws Throwable
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance( "SHA1" );

			byte[] md5hash = new byte[32];

			md.update( text.getBytes( "iso-8859-1" ), 0, text.length() );

			md5hash = md.digest();

			return convertToHex( md5hash );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static String getTrustedToken( String data, String pwd ) throws Throwable
	{
		return Utils.SHA1( pwd + data );
	}

	public static boolean isTrustedToken( String token, String data, String pwd ) throws Throwable
	{
		return token.equalsIgnoreCase( getTrustedToken( data, pwd ) );
	}

	public static String encrypt( String data, String pwd )
	{
		try
		{
			return convertToHex( Cryptor.encrypt( data.getBytes(), pwd ) );
		}
		catch ( Throwable e )
		{
			return data;
		}
	}

	public static String decrypt( String data, String pwd )
	{
		try
		{
			return new String( Cryptor.decrypt( convertFromHex( data.getBytes() ), pwd ) );
		}
		catch ( Throwable e )
		{
			return data;
		}
	}

	public static int getInt( String value, int defValue )
	{
		try
		{
			return Integer.parseInt( value );
		}
		catch ( Throwable e )
		{
			return defValue;
		}
	}

	public static long getLong( String value, long defValue )
	{
		try
		{
			return Long.parseLong( value );
		}
		catch ( Throwable e )
		{
			return defValue;
		}
	}

	public static double getDouble( String value, double defValue )
	{
		try
		{
			return Double.parseDouble( value );
		}
		catch ( Throwable e )
		{
			return defValue;
		}
	}

	public static String getExceptionMessage( Throwable e )
	{
		String txt;

		if ( e.getMessage() != null )
			txt = e.getMessage();
		else if ( e.getCause() != null )
			txt = getExceptionMessage( e.getCause() );
		else
			txt = e.toString();

		return txt;
	}

	public static String getExceptionString( Throwable e )
	{
		String txt = "";

		StackTraceElement stack[] = e.getStackTrace();

		if ( stack.length > 0 )
		{
			boolean msg = true;

			txt = e.getClass().getName() + " ";

			for ( int i = 0; i < stack.length; i++ )
			{
				if ( msg )
				{
					txt += getExceptionMessage( e );

					msg = false;
				}

				txt += "\nat class " + stack[i].getClassName() + " method " + stack[i].getMethodName() + " line " + stack[i].getLineNumber();
			}
		}

		return txt;
	}

	public static void logException( Throwable e, Logger LOG )
	{
		StackTraceElement stack[] = e.getStackTrace();

		if ( stack.length > 0 )
		{
			boolean msg = true;

			String txt = e.getClass().getName() + " ";
			String extra = "";

			for ( int i = 0; i < stack.length; i++ )
			{
				if ( msg )
				{
					txt += getExceptionMessage( e );

					msg = false;
				}

				extra += "\n\tat class " + stack[i].getClassName() + " method " + stack[i].getMethodName() + " line " + stack[i].getLineNumber();
			}

			LOG.error( txt + extra );
		}
	}

	public static String getRandomPassword( int length )
	{
		return (new RandPass()).getPass( length );
	}

	public static void sendMail( String from, String to, String subject, String host, String user, String password, String text, List<Attachment> attachments ) throws Throwable
	{
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.put( "mail.smtp.auth", "true" );

		// Get the default Session object.
		Session session = Session.getDefaultInstance( properties );

		// a default MimeMessage object.
		MimeMessage message = new MimeMessage( session );

		// Set the RFC 822 "From" header field using the
		// value of the InternetAddress.getLocalAddress method.
		try
		{
			message.setFrom( new InternetAddress( from ) );

			// Add the given addresses to the specified recipient type.
			String recipients[] = to.split( "," );

			if ( recipients != null )
			{
				for ( String recipient : recipients )
					message.addRecipient( Message.RecipientType.TO, new InternetAddress( recipient ) );

				// Set the "Subject" header field.
				message.setSubject( subject );

				// Sets the given String as this part's content,
				// with a MIME type of "text/plain".

				// create the message part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText( text );

				Multipart multipart = new MimeMultipart( "mixed" );

				multipart.addBodyPart( messageBodyPart );

				if ( attachments != null )
				{
					for ( Attachment attachment : attachments )
					{
						MimeBodyPart part = new MimeBodyPart();

						DataSource source = new ByteArrayDataSource( attachment.getContent(), attachment.getType() );

						part.setDataHandler( new DataHandler( source ) );
						part.setFileName( attachment.getName() );
						multipart.addBodyPart( part );
					}
				}

				message.setContent( multipart );

				// Send message
				Transport tr = session.getTransport( "smtp" );
				tr.connect( host, user, password );
				tr.sendMessage( message, message.getAllRecipients() );
				tr.close();
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static int getCalendarTimeAsInt( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return day + month * 100 + year * 10000;
	}

	public static int getCalendarDateAsInt( Calendar calendar, int offset )
	{
		calendar.add( Calendar.DATE, offset );

		return getCalendarTimeAsInt( calendar );
	}

	public static long getCalendarTimeAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );
		int min = calendar.get( Calendar.MINUTE );
		int sec = calendar.get( Calendar.SECOND );

		return (long)sec + (long)min * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarDateAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.DATE, offset );

		return getCalendarTimeAsLong( calendar );
	}

	public static long getCalendarHourAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.HOUR_OF_DAY, offset );

		return getCalendarTimeAsLong( calendar );
	}

	public static long getCalendarMinuteAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.MINUTE, offset );

		return getCalendarTimeAsLong( calendar );
	}

	public static int getDateAsInt( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return getCalendarDateAsInt( calendar, 0 );
	}

	public static long getDateAsLong( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return getCalendarDateAsLong( calendar, 0 );
	}

	public static long getTodayAsLong( String horario )
	{
		return getCalendarDateAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ), 0 );
	}

	public static long getDayAsLong( TimeZone timezone, int offset )
	{
		return getCalendarDateAsLong( GregorianCalendar.getInstance( timezone ), offset );
	}

	public static long getDayAsLong( int offset )
	{
		return getCalendarDateAsLong( GregorianCalendar.getInstance(), offset );
	}

	public static long getTodayFirstSecondAsLong( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getTomorrowFirstSecondAsLong( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.add( Calendar.DATE, 1 );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static Date getTodayFirstSecondAsDate( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );

		return calendar.getTime();
	}
	
	public static Date getFirstSecondAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance( );
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );

		return calendar.getTime();
	}

	public static Date getTodayLastSecondAsDate( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 23, 59, 59 );

		return calendar.getTime();
	}
	
	public static Date getLastSecondAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance( );
		calendar.setTime( date );
		
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 23, 59, 59 );

		return calendar.getTime();
	}

	public static Date getLastSecondHourAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), calendar.get( Calendar.HOUR_OF_DAY ), 59, 59 );

		return calendar.getTime();
	}

	public static long getElapsedTimeUntilNow( Date date, String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
		calendar.setTime( date );

		return getDateHourFromLong( getTodayAsLong( horario ) ).getTime() - calendar.getTime().getTime();
	}

	public static Date getCurrentDate( String horario )
	{
		return getDateHourFromLong( getTodayAsLong( horario ) );
	}

	public static long getHourFirstSecondAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.HOUR_OF_DAY, offset );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );

		return (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getHourFirstSecondAsLong( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return getHourFirstSecondAsLong( calendar, 0 );
	}

	public static long getHourLastSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );

		return (long)59 + (long)59 * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getHourLastSecondAsLong( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return getHourLastSecondAsLong( calendar );
	}

	// hector.licea
	public static long getDayFirstSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getCalendarFirstDayWeekAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DAY_OF_MONTH ) );

		while ( cal.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY )
		{
			cal.add( Calendar.DAY_OF_MONTH, -1 );
		}

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	/**
	 * @author Dismer Ronda </br>
	 *         <b>Description:</b> </br>
	 *         <p>
	 *         Return the <b>last day</b> of a month from the given date, -1 in
	 *         case of error.
	 *         </p>
	 * 
	 * @param date
	 * @return -> The last day of the month from the given parameter, -1 in case
	 *         of error.
	 */
	public static int getLastDayOfMonth( Date date )
	{
		int result = -1;

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		result = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
		return result;
	}

	/**
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean isFirstDayOfMonth( Date date )
	{
		int monthDay = -1;

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		monthDay = calendar.get( Calendar.DAY_OF_MONTH );

		return monthDay == 1;
	}

	/**
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean isLastDayOfMonth( Date date )
	{
		int monthDay = -1;
		int lasMonthDay = -2;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		monthDay = calendar.get( Calendar.DAY_OF_MONTH );
		lasMonthDay = Utils.getLastDayOfMonth( date );

		return monthDay == lasMonthDay;

	}

	// hector.licea
	public static long getCalendarLastDayWeekAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DAY_OF_MONTH ) );

		while ( cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY )
		{
			cal.add( Calendar.DAY_OF_MONTH, 1 );
		}

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getCalendarFirstDayMonthAsLong( Calendar calendar )
	{
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)1 * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getCalendarLastDayMonthAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getCalendarFirstDayYearAsLong( Calendar calendar )
	{
		int year = calendar.get( Calendar.YEAR );

		return (long)1 * 1000000 + (long)1 * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getCalendarLastDayYearAsLong( Calendar calendar )
	{
		int year = calendar.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)31 * 1000000 + (long)12 * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static long getDayLastSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	// hector.licea
	public static Calendar getCalendarBefore( Calendar calendar, int field, int offset )
	{
		calendar.add( field, offset * (-1) );

		return calendar;
	}

	// hector.licea
	public static Calendar getCalendarAfter( Calendar calendar, int field, int offset )
	{
		calendar.add( field, offset );

		return calendar;
	}

	// hector.licea
	public static Date getDate( Date date, int field, int offset )
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.setTime( date );

		calendar.add( field, offset );

		return calendar.getTime();
	}

	public static long getHowMuchToWaitForFirstMinuteOfNextHourAsLong()
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.add( Calendar.HOUR_OF_DAY, 1 );

		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - GregorianCalendar.getInstance().getTime().getTime();
	}

	public static Date getDateFromInt( int date )
	{
		try
		{
			return new SimpleDateFormat( "yyyyMMdd" ).parse( Integer.toString( date ) );
		}
		catch ( Throwable e )
		{
			return new Date();
		}
	}

	public static Date getMonthFromInt( int date )
	{
		try
		{
			return new SimpleDateFormat( "yyyyMM" ).parse( Integer.toString( date ) );
		}
		catch ( Throwable e )
		{
			return new Date();
		}
	}

	public static Date getDateHourFromLong( long ldate )
	{
		Date date = null;
		try
		{
			date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );
		}
		catch ( ParseException e )
		{
			date = new Date();
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );

		return cal.getTime();
	}

	public static String getDateFromLongAsString( long date, String format )
	{
		try
		{
			Date input = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( date ) );

			return new SimpleDateFormat( format ).format( input );
		}
		catch ( Throwable e )
		{
			return "";
		}
	}

	public static String getCalendarAsString( Calendar calendar, String format )
	{
		return new SimpleDateFormat( format ).format( calendar.getTime() );
	}

	public static int getTodayAsInt()
	{
		return getCalendarDateAsInt( GregorianCalendar.getInstance(), 0 );
	}

	public static Calendar getCalendarNow( String horario )
	{
		return GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
	}

	public static Calendar getCalendarNow()
	{
		return GregorianCalendar.getInstance();
	}

	public static boolean isExpired( int from, int expiration ) throws ImedigException
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.setTime( getDateFromInt( from ) );
		calendar.add( Calendar.DATE, expiration );

		return getTodayAsInt() > getCalendarDateAsInt( calendar, 0 );
	}

	public static String toJson( Object obj ) throws Throwable
	{
		try
		{
			emptyToNull( obj, obj.getClass() );

			return new Gson().toJson( obj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static Object toPojo( String text, Class clazz, boolean empty ) throws Throwable
	{
		try
		{
			Gson gson = new Gson();

			Object obj = gson.fromJson( text, clazz );

			if ( empty )
				nullToEmpty( obj, clazz );

			return obj;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static ArrayList toArrayList( String text, Type listType ) throws ImedigException
	{
		try
		{
			return new Gson().fromJson( text, listType );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static <T extends Serializable> T clone2( T dto )
	{
		return SerializationUtils.clone( dto );
	}

	public static Object clone( Object o ) throws Throwable
	{
		try
		{
			Object clone = o.getClass().newInstance();

			for ( Class obj = o.getClass(); !obj.equals( Object.class ); obj = obj.getSuperclass() )
			{
				Field[] fields = obj.getDeclaredFields();

				for ( int i = 0; i < fields.length; i++ )
				{
					if ( !Modifier.isStatic( fields[i].getModifiers() ) )
					{
						fields[i].setAccessible( true );
						fields[i].set( clone, fields[i].get( o ) );
					}
				}
			}

			return clone;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static void copy( Object org, Object clone ) throws Throwable
	{
		try
		{
			for ( Class obj = org.getClass(); !obj.equals( Object.class ); obj = obj.getSuperclass() )
			{
				Field[] fields = obj.getDeclaredFields();

				for ( int i = 0; i < fields.length; i++ )
				{
					if ( !Modifier.isStatic( fields[i].getModifiers() ) )
					{
						fields[i].setAccessible( true );
						fields[i].set( clone, fields[i].get( org ) );
					}
				}
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static boolean isPersonIdentifier( String text )
	{
		for ( int i = 0; i < text.length(); i++ )
			if ( Character.isDigit( text.charAt( i ) ) )
				return true;

		return false;
	}

	public static Method getGetter( Method[] methods, String field )
	{
		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "get" + field ) )
				return method;
		}

		return null;
	}

	public static Method getSetter( Method[] methods, String field )
	{
		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "set" + field ) )
				return method;
		}

		return null;
	}

	public static void emptyToNull( Object obj, Class clazz ) throws Throwable
	{
		try
		{
			if ( List.class.isAssignableFrom( obj.getClass() ) )
			{
				for ( Object subObj : (List)obj )
					emptyToNull( subObj, subObj.getClass() );
			}
			else
			{
				Class superClazz = clazz.getSuperclass();

				if ( superClazz != null )
					Utils.emptyToNull( obj, superClazz );

				Field[] newFields = clazz.getDeclaredFields();
				Method[] methods = clazz.getDeclaredMethods();

				Object empty[] = new Object[] {};
				Object nulls[] = new Object[]
				{ null };

				for ( Field field : newFields )
				{
					if ( !Modifier.isStatic( field.getModifiers() ) )
					{
						String fname = field.getName();

						Method getter = getGetter( methods, fname );

						if ( getter != null )
						{
							Object value = getter.invoke( obj, empty );

							if ( field.getType().equals( String.class ) )
							{
								Method setter = getSetter( methods, fname );

								if ( "".equals( value ) )
									setter.invoke( obj, nulls );
							}
							else if ( field.getType().equals( List.class ) && value != null )
							{
								for ( Object subObj : (List)value )
									emptyToNull( subObj, subObj.getClass() );
							}
						}
					}
				}
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static void nullToEmpty( Object obj, Class clazz ) throws Throwable
	{
		try
		{
			Class superClazz = clazz.getSuperclass();

			if ( superClazz != null )
				Utils.nullToEmpty( obj, superClazz );

			Field[] newFields = clazz.getDeclaredFields();
			Method[] methods = clazz.getDeclaredMethods();

			Object empty[] = new Object[] {};
			Object empties[] = new Object[]
			{ "" };

			for ( Field field : newFields )
			{
				if ( !Modifier.isStatic( field.getModifiers() ) )
				{
					String fname = field.getName();

					Method getter = getGetter( methods, fname );

					if ( getter != null )
					{
						Object value = getter.invoke( obj, empty );

						if ( field.getType().equals( String.class ) )
						{
							Method setter = getSetter( methods, fname );

							if ( value == null )
								setter.invoke( obj, empties );
						}
						else if ( field.getType().equals( List.class ) && value != null )
						{
							for ( Object subObj : (List)value )
								nullToEmpty( subObj, subObj.getClass() );
						}
					}
				}
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static String getUUID()
	{
		return java.util.UUID.randomUUID().toString().replaceAll( "-", "" );
	}

	/**
	 * Limpia textos que introduce el usuario que pueden ser perjudiciales para
	 * la integridad de los datos
	 * 
	 * @param valor
	 *            Texto a limpiar de impurezas
	 * @return Texto limpio para ser guardado en la Base de datos
	 */
	public static String sanitize( String valor )
	{
		if ( valor == null )
			return "";
		valor = valor.trim();
		if ( valor.equals( "<br>" ) || valor.equals( "&nbsp;" ) )
		{
			return "";
		}
		return valor;
	}

	public static void Sleep( long time )
	{
		try
		{
			Thread.sleep( time );
		}
		catch ( InterruptedException e )
		{
			LOG.error( "InterruptedException during a sleep {}", e );
		}
	}

	public static boolean ExistsFile( String fileName )
	{
		return (new File( fileName )).exists();
	}

	public static void DeleteFile( String fileName )
	{
		(new File( fileName )).delete();
	}

	public static void ParseQueryParameters( String query, HashMap<String, String> parameters )
	{
		if ( query != null && query != "" )
		{
			try
			{
				String queries[] = URLDecoder.decode( query, "UTF-8" ).split( "&" );

				for ( int i = 0; i < queries.length; i++ )
				{
					String attrs[] = queries[i].split( "=" );

					if ( attrs.length == 2 )
						parameters.put( attrs[0], attrs[1] );
				}
			}
			catch ( UnsupportedEncodingException e )
			{
				LOG.error( "UnsupportedEncodingException decoding query parameters {}", e );
			}
		}
	}

	public static String getLastYearDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.YEAR, -1 );

		return format.format( now.getTime() );
	}

	public static Date getLastYearDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.YEAR, -1 );

		return now.getTime();
	}

	public static String getLastMonthDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.MONTH, -1 );

		return format.format( now.getTime() );
	}

	public static Date getLastMonthDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.MONTH, -1 );

		return now.getTime();
	}

	public static String getLastWeekDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.WEEK_OF_MONTH, -1 );

		return format.format( now.getTime() );
	}

	public static String getDayOfWeekAsString( Date date, int field, String format )
	{
		SimpleDateFormat formatter = new SimpleDateFormat( format );

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( Calendar.DAY_OF_WEEK, field );

		return formatter.format( calendar.getTime() );
	}

	public static Date getLastWeekDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.WEEK_OF_MONTH, -1 );

		return now.getTime();
	}

	public static String getYesterdayDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.DATE, -1 );

		return format.format( now.getTime() );
	}

	public static Date getYesterdayDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.DATE, -1 );

		return now.getTime();
	}

	public static String getTodayDate( String format )
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );

		return dateFormat.format( now.getTime() );
	}

	public static double roundDouble( double value, int decimals )
	{
		BigDecimal bd = new BigDecimal( value );

		bd = bd.setScale( decimals, BigDecimal.ROUND_HALF_EVEN );

		return bd.doubleValue();
	}

	public static byte lobyte( int value )
	{
		return (byte)(value & 0x00FF);
	}

	public static byte hibyte( int value )
	{
		return (byte)((value & 0xFF00) >> 8);
	}

	public static int getShort( int hi, int lo )
	{
		return ((hi & 0xff) << 8) | (lo & 0xff);
	}

	public static int getInt( int hi, int lo )
	{
		return ((hi & 0xffff) << 16) | (lo & 0xffff);
	}

	private static final int wCRCTable[] =
	{ 0X0000, 0XC0C1, 0XC181, 0X0140, 0XC301, 0X03C0, 0X0280, 0XC241, 0XC601, 0X06C0, 0X0780, 0XC741, 0X0500, 0XC5C1, 0XC481, 0X0440, 0XCC01, 0X0CC0, 0X0D80, 0XCD41, 0X0F00, 0XCFC1, 0XCE81, 0X0E40, 0X0A00, 0XCAC1, 0XCB81, 0X0B40, 0XC901, 0X09C0, 0X0880, 0XC841, 0XD801, 0X18C0, 0X1980, 0XD941, 0X1B00, 0XDBC1, 0XDA81, 0X1A40, 0X1E00, 0XDEC1, 0XDF81, 0X1F40, 0XDD01, 0X1DC0, 0X1C80, 0XDC41, 0X1400, 0XD4C1, 0XD581, 0X1540, 0XD701, 0X17C0, 0X1680, 0XD641, 0XD201, 0X12C0, 0X1380, 0XD341, 0X1100, 0XD1C1, 0XD081, 0X1040, 0XF001, 0X30C0, 0X3180, 0XF141, 0X3300, 0XF3C1, 0XF281, 0X3240, 0X3600, 0XF6C1, 0XF781, 0X3740, 0XF501, 0X35C0, 0X3480, 0XF441, 0X3C00, 0XFCC1, 0XFD81, 0X3D40, 0XFF01, 0X3FC0, 0X3E80, 0XFE41, 0XFA01, 0X3AC0, 0X3B80, 0XFB41, 0X3900, 0XF9C1, 0XF881, 0X3840, 0X2800, 0XE8C1, 0XE981, 0X2940, 0XEB01, 0X2BC0, 0X2A80, 0XEA41, 0XEE01, 0X2EC0, 0X2F80, 0XEF41, 0X2D00, 0XEDC1, 0XEC81, 0X2C40, 0XE401, 0X24C0, 0X2580, 0XE541, 0X2700, 0XE7C1, 0XE681, 0X2640, 0X2200, 0XE2C1, 0XE381, 0X2340, 0XE101, 0X21C0, 0X2080, 0XE041, 0XA001, 0X60C0, 0X6180, 0XA141, 0X6300, 0XA3C1, 0XA281, 0X6240, 0X6600, 0XA6C1, 0XA781, 0X6740, 0XA501, 0X65C0, 0X6480, 0XA441, 0X6C00, 0XACC1, 0XAD81, 0X6D40, 0XAF01, 0X6FC0, 0X6E80, 0XAE41, 0XAA01, 0X6AC0, 0X6B80, 0XAB41, 0X6900, 0XA9C1, 0XA881, 0X6840, 0X7800, 0XB8C1, 0XB981, 0X7940, 0XBB01, 0X7BC0, 0X7A80, 0XBA41, 0XBE01, 0X7EC0, 0X7F80, 0XBF41, 0X7D00, 0XBDC1, 0XBC81, 0X7C40, 0XB401, 0X74C0, 0X7580, 0XB541, 0X7700, 0XB7C1, 0XB681, 0X7640, 0X7200, 0XB2C1, 0XB381, 0X7340, 0XB101, 0X71C0, 0X7080, 0XB041, 0X5000, 0X90C1, 0X9181, 0X5140, 0X9301, 0X53C0, 0X5280, 0X9241, 0X9601, 0X56C0, 0X5780, 0X9741, 0X5500, 0X95C1, 0X9481, 0X5440, 0X9C01, 0X5CC0, 0X5D80, 0X9D41, 0X5F00, 0X9FC1, 0X9E81, 0X5E40, 0X5A00, 0X9AC1, 0X9B81, 0X5B40, 0X9901, 0X59C0, 0X5880, 0X9841, 0X8801, 0X48C0, 0X4980, 0X8941, 0X4B00, 0X8BC1, 0X8A81, 0X4A40, 0X4E00, 0X8EC1, 0X8F81, 0X4F40, 0X8D01, 0X4DC0, 0X4C80, 0X8C41, 0X4400, 0X84C1, 0X8581, 0X4540, 0X8701, 0X47C0, 0X4680, 0X8641, 0X8201, 0X42C0, 0X4380, 0X8341, 0X4100, 0X81C1, 0X8081, 0X4040 };

	public static int calculate_crc( int nData[], int length )
	{
		int index;
		int crc = 0xFFFF;

		for ( int i = 0; i < length; i++ )
		{
			index = (nData[i] ^ crc) & 0x00FF;
			crc >>= 8;
			crc ^= wCRCTable[index];
		}

		return crc;
	}

	public static void getParameters( String queryString, HashMap<String, String> parameters )
	{
		if ( queryString != null && queryString != "" )
		{
			try
			{
				String queries[] = URLDecoder.decode( queryString, "UTF-8" ).split( "&" );

				for ( int i = 0; i < queries.length; i++ )
				{
					String attrs[] = queries[i].split( "=" );

					if ( attrs.length == 2 )
						parameters.put( attrs[0], attrs[1] );
				}
			}
			catch ( Throwable e )
			{
			}
		}
	}

	public static int getDecimalValue( String format )
	{
		int index = format.indexOf( "." );

		return index == -1 ? 0 : format.substring( index + 1 ).length();
	}

	public static String getTokenString( String data, String key )
	{
		try
		{
			return Utils.getTrustedToken( data, key );
		}
		catch ( Throwable e )
		{
		}

		return "";
	}

	/**
	 * @author Dismer Ronda
	 * 
	 * @param origin
	 * @param substring
	 * @return
	 */
	public static String removeSubString( String origin, String substring )
	{
		if ( origin != null && substring != null )
		{
			int pos = origin.indexOf( substring );

			return pos != -1 ? origin.substring( 0, pos ) + origin.substring( pos + substring.length(), origin.length() ) : origin;
		}
		else
			return origin;
	}

	/**
	 * Cast and validate objet form String to Integer
	 * 
	 * @param object
	 * @return
	 */
	public static Integer castToIntegerFromObject( Object object ) throws Throwable
	{
		Integer result = null;

		try
		{
			if ( object instanceof String )
			{
				if ( (String)object != null && ((String)object).compareTo( "" ) != 0 )
				{
					result = Integer.parseInt( (String)object );
				}
			}
			else
			{
				result = (Integer)object;
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}

		return result;
	}

	/**
	 * Cast and validate objet form String to Double
	 * 
	 * @param object
	 * @return
	 */
	public static Double castToDoubleFromObject( Object object ) throws Throwable
	{
		Double result = null;

		try
		{
			if ( object instanceof String )
			{
				if ( (String)object != null && ((String)object).compareTo( "" ) != 0 )
				{
					result = Double.parseDouble( (String)object );
				}
			}
			else
			{
				result = (Double)object;
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}

		return result;
	}

	public static boolean isSameHour( Calendar from, Calendar to )
	{
		return from.get( Calendar.DATE ) == to.get( Calendar.DATE ) && from.get( Calendar.MONTH ) == to.get( Calendar.MONTH ) && from.get( Calendar.YEAR ) == to.get( Calendar.YEAR ) && from.get( Calendar.HOUR_OF_DAY ) == to.get( Calendar.HOUR_OF_DAY );
	}

	public static Calendar getCalendarFromDateLong( long time ) throws Throwable
	{
		try
		{
			Calendar calendar = new GregorianCalendar();

			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmSS" ).parse( Long.toString( time ) ) );

			return calendar;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static Calendar getCalendarFromDateLong( long time, String horario ) throws Throwable
	{
		try
		{
			Calendar calendar = new GregorianCalendar( TimeZone.getTimeZone( horario ) );

			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmSS" ).parse( Long.toString( time ) ) );

			return calendar;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static long getTotalMinutes( Calendar from, Calendar to )
	{
		return (to.getTimeInMillis() - from.getTimeInMillis()) / Utils.ONE_MINUTE;
	}

	public static String getUserLoginFromRequest( HashMap<String, String> parameters )
	{
		String login = parameters.get( "login" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "user" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "usr" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "usuario" );

		if ( login == null )
			login = "";

		return login;
	}

	public static String getUserPasswordFromRequest( HashMap<String, String> parameters )
	{
		String password = parameters.get( "pwd" );

		if ( password == null || password.isEmpty() )
			password = parameters.get( "password" );

		if ( password == null )
			password = "";

		return password;
	}

	/**
	 * 
	 * @param fechaIn
	 * @return
	 */
	public static String formatFecha( String strFecha, String strFechaPattern, String resulFechaPattern ) throws Throwable
	{
		SimpleDateFormat formatIn = new SimpleDateFormat( strFechaPattern );
		SimpleDateFormat formatOut = new SimpleDateFormat( resulFechaPattern );
		String fechaFormated = "";

		if ( strFecha.compareTo( "" ) != 0 )
		{
			try
			{
				Date dateInFormated = formatIn.parse( strFecha );
				fechaFormated = formatOut.format( dateInFormated );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );

				throw e;
			}
		}

		return fechaFormated;
	}

	public static boolean isValidTimeRange( String start, String end )
	{
		try
		{
			Date d1 = timeFormat.parse( start );
			Date d2 = timeFormat.parse( end );

			return d1.before( d2 );
		}
		catch ( ParseException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static LocalTime getTime( String time )
	{
		return LocalTime.parse( time );
	}
	
	public static Date getDateWithTime( Date date, LocalTime time )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		calendar.set( Calendar.HOUR_OF_DAY, time.getHourOfDay() );
		calendar.set( Calendar.MINUTE, time.getMinuteOfHour());
		//calendar.set( Calendar.SECOND, 0 );
		
		return calendar.getTime();
	}

//	public static Date getTime( String time )
//	{
//
//		Date date = new Date( 0 );
//
//		try
//		{
//			Date d1 = timeFormat.parse( time );
//
//			Calendar calendar = GregorianCalendar.getInstance();
//			calendar.setTime( date );
//			Calendar calendar2 = GregorianCalendar.getInstance();
//			calendar.setTime( d1 );
//
//			calendar.set( Calendar.HOUR_OF_DAY, calendar2.get( Calendar.HOUR_OF_DAY ) );
//			calendar.set( Calendar.MINUTE, calendar2.get( Calendar.MINUTE ) );
//
//			return calendar.getTime();
//		}
//		catch ( ParseException e )
//		{
//		}
//
//		return new Date( 0 );
//	}

	public static Calendar getCalendarFromDate( Date date, String horario, int field, int offset )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.setTime( date );
		calendar.add( field, offset );

		return calendar;
	}

	public static long getHowMuchToWaitForFirstSecondOfNextHour()
	{
		Calendar calendar = Calendar.getInstance();

		calendar.add( Calendar.HOUR_OF_DAY, 1 );

		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime();
	}

	public static long getHowMuchToWaitForFirstSecondOfNextMinute()
	{
		Calendar calendar = Calendar.getInstance();

		calendar.add( Calendar.MINUTE, 1 );

		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime();
	}

	public static Method getGetter( String name, Class clazz )
	{
		Method[] methods = clazz.getDeclaredMethods();

		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "get" + name ) )
				return method;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getGetter( name, superClazz );

		return null;
	}

	public static Method getSetter( String name, Class clazz )
	{
		Method[] methods = clazz.getDeclaredMethods();

		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "set" + name ) )
				return method;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getGetter( name, superClazz );

		return null;
	}

	public static Field getField( String name, Class clazz )
	{
		Field[] fields = clazz.getDeclaredFields();

		for ( Field field : fields )
		{
			if ( field.getName().equalsIgnoreCase( name ) )
				return field;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getField( name, superClazz );

		return null;
	}

	public static Object getFieldObjectInClass( Object obj, String name, Class clazz ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Field field = getField( name, clazz );

		if ( field != null )
		{
			Method getter = getGetter( name, clazz );

			if ( getter != null )
			{
				Object empty[] = new Object[] {};

				return getter.invoke( obj, empty );
			}
		}

		return null;
	}

	public static Object getFieldObject( Object obj, String name ) throws Throwable
	{
		try
		{
			int p = name.indexOf( "." );

			String field = p != -1 ? name.substring( 0, p ) : name;
			String rest = p != -1 ? name.substring( p + 1 ) : "";

			Object subObj = getFieldObjectInClass( obj, field, obj.getClass() );

			if ( rest.isEmpty() )
				return subObj;

			return getFieldObject( subObj, rest );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static String getFormatedDate( String format ) throws Throwable
	{
		try
		{
			SimpleDateFormat formato = new SimpleDateFormat( format );

			Calendar cal = new GregorianCalendar();
			java.util.Date fecha = cal.getTime();

			return formato.format( fecha );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static String getFormatedDate( Long ldate, String format ) throws ImedigException
	{
		try
		{
			Date date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis( date.getTime() );

			return new SimpleDateFormat( format ).format( cal.getTime() );
		}
		catch ( ParseException e )
		{
			return Long.toString( ldate );
		}
	}

	public static String getFormatedMonth( Integer ldate, String format )
	{
		try
		{
			Date date = new SimpleDateFormat( "yyyyMM" ).parse( Long.toString( ldate ) );

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis( date.getTime() );

			return new SimpleDateFormat( format ).format( cal.getTime() );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String getFormatedDate( Date date, String format )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		return new SimpleDateFormat( format ).format( cal.getTime() );
	}

	public static Class getMethodClass( Class classZ, String name, Class types[] ) throws Throwable
	{
		try
		{
			return classZ.getMethod( name, types ).getReturnType();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static Map<String, Field> getAllFields( Class clazz )
	{

		Map<String, Field> fields = new HashMap<String, Field>();

		Class cl = clazz;
		while ( cl != null && !(cl.getName().equalsIgnoreCase( "java.lang.Object" )) )
		{
			Field[] fieldsClass = cl.getDeclaredFields();

			for ( Field field : fieldsClass )
			{
				fields.put( field.getName(), field );
			}
			cl = cl.getSuperclass();
		}

		return fields;
	}

	public static Class getFieldClass( Class classZ, String name ) throws Throwable
	{
		try
		{
			StringTokenizer st = new StringTokenizer( name, "." );
			Map<String, Field> metodos = getAllFields( classZ );
			Field field = null;
			while ( st.hasMoreTokens() )
			{
				String atributo = st.nextToken();
				field = metodos.get( atributo );
				if ( field == null )
				{
					throw new Exception( "field unknown: " + atributo + " in " + name );
				}
				Class tipo = field.getType();
				metodos = getAllFields( tipo );
			}

			if ( field == null )
				throw new Exception( "field " + name + " not found" );

			return field.getType();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			throw e;
		}
	}

	public static Date getDateFromString( String date )
	{
		try
		{
			Calendar calendar = new GregorianCalendar();

			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmSS" ).parse( date ) );

			return calendar.getTime();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			return new Date();
		}
	}

	public static List<String> getActiveVariables( String variables )
	{
		try
		{
			return Utils.toArrayList( variables, new TypeToken<ArrayList<String>>()
			{
			}.getType() );
		}
		catch ( ImedigException e )
		{
		}

		return null;
	}

	public static String addCause( String cause, String newCause )
	{
		if ( !cause.isEmpty() )
			cause += ", ";
		cause += newCause;

		return cause;
	}

	public static boolean isSameHour( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.DAY_OF_MONTH ) == calNext.get( Calendar.DAY_OF_MONTH ) && calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR ) && calPrev.get( Calendar.HOUR_OF_DAY ) == calNext.get( Calendar.HOUR_OF_DAY );
	}

	public static boolean isSameDay( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.DAY_OF_MONTH ) == calNext.get( Calendar.DAY_OF_MONTH ) && calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameMonth( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameYear( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameWeek( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setFirstDayOfWeek( Calendar.MONDAY );
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setFirstDayOfWeek( Calendar.MONDAY );
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.WEEK_OF_YEAR ) == calNext.get( Calendar.WEEK_OF_YEAR ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static Date setTimeToDate( Date date, Date time )
	{
		Calendar calDate = GregorianCalendar.getInstance();
		calDate.setTime( date );

		Calendar calTime = GregorianCalendar.getInstance();
		calTime.setTime( time );

		calDate.set( Calendar.SECOND, 0 );
		calDate.set( Calendar.HOUR_OF_DAY, calTime.get( Calendar.HOUR_OF_DAY ) );
		calDate.set( Calendar.MINUTE, calTime.get( Calendar.MINUTE ) );

		return calDate.getTime();
	}

	public static LocalTime getTime( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		
		return new LocalTime( calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ), 0 );
	}

	public static Date getTime( Integer date )
	{
		try
		{
			String s = date.toString();

			if ( s.length() == 1 )
				s = "000" + s;
			else if ( s.length() == 2 )
				s = "00" + s;
			else if ( s.length() == 3 )
				s = "0" + s;

			return new SimpleDateFormat( "HHmm" ).parse( s );
		}
		catch ( ParseException e )
		{
			Utils.logException( e, LOG );

			return new Date();
		}
	}

	public static String replaceWildcards( String text, Usuario usuario )
	{
		return text.replaceAll( "%login%", usuario.getLogin() ).replaceAll( "%password%", usuario.getPwd() );
	}

	public static int compateDates( String date1, String date2 )
	{
		SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );

		try
		{
			Date d1 = format.parse( date1 );
			Date d2 = format.parse( date2 );

			return d1.compareTo( d2 );
		}
		catch ( Exception e )
		{
		}

		return 0;
	}

	public static int compareInstances( String number1, String number2 )
	{
		try
		{
			int in1 = Integer.parseInt( number1 );
			int in2 = Integer.parseInt( number2 );

			return in1 - in2;
		}
		catch ( Exception e )
		{
		}

		return 0;
	}

	public static boolean isValidRequest( String token, String data, String ts, String password, long timeout )
	{
		if ( token == null || data == null || ts == null || password == null )
			return false;

		try
		{
			if ( Utils.isTrustedToken( token, data, password ) )
			{
				long when = Utils.getLong( ts, 0 );
				long now = new Date().getTime();

				return timeout != 0 ? when + timeout >= now : true;
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		return false;
	}

	public static String getAge( Date dob, String month, String year )
	{
		Calendar calDOB = GregorianCalendar.getInstance();
		Calendar calNow = GregorianCalendar.getInstance();

		calDOB.setTime( dob );
		calNow.setTime( new java.util.Date() );

		int ageYr = (calNow.get( Calendar.YEAR ) - calDOB.get( Calendar.YEAR ));
		int ageMo = (calNow.get( Calendar.MONTH ) - calDOB.get( Calendar.MONTH ));

		if ( calNow.get( Calendar.DAY_OF_YEAR ) < calDOB.get( Calendar.DAY_OF_YEAR ) )
			ageYr--;

		if ( ageYr <= 0 )
			return Integer.toString( ageMo ) + month;
		else
			return Integer.toString( ageYr ) + year;
	}
	
	public static String elapseTimeFromSec( Integer seconds )
	{
        int sec = seconds % 60;
        int hour = seconds / 60;
        int min = hour % 60;
        hour = hour / 60;
        
        if (min == 0) return String.format( "%dh", hour);
        
        return String.format( "%dh, %dmin", hour, min );
	}

	public static String elapseTimeFromMin( Integer minutes )
	{
		return elapseTimeFromSec( minutes * 60 );
	}

	public static String getLastYearDate( String format )
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.YEAR, -1 );

		return dateFormat.format( now.getTime() );
	}

	public static String getLastMonthDate( String format )
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.MONTH, -1 );

		return dateFormat.format( now.getTime() );
	}

	public static String getLastWeekDate( String format )
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.WEEK_OF_MONTH, -1 );

		return dateFormat.format( now.getTime() );
	}

	public static String getYesterdayDate( String format )
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.DATE, -1 );

		return dateFormat.format( now.getTime() );
	}

	public static String getEnviroment( String variable )
	{
		String value = System.getenv( variable );

		return value == null ? "" : value;
	}

	public static void setProperty( String key, String value )
	{
		if ( getProperty( key ) != null )
			return;

		System.setProperty( key, value );
	}

	public static String getProperty( String key )
	{
		return System.getProperty( key );
	}

	public static String readFile( String filename )
	{
		String content = "";

		File file = new File( filename );
		try
		{
			FileReader reader = new FileReader( file );

			BufferedReader bufferedReader = new BufferedReader( reader );

			String line = null;
			while ( (line = bufferedReader.readLine()) != null )
			{
				if ( !content.isEmpty() )
					content += "\n";

				content += line;
			}

			bufferedReader.close();
		}
		catch ( Throwable e )
		{
			LOG.info( filename );
			e.printStackTrace();
		}

		return content;
	}

	public static void writeFile( String filename, String text )
	{
		try
		{
			FileWriter writer = new FileWriter( filename );

			BufferedWriter bufferedWriter = new BufferedWriter( writer );

			bufferedWriter.write( text );
			// bufferedWriter.newLine();

			bufferedWriter.close();
		}
		catch ( IOException e )
		{
			LOG.info( filename );
			e.printStackTrace();
		}
	}

	public static String cmdExec( String cmdLine )
	{
		LOG.info( "cmdExec " + cmdLine );

		String line;
		String output = "";
		try
		{
			String[] cmd =
			{ "/bin/sh", "-c", cmdLine };
			Process p = Runtime.getRuntime().exec( cmd );

			BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			while ( (line = input.readLine()) != null )
			{
				output += (line + '\n');
			}
			input.close();
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}

		return output;
	}

	public static void cmdExecNoWait( String cmdLine )
	{
		try
		{
			String[] cmd =
			{ "/bin/sh", "-c", cmdLine };
			Runtime.getRuntime().exec( cmd );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
	}

	public static byte[] getSerializedObject( Object obj )
	{
		ObjectOutputStream oos = null;

		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			oos = new ObjectOutputStream( os );
			oos.writeObject( obj );

			return os.toByteArray();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( oos != null )
			{
				try
				{
					oos.close();
				}
				catch ( Throwable e )
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public static Object getDeserializedObject( String content )
	{
		ObjectInputStream ois = null;

		try
		{

			ByteArrayInputStream is = new ByteArrayInputStream( content.getBytes() );

			ois = new ObjectInputStream( is );

			return ois.readObject();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( ois != null )
			{
				try
				{
					ois.close();
				}
				catch ( Throwable e )
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public static boolean writeObject( String fileName, Object obj )
	{
		try
		{
			FileOutputStream fileOut = new FileOutputStream( fileName );

			ObjectOutputStream objectStream = new ObjectOutputStream( fileOut );

			objectStream.writeObject( obj );

			objectStream.close();
			fileOut.close();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return false;
	}

	public static Object readObject( String fileName )
	{
		try
		{
			FileInputStream fileIn = new FileInputStream( fileName );

			ObjectInputStream objectStream = new ObjectInputStream( fileIn );

			Object request = objectStream.readObject();

			objectStream.close();
			fileIn.close();

			return request;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return null;
	}

	public static String extractLastDir( String path )
	{
		File file = new File( path );

		return file.getName();
	}

	public static String formatTimeSpan( int years, int days, int hours, int mins, int secs )
	{
		String ret = "";

		int parts = 0;

		if ( years > 0 && parts < 2 )
		{
			ret += String.format( "%02dy", years );
			parts++;
		}

		if ( days > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02dd", days );
			parts++;
		}

		if ( hours > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02dh", hours );
			parts++;
		}

		if ( mins > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02d'", mins );
			parts++;
		}

		if ( secs > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02d\"", secs );
			parts++;
		}

		return ret;
	}

	public static Calendar getServerCalendarFromDateLong( long time )
	{
		Calendar calendar = GregorianCalendar.getInstance();

		try
		{
			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( time ) ) );
		}
		catch ( Throwable e )
		{
		}

		return calendar;
	}

	public static String getDuration( long duration )
	{
		try
		{
			int years = (int)duration / (365 * 3600 * 24);
			int remainder = (int)duration - years * 365 * 3600 * 24;

			int days = (int)remainder / (3600 * 24);
			remainder = (int)remainder - days * 3600 * 24;

			int hours = (int)remainder / 3600;
			remainder = (int)remainder - hours * 3600;

			int mins = remainder / 60;
			remainder = remainder - mins * 60;

			int secs = remainder;

			return Utils.formatTimeSpan( years, days, hours, mins, secs );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return "";
	}

	public static String getProtocolFromUrl( String url )
	{
		if ( url.startsWith( "https://" ) )
			return "https";
		if ( url.startsWith( "http://" ) )
			return "http";

		return "http";
	}

	public static boolean isHostSeparator( char c )
	{
		return c == '/' || c == '?' || c == ':';
	}

	public static boolean isPortSeparator( char c )
	{
		return c == '/' || c == '?';
	}

	public static String getHostFromUrl( String url )
	{
		int pos = url.indexOf( "://" );

		String ret = "";

		int i = pos != -1 ? pos + 3 : 0;

		while ( i < url.length() && !isHostSeparator( url.charAt( i ) ) )
		{
			ret += url.charAt( i );
			i++;
		}

		return ret;
	}

	public static int getPortFromUrl( String url )
	{
		String protocol = getProtocolFromUrl( url );

		String ret = "";

		int pos = url.indexOf( "://" );
		int i = pos != -1 ? pos + 3 : 0;
		while ( i < url.length() && url.charAt( i ) != ':' )
			i++;
		if ( i < url.length() )
			i++;

		while ( i < url.length() && !isPortSeparator( url.charAt( i ) ) )
		{
			ret += url.charAt( i );
			i++;
		}

		return !ret.isEmpty() ? Integer.parseInt( ret ) : (protocol.equals( "https" ) ? 443 : 80);
	}

	public static String getUriFromUrl( String url )
	{
		int pos = url.indexOf( "://" );

		String ret = "";

		int i = pos != -1 ? pos + 3 : 0;

		while ( i < url.length() && url.charAt( i ) != '/' )
			i++;

		while ( i < url.length() )
		{
			ret += url.charAt( i );
			i++;
		}

		return ret;
	}

	public static String removeTrailing( String source, String trail )
	{
		if ( source.endsWith( trail ) )
			return source.substring( 0, source.length() - trail.length() );

		return source;
	}
	
	public static <T> T head( List<T> list )
	{
		if ( list == null || list.isEmpty() )
			return null;

		return list.get( 0 );
	}

	public static <T> T end( List<T> list )
	{
		if ( list == null || list.isEmpty() )
			return null;

		return list.get( list.size() - 1 );
	}

	public static <T> List<T> init( List<T> list )
	{
		if ( list.isEmpty() )
			return list;

		return list.subList( 0, list.size() - 1 );
	}

	public static <T> List<T> tail( List<T> list )
	{
		if ( list.isEmpty() )
			return null;

		return list.subList( 1, list.size() );
	}
}
