package es.pryades.imedig.cloud.common;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import es.pryades.imedig.cloud.core.dto.ImedigContext;

public class FooterComponent extends HorizontalLayout
{
	private static final long serialVersionUID = 4609154621782766530L;
	
	private static final String  URL = "http://www.pryades.com";
	
	private ImedigContext ctx;
	
	public FooterComponent(ImedigContext ctx){
		this.ctx 		= ctx;
		
		setWidth( "100%" );
		setStyleName( "footer" );
		setMargin( new MarginInfo( false, true, true, true ) );
		
		Component component = buildLeft();
		addComponent( component );
		//setComponentAlignment( component, Alignment.MIDDLE_LEFT );
		
		component = buildRight();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_RIGHT );
		
		setExpandRatio(component, 1.0f);
	}
	
	private Component buildLeft(){
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth( "-1px");
		Link link = new Link();
        link.setResource(new ExternalResource(URL));
        link.setDescription(URL);
        link.setIcon(new ThemeResource( "images/lomisa.png" ));
        addComponent(link);

        return layout;
	}

	private Component buildCenter(){
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		
		return layout;
	}

	private Component buildRight(){
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );
		layout.setMargin( false );

		Label label = new Label( ctx.getString( "words.copyright" ) );
		layout.addComponent( label );

		Link link = new Link(ctx.getString( "words.company" ),new ExternalResource(URL));
        link.setDescription(URL);
        layout.addComponent(link);

		label = new Label( ctx.getString( "words.all.rights" ) );
		layout.addComponent( label );
		
		return layout;
	}

}
