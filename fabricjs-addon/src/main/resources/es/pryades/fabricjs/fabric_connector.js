var es_pryades_fabricjs_FabricJs = function() {

    var state = this.getState();
    var component = this;
    var container = this.getElement();


    var fabricJs = new FabricJs.FabricJsApp({
        configuration: JSON.parse(state.figureConfiguration),
        loaderConfiguration: JSON.parse(state.loaderConfiguration),
        rulerConfiguration: JSON.parse(state.rulerConfiguration),
        showSpinnerOnImageLoad: state.showSpinnerOnImageLoad,
        container: container,
        action: state.action,
        component: component,
        drawMode: state.drawMode
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


    component.imagesUrls = JSON.parse(state.imagesUrl);
    fabricJs.onLoadImage(component.imagesUrls);


    this.onStateChange = function() {
        component.setDimensions(container.offsetWidth, container.offsetHeight);
        if (component.imagesUrls !== state.imagesUrl) {
            component.imagesUrls = state.imagesUrl;
            fabricJs.setDimensions({width: container.offsetWidth, height: container.offsetHeight});
            fabricJs.onLoadImage(JSON.parse(component.imagesUrls));
        }

        if (component.figureConfiguration !== state.figureConfiguration) {
            component.figureConfiguration = state.figureConfiguration;
            fabricJs.setConfiguration(JSON.parse(state.figureConfiguration));
        }

        if (component.loaderConfiguration !== state.loaderConfiguration) {
            component.loaderConfiguration = state.loaderConfiguration;
            fabricJs.setLoaderConfiguration(JSON.parse(state.loaderConfiguration));
        }

        if (component.rulerConfiguration !== state.rulerConfiguration) {
            component.rulerConfiguration = state.rulerConfiguration;
            fabricJs.setRulerConfiguration(JSON.parse(state.rulerConfiguration));
        }

        if (component.showSpinnerOnImageLoad !== state.showSpinnerOnImageLoad) {
            component.showSpinnerOnImageLoad = state.showSpinnerOnImageLoad;
            fabricJs.setShowSpinnerOnImageLoad(state.showSpinnerOnImageLoad);
        }

        var commands = JSON.parse(state.commands);

        for (var i in commands) {

            var command = commands[i];

            switch (command.canvasAction) {
                case "ADD_NOTE":
                    if (command.payload) {
                        var note = JSON.parse(command.payload);
                        fabricJs.addNotes(note.key, note.text, note.notesConfiguration);
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
                case "REMOVE_FIGURE":
                    if (command.payload) {
                        fabricJs.removeFigure(command.payload);
                    }
                    break;
                case "REMOVE_NOTE":
                    if (command.payload) {
                        fabricJs.removeNote(command.payload);
                    }
                    break;
                case "SET_TEXT":
                    if (command.payload) {
                        var figure = JSON.parse(command.payload);
                        fabricJs.setText(figure);
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
                case "SHOW_LOADER":
                    fabricJs.showLoader();
                    break;
                case "HIDE_LOADER":
                    fabricJs.hideLoader();
                    break;
                case "CHAIN_COMMAND":
                    if (command.payload) {

                        var chainCommand = JSON.parse(command.payload);

                        if (chainCommand.imagesUrl) {
                            component.imagesUrls = chainCommand.imagesUrl;
                        }

                        if (chainCommand.figureConfiguration) {
                            component.figureConfiguration = chainCommand.figureConfiguration;
                        }

                        if (chainCommand.loaderConfiguration) {
                            component.loaderConfiguration = chainCommand.loaderConfiguration;
                        }

                        if (chainCommand.rulerConfiguration) {
                            component.rulerConfiguration = chainCommand.rulerConfiguration;
                        }

                        fabricJs.executeChainOfCommand(chainCommand);
                    }
                    break;
            }
        }

        if (commands.length && commands[0].canvasAction !== "NONE") {
            component.clearCommands();
        }
    };
};
