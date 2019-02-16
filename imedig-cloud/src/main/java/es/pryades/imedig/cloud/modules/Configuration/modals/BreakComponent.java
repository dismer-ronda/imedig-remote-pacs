package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.UtilsUI;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.TimeRange;

public class BreakComponent extends HorizontalLayout
{
	private static final long serialVersionUID = 4942752169784003210L;
	
	private final int day;
	private final ImedigContext ctx;
	private VerticalLayout breaksContent;
	
	private static final String DEFAULT_START = "12:00";
	private static final String DEFAULT_END = "13:00";
	
	public BreakComponent(ImedigContext ctx,  int day )
	{
		super();
		this.ctx = ctx;
		this.day = day;
		
		setSpacing( true );
		buildComponents();
		setWidth( "250px" );
	}
	
	private void buildComponents()
	{
		GridLayout grid = new GridLayout( 2, 1 );
		grid.setWidth( "100%" );
		grid.setColumnExpandRatio( 0, 0.15f );
		grid.setColumnExpandRatio( 1, 0.85f );
		Button button = new Button( FontAwesome.PLUS);
		button.setDescription( ctx.getString( "words.break.add" ) );
		button.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		button.addStyleName( ValoTheme.BUTTON_TINY );
		button.addStyleName( ValoTheme.BUTTON_PRIMARY );
		button.addStyleName( "action" );
		grid.addComponent( button);
		grid.setComponentAlignment( button, Alignment.TOP_LEFT );
		button.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -417077206252877854L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				//FontsDlg dlg = new FontsDlg();
				//UI.getCurrent().addWindow( dlg );
				breaksContent.addComponent( new BreakDetail( ) );
			}
		} );
		
		breaksContent = new VerticalLayout();
		breaksContent.setWidth( "100%" );
		breaksContent.setSpacing( true );
		grid.addComponent( breaksContent );
		addComponent( grid );
	}
	
	public boolean isValidBreaksTime(){
		if (breaksContent.getComponentCount() == 0) return true;
		
		for ( Component component : breaksContent )
		{
			if (!((BreakDetail)component).isValid()) return false;
		}

		return true;
	}
	
	
	public List<TimeRange<String>> getBreaksTime(){
		if (breaksContent.getComponentCount() == 0) return new ArrayList<>();
		
		List<TimeRange<String>> result = new ArrayList<>();
		
		for ( Component component : breaksContent )
		{
			String start = ((BreakDetail)component).start.getValue() ;
			String end = ((BreakDetail)component).end.getValue() ;
			TimeRange<String> range = new TimeRange<>( start, end );
			
			if (!result.contains( range )) result.add( new TimeRange<String>( start, end ) );
		}
		
		return result;
	}
	
	public void setBreaks( List<TimeRange<String>> breaks )
	{
		if (breaks == null) return;
		
		for ( TimeRange<String> timeRange : breaks )
		{
			BreakDetail detail = new BreakDetail();
			detail.start.setValue( timeRange.getStart() );
			detail.end.setValue( timeRange.getEnd() );
			
			breaksContent.addComponent( detail );
		}
		
	}


	private class BreakDetail extends HorizontalLayout{

		private static final long serialVersionUID = 130076493031179709L;

		Button remove;
		TextField start;
		TextField end;
		Validator validator  = new RegexpValidator( UtilsUI.TIME_REGEX, null );
		
		public BreakDetail()
		{
			super();
			setSpacing( true );
			this.buildComponents();
		}
		
		public boolean isValid(){
			return start.isValid() && end.isValid();
		}

		private void buildComponents()
		{
			remove = new Button( FontAwesome.CLOSE );
			remove.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
			remove.addStyleName( ValoTheme.BUTTON_DANGER );
			remove.addStyleName( ValoTheme.BUTTON_TINY );
			remove.addStyleName( "action" );
			remove.setDescription( ctx.getString( "words.break.remove" ) );
			remove.addClickListener( new ClickListener()
			{
				private static final long serialVersionUID = 4406224508319507501L;

				@Override
				public void buttonClick( ClickEvent event )
				{
					removeThis();
				}
			} );
			
			start = UtilsUI.createTimeImput( null, null );
			//start.setCaption( ctx.getString( "words.start" ) );
			start.setWidth( "60px" );
			start.addStyleName( ValoTheme.TEXTFIELD_SMALL );
			start.setValue( DEFAULT_START );
			
			end = UtilsUI.createTimeImput( null, null );
			//end.setCaption( ctx.getString( "words.end" ) );
			end.setWidth( "60px" );
			end.addStyleName( ValoTheme.TEXTFIELD_SMALL );
			end.setValue( DEFAULT_END );
			
//			FormLayout s = new FormLayout( start );
//			s.setMargin( false );
//			FormLayout e = new FormLayout( end );
//			e.setMargin( false );
			addComponents( remove, start, end );
			setComponentAlignment( remove, Alignment.MIDDLE_CENTER );
			setComponentAlignment( start, Alignment.MIDDLE_CENTER );
			setComponentAlignment( end, Alignment.MIDDLE_CENTER );
		}

		private void removeThis()
		{
			breaksContent.removeComponent( this );
		}
		
	}
}
