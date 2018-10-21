package es.pryades.imedig.cloud.common;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import es.pryades.imedig.cloud.core.dto.ImedigContext;

public abstract class FilteredContentCloseable extends FilteredContent{

	private static final long serialVersionUID = -8726097658387278925L;

	private Button btnClose;
	
	
	public FilteredContentCloseable( ImedigContext ctx ){
		super( ctx );
	}

	/**
	 * 
	 * @param listener
	 */
	public void initComponents(ClickListener listener){
		if (isInitialized) return;
		
		super.initComponents();
		
		btnClose = new Button();
		btnClose.setCaption( getContext().getString( "words.close" ) );
		
		addOpRight( btnClose );
		
		addCloseClickListener( listener );
	}
	
	private void addCloseClickListener(ClickListener listener){
		if (listener == null) return;
		btnClose.addClickListener( listener );
	}
}
