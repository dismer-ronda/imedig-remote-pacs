package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.HorariosManager;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dal.MonedasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.Horario;
import es.pryades.imedig.cloud.dto.Imagen;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Moneda;
import es.pryades.imedig.cloud.dto.query.ImagenQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.cloud.modules.components.ModalWindowsCRUD;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public final class ModalNewCentro extends ModalWindowsCRUD
{
	private static final Logger LOG = Logger.getLogger( ModalNewCentro.class );

	protected DetalleCentro newCentro;

	private TextField editCentroNombre;
	private TextField editCentroDescripcion;
	private TextArea editCentroDireccion;
	private TextArea editCentroContactos;
	private TextField editCentroCoordenadas;
	private ComboBox comboBoxCentroImagen;
	private TextField editCentroOrden;
	private ComboBox comboBoxCentroHorario;
	private ComboBox comboBoxCentroMoneda;
	private TextField editCentroSerie;

	private ImagenesManager imgagenesManager;

	public ModalNewCentro( ImedigContext ctx, Operation modalOperation, DetalleCentro orgCentro, ModalParent parentWindow, String right )
	{
		super( ctx, parentWindow, modalOperation, orgCentro, right );
		
		imgagenesManager = (ImagenesManager) IOCManager.getInstanceOf( ImagenesManager.class );
		
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
			newCentro = (DetalleCentro) Utils.clone( (DetalleCentro) orgDto );
		}
		catch ( Throwable e1 )
		{
			newCentro = new DetalleCentro();
		}

		bi = new BeanItem<ImedigDto>( newCentro );

		editCentroNombre = new TextField( getContext().getString( "modalNewCenter.lbCentroNombre" ), bi.getItemProperty( "nombre" ) );
		editCentroNombre.setWidth( "100%" );
		editCentroNombre.setNullRepresentation( "" );

		editCentroDescripcion = new TextField( getContext().getString( "modalNewCenter.lbCentroDescripcion" ), bi.getItemProperty( "descripcion" ) );
		editCentroDescripcion.setWidth( "100%" );
		editCentroDescripcion.setNullRepresentation( "" );

		editCentroDireccion = new TextArea( getContext().getString( "modalNewCenter.lbCentroDireccion" ), bi.getItemProperty( "direccion" ) );
		editCentroDireccion.setWidth( "100%" );
		editCentroDireccion.setRows( 3 );
		editCentroDireccion.setNullRepresentation( "" );

		editCentroContactos = new TextArea( getContext().getString( "modalNewCenter.lbCentroContactos" ), bi.getItemProperty( "contactos" ) );
		editCentroContactos.setWidth( "100%" );
		editCentroContactos.setRows( 3 );
		editCentroContactos.setNullRepresentation( "" );

		editCentroCoordenadas = new TextField( getContext().getString( "modalNewCenter.lbCentroCoordenadas" ), bi.getItemProperty( "coordenadas" ) );
		editCentroCoordenadas.setWidth( "100%" );
		editCentroCoordenadas.setNullRepresentation( "" );

		comboBoxCentroImagen = new ComboBox(getContext().getString( "modalNewCenter.lbCentroImagen" ));
		comboBoxCentroImagen.setWidth( "100%" );
		comboBoxCentroImagen.setNullSelectionAllowed( true );
		comboBoxCentroImagen.setTextInputAllowed( false );
		comboBoxCentroImagen.setImmediate( true );
		comboBoxCentroImagen.setPropertyDataSource( bi.getItemProperty( "imagen" ) );

		editCentroOrden = new TextField( getContext().getString( "modalNewCenter.lbCentroOrden" ), bi.getItemProperty( "orden" ) );
		editCentroOrden.setWidth( "100%" );
		editCentroOrden.setNullRepresentation( "" );

		comboBoxCentroHorario = new ComboBox(getContext().getString( "modalNewCenter.lbCentroHorario" ));
		comboBoxCentroHorario.setWidth( "100%" );
		comboBoxCentroHorario.setTextInputAllowed( false );
		comboBoxCentroHorario.setNullSelectionAllowed( false );
		comboBoxCentroHorario.setPropertyDataSource( bi.getItemProperty( "horario" ) );

		comboBoxCentroMoneda = new ComboBox(getContext().getString( "modalNewCenter.lbCentroMoneda" ));
		comboBoxCentroMoneda.setWidth( "100%" );
		comboBoxCentroMoneda.setTextInputAllowed( false );
		comboBoxCentroMoneda.setNullSelectionAllowed( false );
		comboBoxCentroMoneda.setPropertyDataSource( bi.getItemProperty( "moneda" ) );

		editCentroSerie = new TextField( getContext().getString( "modalNewCenter.lbCentroSerie" ), bi.getItemProperty( "serie" ) );
		editCentroSerie.setWidth( "100%" );
		editCentroSerie.setNullRepresentation( "" );

		fillComboBoxes();

		FormLayout left = new FormLayout(editCentroSerie, editCentroNombre, comboBoxCentroImagen, comboBoxCentroHorario, editCentroDireccion);
		left.setMargin( false );
		
		FormLayout right = new FormLayout(editCentroOrden, editCentroDescripcion, editCentroCoordenadas, comboBoxCentroMoneda, editCentroContactos);
		right.setMargin( false );
		HorizontalLayout top = new HorizontalLayout( left, right );
		top.setWidth( "100%" );
		top.setSpacing( true );
		
		int index = 1;
		editCentroSerie.setTabIndex( index++ );
		editCentroOrden.setTabIndex( index++ );
		editCentroNombre.setTabIndex( index++ );
		editCentroDescripcion.setTabIndex( index++ );
		comboBoxCentroImagen.setTabIndex( index++ );
		editCentroCoordenadas.setTabIndex( index++ );
		comboBoxCentroHorario.setTabIndex( index++ );
		comboBoxCentroMoneda.setTabIndex( index++ );
		editCentroDireccion.setTabIndex( index++ );
		editCentroContactos.setTabIndex( index++ );

		componentsContainer.addComponent( top );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewCenter";
	}

	@Override
	protected void defaultFocus()
	{
		editCentroOrden.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			CentrosManager conexMan = (CentrosManager) IOCManager.getInstanceOf( CentrosManager.class );

			newCentro.setId( null );
			newCentro.setPuerto( 8080 );
			
			conexMan.setRow( context, null, newCentro );

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
			CentrosManager centroMan = (CentrosManager) IOCManager.getInstanceOf( CentrosManager.class );

			centroMan.setRow( context, (DetalleCentro) orgDto, newCentro );

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
			CentrosManager conexMan = (CentrosManager) IOCManager.getInstanceOf( CentrosManager.class );

			conexMan.delRow( context, newCentro );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
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
				if ( newCentro.getId() != null )
				{
					ImagenQuery imagenQuery = new ImagenQuery();
					imagenQuery.setCentro( newCentro.getId() );
					
					List<Imagen> imagenes = imgagenesManager.getRows( context, imagenQuery );

					for ( Imagen imagen : imagenes )
					{
						comboBoxCentroImagen.addItem( imagen.getId() );
						comboBoxCentroImagen.setItemCaption( imagen.getId(), imagen.getNombre() );
					}
				}
			}
			catch ( ImedigException e )
			{
			}

			try
			{
				HorariosManager horarioMan = (HorariosManager) IOCManager.getInstanceOf( HorariosManager.class );
				Horario horarioQuery = new Horario();
				List<Horario> listaHorarios = null;

				listaHorarios = horarioMan.getRows( context, horarioQuery );

				for ( Horario horarioItem : listaHorarios )
				{
					comboBoxCentroHorario.addItem( horarioItem.getId() );
					comboBoxCentroHorario.setItemCaption( horarioItem.getId(), horarioItem.getNombre() );
				}
			}
			catch ( ImedigException e )
			{
			}

			try
			{
				MonedasManager monedasMan = (MonedasManager) IOCManager.getInstanceOf( MonedasManager.class );
				Moneda monedaQuery = new Moneda();
				List<Moneda> listaMonedas = null;

				listaMonedas = monedasMan.getRows( context, monedaQuery );

				for ( Moneda monedaItem : listaMonedas )
				{
					comboBoxCentroMoneda.addItem( monedaItem.getId() );
					comboBoxCentroMoneda.setItemCaption( monedaItem.getId(), monedaItem.getNombre() );
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
