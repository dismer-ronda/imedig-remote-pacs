package es.pryades.imedig.cloud.dto.viewer;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.google.gson.reflect.TypeToken;

import es.pryades.imedig.cloud.common.Utils;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class Image 
{
	private String StudyInstanceUID;
	private String SeriesInstanceUID;
	private String SOPInstanceUID;
	private String InstanceNumber;
	private String SOPClassUID;
	private String WadoUrl;

	static public Image getImage( String text ) throws Throwable
	{
		return (Image)Utils.toPojo( text, Image.class, true );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public List<Image> getImages( String text ) throws Exception
	{
		return (List) Utils.toArrayList( text, new TypeToken<ArrayList<Image>>() {}.getType() );
	}
}
