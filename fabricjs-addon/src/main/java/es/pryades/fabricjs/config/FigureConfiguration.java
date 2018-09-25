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

import es.pryades.fabricjs.enums.CanvasAction;
import es.pryades.fabricjs.enums.FontStyle;
import es.pryades.fabricjs.enums.FontWeight;
import es.pryades.fabricjs.enums.StrokeLineCap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author geanny
 */
public class FigureConfiguration extends FontConfiguration implements Serializable {

    private String backgroundColor;

    private double strokeWidth;
    private String fillColor;
    private String strokeColor;

    private List<Double> strokeDashArray;

    private String strokeLineCap;
    private boolean visible;
    private CanvasAction action;
    private String cursor;
    private Integer cancelDrawKeyCode;
    private String hoverColor;
    private String figureShadow;

    public FigureConfiguration() {
        super();
        this.backgroundColor = "#FFF";
        this.strokeWidth = 1;
        this.fillColor = "red";
        this.strokeColor = "red";
        this.strokeLineCap = "butt";
        this.strokeDashArray = new ArrayList<>();
        this.visible = true;
        this.cursor = "default";
        this.action = CanvasAction.NONE;
        this.cancelDrawKeyCode = 27;

    }

    public FigureConfiguration(FigureConfiguration configuration) {
        super(configuration);
        this.backgroundColor = configuration.getBackgroundColor();
        this.strokeWidth = configuration.getStrokeWidth();
        this.fillColor = configuration.getFillColor();
        this.strokeColor = configuration.getStrokeColor();
        this.strokeDashArray = configuration.getStrokeDashArray();
        this.strokeLineCap = configuration.getStrokeLineCap().toLowerCase();
        this.visible = configuration.isVisible();
        this.action = configuration.getAction();
        this.cursor = configuration.getCursor();
        this.cancelDrawKeyCode = configuration.getCancelDrawKeyCode();
        this.hoverColor = configuration.getHoverColor();
        this.figureShadow = configuration.getFigureShadow();

    }

    public String getFigureShadow() {
        return figureShadow;
    }

    public void setFigureShadow(String figureShadow) {
        this.figureShadow = figureShadow;
    }

    public String getStrokeLineCap() {
        return strokeLineCap;
    }

    public void setStrokeLineCap(String strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
    }

    public String getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(String hoverColor) {
        this.hoverColor = hoverColor;
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

    public void setStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.strokeLineCap = strokeLineCap.name().toLowerCase();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public CanvasAction getAction() {
        return action;
    }

    public void setAction(CanvasAction action) {
        this.action = action;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public Integer getCancelDrawKeyCode() {
        return cancelDrawKeyCode;
    }

    public void setCancelDrawKeyCode(Integer cancelDrawKeyCode) {
        this.cancelDrawKeyCode = cancelDrawKeyCode;
    }

    public void clearStrokeDashArray() {
        if (this.strokeDashArray != null) {
            this.strokeDashArray = new ArrayList<>();
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

    public FigureConfiguration withAction(CanvasAction action) {
        this.setAction(action);
        return this;
    }

    public FigureConfiguration withCursor(String cursor) {
        this.setCursor(cursor);
        return this;
    }

    public FigureConfiguration withCancelDrawKeyCode(Integer cancelDrawKeyCode) {
        this.setCancelDrawKeyCode(cancelDrawKeyCode);
        return this;
    }

    public FigureConfiguration withTextShadow(String textShadow) {
        this.setTextShadow(textShadow);
        return this;
    }

    public FigureConfiguration withHoverColor(String hoverColor) {
        this.setHoverColor(hoverColor);
        return this;
    }

    public FigureConfiguration withFigureShadow(String figureShadow) {
        this.setFigureShadow(figureShadow);
        return this;
    }

    public FigureConfiguration clone() {
        return new FigureConfiguration(this);
    }

}
