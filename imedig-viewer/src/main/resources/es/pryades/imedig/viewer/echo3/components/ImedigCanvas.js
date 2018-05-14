
// Ensure namespace is defined.
if (!Core.get(window, ["es", "pryades", "imedig", "viewer", "echo3", "components"])) {
    Core.set(window, ["es", "pryades", "imedig",  "viewer", "echo3", "components"], { });
}

function leftClick( e ) 
{
	if ( parseInt( navigator.appVersion ) > 3 ) 
	{
		var clickType = 1;
	  
		if ( navigator.appName == "Netscape" || navigator.appName == "Opera" || navigator.appName == "Microsoft Internet Explorer" ) 
			clickType = e.which;
		else 
			clickType = e.button;
		
		if ( clickType == 1 ) 
			return true;
	 }
	
	 return false;
}

es.pryades.imedig.viewer.echo3.components.ImedigCanvas = Core.extend(Echo.Component, {

    $load: function() {
        Echo.ComponentFactory.registerType("es.pryades.imedig.viewer.echo3.components.ImedigCanvas", this);
    },

    componentType: "es.pryades.imedig.viewer.echo3.components.ImedigCanvas",
    
    doAction: function() {
        this.fireEvent({type: "action", source: this, actionCommand: this.get("actionCommand")});
    }
    
});

