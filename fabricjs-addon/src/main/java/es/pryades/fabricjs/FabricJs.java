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
import es.pryades.fabricjs.data.Point;
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

    private final FabricCanvasConfiguration canvasConfiguration;
    private NotesConfiguration notesConfiguration;

    private double vWidth;
    private double vHeight;

    private String backgroundImage;

    private CanvasDimensions canvasDimensions;
    private final CanvasAction defauAction = CanvasAction.NONE;
    private CanvasAction action;

    private final List<Note> notes;

    private List<Figure> figures;
    
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
        this.figures = new ArrayList<>();
        this.addStyleName("canvas-wrapper");

        this.notes = new ArrayList<>();
        this.canvasConfiguration = new FabricCanvasConfiguration();

        this.createDefaultsDimesions();
        this.generateConfiguration();
        this.setJavascriptFunctions();
            
        notesConfiguration = new NotesConfiguration();
        backgroundImage = "";       
    }

    public FabricJs(FabricCanvasConfiguration canvasConfiguration) {       
        this.figures = new ArrayList<>();
        this.addStyleName("canvas-wrapper");

        this.notes = new ArrayList<>();
        this.canvasConfiguration = canvasConfiguration;        

        this.createDefaultsDimesions();
        this.generateConfiguration();
        this.setJavascriptFunctions();

        this.action = defauAction;
        
        notesConfiguration = new NotesConfiguration();
        backgroundImage = "";
    }

            
    public void setImageUrl(String url) {
        backgroundImage = url;
        this.getState().command = getPayload(new Command("SET_IMAGE", this.backgroundImage));
    }

    public void setImageUrl(ExternalResource resource) {
        setImageUrl(resource.getURL());
    }
    
    public void setAction(CanvasAction action){
        getState().command = getPayload(new Command("SET_ACTION",action.name()));
    }

    public void setCursor(String cursor){
        getState().command = getPayload(new Command("SET_CURSOR", cursor));
    }
    
    
    public void setText(String text){
        getState().command = getPayload(new Command("SET_TEXT", text));
        this.figures.get(this.figures.size()-1).setText(text);
    }
    
    public void draw (Figure figure){
        getState().command = getPayload(new Command("DRAW_FIGURE",getPayload(figure)));
    }
    
    
    public void clear(){
         getState().command = getPayload(new Command("CLEAR_ALL", ""));
    }
    
    public void clearImage(){
         getState().command = getPayload(new Command("CLEAR_IMAGE", ""));
    }
    
    public void clearNotes(){
         getState().command = getPayload(new Command("CLEAR_NOTES", ""));
    }
    
    public void clearDraw(){
         getState().command = getPayload(new Command("CLEAR_DRAW", ""));
    }
    
    /*public void drawLine(double x1, double y1, double x2, double y2) {
        this.figures.add(new Line(Arraysx1, y1, x2, y2));
        this.getState().command = getPayload(new Command("DRAW_LINE", getPayload(this.figures)));
    }

    public void setLineText(String text) {
        Line line = (Line) this.figures.get(this.figures.size() - 1);
        line.setText(text);
        this.getState().command = getPayload(new Command("DRAW_LINE", getPayload(this.figures)));
    }

    public void moveLineTo(double x2, double y2) {
        Line line = (Line) this.figures.get(this.figures.size() - 1);
        line.setX2(x2);
        line.setY2(y2);
                        
        this.getState().command = getPayload(new Command("DRAW_LINE", getPayload(this.figures)));
    }*/

   
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
        notes.add(new Note(text, configuration));
        this.getState().command = getPayload(new Command("ADD_NOTE", getPayload(notes)));
    }

    public FabricCanvasConfiguration getCanvasConfiguration() {
        return canvasConfiguration;
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

    public Double getvWidth() {
        return vWidth;
    }

    public Double getvHeight() {
        return vHeight;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
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

    //métodos privados
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

        addFunction("onMouseMove", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(mouseMoveListener)) {
                    Point point = new Point(arguments.getNumber(0), arguments.getNumber(1));
                    mouseMoveListener.onMouseMove(point);
                }
            }
        });

        addFunction("onMouseDown", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(mouseDownListener)) {
                    Point point = new Point(arguments.getNumber(0), arguments.getNumber(1));
                    mouseDownListener.onMouseDown(point);
                }
            }
        });

        addFunction("onMouseUp", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                if (!Objects.isNull(mouseUpListener)) {
                    Point point = new Point(arguments.getNumber(0), arguments.getNumber(1));
                    mouseUpListener.onMouseUp(point);
                }
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
                    figures.add(figure);
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
