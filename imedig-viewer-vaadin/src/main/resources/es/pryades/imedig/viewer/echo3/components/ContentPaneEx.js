// Ensure namespace is defined.
if (!Core.get(window, ["es", "pryades", "imedig", "viewer", "echo3", "components"])) {
    Core.set(window, ["es", "pryades", "imedig", "viewer", "echo3", "components"], { });
}


// Component
es.pryades.imedig.viewer.echo3.components.ContentPaneEx = Core.extend(Echo.ContentPane, {

    $load: function() {
        Echo.ComponentFactory.registerType("es.pryades.imedig.viewer.echo3.components.ContentPaneEx", this);
    },

    componentType: "es.pryades.imedig.viewer.echo3.components.ContentPaneEx",
    
    doAction: function() {
        this.fireEvent({type: "action", source: this, actionCommand: this.get("actionCommand")});
    }
});


Timer = Core.extend(Core.Web.Scheduler.Runnable, 
{
    _pane: null,

    $construct: function(pane) {
        this._pane = pane;
    },

    timeInterval: 100,

    run: function() 
    {
    	if ( this._pane._first ) 
    		this._pane._onResize();
    }
});


// Sync component
es.pryades.imedig.viewer.echo3.components.ContentPaneEx.Sync = Core.extend(Echo.Sync.ContentPane, { 

    $load: function() {
    	Echo.Render.registerPeer("es.pryades.imedig.viewer.echo3.components.ContentPaneEx", this);
    },
    
    _first: true,
    
    _onResize: function() 
    {
    	var b = this.getSize();
		
    	this.component.set( "width", b.width );
    	this.component.set( "height", b.height );

    	this.component.doAction();
    	
    	this._first = false;
    },
    
    renderDisplay: function() 
    {
    	Echo.Sync.ContentPane.prototype.renderDisplay.call(this);
    	
    	if ( this._first ) 
    		Core.Web.Scheduler.add( new Timer( this ) );
    	else 
    		this._onResize();
    }

});
