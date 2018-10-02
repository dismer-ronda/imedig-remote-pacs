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

import es.pryades.fabricjs.config.RulerConfiguration;
import es.pryades.fabricjs.enums.FigureType;
import java.io.Serializable;

/**
 *
 * @author geanny
 */
public class Ruler implements Serializable {

    private FigureType figureType;
    private String text;
    private boolean Drawed;
    private String key;
    private RulerConfiguration configuration;

    public Ruler(String text, RulerConfiguration configuration) {
        this.text = text;
        this.configuration = configuration;
        this.figureType = FigureType.RULER;
    }
    
    

    public FigureType getFigureType() {
        return figureType;
    }

    public void setFigureType(FigureType figureType) {
        this.figureType = figureType;
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

    public RulerConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RulerConfiguration configuration) {
        this.configuration = configuration;
    }

}
