package es.pryades.imedig.viewer.components.citas;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.core.dal.EstudiosManager;
import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.ioc.IOCManager;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings({"unchecked"})
public class CitationsViewer extends VerticalLayout implements ModalParent, Property.ValueChangeListener
{

	private static final long serialVersionUID = 486852411457445182L;

	private static final Logger LOG = Logger.getLogger( CitationsViewer.class );

	@Setter @Getter private String nombre;
	@Setter @Getter private String identificador;
	@Setter @Getter private Integer fecha;
	
	private BeanItem<CitationsViewer> bi;
	
	private TextField textNombre;
	private TextField textIdentificador;
	private ComboBox comboFecha;

	private PacientesManager pacientesManager;
	private EstudiosManager estudiosManager;
	
	private static final String COMBO_WIDTH = "200px";
	private static final String TEXT_WIDTH = "200px";
	
	//private static final Integer PERFIL_IMAGENOLOGO = 3;
	//private static final Integer PERFIL_USUARIO = 2;
	
	//private boolean defaultSearch;
	//private boolean showMessage;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public CitationsViewer( ImedigContext ctx )
	{
		pacientesManager = (PacientesManager) IOCManager.getInstanceOf( PacientesManager.class );
		estudiosManager = (EstudiosManager) IOCManager.getInstanceOf( EstudiosManager.class );
		
		setSizeFull();
		setMargin( true );
		buildComponents();
	}
	
	private void buildComponents(){
		buildDeviceCitations();
	}

	private void buildDeviceCitations()
	{
		Calendar calendar = new Calendar();
		calendar.setSizeFull();
		
		addComponent( calendar );
		
	}

	@Override
	public void valueChange( ValueChangeEvent event )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshVisibleContent()
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
}
