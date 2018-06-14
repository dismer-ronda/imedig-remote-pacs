var es_pryades_fabricjs_FabricJs = function() {

    var state = this.getState();
    var component = this;
    var container = this.getElement();


    var fabricJs = new FabricJs.FabricJsApp({
        configuration: JSON.parse(state.canvasConfiguration),
        container: container,
        action: state.action,
        component: component
    });

    fabricJs.setDimensions({width: container.offsetWidth, height: container.offsetHeight});

    this.addResizeListener(container, function() {
        fabricJs.setDimensions({width: container.offsetWidth, height: container.offsetHeight});
        component.setDimensions(container.offsetWidth, container.offsetHeight);
        if (component.imageUrl) {
            fabricJs.onLoadImage(component.imageUrl);
        }
        component.onResize(container.offsetWidth, container.offsetHeight);
    });

    /*window.addEventListener("resize", function() {
     fabricJs.setDimensions({width: container.offsetWidth, height: container.offsetHeight});
     component.setDimensions(container.offsetWidth, container.offsetHeight);
     component.onResize(container.offsetWidth, container.offsetHeight);
     });*/

    this.onStateChange = function() {
        component.setDimensions(container.offsetWidth, container.offsetHeight);

        var command = JSON.parse(state.command);
        switch (command.canvasAction) {
            case "SET_IMAGE":
                if (command.payload) {
                    component.imageUrl = command.payload;
                    fabricJs.setDimensions({width: container.offsetWidth, height: container.offsetHeight});
                    fabricJs.onLoadImage(command.payload);
                }
                break;
            case "ADD_NOTE":
                if (command.payload) {
                    var notes = JSON.parse(command.payload);
                    if (notes.length) {
                        fabricJs.addNotes(notes[notes.length - 1].text, notes[notes.length - 1].notesConfiguration);
                    }
                }
                break;
            case "DRAW_LINE":
                if (command.payload) {
                    var lines = JSON.parse(command.payload);
                    if (lines.length) {
                        var i = lines.length - 1;
                        fabricJs.drawLine(lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2, lines[i].text);
                    }
                }
                break;

            case "DRAW_FIGURE":
                if (command.payload) {
                    var figure = JSON.parse(command.payload);
                    fabricJs.draw(figure);
                }
                break;
            case "SET_ACTION":
                if (command.payload) {
                    fabricJs.setAction(command.payload);
                }
                break;

            case "SET_TEXT":
                if (command.payload) {
                    fabricJs.setText(command.payload);
                }
                break;

            case "SET_CURSOR":
                if (command.payload) {
                    fabricJs.setCursor("url('./images/anadir-puntero.png')");
                }
                break;

            case "CLEAR_ALL":
                fabricJs.clear();
                break;
            case "CLEAR_IMAGE":
                fabricJs.clearImage();
                break;
            case "CLEAR_NOTES":
                fabricJs.clearNotes();
                break;
            case "CLEAR_DRAW":
                fabricJs.clearDraw();
                break;
        }

    };
};
