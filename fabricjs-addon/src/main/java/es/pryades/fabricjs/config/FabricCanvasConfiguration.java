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
package es.pryades.fabricjs.config;

import es.pryades.fabricjs.enums.StrokeLineCap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author geanny
 */
public class FabricCanvasConfiguration extends FontConfiguration implements Serializable {

    private String backgroundColor;

    private double strokeWidth;
    private String fillColor;
    private String strokeColor;

    private List<Double> strokeDashArray;

    private String strokeLineCap;

    public FabricCanvasConfiguration() {
        super();
        this.backgroundColor = "#FFF";
        this.strokeWidth = 2;
        this.fillColor = "red";
        this.strokeColor = "red";
        this.strokeLineCap = "butt";
        this.strokeDashArray = new ArrayList<>();      
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public List<Double> getStrokeDashArray() {
        return strokeDashArray;
    }

    public void setStrokeDashArray(List<Double> strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
    }

    public StrokeLineCap getStrokeLineCap() {
        return StrokeLineCap.valueOf(strokeLineCap.toUpperCase());
    }

    public void setStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.strokeLineCap = strokeLineCap.name().toLowerCase();
    }

}
