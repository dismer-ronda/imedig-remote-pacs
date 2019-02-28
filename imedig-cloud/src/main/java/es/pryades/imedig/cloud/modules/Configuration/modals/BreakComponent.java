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

import es.pryades.imedig.cloud.common.OverlappingTimeValidator;
import es.pryades.imedig.cloud.common.TimeAfterRangeValidator;
import es.pryades.imedig.cloud.common.TimeField;
import es.pryades.imedig.cloud.common.TimeLimitRangeValidator;
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
	
	private LocalTime startLimit;
	private LocalTime endLimit;
	
	public BreakComponent(ImedigContext ctx,  int day, LocalTime startLimit, LocalTime endLimit )
	{
		super();
		this.ctx = ctx;
		this.day = day;
		this.startLimit = startLimit;
		this.endLimit = endLimit;
		
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
	
	public void setStartLimit( LocalTime startLimit )
	{
		this.startLimit = startLimit;
		
		for ( Component component : breaksContent )
		{
			((BreakDetail)component).updateStartLimit();
		}
		
	}

	public void setEndLimit( LocalTime endLimit )
	{
		this.endLimit = endLimit;
		
		for ( Component component : breaksContent )
		{
			((BreakDetail)component).updateEndLimit();
		}
	}
	
	private void verificarSobreposicion(){
		List<TimeRange<LocalTime>> ranges = new ArrayList<>();
		
		for ( Component component : breaksContent )
		{
			((BreakDetail)component).validar( new ArrayList<>( ranges ) );
			ranges.add( ((BreakDetail)component).rangeTime());
		}
	}

	private class BreakDetail extends HorizontalLayout{

		private static final long serialVersionUID = 130076493031179709L;

		Button remove;
		TimeField start;
		TimeField end;
		TimeLimitRangeValidator limitRangeValidator; 
		OverlappingTimeValidator overlappingValidator;
		
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
			
			limitRangeValidator = new TimeLimitRangeValidator( startLimit, endLimit, ctx.getString( "modalNewTipoHorario.limit.error" ) ); 
			overlappingValidator = new OverlappingTimeValidator( new ArrayList<TimeRange<LocalTime>>(), ctx.getString( "modalNewTipoHorario.error.breaks.overlapping" ) );
			start = new TimeField();
			start.setCaption( ctx.getString( "words.start" ) );
			start.setValue( DEFAULT_START );
			start.setStartLimit( startLimit );
			start.setEndLimit( endLimit );
			start.addValidator( limitRangeValidator );
			start.addValidator( overlappingValidator );
			end = new TimeField();
			end.setValidationVisible( true );
			end.setCaption( ctx.getString( "words.end" ) );
			end.setValue( DEFAULT_END );
			end.setStartLimit( startLimit );
			end.setEndLimit( endLimit );
			Validator validatorEnd = new TimeAfterRangeValidator( start, ctx.getString( "words.timerange.error" ) );
			end.addValidator( validatorEnd );
			end.addValidator( limitRangeValidator );
			end.addValidator( overlappingValidator );
			
			final ValueChangeListener changeListener1 = new ValueChangeListener()
			{
				private static final long serialVersionUID = 46676985918961579L;

				@Override
				public void valueChange( ValueChangeEvent event )
				{
					verificarSobreposicion();
				}
			};
			
			final ValueChangeListener changeListener2 = new ValueChangeListener()
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
			
			start.addValueChangeListener( changeListener1 );
			start.addValueChangeListener( changeListener2 );
			end.addValueChangeListener( changeListener1 );
			end.addValueChangeListener( changeListener2 );
			
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
			verificarSobreposicion();
		}
		
		
		public TimeRange<String> range(){
			String start = this.start.getValue().toString( "HH:mm" ) ;
			String end = this.end.getValue().toString( "HH:mm" ) ;
			return  new TimeRange<>( start, end );
		}
		
		public TimeRange<LocalTime> rangeTime(){
			return  new TimeRange<>( this.start.getValue(), this.end.getValue() );
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
		
		public void validar(List<TimeRange<LocalTime>> ranges){
			overlappingValidator.setRanges( ranges );
			validateStartEnd();
		}
		
		public void updateStartLimit(){
			limitRangeValidator.setStartLimit( startLimit );
			start.setStartLimit( startLimit );
			end.setStartLimit( startLimit );
			
			validateStartEnd();
		}

		public void updateEndLimit(){
			limitRangeValidator.setEndLimit( endLimit );
			start.setEndLimit( endLimit );
			end.setEndLimit( endLimit );
			
			validateStartEnd();
		}

		private void validateStartEnd(){
			start.setValidationVisible(false);
			try {
				start.validate();
			} catch (InvalidValueException e) {
				start.setValidationVisible(true);
			}
			
			end.setValidationVisible(false);
			try {
				end.validate();
			} catch (InvalidValueException e) {
				end.setValidationVisible(true);
			}
			
		}
	}
}
