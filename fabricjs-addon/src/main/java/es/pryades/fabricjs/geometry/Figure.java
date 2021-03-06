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
package es.pryades.fabricjs.geometry;

import es.pryades.fabricjs.config.FigureConfiguration;
import es.pryades.fabricjs.data.Point;
import es.pryades.fabricjs.enums.FigureType;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author geanny
 */
public class Figure implements Serializable {

	private static final long serialVersionUID = 8730518690759122835L;
	
	private FigureType figureType;
    private List<Point> points;
    private String text;
    private boolean Drawed;
    private String key;
    private FigureConfiguration configuration;

    public Figure(FigureType figureType, List<Point> points) {
        this.figureType = figureType;
        this.points = points;
        this.text = "";
        this.Drawed = true;
    }

    public Figure(FigureType figureType, List<Point> points, String text) {
        this.figureType = figureType;
        this.text = text;
        this.points = points;
        this.Drawed = true;
    }
    
     public Figure(String key,FigureType figureType, List<Point> points, String text) {
        this.figureType = figureType;
        this.text = text;
        this.points = points;
        this.Drawed = true;
        this.key = key;
    }

    public FigureType getFigureType() {
        return figureType;
    }

    public void setFigureType(FigureType figureType) {
        this.figureType = figureType;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDrawed() {
        return Drawed;
    }

    public void setDrawed(boolean Drawed) {
        this.Drawed = Drawed;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FigureConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(FigureConfiguration configuration) {
        this.configuration = configuration;
    }        

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Figure other = (Figure) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }
    
    

}
