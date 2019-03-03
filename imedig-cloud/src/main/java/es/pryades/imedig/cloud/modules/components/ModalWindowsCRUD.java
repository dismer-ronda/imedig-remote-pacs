package es.pryades.imedig.cloud.modules.components;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.core.common.ModalParent;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public abstract class ModalWindowsCRUD extends Window
{
	private static final long serialVersionUID = -6161493988046489729L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalWindowsCRUD.class );

	@Getter protected ImedigContext context;

	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesContainer;
	protected HorizontalLayout operacionesLeft;
	protected HorizontalLayout operacionesRight;

	protected Button bttnOperacion;
	protected Button bttnCancelar;

	protected ModalParent modalParent;

	protected Operation operation;

	protected VerticalLayout layout;

	protected ImedigDto orgDto;
	protected BeanItem<ImedigDto> bi;

	@Getter protected String right;
	
	public static enum Operation
	{
		OP_VIEW("view"), OP_ADD("add"), OP_MODIFY("modify"), OP_DELETE("delete");

		Operation( String op )
		{
			opName = op;
		}

		private String opName = "";

		public String getOpName()
		{
			return opName;
		}
	}

	/**
     * 
     */
	public ModalWindowsCRUD( ImedigContext ctx, ModalParent modalParent, Operation modalOperation, ImedigDto orgDto, String right )
	{
		super();

		this.context = ctx;
		this.modalParent = modalParent;
		this.operation = modalOperation;
		this.orgDto = orgDto;
		this.right = right;

		addCloseShortcut( KeyCode.ESCAPE );
		center();

		setWidth( "1024px" );
		setHeight( "-1px" );

		layout = new VerticalLayout();
		
		layout.setSizeUndefined();
		layout.setWidth( "100%" );
		layout.setMargin( true );
		layout.setSpacing( true );

		setContent( layout );
		
		setModal( true );
		setResizable( false );
		setClosable( false );
		
		setCaption( getContext().getString( getWindowResourceKey() + ".wndCaption." + operation.getOpName() ) );
	}

	public String getOpName()
	{
		return operation.getOpName();
	}
	
	public void initComponents()
	{
		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );

		operacionesLeft = new HorizontalLayout();
		operacionesLeft.setSpacing( true );

		operacionesRight = new HorizontalLayout();
		operacionesRight.setSpacing( true );

		operacionesContainer = new HorizontalLayout(operacionesLeft, operacionesRight);
		operacionesContainer.setSpacing( true );
		operacionesContainer.setWidth( "100%" );
		operacionesContainer.setComponentAlignment( operacionesLeft, Alignment.MIDDLE_LEFT );
		operacionesContainer.setComponentAlignment( operacionesRight, Alignment.MIDDLE_RIGHT );

		String opCaption = operation.equals( Operation.OP_VIEW ) ? "close" : getOpName();
		
		bttnOperacion = new Button( getContext().getString( "words." + opCaption ) );
		
		if ( !operation.equals( Operation.OP_VIEW ) )
		{
			bttnCancelar = new Button( getContext().getString( "words.cancel" ) );

			//bttnOperacion.setClickShortcut( KeyCode.ENTER );
			bttnCancelar.focus();
		}
		else
			bttnOperacion.focus();

		operacionesRight.addComponent( bttnOperacion );
		if ( !operation.equals( Operation.OP_VIEW ) )
			operacionesRight.addComponent( bttnCancelar );
		operacionesRight.setComponentAlignment( bttnOperacion, Alignment.BOTTOM_RIGHT );
		if ( !operation.equals( Operation.OP_VIEW ) )
			operacionesRight.setComponentAlignment( bttnCancelar, Alignment.BOTTOM_RIGHT );

		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesContainer );
		//layout.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( operacionesContainer, 1.0f );
		
		switch ( operation )
		{
			case OP_VIEW:
				bttnViewListener();
				break;
			case OP_ADD:
				bttnAddListener();
				break;
			case OP_MODIFY:
				bttnModifyListener();
				break;
			case OP_DELETE:
				bttnDeleteListener();
				bttnCancelar.focus();
				break;
		}

		if ( !operation.equals( Operation.OP_VIEW ) )
			bttnCancelListener();
	}

	protected abstract boolean onAdd();

	protected abstract boolean onModify();

	protected abstract boolean onDelete();

	protected abstract void defaultFocus();

	protected String getWindowResourceKey()
	{
		return this.getClass().getSimpleName();
	}

	public void showModalWindow()
	{
		UI.getCurrent().addWindow( this );
	}

	public void closeModalWindow( boolean refresh )
	{
		UI.getCurrent().removeWindow( this );
		
		if ( modalParent != null && refresh )
			modalParent.refreshVisibleContent();
	}

	private void bttnCancelListener()
	{
		bttnCancelar.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7591024099361245222L;

			public void buttonClick( ClickEvent event )
			{
				closeModalWindow( false );
			}
		} );
	}

	private void bttnAddListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5858612612327120717L;

			public void buttonClick( ClickEvent event )
			{
				if ( !context.hasRight( right  ) )
					Notification.show( getContext().getString( "error.more.rights" ), Notification.Type.ERROR_MESSAGE );
				else if ( onAdd() )
					closeModalWindow( true );
			}
		} );
	}

	private void bttnModifyListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7587457903111881354L;

			public void buttonClick( ClickEvent event )
			{
				if ( !context.hasRight( right  ) )
					Notification.show( getContext().getString( "error.more.rights" ), Notification.Type.ERROR_MESSAGE );
				else if ( onModify() )
					closeModalWindow( true );
			}
		} );
	}

	private void bttnDeleteListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 581142912127960955L;

			public void buttonClick( ClickEvent event )
			{
				if ( !context.hasRight( right  ) )
					Notification.show( getContext().getString( "error.more.rights" ), Notification.Type.ERROR_MESSAGE );
				else
				{
					ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( getWindowResourceKey() + ".confirm.delete" ),
			        new ConfirmDialog.Listener() 
					{
						private static final long serialVersionUID = -3142429497962370163L;

						public void onClose(ConfirmDialog dialog) 
			            {
			                if ( dialog.isConfirmed() ) 
			                {
								if ( onDelete() )
								{
									closeModalWindow( true, true );
								}
			                } 
			            }
			        });
				}
			}
		} );
	}

	public void closeModalWindow( boolean refresh, boolean repage )
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );

		if ( modalParent != null && refresh )
			modalParent.refreshVisibleContent();
	}

	private void bttnViewListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7587457903111881354L;

			public void buttonClick( ClickEvent event )
			{
				closeModalWindow( false );
			}
		} );
	}

	protected String getString(String key){
		return getContext().getString( key );
	}
	
	public void showErrorMessage( Throwable e )
	{
		String msg = "";

		e.printStackTrace();
		
		if ( e instanceof ImedigException )
		{
			msg = getContext().getString( "exception.code." + ((ImedigException)e).getImedigError() );

			Notification.show( msg, Notification.Type.ERROR_MESSAGE );
		}
		else
			Notification.show( getContext().getString( "error.unknown" ), Notification.Type.ERROR_MESSAGE );
	}
}
