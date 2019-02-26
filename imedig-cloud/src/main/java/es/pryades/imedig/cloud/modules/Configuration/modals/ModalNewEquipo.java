package es.pryades.imedig.cloud.modules.Configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.ui.ComboBox;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Recurso;
import es.pryades.imedig.core.common.ModalParent;

/**
 * 
 * @author hector.licea
 * 
 */
public final class ModalNewEquipo extends ModalNewRecurso
{
	private static final long serialVersionUID = 6296467137689706645L;
	
	private static final Logger LOG = Logger.getLogger( ModalNewEquipo.class );

	public ModalNewEquipo( ImedigContext ctx, Operation modalOperation, Recurso recurso, ModalParent parentWindow, String right )
	{
		super( ctx, modalOperation, recurso, parentWindow, right );
	}

	@Override
	protected void fillModalidad( ComboBox comboBox )
	{
		String modList = context.getString( "QueryForm.Modalities" );

		String modalities[] = modList.split( "," );

		for ( String modality : modalities )
		{
			comboBox.addItem( modality );
			comboBox.setItemCaption( modality, modality +" - "+getContext().getString( "words.modality."+modality ) );
		}
	}

	@Override
	protected String getMainResourceKey()
	{
		return "modalNewEquipo";
	}

	@Override
	protected Integer getTipo()
	{
		return Constants.TYPE_IMAGING_DEVICE;
	}

	
}
