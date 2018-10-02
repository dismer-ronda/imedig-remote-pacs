package es.pryades.imedig.viewer.actions;

import es.pryades.imedig.cloud.dto.viewer.StudyTree;

public class CloseStudies extends AbstractAction<Object, StudyTree> {

	public CloseStudies(Object source) {
		this(source, null);
	}
	
	public CloseStudies(Object source, StudyTree data) {
		super(source, data);
	}

}
