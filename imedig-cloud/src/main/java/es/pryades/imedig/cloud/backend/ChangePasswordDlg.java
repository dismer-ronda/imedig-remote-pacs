package es.pryades.imedig.cloud.backend;

import java.util.ResourceBundle;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.BaseDesktopWindow;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class ChangePasswordDlg extends BaseDesktopWindow 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5722841682959892036L;

	private VerticalLayout layout;
	private GridLayout grid;

	private PasswordField password1;
	private PasswordField password2;
	
	private UsuariosManager usuariosManager;
	
	private boolean changed = false;
	
	public ChangePasswordDlg( String title, ResourceBundle resources, String comments )
	{
		super( title, resources );
		
		usuariosManager = (UsuariosManager) IOCManager.getInstanceOf( UsuariosManager.class );

		layout = new VerticalLayout();

		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		setContent( layout );
		
		grid = new GridLayout();
		
	    grid.setColumns( 2 );
		grid.setMargin( false );
		grid.setSpacing( true );
		grid.setSizeUndefined();

		AppUtils.createLabel( comments, layout );

	    layout.addComponent( grid );
	    
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	@SuppressWarnings("serial")
	private void addComponents() 
	{
		AppUtils.createLabel( getString( "ChangePasswordDlg.new" ), grid );		
		password1 = AppUtils.createPassword( grid, "ChangePasswordDlg.new" );
		
		AppUtils.createLabel( getString( "ChangePasswordDlg.repeat" ), grid );		
		password2 = AppUtils.createPassword( grid, "ChangePasswordDlg.repeat" );
		
		Button button1 = AppUtils.createButton( getString( "words.change" ), getString( "words.change" ), "ChangePasswordDlg.change", layout );
		button1.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) 
            {
				onChangePassword();
            }
        });
        layout.setComponentAlignment( button1, Alignment.BOTTOM_RIGHT );
	}

	private void onChangePassword()
	{
    	String pwd1 = (String) password1.getValue();
    	String pwd2 = (String) password2.getValue();

    	if ( pwd1.equals( pwd2 ) )
    	{
	       	try 
	       	{
	       		Usuario usuario = getContext().getUsuario();
	       		
	    		if ( Utils.MD5( pwd1 ).equalsIgnoreCase( usuario.getPwd() ) )
	    			Notification.show( getString( "ChangePasswordDlg.different" ), Notification.Type.ERROR_MESSAGE );
				else
				{
					usuario.setPwd( pwd1 );
	    	    	usuario.setEstado( Usuario.PASS_OK );	
	    	    	usuario.setIntentos( 0 );
	    	    	
	    			usuariosManager.setPassword( getContext(), usuario, getString( "ChangePasswordDlg.message.subject" ), getString( "ChangePasswordDlg.message.body" ), false );
	
		           	setChanged( true );
	
		           	getUI().removeWindow( this );
				}
	       	}
	       	catch ( Throwable e ) 
	        {
           		Notification.show( getString( "error.unknown" ), Notification.Type.ERROR_MESSAGE );
	        }
    	}
    	else
    		Notification.show( getString( "ChangePasswordDlg.missmatch" ), Notification.Type.ERROR_MESSAGE );
    }
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
