package es.pryades.imedig.cloud.backend;

import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.imedig.cloud.common.AppUtils;
import es.pryades.imedig.cloud.common.BaseDesktopWindow;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dal.PerfilesDerechosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Perfil;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.ioc.IOCManager;

@SuppressWarnings(
{ "serial", "unchecked", "unused" })
public class LoginPanel extends VerticalLayout
{
	private static final Logger LOG = Logger.getLogger( LoginPanel.class );

	private static final long serialVersionUID = -644736804420195948L;

	private ImedigContext ctx;

	private PasswordField passwordField;
	private TextField userField;
	private TextField userFieldForgot;
	private BackendMainWnd loginWnd;

	private UsuariosManager usuariosManager;

	private String login;
	private String password;

	public LoginPanel( ImedigContext ctx, BackendMainWnd mainWnd )
	{
		this.ctx = ctx;
		this.loginWnd = mainWnd;

		usuariosManager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );

		HashMap<String, String> parameters = (HashMap<String, String>)ctx.getData( "parameters" );

		login = "";
		password = "";

		setSpacing( true );
		setMargin( false );
		
		setWidth( "100%" );

		Component component = null;

		component = buildLogo();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		component = loginPanel();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		/*component = loginSendPassword();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );*/
	}

	private Component buildLogo()
	{
		Embedded img = new Embedded( null, new ThemeResource( "images/logo.png" ) );
		img.setHeight( "101px" );
		img.setWidth( "362px" );

		return img;
	}

	private Component loginPanel()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth( "340px" );
		layout.setSpacing( true );
		layout.setMargin( false );

		VerticalLayout form = new VerticalLayout();
		form.setStyleName( "loginform" );
		form.setWidth( "338px" );

		HorizontalLayout rowHeader = new HorizontalLayout();
		rowHeader.setWidth( "100%" );
		rowHeader.setHeight( "33px" );
		rowHeader.setStyleName( "header" );
		rowHeader.setMargin( false );
		rowHeader.setSpacing( false );

		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );
		rowButton.setMargin( false );
		rowButton.setSpacing( false );

		Label labelCaption = new Label( ctx.getString( "LoginDlg.title" ) );
		labelCaption.setStyleName( "header" );
		labelCaption.setWidth( "100%" );
		
		rowHeader.addComponent( labelCaption );
		rowHeader.setComponentAlignment( labelCaption, Alignment.MIDDLE_LEFT );
		form.addComponent( rowHeader );

		VerticalLayout inside = new VerticalLayout();
		inside.setStyleName( "loginuser" );
		inside.setWidth( "100%" );
		inside.setMargin( true );
		inside.setSpacing( true );

		HorizontalLayout rowUser = new HorizontalLayout();
		rowUser.setWidth( "100%" );
		rowUser.setMargin( false );
		rowUser.setSpacing( true );

		HorizontalLayout rowPwd = new HorizontalLayout();
		rowPwd.setWidth( "100%" );
		rowPwd.setMargin( false );
		rowPwd.setSpacing( true );

		userField = new TextField();
		userField.setImmediate( false );
		userField.setId( "LoginDlg.user" );
		userField.setValue( login );
		userField.setWidth( "100%" );
		//userField.setInputPrompt( ctx.getString( "words.user" ) );
		//userField.setStyleName( "login" );

		Label label = new Label( ctx.getString( "words.user" ) );
		label.setStyleName( "login" );
		label.setWidth( "120px" );
		rowUser.addComponent( label );
		rowUser.setComponentAlignment( label, Alignment.MIDDLE_RIGHT );
		rowUser.addComponent( userField );
		rowUser.setExpandRatio( userField, 1.0f );
		//rowUser.setComponentAlignment( userField, Alignment.MIDDLE_LEFT );
		
		passwordField = new PasswordField();
		passwordField.setImmediate( false );
		passwordField.setId( "LoginDlg.password" );
		passwordField.setValue( password );
		passwordField.setWidth( "100%" );
		//passwordField.setInputPrompt( ctx.getString( "words.password" ) );
		//passwordField.setStyleName( "login" );

		label = new Label( ctx.getString( "words.password" ) );
		label.setStyleName( "login" );
		label.setWidth( "120px" );
		rowPwd.addComponent( label );
		rowPwd.setComponentAlignment( label, Alignment.MIDDLE_RIGHT );
		rowPwd.addComponent( passwordField );
		rowPwd.setExpandRatio( passwordField, 1.0f );
		//rowPwd.setComponentAlignment( passwordField, Alignment.MIDDLE_LEFT );

		inside.addComponent( rowUser );
		inside.addComponent( rowPwd );
		
		form.addComponent( inside );

		layout.addComponent( form );

		Button btn = new Button( ctx.getString( "words.login" ) );
		/*btn.addStyleName( "login" );
		btn.addStyleName( "primary" );*/
		btn.setClickShortcut( KeyCode.ENTER );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316141851767L;

			public void buttonClick( ClickEvent event )
			{
				onLogin();
			}
		} );
		rowButton.addComponent( btn );
		rowButton.setComponentAlignment( btn, Alignment.MIDDLE_RIGHT );
		
		layout.addComponent( rowButton );
		//layout.setComponentAlignment( rowButton, Alignment.MIDDLE_RIGHT );

		userField.focus();
		
		return layout;
	}

	private Component loginSendPassword()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth( "340px" );
		layout.setSpacing( true );
		layout.setMargin( false );

		VerticalLayout form = new VerticalLayout();
		form.setStyleName( "loginform" );
		form.setWidth( "338px" );

		HorizontalLayout rowHeader = new HorizontalLayout();
		rowHeader.setWidth( "100%" );
		rowHeader.setHeight( "33px" );
		rowHeader.setStyleName( "header" );
		rowHeader.setMargin( false );
		rowHeader.setSpacing( false );
		
		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );
		rowButton.setMargin( false );
		rowButton.setSpacing( false );

		Label labelCaption = new Label( ctx.getString( "LoginDlg.password.forgot" ) );
		labelCaption.setStyleName( "header" );
		labelCaption.setWidth( "100%" );
		rowHeader.addComponent( labelCaption );
		rowHeader.setComponentAlignment( labelCaption, Alignment.MIDDLE_LEFT );
		form.addComponent( rowHeader );

		VerticalLayout inside = new VerticalLayout();
		inside.setStyleName( "loginuser" );
		inside.setWidth( "100%" );
		inside.setMargin( true );
		inside.setSpacing( false );

		userFieldForgot = new TextField();
		userFieldForgot.setImmediate( false );
		userFieldForgot.setInputPrompt( ctx.getString( "words.email" ) );
		userFieldForgot.setWidth( "100%" );
		//userFieldForgot.setStyleName( "login" );
		
		inside.addComponent( userFieldForgot );

		form.addComponent( inside );

		Button btn = new Button( ctx.getString( "PasswordRenewDlg.send" ) );
		//btn.addStyleName( "login" );
		btn.addClickListener( new Button.ClickListener()
		{

			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				onRenew();
			}
		} );
		rowButton.addComponent( btn );
		rowButton.setComponentAlignment( btn, Alignment.MIDDLE_RIGHT );
		
		layout.addComponent( form );

		layout.addComponent( rowButton );

		return layout;
	}

	private void onLogin()
	{
		String login = (String)userField.getValue();
		String password = (String)passwordField.getValue();
		String subject = ctx.getString( "LoginDlg.message.subject" );
		String body = ctx.getString( "LoginDlg.message.body" );

		try
		{
			usuariosManager.validateUser( ctx, login, password, subject, body, true );

			if ( ctx.isAccountExpired() )
				ctx.getUsuario().setEstado( Usuario.PASS_EXPIRY );

			if ( ctx.getUsuario().getEstado() != Usuario.PASS_OK )
				changePassword();
			else
				Login();
		}
		catch ( Throwable e )
		{
			Notification.show( ctx.getString( "LoginDlg.loginfail" ), Notification.Type.ERROR_MESSAGE );
		}
	}

	private void onRenew()
	{
		String email = (String)this.userFieldForgot.getValue();

		if ( email.isEmpty() )
			Notification.show( ctx.getString( "PasswordRenewDlg.notblank" ), Notification.Type.ERROR_MESSAGE );
		else
		{
			if ( !"demo".equals( email ) )
			{
				try
				{
					usuariosManager.sendNewPassword( ctx, email, ctx.getString( "PasswordRenewDlg.message.subject" ), ctx.getString( "PasswordRenewDlg.message.body" ), true );
	
					Notification.show( ctx.getString( "PasswordRenewDlg.sent" ), Notification.Type.TRAY_NOTIFICATION );
				}
				catch ( Throwable e )
				{
					Notification.show( ctx.getString( "error.email.fail" ), Notification.Type.ERROR_MESSAGE );
				}
			}
		}
	}

	private void changePassword()
	{
		String comments = "";

		switch ( ctx.getUsuario().getEstado() )
		{
			case Usuario.PASS_CHANGED:
				comments = ctx.getString( "LoginDlg.password.renew" );
				break;

			case Usuario.PASS_EXPIRY:
				comments = ctx.getString( "LoginDlg.password.expired" );
				break;

			case Usuario.PASS_FORGET:
				comments = ctx.getString( "LoginDlg.password.forget" );
				break;

			case Usuario.PASS_NEW:
				ctx.getString( "LoginDlg.password.new" );
				break;
		}

		BaseDesktopWindow dlg = new ChangePasswordDlg( ctx.getString( "ChangePasswordDlg.title" ), ctx.getResources(), comments + "\n" );

		dlg.setContext( ctx );

		dlg.addCloseListener( new Window.CloseListener()
		{
			@Override
			public void windowClose( CloseEvent e )
			{
				onPasswordChanged( e );
			}
		} );

		getUI().addWindow( dlg );
	}

	private void onPasswordChanged( CloseEvent event )
	{
		ChangePasswordDlg dlg = (ChangePasswordDlg)event.getWindow();

		if ( dlg.isChanged() )
		{
			Login();
		}
	}

	private void Login()
	{
		Usuario autorizacion = ctx.getUsuario();

		try
		{
			PerfilesDerechosManager perfManager = (PerfilesDerechosManager)IOCManager.getInstanceOf( PerfilesDerechosManager.class );

			Perfil query = new Perfil();
			query.setId( autorizacion.getPerfil() );
			ctx.setDerechos( perfManager.getRows( ctx, query ) );

			if ( ctx.hasRight( "login" ) )
			{
				loginWnd.showMainWindow();
			}
			else
				Notification.show( ctx.getString( "error.login.right" ), Notification.Type.ERROR_MESSAGE );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			Notification.show( ctx.getString( "error.unknown" ), Notification.Type.ERROR_MESSAGE );
		}
	}
}
