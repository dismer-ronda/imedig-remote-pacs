package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.LocalTime;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.cloud.common.TimeAfterRangeValidator;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.TimeRange;

public class BreakComponent extends HorizontalLayout
{
	private static final long serialVersionUID = 4942752169784003210L;
	
	private final int day;
	private final ImedigContext ctx;
	private VerticalLayout breaksContent;
	
	private static final LocalTime DEFAULT_START = LocalTime.parse( "12:00" );
	private static final LocalTime DEFAULT_END = LocalTime.parse( "13:00" );
	
	public BreakComponent(ImedigContext ctx,  int day )
	{
		super();
		this.ctx = ctx;
		this.day = day;
		
		setSpacing( true );
		setMargin( false );
		buildComponents();
		setWidth( "415px" );
	}
	
	private void buildComponents()
	{
		GridLayout grid = new GridLayout( 2, 1 );
		//grid.setSpacing( true );
		grid.setMargin( false );
		grid.setWidth( "100%" );
		grid.setColumnExpandRatio( 0, 1 );
		grid.setColumnExpandRatio( 1, 10 );
		Button button = new Button( FontAwesome.PLUS);
		button.setDescription( ctx.getString( "words.break.add" ) );
		button.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
		button.addStyleName( ValoTheme.BUTTON_TINY );
		button.addStyleName( ValoTheme.BUTTON_PRIMARY );
		button.addStyleName( "action" );
		grid.addComponent( button);
		//grid.setComponentAlignment( button, Alignment.TOP_LEFT );
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
	
	public boolean isValidBreaksRanges(){
		
		List<TimeRange<String>> ranges = new ArrayList<>();
		
		for ( Component component : breaksContent )
		{
			if (!((BreakDetail)component).isValidRange( ranges )) return false;
			ranges.add( ((BreakDetail)component).range() );
		}

		return true;
	}
	
	
	public List<TimeRange<String>> getBreaksTime(){
		if (breaksContent.getComponentCount() == 0) return new ArrayList<>();
		
		List<TimeRange<String>> result = new ArrayList<>();
		
		for ( Component component : breaksContent )
		{
			TimeRange<String> range = ((BreakDetail)component).range();
			
			if (!result.contains( range )) result.add( range );
		}
		
		Comparator<TimeRange<String>> comparator = new Comparator<TimeRange<String>>()
		{

			@Override
			public int compare( TimeRange<String> o1, TimeRange<String> o2 )
			{
				LocalTime start1 = LocalTime.parse( o1.getStart() );
				LocalTime start2 = LocalTime.parse( o2.getStart() );
				return start1.compareTo( start2 );
			}
		};
		
		Collections.sort( result, comparator );
		
		return result;
	}
	
	public void setBreaks( List<TimeRange<String>> breaks )
	{
		if (breaks == null) return;
		
		for ( TimeRange<String> timeRange : breaks )
		{
			BreakDetail detail = new BreakDetail();
			//TODO tocado
			detail.start.setValue( LocalTime.parse( timeRange.getStart()) );
			detail.end.setValue( LocalTime.parse( timeRange.getEnd() ) );
			
			breaksContent.addComponent( detail );
		}
		
	}

	private class BreakDetail extends HorizontalLayout{

		private static final long serialVersionUID = 130076493031179709L;

		Button remove;
		TimeField start;
		TimeField end;
		
		public BreakDetail()
		{
			super();
			setSpacing( true );
			setMargin( false );
			this.buildComponents();
			
			start.focus();
		}
		
		public boolean isValid(){
			try
			{
				start.validate();
				end.validate();
				return true;
			}
			catch ( InvalidValueException e )
			{
				return false;
			}
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
			
			start = new TimeField();
			start.setCaption( ctx.getString( "words.start" ) );
			start.setValue( DEFAULT_START );
			
			end = new TimeField();
			end.setValidationVisible( true );
			end.setCaption( ctx.getString( "words.end" ) );
			end.setValue( DEFAULT_END );
			Validator validatorEnd = new TimeAfterRangeValidator( start, ctx.getString( "words.daterange.error" ) );
			end.addValidator( validatorEnd );
			final ValueChangeListener changeListener = new ValueChangeListener()
			{
				private static final long serialVersionUID = 46676985918961579L;

				@Override
				public void valueChange( ValueChangeEvent event )
				{
					end.setValidationVisible(false);
					try {
						end.validate();
					} catch (InvalidValueException e) {
						end.setValidationVisible(true);
					}
				}
			};
			
			start.addValueChangeListener( changeListener );
			end.addValueChangeListener( changeListener );
			
			FormLayout s = new FormLayout( start );
			s.setMargin( false );
			FormLayout e = new FormLayout( end );
			e.setMargin( false );
			addComponents( remove, s, e );
			setComponentAlignment( remove, Alignment.MIDDLE_CENTER );
			setComponentAlignment( s, Alignment.MIDDLE_CENTER );
			setComponentAlignment( e, Alignment.MIDDLE_CENTER );
		}

		private void removeThis()
		{
			breaksContent.removeComponent( this );
		}
		
		
		public TimeRange<String> range(){
			String start = this.start.getValue().toString( "HH:mm" ) ;
			String end = this.end.getValue().toString( "HH:mm" ) ;
			return  new TimeRange<>( start, end );
		}
		
		/**
		 * Validar si no se solapa el valor de los rangos de horas con otros rangos
		 * @param ranges
		 * @return false en caso que se solape con alguno
		 */
		public boolean isValidRange(List<TimeRange<String>> ranges){
			if (!isValid()) return false;
			for ( TimeRange<String> timeRange : ranges )
			{
				if (isInside( timeRange )) return false;
			}
			
			return true;
		}
		
		private boolean isInside(TimeRange<String> range){
			LocalTime rangeStart = LocalTime.parse( range.getStart() );
			LocalTime rangeEnd = LocalTime.parse( range.getEnd() );
			
			LocalTime localStart = start.getValue();
			LocalTime localEnd = end.getValue();
			
			//Para conocer si está dentro de un intervalo
			return localStart.equals( rangeStart ) //Si el inicio local es el mismo que el inicio del rango 
					|| localEnd.equals( rangeEnd ) //Si el final local es el mismo que el final del rango
					|| (localStart.isBefore( rangeStart ) && localEnd.isAfter( rangeStart ))//Si el inicio del rango está entre el inicio y final local
					|| (localStart.isBefore( rangeEnd ) && localEnd.isAfter( rangeEnd ));//Si el final del rango está entre el inicio y final local
			
		}
	}

//	private class BreakDetail extends HorizontalLayout{
//
//		private static final long serialVersionUID = 130076493031179709L;
//
//		Button remove;
//		TextField start;
//		TextField end;
//		Validator validator  = new TimeValidador();
//		
//		public BreakDetail()
//		{
//			super();
//			setSpacing( true );
//			this.buildComponents();
//			
//			start.focus();
//		}
//		
//		public boolean isValid(){
//			return start.isValid() && end.isValid() && Utils.isValidTimeRange( start.getValue(), end.getValue() );
//		}
//
//		private void buildComponents()
//		{
//			remove = new Button( FontAwesome.CLOSE );
//			remove.addStyleName( ValoTheme.BUTTON_ICON_ONLY );
//			remove.addStyleName( ValoTheme.BUTTON_DANGER );
//			remove.addStyleName( ValoTheme.BUTTON_TINY );
//			remove.addStyleName( "action" );
//			remove.setDescription( ctx.getString( "words.break.remove" ) );
//			remove.addClickListener( new ClickListener()
//			{
//				private static final long serialVersionUID = 4406224508319507501L;
//
//				@Override
//				public void buttonClick( ClickEvent event )
//				{
//					removeThis();
//				}
//			} );
//			
//			start = UtilsUI.createTimeImput( null, null );
//			start.addValidator( validator );
//			start.setCaption( ctx.getString( "words.start" ) );
//			start.setWidth( "60px" );
//			start.addStyleName( ValoTheme.TEXTFIELD_SMALL );
//			start.setValue( DEFAULT_START );
//			
//			end = UtilsUI.createTimeImput( null, null );
//			end.addValidator( validator );
//			end.setCaption( ctx.getString( "words.end" ) );
//			end.setWidth( "60px" );
//			end.addStyleName( ValoTheme.TEXTFIELD_SMALL );
//			end.setValue( DEFAULT_END );
//			
//			FormLayout s = new FormLayout( start );
//			s.setMargin( false );
//			FormLayout e = new FormLayout( end );
//			e.setMargin( false );
//			addComponents( remove, s, e );
//			setComponentAlignment( remove, Alignment.MIDDLE_CENTER );
//			setComponentAlignment( s, Alignment.MIDDLE_CENTER );
//			setComponentAlignment( e, Alignment.MIDDLE_CENTER );
//		}
//
//		private void removeThis()
//		{
//			breaksContent.removeComponent( this );
//		}
//		
//		
//		public TimeRange<String> range(){
//			String start = this.start.getValue() ;
//			String end = this.end.getValue() ;
//			return  new TimeRange<>( start, end );
//		}
//		
//		/**
//		 * Validar si no se solapa el valor de los rangos de horas con otros rangos
//		 * @param ranges
//		 * @return false en caso que se solape con alguno
//		 */
//		public boolean isValidRange(List<TimeRange<String>> ranges){
//			if (!isValid()) return false;
//			for ( TimeRange<String> timeRange : ranges )
//			{
//				if (isInside( timeRange )) return false;
//			}
//			
//			return true;
//		}
//		
//		private boolean isInside(TimeRange<String> range){
//			LocalTime rangeStart = LocalTime.parse( range.getStart() );
//			LocalTime rangeEnd = LocalTime.parse( range.getEnd() );
//			
//			LocalTime localStart = LocalTime.parse( start.getValue() );
//			LocalTime localEnd = LocalTime.parse( end.getValue() );
//			
//			//Para conocer si está dentro de un intervalo
//			return localStart.equals( rangeStart ) //Si el inicio local es el mismo que el inicio del rango 
//					|| localEnd.equals( rangeEnd ) //Si el final local es el mismo que el final del rango
//					|| (localStart.isBefore( rangeStart ) && localEnd.isAfter( rangeStart ))//Si el inicio del rango está entre el inicio y final local
//					|| (localStart.isBefore( rangeEnd ) && localEnd.isAfter( rangeEnd ));//Si el final del rango está entre el inicio y final local
//			
//		}
//	}
}
