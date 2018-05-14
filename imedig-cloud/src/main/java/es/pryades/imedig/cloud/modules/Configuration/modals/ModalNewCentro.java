package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dal.CentrosManager;
import es.pryades.imedig.cloud.core.dal.HorariosManager;
import es.pryades.imedig.cloud.core.dal.ImagenesManager;
import es.pryades.imedig.cloud.core.dal.MonedasManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DetalleCentro;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Horario;
import es.pryades.imedig.cloud.dto.Imagen;
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

	private Label lbCentroNombre;
	private Label lbCentroDescripcion;
	private Label lbCentroDireccion;
	private Label lbCentroContactos;
	private Label lbCentroCoordenadas;
	private Label lbCentroImagen;
	private Label lbCentroOrden;
	private Label lbCentroHorario;
	private Label lbCentroMoneda;
	private Label lbCentroSerie;

	private TextField editCentroNombre;
	private TextField editCentroDescripcion;
	private TextField editCentroDireccion;
	private TextField editCentroContactos;
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

		lbCentroNombre = new Label( getContext().getString( "modalNewCenter.lbCentroNombre" ) );
		lbCentroNombre.setWidth( Constants.WIDTH_LABEL );

		lbCentroDescripcion = new Label( getContext().getString( "modalNewCenter.lbCentroDescripcion" ) );
		lbCentroDescripcion.setWidth( Constants.WIDTH_LABEL );

		lbCentroDireccion = new Label( getContext().getString( "modalNewCenter.lbCentroDireccion" ) );
		lbCentroDireccion.setWidth( Constants.WIDTH_LABEL );

		lbCentroContactos = new Label( getContext().getString( "modalNewCenter.lbCentroContactos" ) );
		lbCentroContactos.setWidth( Constants.WIDTH_LABEL );

		lbCentroCoordenadas = new Label( getContext().getString( "modalNewCenter.lbCentroCoordenadas" ) );
		lbCentroCoordenadas.setWidth( Constants.WIDTH_LABEL );

		lbCentroImagen = new Label( getContext().getString( "modalNewCenter.lbCentroImagen" ) );
		lbCentroImagen.setWidth( Constants.WIDTH_LABEL );

		lbCentroOrden = new Label( getContext().getString( "modalNewCenter.lbCentroOrden" ) );
		lbCentroOrden.setWidth( Constants.WIDTH_LABEL );

		lbCentroHorario = new Label( getContext().getString( "modalNewCenter.lbCentroHorario" ) );
		lbCentroHorario.setWidth( Constants.WIDTH_LABEL );

		lbCentroMoneda = new Label( getContext().getString( "modalNewCenter.lbCentroMoneda" ) );
		lbCentroMoneda.setWidth( Constants.WIDTH_LABEL );

		lbCentroSerie = new Label( getContext().getString( "modalNewCenter.lbCentroSerie" ) );
		lbCentroSerie.setWidth( Constants.WIDTH_LABEL );

		editCentroNombre = new TextField( bi.getItemProperty( "nombre" ) );
		editCentroNombre.setWidth( "100%" );
		editCentroNombre.setNullRepresentation( "" );

		editCentroDescripcion = new TextField( bi.getItemProperty( "descripcion" ) );
		editCentroDescripcion.setWidth( "100%" );
		editCentroDescripcion.setNullRepresentation( "" );

		editCentroDireccion = new TextField( bi.getItemProperty( "direccion" ) );
		editCentroDireccion.setWidth( "100%" );
		editCentroDireccion.setNullRepresentation( "" );

		editCentroContactos = new TextField( bi.getItemProperty( "contactos" ) );
		editCentroContactos.setWidth( "100%" );
		editCentroContactos.setNullRepresentation( "" );

		editCentroCoordenadas = new TextField( bi.getItemProperty( "coordenadas" ) );
		editCentroCoordenadas.setWidth( "100%" );
		editCentroCoordenadas.setNullRepresentation( "" );

		comboBoxCentroImagen = new ComboBox();
		comboBoxCentroImagen.setWidth( "100%" );
		comboBoxCentroImagen.setNullSelectionAllowed( true );
		comboBoxCentroImagen.setTextInputAllowed( false );
		comboBoxCentroImagen.setImmediate( true );
		comboBoxCentroImagen.setPropertyDataSource( bi.getItemProperty( "imagen" ) );

		editCentroOrden = new TextField( bi.getItemProperty( "orden" ) );
		editCentroOrden.setWidth( "100%" );
		editCentroOrden.setNullRepresentation( "" );

		comboBoxCentroHorario = new ComboBox();
		comboBoxCentroHorario.setWidth( "100%" );
		comboBoxCentroHorario.setTextInputAllowed( false );
		comboBoxCentroHorario.setNullSelectionAllowed( false );
		comboBoxCentroHorario.setPropertyDataSource( bi.getItemProperty( "horario" ) );

		comboBoxCentroMoneda = new ComboBox();
		comboBoxCentroMoneda.setWidth( "100%" );
		comboBoxCentroMoneda.setTextInputAllowed( false );
		comboBoxCentroMoneda.setNullSelectionAllowed( false );
		comboBoxCentroMoneda.setPropertyDataSource( bi.getItemProperty( "moneda" ) );

		editCentroSerie = new TextField( bi.getItemProperty( "serie" ) );
		editCentroSerie.setWidth( "100%" );
		editCentroSerie.setNullRepresentation( "" );

		fillComboBoxes();

		HorizontalLayout rowNombre = new HorizontalLayout();
		rowNombre.setWidth( "100%" );
		rowNombre.addComponent( lbCentroNombre );
		rowNombre.addComponent( editCentroNombre );
		rowNombre.setExpandRatio( editCentroNombre, 1.0f );

		HorizontalLayout rowDescripcion = new HorizontalLayout();
		rowDescripcion.setWidth( "100%" );
		rowDescripcion.addComponent( lbCentroDescripcion );
		rowDescripcion.addComponent( editCentroDescripcion );
		rowDescripcion.setExpandRatio( editCentroDescripcion, 1.0f );

		HorizontalLayout rowDireccion = new HorizontalLayout();
		rowDireccion.setWidth( "100%" );
		rowDireccion.addComponent( lbCentroDireccion );
		rowDireccion.addComponent( editCentroDireccion );
		rowDireccion.setExpandRatio( editCentroDireccion, 1.0f );

		HorizontalLayout rowContactos = new HorizontalLayout();
		rowContactos.setWidth( "100%" );
		rowContactos.addComponent( lbCentroContactos );
		rowContactos.addComponent( editCentroContactos );
		rowContactos.setExpandRatio( editCentroContactos, 1.0f );

		HorizontalLayout rowCoordenadas = new HorizontalLayout();
		rowCoordenadas.setWidth( "100%" );
		rowCoordenadas.addComponent( lbCentroCoordenadas );
		rowCoordenadas.addComponent( editCentroCoordenadas );
		rowCoordenadas.setExpandRatio( editCentroCoordenadas, 1.0f );

		HorizontalLayout rowImagen = new HorizontalLayout();
		rowImagen.setWidth( "100%" );
		rowImagen.addComponent( lbCentroImagen );
		rowImagen.addComponent( comboBoxCentroImagen );
		rowImagen.setExpandRatio( comboBoxCentroImagen, 1.0f );

		HorizontalLayout rowOrden = new HorizontalLayout();
		rowOrden.setWidth( "100%" );
		rowOrden.addComponent( lbCentroOrden );
		rowOrden.addComponent( editCentroOrden );
		rowOrden.setExpandRatio( editCentroOrden, 1.0f );

		HorizontalLayout rowHorarios = new HorizontalLayout();
		rowHorarios.setWidth( "100%" );
		rowHorarios.addComponent( lbCentroHorario );
		rowHorarios.addComponent( comboBoxCentroHorario );
		rowHorarios.setExpandRatio( comboBoxCentroHorario, 1.0f );

		HorizontalLayout rowMoneda = new HorizontalLayout();
		rowMoneda.setWidth( "100%" );
		rowMoneda.addComponent( lbCentroMoneda );
		rowMoneda.addComponent( comboBoxCentroMoneda );
		rowMoneda.setExpandRatio( comboBoxCentroMoneda, 1.0f );

		HorizontalLayout rowSerie = new HorizontalLayout();
		rowSerie.setWidth( "100%" );
		rowSerie.addComponent( lbCentroSerie );
		rowSerie.addComponent( editCentroSerie );
		rowSerie.setExpandRatio( editCentroSerie, 1.0f );


		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowSerie );
		row1.addComponent( rowOrden );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowNombre );
		row2.addComponent( rowDescripcion );
		
		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( rowImagen );
		row3.addComponent( rowCoordenadas );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( rowHorarios );
		row4.addComponent( rowMoneda );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( rowDireccion );
		componentsContainer.addComponent( rowContactos );
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
