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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.pryades.fabricjs.enums.FontStyle;
import es.pryades.fabricjs.enums.FontWeight;
import es.pryades.fabricjs.enums.StrokeLineCap;

/**
 *
 * @author geanny
 */
public class FigureConfiguration extends FontConfiguration implements Cloneable, Serializable {

    private String backgroundColor;

    private double strokeWidth;
    private String fillColor;
    private String strokeColor;

    private List<Double> strokeDashArray;

    private String strokeLineCap;
    private boolean visible;

    public FigureConfiguration() {
        super();
        this.backgroundColor = "#FFF";
        this.strokeWidth = 1;
        this.fillColor = "red";
        this.strokeColor = "red";
        this.strokeLineCap = "butt";
        this.strokeDashArray = new ArrayList<>();
        this.visible = true;
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
    
    protected void setStrokeLineCap(String strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void clearStrokeDashArray() {
        if (!Objects.isNull(this.strokeDashArray)) {
            this.strokeDashArray.clear();
        }
    }

    public FigureConfiguration withBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public FigureConfiguration withStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public FigureConfiguration withFillColor(String fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public FigureConfiguration withStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    public FigureConfiguration withStrokeDashArray(List<Double> strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
        return this;
    }

    public FigureConfiguration withStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.strokeLineCap = strokeLineCap.name().toLowerCase();
        return this;
    }

    public FigureConfiguration withVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public FigureConfiguration withTextBackgroundColor(String textBackgroundColor) {
        this.setTextBackgroundColor(textBackgroundColor);
        return this;
    }

    public FigureConfiguration withTextFillColor(String textFillColor) {
        this.setTextFillColor(textFillColor);
        return this;
    }

    public FigureConfiguration withTextFontFamily(String textFontFamily) {
        this.setTextFontFamily(textFontFamily);
        return this;
    }

    public FigureConfiguration withTextFontSize(double textFontSize) {
        this.setTextFontSize(textFontSize);
        return this;
    }

    public FigureConfiguration withTextFontStyle(FontStyle textFontStyle) {
        this.setTextFontStyle(textFontStyle);
        return this;
    }

    public FigureConfiguration withTextFontWeight(FontWeight textFontWeight) {
        this.setTextFontWeight(textFontWeight);
        return this;
    }
    
    @Override
    public FigureConfiguration clone(){
    	FigureConfiguration clone = new FigureConfiguration();
    	clone.setTextBackgroundColor(this.getTextBackgroundColor());
    	clone.setTextFillColor(this.getTextFillColor());
    	clone.setTextFontFamily(this.getTextFontFamily());
    	clone.setTextFontSize(this.getTextFontSize());
    	clone.setTextFontStyle(this.getTextFontStyle());
    	clone.setTextFontWeight(this.getTextFontWeight());
    	
    	clone.setBackgroundColor(backgroundColor);
    	clone.setStrokeWidth(strokeWidth);
    	clone.setFillColor(fillColor);
    	clone.setStrokeColor(strokeColor);
    	clone.setStrokeDashArray((List<Double>)((ArrayList<Double>)strokeDashArray).clone());
    	clone.setStrokeLineCap(strokeLineCap);
    	clone.setVisible(visible);
    	    	
    	return clone;
    }

}
