package es.pryades.imedig.pacs.dal;

import org.apache.log4j.Logger;

import es.pryades.imedig.core.common.ImedigManager;
import es.pryades.imedig.pacs.dal.ibatis.StudySearchMapper;
import es.pryades.imedig.pacs.dto.Study;

/**
*
* @author hector.licea 
* @since 2.0.0.0
*/
public class StudiesSearchManagerImpl extends ImedigPacsManagerImpl implements StudiesSearchManager {

	private static final long serialVersionUID = 1894369607154251573L;

	private static final Logger LOG = Logger.getLogger( StudiesSearchManagerImpl.class );

	public static ImedigManager build()
	{
		return new StudiesSearchManagerImpl();
	}

	public StudiesSearchManagerImpl()
	{
		super( StudySearchMapper.class, Study.class, LOG );
	}
}
