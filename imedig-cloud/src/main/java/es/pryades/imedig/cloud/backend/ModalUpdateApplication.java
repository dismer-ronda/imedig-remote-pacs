package es.pryades.imedig.cloud.backend;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.DialogLabel;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalUpdateApplication extends Window implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver, Upload.StartedListener, Upload.ProgressListener
{
	private static final long serialVersionUID = 2689534974013982262L;

	private static final Logger LOG = Logger.getLogger( ModalUpdateApplication.class );

	@Getter protected ImedigContext context;
	protected ModalParent modalParent;

	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesRow;

	private Label lbFicheroImagen;
	private Label lbProgress;
	protected VerticalLayout layout;
	protected Button bttnCancel;
	private Upload upload;
	private TextField editImageFicheroSeleccionado;
	private ByteArrayOutputStream stream;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalUpdateApplication( ImedigContext context, ModalParent modalParent )
	{
		super();
		
		this.context = context;
		this.modalParent = modalParent;
		
		setCaption( getContext().getString( "modalUpdateApplication.wndCaption" ) );

		setStyleName( "imedigModalfieldSet" );
		addCloseShortcut( KeyCode.ESCAPE );
		center();

		setWidth( "700px" );
		setHeight( "-1px" );

		setModal( true );
		setResizable( false );
		setClosable( false );

		initComponents();
	}

	private void bttnCancelListener()
	{
		bttnCancel.addClickListener( new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 7591024099361245222L;

			public void buttonClick( ClickEvent event )
			{
				closeModalWindow( false );
			}
		} );
	}

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	public void closeModalWindow( boolean refresh )
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );

		if ( modalParent != null && refresh )
			modalParent.refreshVisibleContent();
	}

	public void initComponents()
	{
		setContent( layout = new VerticalLayout() );
		
		layout.setSizeUndefined();
		layout.setWidth( "100%" );
		layout.setMargin( true );
		layout.setSpacing( true );

		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );

		operacionesRow = new HorizontalLayout();
		operacionesRow.setSpacing( true );

		bttnCancel = new Button( getContext().getString( "words.close" ) );
		bttnCancelListener();

		bttnCancel.focus();

		lbProgress = new Label( "" );

		operacionesRow.addComponent( lbProgress );
		operacionesRow.setComponentAlignment( lbProgress, Alignment.MIDDLE_LEFT );
		operacionesRow.addComponent( bttnCancel );
		operacionesRow.setComponentAlignment( bttnCancel, Alignment.BOTTOM_RIGHT );

		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesRow );
		layout.setComponentAlignment( operacionesRow, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( operacionesRow, 1.0f );

		lbFicheroImagen = new DialogLabel( getContext().getString( "modalUpdateApplication.lbFileName" ), "-1px" );
		
		upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalUpdateApplication.uploadCaption" ) );
		upload.addSucceededListener( this );
		upload.addStartedListener( this );
		upload.addProgressListener( this );

		editImageFicheroSeleccionado = new TextField();
		editImageFicheroSeleccionado.setValue( getContext().getString( "modalUpdateApplication.editFile" ) );
		editImageFicheroSeleccionado.setWidth( "100%" );

		HorizontalLayout rowSeleccionar = new HorizontalLayout();
		rowSeleccionar.setWidth( "100%" );
		rowSeleccionar.setSpacing( true );
		rowSeleccionar.addComponent( lbFicheroImagen );
		rowSeleccionar.addComponent( editImageFicheroSeleccionado );
		rowSeleccionar.setExpandRatio( editImageFicheroSeleccionado, 1.0f );
		rowSeleccionar.addComponent( upload );

		componentsContainer.addComponent( rowSeleccionar );
	}

	@Override
	public void uploadStarted( StartedEvent event )
	{
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		bttnCancel.setEnabled( false );
		upload.setEnabled( false );

		stream = new ByteArrayOutputStream(); 
		
		return stream;
	}

	@Override
	public void uploadFailed( FailedEvent event )
	{
		LOG.error( "Upload Fail <><> File: " + event.getFilename() );
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		editImageFicheroSeleccionado.setValue( event.getFilename() );

		try 
		{
			FileOutputStream fileStream;

			fileStream = new FileOutputStream( "/tmp/imedig-update.zip" ); // + event.getFilename() );
			fileStream.write( stream.toByteArray() );
			fileStream.close();

			LOG.info( "fichero importado "  + event.getFilename() );
			
			ConfirmDialog.show( UI.getCurrent(), 
					context.getString( "words.confirm" ), 
					getContext().getString( "modalUpdateApplication.confirm" ), 
					context.getString( "Generic.Yes" ), 
					context.getString( "Generic.No" ), 
					new ConfirmDialog.Listener() 
					{
						private static final long serialVersionUID = -3142429497962370163L;

						public void onClose(ConfirmDialog dialog) 
			            {
			                if ( dialog.isConfirmed() ) 
			                	updateApp();
			                else
			                	cancelUpdate(); 
			            }
			        } );
		} 
		catch (Throwable e) 
		{
			e.printStackTrace();
		} 

		bttnCancel.setEnabled( true );
		upload.setEnabled( true );
	}

	@Override
	public void updateProgress( long readBytes, long contentLength )
	{
		lbProgress.setValue( getContext().getString( "modalUpdateApplication.uploading" ) + " " + Utils.roundDouble( 100 * (readBytes/(double)contentLength), 2 ) + " %" );
		lbProgress.markAsDirty();
	}

	private void updateApp()
	{
		String message = Utils.cmdExec( "/opt/imedig/bin/update.sh" );
		
		if ( !message.contains( "ERROR:" ) )
			Utils.cmdExecNoWait( "/opt/imedig/bin/restart.sh" );
		
		Notification.show( message, Notification.Type.TRAY_NOTIFICATION );
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}

	private void cancelUpdate()
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}
}
