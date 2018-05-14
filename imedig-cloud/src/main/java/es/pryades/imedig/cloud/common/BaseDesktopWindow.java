package es.pryades.imedig.cloud.common;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.backend.BackendMainWnd;
import es.pryades.imedig.cloud.common.BaseWindow;
import es.pryades.imedig.cloud.core.dto.ImedigContext;

@SuppressWarnings("unused")
public class BaseDesktopWindow extends BaseWindow 
{
	private static final long serialVersionUID = -1048039196369976768L;

	private static final Logger LOG = Logger.getLogger( BackendMainWnd.class );
	
	ImedigContext ctx;

	public BaseDesktopWindow( String title, ResourceBundle resources )
	{
		super( title, resources );
	}

	public ImedigContext getContext()
	{
		return ctx;
	}
	
	public void setContext( ImedigContext ctx )
	{
		this.ctx = ctx;
	}
}
