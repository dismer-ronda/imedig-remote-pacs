package es.pryades.imedig.cloud.common;

import java.awt.Dimension;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import com.vaadin.ui.UI;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AppUtils implements Serializable 
{
	private static final long serialVersionUID = -4138928948020179063L;
	
	private static final Logger LOG = Logger.getLogger( AppUtils.class );

	public AppUtils()
	{
	}

	public static Label createLabel( String text, Layout layout )
	{
		Label label1 = new Label();
		
		label1.setImmediate( false );
		label1.setWidth( "-1px" );
		label1.setHeight( "-1px" );
		label1.setValue( text );

		layout.addComponent( label1 );
		
		return label1;
	}
	
	public static TextField createInput( String value, String width, boolean readOnly, Layout layout, String debugId )
	{
		TextField text1 = new TextField();
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setId( debugId );
		text1.setValue( value );
		text1.setReadOnly( readOnly );
		
		layout.addComponent( text1 );
		
		return text1;
	}

	public static TextArea createTextArea( String value, String width, boolean readOnly, Layout layout, String debugId )
	{
		TextArea text1 = new TextArea();
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setId( debugId );
		text1.setValue( value );
		text1.setReadOnly( readOnly );
		
		layout.addComponent( text1 );
		
		return text1;
	}

	public static PasswordField createPassword( Layout layout, String debugId )
	{
		PasswordField password1 = new PasswordField();
		
		password1.setImmediate( false );
		password1.setWidth( "-1px" );
		password1.setHeight( "-1px" );
		password1.setId( debugId );
		
		layout.addComponent( password1 );
	
		return password1;
	}
	
	public static Button createButton( String text, String tooltip, String debugId, Layout layout )
	{
		Button button1 = new Button();
		
		button1.setCaption( text );
		button1.setDescription( tooltip );
		button1.setImmediate(true);
		button1.setWidth("-1px");
		button1.setHeight("-1px");
		button1.setId( debugId );
		
		layout.addComponent( button1 );

		return button1;
	}
	
	public static CheckBox createCheckbox( String text, String tooltip, String debugId, Layout layout )
	{
		CheckBox cb = new CheckBox();
		
		cb.setCaption( text );
		cb.setDescription( tooltip );
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		cb.setId( debugId );
		
		layout.addComponent( cb );

		return cb ;
	}

	public static HorizontalLayout createRow( boolean margin, boolean spacing )
	{
		HorizontalLayout row = new HorizontalLayout();

		row.setMargin( margin );
		row.setSpacing( spacing );
		
		return row;
	}
	
	public static VerticalLayout createColumn( boolean margin, boolean spacing )
	{
		VerticalLayout col = new VerticalLayout();

		col.setMargin( margin );
		col.setSpacing( spacing );
		
		return col;
	}

	public static GridLayout createGrid( boolean margin, boolean spacing, int cols )
	{
		GridLayout grid = new GridLayout();

		grid.setMargin( margin );
		grid.setSpacing( spacing );
		grid.setColumns( cols );
		
		return grid;
	}

    public static boolean isLocaleSupported( Locale locale, String langs ) 
    {
		if ( locale == null )
			return false;
		
		String languages[] = langs.split( "," );

		for ( String l : languages )
		{
			if ( l.equals( locale.getLanguage() ) )
				return true;
		}
    	
		return false;
    }
    
    public static Locale getDefaultLocale()
    {
		return new Locale( "en" );
    }

    public static Locale getLocaleFromBrowser( UI ui, String langs )
    {
		Locale locale = ui.getSession().getLocale();
        
		LOG.info( "Locale from browser = " + locale.getDisplayName() );

		if ( isLocaleSupported( locale, langs ) )
			return locale;
		
		return null;
    }
    
	public static ComboBox createCombobox( String debugId, Layout layout )
	{
		ComboBox cb = new ComboBox();
		
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		cb.setId( debugId );
		
		layout.addComponent( cb );

		return cb ;
	}

	public static Panel createPanel( String text, Layout layout )
	{
		Panel cb = new Panel();
		
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		cb.setCaption( text );
		
		layout.addComponent( cb );

		return cb ;
	}
	
    public static String getString( ResourceBundle resources, String key )
    {
		try 
		{
			return resources.getString( key );
		}
		catch ( Throwable e )
		{
			return key;
		}
    }	
	
    public static String getFechaByLong(String date){
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String value = null;
		try {
			Date dateformatted = df.parse(date);
			value = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateformatted);
			
		} catch (ParseException e) {
			System.err.println("can not read date");
		}
		return value;
	}
	public static String getFechaBy(String date){
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String value = null;
		try {
			Date dateformatted = df.parse(date);
			value = new SimpleDateFormat("dd/MM/yyyy").format(dateformatted);
			
		} catch (ParseException e) {
			System.err.println("can not read date");
		}
		return value;
	}
	
	@SuppressWarnings("rawtypes") 
	public static OptionData createOption( String id, String caption, String tooltip, String icon, Boolean enabled, String right, Class clazz  )
	{
		OptionData option = new OptionData();
		
		option.setId( id );
		option.setCaption( caption );
		option.setToolTip( tooltip );
		option.setIcon( icon );
		option.setClazz( clazz );
		option.setEnabled( enabled );
		option.setRight( right );
		
		return option;
	}	

	@SuppressWarnings("rawtypes")
	public static Dimension getImageDimension( StreamResource resource) throws IOException
	{
		ImageInputStream in = ImageIO.createImageInputStream( resource.getStreamSource().getStream() );

		final Iterator readers = ImageIO.getImageReaders(in);
        
		if (readers.hasNext()) 
		{
            ImageReader reader = (ImageReader) readers.next();
        
            try 
            {
                reader.setInput(in);

                return new Dimension( reader.getWidth(0), reader.getHeight(0) );
            } 
            finally 
            {
            	reader.dispose();
            }
        }
		
		return null;
	}
}
