package es.pryades.imedig.cloud.submodules.centers;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import lombok.Getter;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.backend.BackendApplication;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Centro;

public class CenterCard extends HorizontalLayout 
{
	private static final long serialVersionUID = -5051524025908432168L;

	public static final int CENTER_AVAILABLE = 0;
	public static final int CENTER_UNAVAILABLE = 1;
	public static final int CENTER_CHECKING = 2;

	@Getter
	private ImedigContext context;
	private CentersView parent;
	
	private VerticalLayout mainLayout;
	
	@Getter
	private Centro centro;
	
	private Dimension dimension = null;
	private static final Integer IMG_MAX_DIM 	= 145;
	
	public CenterCard( Centro centro, ImedigContext context, CentersView parent )
	{
		this.context 	= context;
		this.centro 	= centro;
		this.parent 	= parent;
		
		setWidth("-1px");
		setHeight("-1px");
		
		mainLayout = new VerticalLayout();
		
		mainLayout.setWidth("200px");
		
		Component component = captionComponent();
		mainLayout.addComponent( component );
		mainLayout.setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		component = imageComponent();
		mainLayout.addComponent( component );
		mainLayout.setComponentAlignment( component, Alignment.MIDDLE_CENTER );
		
		addComponent(mainLayout);
	}
	
	private Component captionComponent()
	{
		Label label = new Label( centro.getDescripcion() );
		label.setStyleName( "centercard_label" );
		
		return label;
	}
	
	Embedded getCenterImage() throws IOException
	{
		BackendApplication app = (BackendApplication) context.getData( "Application" );
		
		StreamResource res = null;
		
		if (centro.getImagen() != null)
			res = app.getImage( context, centro.getImagen() );
		
		if ( res == null )
		{
			this.dimension = new Dimension( IMG_MAX_DIM, IMG_MAX_DIM );
			
			return new Embedded( null, new ThemeResource( "images/centerdefault.png" ) );
		}
		
		ImageInputStream in = ImageIO.createImageInputStream(((StreamResource)res).getStreamSource().getStream());

		@SuppressWarnings("rawtypes")
		final Iterator readers = ImageIO.getImageReaders(in);

		if ( readers.hasNext() ) 
		{
            ImageReader reader = (ImageReader) readers.next();
        
            try 
            {
                reader.setInput(in);
                dimension = new Dimension(reader.getWidth(0), reader.getHeight(0));
            } 
            finally 
            {
            	reader.dispose();
            }
        }
		
		return new Embedded( null, res );
	}

	private Component imageComponent()
	{
		VerticalLayout layout = new VerticalLayout();

		layout.setHeight( "155px" );
		layout.setWidth( "175px" );
		
		VerticalLayout inside = new VerticalLayout();
		inside.setSizeUndefined();
		
		layout.addComponent( inside );
		layout.setComponentAlignment( inside, Alignment.MIDDLE_CENTER );
		
		Embedded img = null;
		
		try
		{
			img = getCenterImage();
			
			if (dimension.getHeight() >= dimension.getWidth() )
				img.setHeight( IMG_MAX_DIM+"px" );
			else
				img.setWidth( IMG_MAX_DIM+"px" );

			inside.addComponent( img );
		}
		catch ( Throwable e )
		{
		}
		
		return layout;
	}
	
	public void setAvailable( Boolean available, Boolean reports )
	{
		String style;
		
		if ( available == null)
			style = "centercardchecking";
		else if ( available.equals( Boolean.TRUE ) )
			style = "centercardavailable";
		else
			style = "centercardunavailable";
		
		if ( reports )
			style = "centercardreports";
		
		setStyleName( style );
		mainLayout.setStyleName( style );
	}
}
