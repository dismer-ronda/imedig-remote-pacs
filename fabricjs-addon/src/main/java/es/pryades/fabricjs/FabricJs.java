package es.pryades.fabricjs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.annotations.JavaScript;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;
import es.pryades.fabricjs.client.FabricJsState;
import es.pryades.fabricjs.config.CanvasDimensions;
import es.pryades.fabricjs.config.FabricCanvasConfiguration;
import es.pryades.fabricjs.config.NotesConfiguration;
import es.pryades.fabricjs.data.Command;
import es.pryades.fabricjs.data.Note;
import es.pryades.fabricjs.enums.CanvasAction;
import es.pryades.fabricjs.geometry.Figure;
import es.pryades.fabricjs.listeners.DrawFigureListener;
import es.pryades.fabricjs.listeners.MouseDownListener;
import es.pryades.fabricjs.listeners.MouseMoveListener;
import es.pryades.fabricjs.listeners.MouseUpListener;
import es.pryades.fabricjs.listeners.MouseWheelListener;
import es.pryades.fabricjs.listeners.ResizeListener;

// This is the server-side UI component that provides public API 
// for FabricJs
@JavaScript({"bower_components/fabric.js/dist/fabric.min.js", "vaadin-fabricjs.js", "fabric_connector.js"})
public class FabricJs extends AbstractJavaScriptComponent {

    private FabricCanvasConfiguration canvasConfiguration;
    private NotesConfiguration notesConfiguration;

    private double vWidth;
    private double vHeight;

    private String backgroundImage;

    private CanvasDimensions canvasDimensions;
    private final CanvasAction defauAction = CanvasAction.NONE;

    private CanvasAction action;

    private final List<Command> commands;

    /**
     * Listeners
     */
    private MouseMoveListener mouseMoveListener;

    private MouseDownListener mouseDownListener;

    private MouseUpListener mouseUpListener;

    private MouseWheelListener mouseWheelListener;

    private ResizeListener resizeListener;

    private DrawFigureListener drawFigureListener;

    public FabricJs() {
        this.commands = new ArrayList<>();
        this.addStyleName("canvas-wrapper");

        this.canvasConfiguration = new FabricCanvasConfiguration();
        notesConfiguration = new NotesConfiguration();
        backgroundImage = "";

        this.createDefaultsDimesions();
        this.generateConfiguration();
        this.setJavascriptFunctions();

    }

    public FabricJs(FabricCanvasConfiguration canvasConfiguration) {
        this.commands = new ArrayList<>();
        this.addStyleName("canvas-wrapper");

        notesConfiguration = new NotesConfiguration();
        backgroundImage = "";
        this.canvasConfiguration = canvasConfiguration;

        this.createDefaultsDimesions();
        this.generateConfiguration();
        this.setJavascriptFunctions();

        this.action = defauAction;

    }

    public void setImageUrl(String url) {
        backgroundImage = url;
        this.commands.add(new Command("SET_IMAGE", this.backgroundImage));
        this.getState().commands = getPayload(this.commands);
    }

    public void setImageUrl(ExternalResource resource) {
        setImageUrl(resource.getURL());
    }

    public void setAction(CanvasAction action) {
        this.commands.add(new Command("SET_ACTION", action.name()));
        this.getState().commands = getPayload(this.commands);
    }

    public void setCursor(String cursor) {
        this.commands.add(new Command("SET_CURSOR", cursor));
        getState().commands = getPayload(this.commands);
    }

    public void setText(Figure figure, String text) {
        figure.setText(text);
        this.commands.add(new Command("SET_TEXT", getPayload(figure)));
        getState().commands = getPayload(this.commands);
        //this.setFigureText(figure.getKey(), text);
    }

    public void draw(Figure figure) {
        this.commands.add(new Command("DRAW_FIGURE", getPayload(figure)));
        getState().commands = getPayload(this.commands);
    }

    public void clear() {
        this.commands.add(new Command("CLEAR_ALL", ""));
        getState().commands = getPayload(this.commands);
    }

    public void clearImage() {
        this.commands.add(new Command("CLEAR_IMAGE", ""));
        getState().commands = getPayload(this.commands);
    }

    public void clearNotes() {
        this.commands.add(new Command("CLEAR_NOTES", ""));
        getState().commands = getPayload(this.commands);
    }

    public void clearDraw() {
        this.commands.add(new Command("CLEAR_DRAW", ""));
        getState().commands = getPayload(this.commands);
    }

    public void setNotesConfiguration(NotesConfiguration configuration) {
        this.notesConfiguration = configuration;
    }

