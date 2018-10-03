package es.pryades.imedig.viewer.components.query;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class FontsDlg extends Window {

	private static final long serialVersionUID = -2546379251017421011L;
	
	private CssLayout content;

	public FontsDlg() {
		super("QueryForm.Title");

		setModal(true);
		setResizable(false);
		//setClosable(false);
		setWidth("900px");
		setHeight("480px");
		content = new CssLayout();
		content.setWidth("100%");

		setContent(content);

		buildFiltros();
		
	}

	private void buildFiltros() {
		for (FontAwesome font : FontAwesome.values()) {
			Button btnOpen = new Button();
			btnOpen.setDescription(font.toString());
			btnOpen.setIcon(font);
			btnOpen.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			btnOpen.addStyleName(ValoTheme.BUTTON_HUGE);
			content.addComponent(btnOpen);
		}
		
	}
}
