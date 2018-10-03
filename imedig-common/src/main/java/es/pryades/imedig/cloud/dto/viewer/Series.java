package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class Series implements Serializable 
{
	private static final long serialVersionUID = -3278877518031756517L;
	
	private String StudyInstanceUID;
	private String SeriesInstanceUID; 
	private String Modality;
	private String SeriesNumber;
	private String ImagesUrl;

	static public Series getSeries( String text ) throws Throwable
	{
		return (Series)Utils.toPojo( text, Series.class, true );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public List<Series> getSeriesList( String text ) throws Exception
	{
		return (List) Utils.toArrayList( text, new TypeToken<ArrayList<Series>>() {}.getType() );
	}
}
