package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.DetallesCentrosManager;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.query.CentroQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

public class ModalNewImage extends ModalWindowsCRUD implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver, Upload.StartedListener
{
	private static final long serialVersionUID = -725959430974010244L;

	private static final Logger LOG = Logger.getLogger( ModalNewImage.class );

	protected Imagen newImagen;

	private Label lbCentro;
	private Label lbImageNombre;
	private Label lbFicheroImagen;

	private ComboBox comboCentro;
	private TextField editImageNombre;
	private Upload upload;
	private TextField editImageFicheroSeleccionado;

	private ByteArrayOutputStream bos;

	private DetallesCentrosManager centrosManager;
	
	public ModalNewImage( ImedigContext ctx, Operation modalOperation, Imagen orgImagen, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, orgImagen, right );

		centrosManager = (DetallesCentrosManager) IOCManager.getInstanceOf( DetallesCentrosManager.class );

		initComponents();
		
		if ( !operation.equals( Operation.OP_DELETE ) )
			defaultFocus();
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newImagen = (Imagen) Utils.clone( orgDto );
		}
		catch ( Throwable e1 )
		{
			newImagen = new Imagen();
		}

		bi = new BeanItem<ImedigDto>( newImagen );

		lbCentro = new Label( getContext().getString( "words.center" ) );
		lbCentro.setWidth( "120px" );

		lbImageNombre = new Label( getContext().getString( "modalNewImage.lbImageNombre" ) );
		lbImageNombre.setWidth( "120px" );

		lbFicheroImagen = new Label( getContext().getString( "modalNewImage.lbFicheroImagen" ) );
		lbFicheroImagen.setWidth( "120px" );

		comboCentro = new ComboBox(getContext().getString( "words.center" ));
		comboCentro.setWidth( "100%" );
		comboCentro.setNullSelectionAllowed( false );
		comboCentro.setTextInputAllowed( false );
		comboCentro.setPropertyDataSource( bi.getItemProperty( "centro" ) );

		editImageNombre = new TextField( getContext().getString( "modalNewImage.lbImageNombre" ), bi.getItemProperty( "nombre" ) );
		editImageNombre.setWidth( "100%" );
		editImageNombre.setImmediate( true );
		editImageNombre.setNullRepresentation( "" );

		upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalNewImage.uploadCaption" ) );
		upload.addSucceededListener( this );
		upload.addStartedListener( this );

		editImageFicheroSeleccionado = new TextField();
		editImageFicheroSeleccionado.setValue( getContext().getString( "modalNewImage.editImageFicheroSeleccionado" ) );
		editImageFicheroSeleccionado.setWidth( "90%" );

		fillComboBoxes();

		HorizontalLayout rowTipo = new HorizontalLayout();
		rowTipo.setWidth( "100%" );
		rowTipo.addComponent( lbCentro );
		rowTipo.addComponent( comboCentro );
		rowTipo.setExpandRatio( comboCentro, 1.0f );

		HorizontalLayout rowNombre = new HorizontalLayout();
		rowNombre.setWidth( "100%" );
		rowNombre.addComponent( lbImageNombre );
		rowNombre.addComponent( editImageNombre );
		rowNombre.setExpandRatio( editImageNombre, 1.0f );

		HorizontalLayout rowSeleccionar = new HorizontalLayout();
		rowSeleccionar.setWidth( "100%" );
		rowSeleccionar.setCaption( getContext().getString( "modalNewImage.lbFicheroImagen" ) );
		rowSeleccionar.addComponent( editImageFicheroSeleccionado );
		rowSeleccionar.setExpandRatio( editImageFicheroSeleccionado, 1.0f );
		rowSeleccionar.addComponent( upload );
		
		FormLayout form = new FormLayout(comboCentro, editImageNombre, rowSeleccionar);
		form.setMargin( false );
		form.setSpacing( true );
		form.setWidth( "100%" );
		

		componentsContainer.addComponent( form );
		//componentsContainer.addComponent( rowNombre );
		//componentsContainer.addComponent( rowSeleccionar );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewImage";
	}

	@Override
	protected void defaultFocus()
	{
		editImageNombre.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			ImagenesManager imagMan = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );

			newImagen.setId( null );
			if ( bos != null )
				newImagen.setDatos( bos.toByteArray() );

			imagMan.setRow( context, null, newImagen );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onModify()
	{
		try
		{
			ImagenesManager imagMan = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );

			if ( bos != null )
				newImagen.setDatos( bos.toByteArray() );

			imagMan.setRow( context, orgDto, newImagen );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onDelete()
	{
		try
		{
			ImagenesManager imagMan = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );

			imagMan.delRow( context, newImagen );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	public void uploadStarted( StartedEvent event )
	{
		bttnOperacion.setEnabled( false );
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		bos = new ByteArrayOutputStream( 102400 );
		return bos;
	}

	@Override
	public void uploadFailed( FailedEvent event )
	{
		LOG.error( "Upload Fail <><> File: " + event.getFilename() );
		bttnOperacion.setEnabled( true );
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		LOG.info( "<><> Uplodaded file: " + event.getFilename() );
		editImageFicheroSeleccionado.setValue( event.getFilename() );
		bttnOperacion.setEnabled( true );
	}

	@SuppressWarnings("unchecked")
	private void fillComboBoxes()
	{
		SqlSession session = context.getSessionCloud();

		boolean finish = ( session == null );

		try
		{
			if ( finish )
				session = context.openSessionCloud();

			try
			{
				CentroQuery query = new CentroQuery();
				
				query.setUsuario( context.getUsuario().getId() );
				
				List<DetalleCentro> centros = centrosManager.getRows( context, query );

				for ( DetalleCentro centro : centros )
				{
					comboCentro.addItem( centro.getId() );
					comboCentro.setItemCaption( centro.getId(), centro.getNombre() );
				}
			}
			catch ( ImedigException e )
			{
			}
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();

			if ( !( e instanceof ImedigException ) )
				new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
		finally
		{
			if ( finish )
			{
				try
				{
					context.closeSessionCloud();
				}
				catch ( ImedigException e )
				{
				}
			}
		}
	}
}
