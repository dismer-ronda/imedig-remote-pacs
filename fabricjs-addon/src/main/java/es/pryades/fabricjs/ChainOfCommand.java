/*
 * Copyright 2018 geanny.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.pryades.fabricjs;

import com.vaadin.server.ExternalResource;
import es.pryades.fabricjs.config.FigureConfiguration;
import es.pryades.fabricjs.config.LoaderConfiguration;
import es.pryades.fabricjs.data.Note;
import es.pryades.fabricjs.geometry.Figure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author geanny
 */
public class ChainOfCommand implements Serializable {

    private List<Figure> figures;
    private List<Note> notes;
    private List<String> imagesUrl;

    private boolean clearDraw;
    private boolean clearImage;
    private boolean clearNotes;
    private boolean clearAll;

    private LoaderConfiguration loaderConfiguration;
    private FigureConfiguration figureConfiguration;

    public List<Figure> getFigures() {
        return figures;
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<ExternalResource> externalImagesUrl) {
        List<String> urls = new ArrayList<>();
        for (ExternalResource img : externalImagesUrl) {
            urls.add(img.getURL());
        }
        this.imagesUrl = urls;
    }

    public boolean isClearDraw() {
        return clearDraw;
    }

    public void setClearDraw(boolean clearDraw) {
        this.clearDraw = clearDraw;
    }

    public boolean isClearImage() {
        return clearImage;
    }

    public void setClearImage(boolean clearImage) {
        this.clearImage = clearImage;
    }

    public boolean isClearNotes() {
        return clearNotes;
    }

    public void setClearNotes(boolean clearNotes) {
        this.clearNotes = clearNotes;
    }

    public LoaderConfiguration getLoaderConfiguration() {
        return loaderConfiguration;
    }

    public void setLoaderConfiguration(LoaderConfiguration loaderConfiguration) {
        this.loaderConfiguration = loaderConfiguration;
    }

    public FigureConfiguration getFigureConfiguration() {
        return figureConfiguration;
    }

    public void setFigureConfiguration(FigureConfiguration figureConfiguration) {
        this.figureConfiguration = figureConfiguration;
    }

    public boolean isClearAll() {
        return clearAll;
    }

    public void setClearAll(boolean clearAll) {
        this.clearAll = clearAll;
    }

    public ChainOfCommand withClearAll(boolean clearAll) {
        this.setClearAll(clearAll);
        return this;
    }

    public ChainOfCommand withFigures(List<Figure> figures) {
        this.setFigures(figures);
        return this;
    }

    public ChainOfCommand withNotes(List<Note> notes) {
        this.setNotes(notes);
        return this;
    }

    public ChainOfCommand withImagesUrl(List<ExternalResource> imagesUrl) {
        this.setImagesUrl(imagesUrl);
        return this;
    }

    public ChainOfCommand withClearDraw(boolean clearDraw) {
        this.setClearDraw(clearDraw);
        return this;
    }

    public ChainOfCommand withClearImage(boolean clearImage) {
        this.setClearImage(clearImage);
        return this;
    }

    public ChainOfCommand withClearNotes(boolean clearNotes) {
        this.setClearNotes(clearNotes);
        return this;
    }

    public ChainOfCommand withLoaderConfiguration(LoaderConfiguration loaderConfiguration) {
        this.setLoaderConfiguration(loaderConfiguration);
        return this;
    }

    public ChainOfCommand withFigureConfiguration(FigureConfiguration figureConfiguration) {
        this.setFigureConfiguration(figureConfiguration);
        return this;
    }

}
