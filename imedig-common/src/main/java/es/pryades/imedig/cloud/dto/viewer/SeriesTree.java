package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.Image;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
 
@Data 
public class SeriesTree implements Serializable 
{
	private static final long serialVersionUID = 7597628042917812899L;
	
	private Series SeriesData;
	private List<Image> ImageList;

	static public SeriesTree getSeriesTree( String text ) throws Throwable
	{
		return (SeriesTree)Utils.toPojo( text, SeriesTree.class, true );
	}
}
