package es.pryades.imedig.viewer.components;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.ImedigTheme;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.viewer.actions.OpenImage;
import es.pryades.imedig.viewer.datas.ImageData;

public class ImageSerie extends CssLayout{

	private static final long serialVersionUID = -9051282826500875115L;

	private static final Logger LOG = LoggerFactory.getLogger( ImageSerie.class );

	private Button btnFooter;
	private Label labelFooter;
	private VerticalLayout thumnails;
	private ImedigContext context;
	private SeriesTree serie;
	private ImageSerie instance;
	private List<ImageData> datas;
	private Integer index;
	private int mode = MODE_ALL;

	private static final int LIMIT_TO_SHOW_ALL = 1;
	private static final int MODE_ALL = 1;
	private static final int MODE_ONE = 2;
	private static final List<String> MODALITIES_SERIES2 = Arrays.asList( "CT", "MR" );

	public ImageSerie( ImedigContext context, SeriesTree serie, List<ImageData> datas )	{
		addStyleName( "study-thumnails-panel" );
		instance = this;
		this.context = context;
		this.serie = serie;
		this.datas = datas;

		setWidth( "100%" );
		VerticalLayout content = new VerticalLayout();
		content.setWidth( "100%" );
		thumnails = new VerticalLayout();
		thumnails.setWidth( "100%" );
		content.addComponents( thumnails );
		addComponent( content );
	}

	public void showSerieImage()	{
		index = 0;

		if ( MODALITIES_SERIES2.contains( serie.getSeriesData().getModality() ) ){
			index = datas.size() / 2;
		}
		
		addSerieImages();
	}

	private void addSerieImages() {
		if ( datas.size() > LIMIT_TO_SHOW_ALL )	{
			showOneImage();
		} else {
			showAllImages();
		}

	}
	
	private void showOneImage() {
		addToThumnails( datas.get( index ) );
	
		addThumnailsFooter();
	
		mode = MODE_ONE;
	}

	private void addThumnailsFooter() {
		if ( labelFooter != null )
			return;

		labelFooter = new Label( String.format( "%d/%d", index + 1, datas.size() ) );
		labelFooter.addStyleName( ImedigTheme.TO_LEFT );
		btnFooter = new Button( FontAwesome.PLUS );
		btnFooter.setDescription( context.getString( "words.expand" ) );
		btnFooter.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		btnFooter.addStyleName( ValoTheme.BUTTON_TINY );
		btnFooter.addStyleName( ImedigTheme.TO_RIGHT );

		btnFooter.addClickListener( new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 267868740813696905L;

			@Override
			public void buttonClick( com.vaadin.ui.Button.ClickEvent event )
			{
				if ( mode == MODE_ONE )
				{
					mode = MODE_ALL;
					labelFooter.setVisible( false );
					btnFooter.setIcon( FontAwesome.MINUS );
					thumnails.removeAllComponents();
					btnFooter.setDescription( context.getString( "words.contract" ) );
					showAllImages();
				}
				else
				{
					thumnails.removeAllComponents();
					mode = MODE_ONE;
					labelFooter.setVisible( true );
					btnFooter.setIcon( FontAwesome.PLUS );
					btnFooter.setDescription( context.getString( "words.expand" ) );
					showOneImage();
				}
			}
		} );

		addComponents( labelFooter, btnFooter );
	}

	private void showAllImages(){
		int count = 0;
		for ( ImageData data : datas ){
			addToThumnails( data );
//			count++;
//			if (count < datas.size()){
//				thumnails.addComponent( new Label( "<hr/>", ContentMode.HTML ) );
//			}
		}
	}

	private void addToThumnails( ImageData data ) {
		com.vaadin.ui.Image img = getImage( data.getSeries(), data.getImage() );
		img.addStyleName( ImedigTheme.CURSOR_POINTER );
		img.setData( data );

		img.addClickListener( new ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -9069741717469561802L;

			@Override
			public void click( ClickEvent event )
			{
				ImageData idata = (ImageData)((com.vaadin.ui.Image)event.getSource()).getData();
				index = datas.indexOf( idata );
				updateLabelValue();
				context.sendAction( new OpenImage( this, idata ) );
			}
		} );
		VerticalLayout inside = new VerticalLayout( img );
		inside.setWidth( "64px" );
		inside.setHeight( "64px" );
		inside.setMargin( false );
		inside.setSpacing( false );
		inside.addStyleName( "margin-bottom" );
		inside.setComponentAlignment( img, Alignment.MIDDLE_CENTER );
		thumnails.addComponent( inside );
		thumnails.setComponentAlignment( inside, Alignment.MIDDLE_CENTER );
	}

	private com.vaadin.ui.Image getImage( SeriesTree series, Image image ) {

		String imageUrl = imageUrl( image );

		String modality = series.getSeriesData().getModality();
		String hint = " ";
		if ( !modality.isEmpty() )
			hint += modality + " ";

		com.vaadin.ui.Image result = new com.vaadin.ui.Image( null, new ExternalResource( imageUrl ) );
		result.setDescription( hint );

		return result;
	}

	public void changeImageFrame( ImageData data )	{
		if ( datas.indexOf( data ) < 0 )
			return;

		index = datas.indexOf( data );

		updateLabelValue();

		if ( mode == MODE_ALL )
			return;

		index = datas.indexOf( data );
		updateImage( datas.get( index ) );
	}

	private void updateImage( ImageData data )
	{

		String imageUrl = imageUrl( data.getImage() );

		com.vaadin.ui.Image image = (com.vaadin.ui.Image)((AbstractOrderedLayout)thumnails.getComponent( 0 )).getComponent( 0 );
		image.setData( data );
		image.setSource( new ExternalResource( imageUrl ) );
	}

	private String imageUrl( Image image )
	{
		String imageUrl = Utils.getEnviroment( "CLOUD_URL" ) + image.getWadoUrl() + "&contentType=image/jpeg&columns=64&rows=64";

		LOG.info( "imageUrl " + imageUrl );

		return imageUrl;
	}

	private void updateLabelValue()
	{
		if ( labelFooter == null )
			return;

		labelFooter.setValue( String.format( "%d/%d", index + 1, datas.size() ) );
	}
}
