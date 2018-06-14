package es.pryades.imedig.viewer.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import es.pryades.imedig.viewer.application.ViewerApplicationUI;
import lombok.Getter;
import lombok.Setter;

public class PageTable extends HorizontalLayout {

	private HorizontalLayout inside;
	private Label label;
	private Button first;
	private Button previous;
	private Button next;
	private Button last;
	
	public static final Integer DEFAULT_SIZE = 10;
	
	@Getter	@Setter private Integer page = 0;
	private Integer pages = 0;
	@Getter	@Setter private Integer size = DEFAULT_SIZE;
	@Getter	@Setter private Integer total = 0;
	
	@Setter private PaginatorListener listener;
	
	

	public PageTable() {
		super();

		setWidth("100%");
		inside = new HorizontalLayout();
		inside.setWidthUndefined();
		inside.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		addComponent(inside);
		setComponentAlignment(inside, Alignment.MIDDLE_CENTER);
		buildBotones();
		resetTotal(0);
	}
	
	public void resetTotal(Integer total){
		page = 0;
		this.total = total; 
		pages = getNumberOfPages();
		updateLabel();
		updateButtons();
	}

	private void buildBotones() {
		label = new Label();
		updateLabel();
		first = getButton("QueryForm.FirstPage", "go-first.png");
		first.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				page = 0;
				updateLabel();
				updateButtons();
				if (listener != null) listener.onFirst();
			}
		});
		previous = getButton("QueryForm.PreviousPage", "go-previous.png");
		previous.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				page--;
				updateLabel();
				updateButtons();
				if (listener != null) listener.onPrevious();
			}
		});
		next = getButton("QueryForm.NextPage", "go-next.png");
		next.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				page++;
				updateLabel();
				updateButtons();
				if (listener != null) listener.onNext();
			}
		});
		last = getButton("QueryForm.LastPage", "go-last.png");
		last.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				page = size - 1;
				updateLabel();
				updateButtons();
				if (listener != null) listener.onLast();
			}
		});

		inside.addComponents(first, previous, label, next, last);
	}

	private static Button getButton(String tooltip, String img) {

		Button btn = new Button(new ThemeResource("img/" + img));
		btn.setDescription(ViewerApplicationUI.getText(tooltip));
		btn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btn.addStyleName(ValoTheme.BUTTON_TINY);

		return btn;
	}
	
	private void updateLabel(){
		if (total == 0) {
			label.setValue("0/0");
			return;
		}
		
		label.setValue((page+1) + "/" + pages);
	}
	
	private int getNumberOfPages(){
		if ( total == 0 )
			return 0;
		else
			return (total - 1) / size + 1;
	}
	
	private void updateButtons(){
		if (total == 0 || pages == 1) {
			disabled(first, previous, next, last);
			return;
		}
		
		if (page == 0){
			disabled(first, previous);
			enabled(next, last);
			return;
		}
		
		if (page == (pages-1)){
			enabled(first, previous);
			disabled(next, last);
			return;
		}
		
		enabled(first, previous, next, last);
	}
	
	private void enabled(Component... components){
		for (Component component : components) {
			component.setEnabled(true);
		}
	}
	
	private void disabled(Component... components){
		for (Component component : components) {
			component.setEnabled(false);
		}
	}
	
	public interface PaginatorListener{
		void onFirst();
		
		void onPrevious();
		
		void onNext();
		
		void onLast();
	}

}
