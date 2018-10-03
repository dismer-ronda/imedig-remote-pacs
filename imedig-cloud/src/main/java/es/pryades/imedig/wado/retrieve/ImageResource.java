package es.pryades.imedig.wado.retrieve;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.image.SimpleYBRColorSpace;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.util.StringUtils;
import org.apache.log4j.Logger;

import es.pryades.imedig.wado.resources.ImedigResource;

/** 
 * Recurso imagen base que define las operaciones para leer de disco y redimensionar una imagen en formato DICOM
 *  
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public abstract class ImageResource extends ImedigResource 
{
	private static final long serialVersionUID = 4741487391264620673L;
	
	private static final Logger LOG = Logger.getLogger( ImageResource.class );
    
	public ImageResource() 
	{
		super();
	}

    /**
     * Redimensiona la imagen
     * 
     * @param bi 
     * 		Imagen a redimensionar
     * @param rows
     *      Nuevo número de filas 
     * @param columns
     *      Nuevo número de columnas
     * @param aspectRatio 
     * 		Relación de aspecto de la imagen
     * @return 
     * 		Imagen redimensionada
     */
    public BufferedImage resize( BufferedImage bi, String rows, String columns, float aspectRatio ) 
    {
        int h = 0;
        int w = 0;
        
        if ( rows == null && columns == null ) 
        {
            h = bi.getHeight();
            w = bi.getWidth();
        } 
        else 
        {
            if ( rows != null ) 
                h = Integer.parseInt(rows);
            if ( columns != null ) 
                w = Integer.parseInt(columns);
        }
        
        double ar = (double)bi.getWidth() / bi.getHeight();
        
        if ( h == 0 || w != 0 && h * ar > w ) 
        	h = (int) (w / ar + .5f);
        else 
        	w = (int) (h * ar + .5f);

        boolean ybr = bi.getColorModel().getColorSpace() instanceof SimpleYBRColorSpace;
        boolean rescale = w != bi.getWidth() || h != bi.getHeight();
        
        if ( !ybr && !rescale )
            return bi;

        boolean banded = bi.getSampleModel() instanceof BandedSampleModel;
        
        BufferedImage tmp = bi;
        
        if ( ybr || banded ) 
        {
            // convert YBR to RGB to workaround jai-imageio-core issue #173:
            // CLibJPEGImageWriter ignores CororSpace != sRGB
            // convert RGB color-by-plane to TYPE_INT_RGB, otherwise
            // scaleOp.filter(bi, null) will throw
            // ImagingOpException("Unable to transform src image")
        	
            tmp = new BufferedImage( bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB );
            
            Graphics2D g = tmp.createGraphics();
            
            try 
            {
                g.drawImage( tmp, 0, 0, null );
            } 
            finally 
            {
                g.dispose();
            }
        }
        
        if ( !rescale )
            return tmp;

        AffineTransform scale = AffineTransform.getScaleInstance( (double) w / tmp.getWidth(), (double) h / tmp.getHeight() );
        
        AffineTransformOp scaleOp = new AffineTransformOp( scale, AffineTransformOp.TYPE_BICUBIC ); 
        
        //BufferedImage biDest = scaleOp.filter( tmp, null );
        
        BufferedImage biDest = scaleOp.createCompatibleDestImage( tmp, tmp.getColorModel() );
        
        scaleOp.filter( tmp, biDest );
        
        return biDest;
    }
    
    /**
     * Lee una imagen en formato DICOM a partir de un fichero 
     * <p>
     * Si <code>rows o columns</code> son distintos de null, la imagen original será redimensionada
     * 
     * @param file
     *		El fichero DICOM
     * @param frame
     * 		El número de cuadro a obtener si se trata de una imagen multi-cuadro. Por defecto es 0.
     * @param rows
     *      Número de filas en la imagen a obtener.
     * @param columns
     *      Número de columnas en la imagen a obtener.
     * @param region
     *		Cadena de caracteres representando una region rectangular de la forma x,y,w,h. 
     *		Los valores estarán entre 0 y 1 representando el % respecto al ancho y alto original de la imagen.  
     * @param windowWidth
     *		Cadena de caracteres en decimal que representa el contraste deseado en la imagen
     * @param windowCenter
     *		Cadena de caracteres en decimal que representa la luminosidad deseada en la imagen
     * @return
     * 		Imagen 
     * @throws 
     */
	public BufferedImage getImage( File file, int frame, String rows, String columns, String region, String windowWidth, String windowCenter ) throws Exception, IOException 
    {
        @SuppressWarnings("rawtypes")
		Iterator it = ImageIO.getImageReadersByFormatName( "DICOM" );
        
        if ( !it.hasNext() ) 
        {
        	LOG.error( "DICOM ImageReader not found. Possible cause is missing appContextProtection=false flag in Tomcat server.xml configuration file" );
        	
        	throw new Exception( "DICOM ImageReader not found. Check appContextProtection flag in Tomcat server.xml configuration file" );
        }
        
        //Class clazz = Class.forName( "com.sun.media.imageio.stream.StreamSegmentMapper" );
        
        ImageReader reader = (ImageReader) it.next();
        
        ImageInputStream in = new FileImageInputStream( file );
        
        try 
        {
            reader.setInput( in, false );
            
            BufferedImage bi = null;
            Rectangle regionRectangle = null;
            
            DicomImageReadParam dcmParam = (DicomImageReadParam) reader.getDefaultReadParam();

            if ( region != null ) 
            {
                String[] ss = StringUtils.split( region, ',' );
                
                int totWidth = reader.getWidth( 0 );
                int totHeight = reader.getHeight( 0 );

                int topX = (int) Math.round(Double.parseDouble(ss[0]) * totWidth); // top left X value
                int topY = (int) Math.round(Double.parseDouble(ss[1]) * totHeight); // top left Y value
                int botX = (int) Math.round(Double.parseDouble(ss[2]) * totWidth); // bottom right X value
                int botY = (int) Math.round(Double.parseDouble(ss[3]) * totHeight); // bottom right Y value

                int w = botX - topX;
                int h = botY - topY;

                regionRectangle = new Rectangle( topX, topY, w, h );

                LOG.info( "Selecting ROI " + regionRectangle );

                dcmParam.setSourceRegion( regionRectangle );
            }

			DicomObject data = ((DicomStreamMetaData) reader.getStreamMetadata()).getDicomObject();
			
            if ( windowWidth != null && windowCenter != null ) 
            {
                data.putString( Tag.WindowWidth, null, windowWidth );
                data.putString( Tag.WindowCenter, null, windowCenter );
            }
            
            dcmParam.setAutoWindowing( true );
            dcmParam.setVoiLutFunction( DicomImageReadParam.LINEAR ); 

            /*if ( windowWidth != null && windowCenter != null )
            {
	            dcmParam.setWindowCenter( Float.parseFloat( windowCenter ) );
	            dcmParam.setWindowWidth( Float.parseFloat( windowWidth ) );
	            
            }*/
            
            bi = reader.read( frame, dcmParam );
                
            /*if (renderOverlays) 
             * {
                mergeOverlays(bi, reader, frame);
            }*/
            
            return resize( bi, rows, columns, reader.getAspectRatio( frame ) );
        } 
        finally 
        {
            // !!!! without this, we get "too many open files" when generating
            // icons in a tight loop
            in.close();
        }
    }
}