es.pryades.imedig.viewer.echo3.components.ImedigCanvas.Sync = Core.extend(Echo.Render.ComponentSync, { 

    $load: function() {
        Echo.Render.registerPeer("es.pryades.imedig.viewer.echo3.components.ImedigCanvas", this);
    },

    _div: null,
    _canvas_image: null,
    _canvas_notes: null,
    _canvas_overlays: null,
    _canvas_temp: null,
    _img: null,
    _cursor: null,
    _width: 0,
    _height: 0,
    _dx: null,
    _dy: null,
    _x1: null,
    _y1: null,
    _x2: null,
    _y2: null,
    _update: null,
    _parentElement: null,
    
    renderAdd: function(update, parentElement) 
    {
        this._width = this.component.render( "width" );
        this._height = this.component.render( "height" );

        // imagen
        this._canvas_image = document.createElement("canvas");
        this._canvas_image.width = 0;
        this._canvas_image.height = 0;
        this._canvas_image.style.position = "absolute";
        this._canvas_image.style.left = 0;
        this._canvas_image.style.top = 0;
        this._canvas_image.style.zindex = 0;
        this._canvas_image.style.cursor = "none";        
        
        // anotaciones
        this._canvas_notes = document.createElement("canvas");
        this._canvas_notes.width = 0;
        this._canvas_notes.height = 0;
        this._canvas_notes.style.position = "absolute";
        this._canvas_notes.style.left = 0;
        this._canvas_notes.style.top = 0;
        this._canvas_notes.style.zindex = 1;
        this._canvas_notes.style.cursor = "none";        
        this._canvas_notes.onselectstart = function () { return false; };

        // overlays 
        this._canvas_overlays = document.createElement("canvas");
        this._canvas_overlays.width = this._width;
        this._canvas_overlays.height = this._height;
        this._canvas_overlays.style.position = "absolute";
        this._canvas_overlays.style.left = 0;
        this._canvas_overlays.style.top = 0;
        this._canvas_overlays.style.zindex = 2;
        this._canvas_overlays.style.cursor = "default";        
        this._canvas_overlays.onselectstart = function () { return false; };

        // temp 
        this._canvas_temp = document.createElement("canvas");
        this._canvas_temp.width = this._width;
        this._canvas_temp.height = this._height;
        this._canvas_temp.style.position = "absolute";
        this._canvas_temp.style.left = 0;
        this._canvas_temp.style.top = 0;
        this._canvas_temp.style.zindex = 3;
        this._canvas_temp.style.cursor = "default";        
        this._canvas_temp.onselectstart = function () { return false; };

        // div
        this._div = document.createElement("div");
        this._div.id = this.component.renderId;
        
        Echo.Sync.renderComponentDefaults(this.component, this._div);
        
        this._div.style.position = "relative";
        this._div.style.width = this._width+"px";
        this._div.style.height = this._height+"px";
        this._div.style.cursor = "none";
        
        this._div.appendChild(this._canvas_image);
        this._div.appendChild(this._canvas_overlays);
        this._div.appendChild(this._canvas_notes);
        this._div.appendChild(this._canvas_temp);
        
        parentElement.appendChild(this._div);
        
        // imagen
        var image = this.component.render("image");
        
        if (image) 
        {
            this._img = document.createElement("img");
            Core.Web.Event.add(this._img, "load", Core.method(this, this._onImgLoad), false);
            Echo.Sync.ImageReference.renderImg(image, this._img);
        }

        var cursor = this.component.render("cursor");
        if (cursor) 
            this._canvas_temp.style.cursor = cursor;        
        
        Core.Web.Event.add(this._canvas_temp, "mouseout", Core.method(this, this._onMouseOut), false);
        
        Core.Web.Event.add(this._canvas_temp, "mousedown", Core.method(this, this._onMouseDown), false);
        Core.Web.Event.add(this._canvas_temp, "mousemove", Core.method(this, this._onMouseMove), false);
        Core.Web.Event.add(this._canvas_temp, "mouseup", Core.method(this, this._onMouseUp), false);

 		Core.Web.Event.add( this._canvas_temp, "mousewheel", Core.method(this, this._onWheel), false );
		Core.Web.Event.add( this._canvas_temp, "DOMMouseScroll", Core.method(this, this._onWheel), false );
        
        this._ShowActive();
    },

    _onImgLoad: function (e) 
    {
    	if ( this._img && this._img.width && this._img.height ) 
    	{
            Core.Web.Event.removeAll( this._img );

            this._dx = (this._width  - this._img.width ) / 2;
            this._dy = (this._height - this._img.height) / 2;

        	Core.Debug.consoleWrite( " on load " + this._img.width + " " + this._img.height ); //.width + " " + this._img.height );

        	this._canvas_image.width = this._img.width;
            this._canvas_image.height = this._img.height;
            this._canvas_image.style.left = this._dx + 'px';
            this._canvas_image.style.top = this._dy + 'px';

	        this._canvas_notes.width = this._img.width;
            this._canvas_notes.height = this._img.height;
            this._canvas_notes.style.left = this._dx + 'px';
            this._canvas_notes.style.top = this._dy + 'px';

            this._canvas_temp.width = this._img.width;
            this._canvas_temp.height = this._img.height;
            this._canvas_temp.style.left = this._dx + 'px';
            this._canvas_temp.style.top = this._dy + 'px';

            var ctx = this._canvas_image.getContext( "2d" );
            
        	this._clearContext( ctx );

            ctx.drawImage( this._img, 0, 0 ); 
    		
    		this._img = null;

            this._ShowAnnotations();
            this._ShowOverlays();
            
            var cursor = this.component.render("cursor");

            if ( cursor ) 
                this._canvas_temp.style.cursor = cursor;       
    	}
    },
    
    _onWheel: function (e) 
    {
    	if ( this._img == null )
    	{
	    	var delta = 0;
	    	
	    	if ( e.wheelDelta )
	    	{ 
	    		if ( e.wheelDelta < 0 )
	    			delta = 1;
	    		else
	    			delta = -1;
	    	}
	    	else if ( e.detail < 0 )
				delta = -1;
	    	else
				delta = 1;
	    	
			this.component.set( "wheel", delta );
			this.component.set( "actionEvent", 2 );
	
	    	this.component.doAction();
	    	
	    	if ( e.preventDefault )
	        	e.preventDefault();
	    	
			e.returnValue = false;
    	}
    },

    _onMouseOut: function (e) 
    {
    },

    _onMouseDown: function (e) 
    {
    	if ( this._x1 == null && leftClick( e ) ) 
    	{
        	var b = Core.Web.DOM.getEventOffset(e);

        	var offsetX = this.component.get( "cursorOffsetX" );
        	var offsetY = this.component.get( "cursorOffsetY" );
        	
            this._x1 = b.x + offsetX; 
        	this._y1 = b.y + offsetY;
        	this._x2 = b.x + offsetX; 
        	this._y2 = b.y + offsetY;
        	
        	//Core.Debug.consoleWrite( " mouse down " + this._x1 + " " + this._y1 );
    	}
    },
    
    _clearContext: function ( ctx )
    {
    	ctx.clearRect( 0, 0, this._width, this._height );
    },

    _onMouseMove: function (e) 
    {
    	var b = Core.Web.DOM.getEventOffset(e);

		if ( this._x1 != null ) 
    	{
        	var offsetX = this.component.get( "cursorOffsetX" );
        	var offsetY = this.component.get( "cursorOffsetY" );
        	
        	this._x2 = b.x + offsetX; 
        	this._y2 = b.y + offsetY;
        	
        	//Core.Debug.consoleWrite( " mouse move " + this._x2 + " " + this._y2 );

        	if ( this.component.get( "showLine" ) == true )
    			this._Draw();
    	}
    },
    
    _onMouseUp: function (e) 
    {
    	var mode = this.component.get( "selectionMode" );
    	
		if ( this._x1 != null && mode != "none" ) 
    	{
	    	var b = Core.Web.DOM.getEventOffset(e);
	
	    	this._clearContext( this._canvas_temp.getContext( "2d" ) );
	
			this.component.set( "roiX1", parseInt( this._x1 ) );
			this.component.set( "roiY1", parseInt( this._y1 ) );
			this.component.set( "roiX2", parseInt( this._x2 ) );
			this.component.set( "roiY2", parseInt( this._y2 ) );
			
			this.component.set( "actionEvent", 1 );
			
	    	this.component.doAction();
    	}

		this._x2 = null;
    	this._y2 = null;
    	this._x1 = null;
    	this._y1 = null;
    },
    
    _Draw: function () 
    {
        var ctx = this._canvas_temp.getContext( "2d" );

        ctx.strokeStyle = this._div.style.color;
        ctx.lineWidth = 1;

        this._clearContext( ctx );

        if ( this.component.get( "selectionMode" ) == "line" )
        {
        	ctx.beginPath(); 
        	
        	ctx.moveTo( this._x1 + 0.5, this._y1 + 0.5 ); 
        	ctx.lineTo( this._x2 + 0.5, this._y2 + 0.5 );
        	
        	ctx.stroke();
        } 
        else if ( this.component.get( "selectionMode" ) == "rect" )
        {
        	ctx.strokeRect( this._x1 + 0.5, this._y1 + 0.5, this._x2 - this._x1, this._y2 - this._y1 );
        }
    },
        
    _ShowDistanceAnnotation: function( x1, y1, x2, y2, text )
    {
        var ctx = this._canvas_notes.getContext( "2d" );

        ctx.strokeStyle = this._div.style.color;
        ctx.lineWidth = 1;
		ctx.fillStyle = this._div.style.color;
        ctx.font = '16px sans-serif';
        ctx.textBaseline = 'top';

    	ctx.beginPath(); 
    	
    	ctx.moveTo( x1, y1 ); 
    	ctx.lineTo( x2, y2 );
    	
    	ctx.stroke();

    	ctx.fillText( text, (x1 + x2)/2, (y1 + y2)/2 + 5 );
    },
    
    _ShowAnnotations: function () 
    {
        var ctx = this._canvas_notes.getContext( "2d" );
        
        this._clearContext( ctx );
        
        var annotations = this.component.get( "annotations" );
        
        if ( annotations )
        {
			var annotations_array = annotations.split( "|" );
	    	
	    	for ( var i = 0; i < annotations_array.length; i++ )
	    	{
	    		if ( annotations_array[i] )
	    		{
	    			var annotation = annotations_array[i].split( "," );
	    			
	    			var type = annotation[0];
	    			var x1 = parseInt( annotation[1] );
	    			var y1 = parseInt( annotation[2] );
	    			var x2 = parseInt( annotation[3] );
	    			var y2 = parseInt( annotation[4] );
	    			var text = annotation[5];
	    			
	    			if ( type == "longitud" )
	    				this._ShowDistanceAnnotation( x1, y1, x2, y2, text );
	    		}
	    	}
    	}
    },

    _ShowOverlay: function( color, size, font, x1, y1, text )
    {
        var ctx = this._canvas_overlays.getContext( "2d" );

        ctx.strokeStyle = this._div.style.color;
        ctx.lineWidth = 1;
		ctx.fillStyle = color; 
        ctx.font = size + 'px ' + font;
        ctx.textBaseline = 'top';

    	ctx.fillText( text, x1, y1 );
    },
    
    _ShowOverlays: function () 
    {
        var ctx = this._canvas_overlays.getContext( "2d" );
        
        this._clearContext( ctx );
        
        var overlays = this.component.get( "overlays" );

        if ( overlays )
        {
			var overlays_array = overlays.split( "|" );
	    	
	    	for ( var i = 0; i < overlays_array.length; i++ )
	    	{
	    		if ( overlays_array[i] )
	    		{
	    			var overlay = overlays_array[i].split( "," );
    			
	    			var color = overlay[0];
	    			var size = overlay[1];
	    			var font = overlay[2];
	    			var x = parseInt( overlay[3] );
	    			var y = parseInt( overlay[4] );
	    			var text = overlay[5];
	    			
	    			this._ShowOverlay( color, size, font, x, y, text );
	    		}
	    	}
    	}

       this._ShowActive();
    },

    renderDispose: function(update) 
    {
        this._img = null;
    	this._canvas_image = null;
    	this._canvas_notes = null;
    	this._canvas_temp = null;
    	this._canvas_overlays = null;
        this._div = null;
    },

    _ShowActive: function () 
    {
       /* var ctx = this._canvas_overlays.getContext( "2d" );

        //ctx.globalCompositeOperation = 'source-atop';

        if ( this.component.get( "active" ) == true )
			ctx.strokeStyle = this.component.get( "activeColor" );
		else
			ctx.strokeStyle = '#000000';
		
        ctx.lineWidth = 2;

        ctx.strokeRect( 1, 1, this._width - 2, this._height - 2 );*/
    },

    renderUpdate: function(update) 
    {
        var w = this.component.render( "width" );   
        var h = this.component.render( "height" );  
        
        var cursor = this.component.render("cursor");

        if (cursor) 
            this._canvas_temp.style.cursor = cursor;       
        
		if ( w && h && this._width != w && this._height != h )
        {
        	this._width = w;
        	this._height = h;
        
	        this._canvas_image.width = this._width;
	        this._canvas_image.height = this._height;
	        
	        this._canvas_notes.width = this._width;
	        this._canvas_notes.height = this._height;

	        this._canvas_overlays.width = this._width;
	        this._canvas_overlays.height = this._height;

	        this._canvas_temp.width = this._width;
	        this._canvas_temp.height = this._height;
        }

		var refresh = this.component.render( "refresh" );
		
		if ( refresh )
		{
	        var ctx = this._canvas_overlays.getContext( "2d" );

	        ctx.strokeStyle = this._div.style.color;
	        ctx.lineWidth = 1;
			ctx.fillStyle = this._div.style.color;
	        ctx.font = '16px sans-serif';
	        ctx.textBaseline = 'top';

	    	ctx.fillText( "Working ... please wait", 4, h-18 );
	    	
	        var image = this.component.render( "image" );
	        
	        if ( image ) 
	        {
	            this._img = document.createElement( "img" );
	            
	            Core.Web.Event.add( this._img, "load", Core.method( this, this._onImgLoad ), false );
	
	            Echo.Sync.ImageReference.renderImg( image, this._img );
	
	            this._canvas_temp.style.cursor = "progress";        
	        }
	        else
	        {
	            this._clearContext( this._canvas_notes.getContext( "2d" ) );
	            this._clearContext( this._canvas_overlays.getContext( "2d" ) );
	            this._clearContext( this._canvas_temp.getContext( "2d" ) );
	            this._clearContext( this._canvas_image.getContext( "2d" ) );
	        }
			
	        this._ShowActive();
		}
		else
		{
            this._ShowAnnotations();
            this._ShowOverlays();
		}
        
    	return true;
    }
    
});
