package es.pryades.imedig.cloud.dto.viewer;

import java.io.Serializable;
import java.util.List;

import es.pryades.imedig.cloud.common.Utils;
import lombok.Data;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

@Data 
public class StudyTree implements Serializable
{
	private static final long serialVersionUID = -4272162619904091477L;
	
	private Study StudyData;
	private List<SeriesTree> SeriesList;

	static public StudyTree getStudyTree( String text ) throws Throwable
	{
		return (StudyTree)Utils.toPojo( text, StudyTree.class, true );
	}
}