    public NotesConfiguration getNotesConfiguration() {
        return this.notesConfiguration;
    }

    public void addNotes(String text) {
        if (Objects.isNull(this.notesConfiguration)) {
            this.notesConfiguration = new NotesConfiguration();
        }
        addNotes(text, this.notesConfiguration);
    }

    public void addNotes(String text, NotesConfiguration configuration) {
        Note note = new Note(text, configuration);
        this.commands.add(new Command("ADD_NOTE", getPayload(note)));
        this.getState().commands = getPayload(this.commands);
    }

    public void removeFigure(Figure figure) {
        this.commands.add(new Command("REMOVE_FIGURE", figure.getKey()));
        this.getState().commands = getPayload(this.commands);
    }

    public void removeNote(Note note) {
        this.commands.add(new Command("REMOVE_NOTE", note.getKey()));
        this.getState().commands = getPayload(this.commands);
    }

    public FabricCanvasConfiguration getCanvasConfiguration() {
        return canvasConfiguration;
    }

    public void setCanvasConfiguration(FabricCanvasConfiguration canvasConfiguration) {
        this.canvasConfiguration = canvasConfiguration;
        getState().canvasConfiguration = getPayload(this.canvasConfiguration);
    }

    public CanvasDimensions getCanvasDimensions() {
        return canvasDimensions;
    }

    public void setCanvasDimensions(CanvasDimensions canvasDimensions) {
        this.canvasDimensions = canvasDimensions;
        this.generateCanvasDimensions();
    }

    public void setCanvasDimensions(int width, int height) {
        this.canvasDimensions = new CanvasDimensions(width, height);
        this.generateCanvasDimensions();
    }

    public double getvWidth() {
        return vWidth;
    }

    public double getvHeight() {
        return vHeight;
    }

    public MouseMoveListener getMouseMoveListener() {
        return mouseMoveListener;
    }

    public void setMouseMoveListener(MouseMoveListener mouseMoveListener) {
        this.mouseMoveListener = mouseMoveListener;
    }

    public MouseDownListener getMouseDownListener() {
        return mouseDownListener;
    }

    public void setMouseDownListener(MouseDownListener mouseDownListener) {
        this.mouseDownListener = mouseDownListener;
    }

    public MouseUpListener getMouseUpListener() {
        return mouseUpListener;
    }

    public void setMouseUpListener(MouseUpListener mouseUpListener) {
        this.mouseUpListener = mouseUpListener;
    }

    public MouseWheelListener getMouseWheelListener() {
        return mouseWheelListener;
    }

    public void setMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListener = mouseWheelListener;
    }

    public ResizeListener getResizeListener() {
        return resizeListener;
    }

    public void setResizeListener(ResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    public DrawFigureListener getDrawFigureListener() {
        return drawFigureListener;
    }

    public void setDrawFigureListener(DrawFigureListener drawFigureListener) {
        this.drawFigureListener = drawFigureListener;
    }

    private String getPayload(Object object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    private void generateConfiguration() {
        getState().canvasConfiguration = getPayload(this.canvasConfiguration);
    }

    private void generateCanvasDimensions() {
        this.getState().dimensions = getPayload(this.canvasDimensions);
    }

    private void createDefaultsDimesions() {
        this.canvasDimensions = new CanvasDimensions(300, 300);
        this.generateCanvasDimensions();
    }

    private void setJavascriptFunctions() {
        addFunction("setDimensions", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                vWidth = arguments.getNumber(0);
                vHeight = arguments.getNumber(1);
            }
        });

        addFunction("clearCommands", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                commands.clear();
                //getState().commands = getPayload(commands);
            }
        });

        addFunction("onMouseWheel", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(mouseWheelListener)) {
                    mouseWheelListener.onMouseWheel(arguments.getNumber(0));
                }
            }
        });

        addFunction("onResize", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(resizeListener)) {
                    double width = arguments.getNumber(0);
                    double height = arguments.getNumber(1);
                    resizeListener.onResize(width, height);
                }
            }
        });

        addFunction("onDrawFigure", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(drawFigureListener)) {
                    Gson gson = new GsonBuilder().create();
                    Figure figure = gson.fromJson(arguments.getString(0), Figure.class);
                    drawFigureListener.onDrawFigure(figure);
                }
            }
        });
    }

    //Override Methods
    // We must override getState() to cast the state to FabricJsState
    @Override
    protected FabricJsState getState() {
        return (FabricJsState) super.getState();
    }

}
