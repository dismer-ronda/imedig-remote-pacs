package es.pryades.imedig.cloud.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import lombok.Getter;

@SuppressWarnings("rawtypes")
public class ColumnAccessor 
{
    private static final Logger LOG = Logger.getLogger( ColumnAccessor.class );
	
	private String column;
	private int pos;
	private String token;

	@Getter
	private String method;
	@Getter
	List<Object> parameters = new ArrayList<Object>();
	@Getter
	List<Class> classes = new ArrayList<Class>();
	
	public ColumnAccessor()
	{
	}

	public boolean setColumn( String column )
	{
		this.column = column;
		pos = 0;
		
		method = "";
		token = "";

		parameters.clear();
		classes.clear();
		
		if ( column.contains( "(" ) )
			return parse();
		
		return true;
	}
	
	public String getProperty(){
		return column;
	}

	public boolean skipBlanks()
	{
		while ( !EOF() && Character.isSpaceChar( getChar() ) )
			pos++;
		
		return !EOF();
	}
	
	private char getChar()
	{
		return column.charAt(pos);
	}
	
	private boolean EOF()
	{
		return pos == column.length();
	}
	
	private boolean parseIdentifier()
	{
		if ( !skipBlanks() )
			return false;
		
		char c = getChar();
		if ( !(Character.isLetter( c ) || c == '_') )
			return false;
		
		token = String.valueOf(c);
		pos++;
		
		while ( !EOF() )
		{
			c = getChar();
			
			if ( Character.isLetterOrDigit( c ) || c == '_' )
			{
				token += c;
				pos++;
			}
			else
				break;
		}
		
		return true;
	}
	
	public boolean parseStringParameter()
	{
		if ( !skipBlanks() )
			return false;
		
		if ( getChar() != '"' )
			return false;
		
		pos++;
		
		token = "";
		
		while ( !EOF() )
		{
			char c = getChar();
			pos++;
			
			if ( c != '"' )
				token += c;
			else
				return true;
		}
			
		return false;
	}

	public boolean parseStaticParameter()
	{
		if ( !skipBlanks() )
			return false;
		
		if ( !Character.isLetter( getChar() ) )
			return false;
		
		int savePos = pos;
		
		String staticParameter = "";
		
		while ( true )
		{
			if ( !parseIdentifier() )
			{
				pos = savePos;
				return false;
			}
			
			staticParameter += token;
			
			if ( !skipBlanks() )
			{
				pos = savePos;
				return false;
			}

			char c = getChar();
			
			if ( c != '.' )
			{
				token = staticParameter;
				return true;
			}

			staticParameter += ".";
			
			pos++;
		}
	}		

	private boolean parseIntegerParameter()
	{
		if ( !skipBlanks() )
			return false;
		
		char c = getChar();
		if ( !Character.isDigit( c ) )
			return false;

		int savePos = pos;
		
		token = String.valueOf(c);
		pos++;
		
		while ( !EOF() )
		{
			c = getChar();
			
			if ( Character.isDigit( c ) )
			{
				token += c;
				pos++;
			}
			else
			{
				return true;
			}
		}

		pos = savePos;
		return false;
	}

	private boolean parseLongParameter()
	{
		if ( !skipBlanks() )
			return false;

		int savePos = pos;

		if ( !parseIntegerParameter() )
			return false;
		
		if ( !skipBlanks() )
			return false;

		if ( getChar() != 'L' )
		{
			pos = savePos;
			return false;
		}
			
		pos++;
		return true;
	}

	public boolean parseParameter()
	{
		if ( parseStringParameter() )
		{
			parameters.add( token );
    		classes.add( String.class );

			return true;
		}	
		
		if ( parseStaticParameter() )
		{
	    	int p = token.lastIndexOf( "." );
	    	
			String clazz = token.substring( 0, p );
			String prop = token.substring( p + 1 );
			
			try
			{
				Field field = Class.forName( clazz ).getField( prop );
				
				parameters.add( field.get( null ) );
				classes.add( field.getType() );

				return true;
			}
			catch ( Throwable e )
			{
				LOG.error( "reflection error accesing  " + token );
			}

			return false;
		}	

		if ( parseLongParameter() )
		{
			parameters.add( Long.parseLong( token ) );
    		classes.add( Long.class );

			return true;
		}	

		if ( parseIntegerParameter() )
		{
			parameters.add( Integer.parseInt( token ) );
			classes.add( Integer.class );

			return true;
		}	

		return false;
	}
	
	public boolean parseParameters()
	{
		if ( !skipBlanks() )
			return false;
		
		if ( getChar() != '(' )
			return false;
		pos++;
		
		int savePos = pos;

		if ( !skipBlanks() )
			return false;

		if ( getChar() == ')' )
		{
			pos++;
			return true;
		}

		while ( true )
		{
			if ( !parseParameter() )
			{
				pos = savePos;
				return false;
			}
			
			if ( !skipBlanks() )
			{
				pos = savePos;
				return false;
			}

			char c = getChar();
			
			if ( c == ')' )
			{
				pos++;
				return true;
			}
			
			if ( c != ',' )
			{
				pos = savePos;
				return false;
			}
			
			pos++;
		}
	}
	
    private boolean parse() 
    {
    	if ( parseIdentifier() )
    	{
    		method = token;
    		
    		if ( parseParameters() )
    			return true;
    	}
    	
    	return false;
    }

    private Object executeMethod( Object row ) throws ImedigException
    {
    	try
    	{
			Object objects [] = new Object[getParameters().size()];
			getParameters().toArray( objects );
			
			Class types [] = new Class[getClasses().size()];
			getClasses().toArray( types );
			
			return row.getClass().getMethod( getMethod(), types ).invoke( row, objects );
    	}
    	catch ( Throwable e )
    	{
			if ( e instanceof ImedigException )
				throw (ImedigException)e;
			
			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
    	}
    }
    
    public Object getColumnObject( Object row ) throws Throwable
    {
		if ( column.contains( "(" ) )
			return executeMethod( row );

		return Utils.getFieldObject( row, column );
    }
    
    public Class getColumnClass( Class classZ ) throws Throwable
    {
		if ( column.contains( "(" ) )
		{
			Class types [] = new Class[getClasses().size()];
			getClasses().toArray( types );

			return Utils.getMethodClass( classZ, method, types );
		}
		else 
			return Utils.getFieldClass( classZ, column );
    }
}
