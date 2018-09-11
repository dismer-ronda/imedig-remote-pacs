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

import es.pryades.fabricjs.enums.FigureAlignment;
import es.pryades.fabricjs.enums.FontStyle;
import es.pryades.fabricjs.enums.FontWeight;
import es.pryades.fabricjs.enums.SpinnerPosition;
import es.pryades.fabricjs.enums.SpinnerSpeed;
import java.io.Serializable;

/**
 *
 * @author geanny
 */
public class LoaderConfiguration extends FontConfiguration implements Serializable {

    private String fillColor;
    private String shadow;
    private String spinnerColor;
    private Integer spinnerWidth;
    private String strokeColor;
    private Integer strokeWidth;
    private String originX;
    private String originY;
    private Double height;
    private String loaderText;
    private SpinnerPosition spinnerPosition;
    private Integer spinnerRadio;
    private SpinnerSpeed spinnerSpeed;

    public LoaderConfiguration() {
        this.originX = FigureAlignment.TOP_LEFT.getOriginX();
        this.originY = FigureAlignment.TOP_LEFT.getOriginY();
        this.fillColor = "#FFF";
        this.shadow = "3 3 10 rgba(0,0,0,0.6)";
        this.spinnerColor = "#00aaf0";
        this.strokeColor = this.fillColor;
        this.strokeWidth = 1;
        this.height = 50.0;
        this.loaderText = "Cargando...";
        this.spinnerWidth = 2;
        this.spinnerPosition = SpinnerPosition.LEFT;
        this.spinnerRadio = 10;
        this.spinnerSpeed = SpinnerSpeed.SLOW;
    }

    public FigureAlignment getLoaderAlignment() {
        return FigureAlignment.valueOf((originY + "_" + originX).toUpperCase());
    }

    public void setLoaderAlignment(FigureAlignment notesAlignment) {
        this.originX = notesAlignment.getOriginX();
        this.originY = notesAlignment.getOriginY();
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getShadow() {
        return shadow;
    }

    public void setShadow(String shadow) {
        this.shadow = shadow;
    }

    public String getSpinnerColor() {
        return spinnerColor;
    }

    public void setSpinnerColor(String spinnerColor) {
        this.spinnerColor = spinnerColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Integer getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(Integer strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getLoaderText() {
        return loaderText;
    }

    public void setLoaderText(String loaderText) {
        this.loaderText = loaderText;
    }

    public SpinnerPosition getSpinnerPosition() {
        return spinnerPosition;
    }

    public void setSpinnerPosition(SpinnerPosition spinnerPosition) {
        this.spinnerPosition = spinnerPosition;
    }

    public Integer getSpinnerWidth() {
        return spinnerWidth;
    }

    public void setSpinnerWidth(Integer spinnerWidth) {
        this.spinnerWidth = spinnerWidth;
    }

    public Integer getSpinnerRadio() {
        return spinnerRadio;
    }

    public void setSpinnerRadio(Integer spinnerRadio) {
        this.spinnerRadio = spinnerRadio;
    }

    public SpinnerSpeed getSpinnerSpeed() {
        return spinnerSpeed;
    }

    public void setSpinnerSpeed(SpinnerSpeed spinnerSpeed) {
        this.spinnerSpeed = spinnerSpeed;
    }

    public LoaderConfiguration withSpinnerSpeed(SpinnerSpeed spinnerSpeed) {
        this.setSpinnerSpeed(spinnerSpeed);
        return this;
    }

    public LoaderConfiguration withSpinnerWidth(Integer spinnerWidth) {
        this.setSpinnerWidth(spinnerWidth);
        return this;
    }

    public LoaderConfiguration withSpinnerRadio(Integer spinnerRadio) {
        this.setSpinnerRadio(spinnerRadio);
        return this;
    }

    public LoaderConfiguration withSpinnerPosition(SpinnerPosition spinnerPosition) {
        this.setSpinnerPosition(spinnerPosition);
        return this;
    }

    public LoaderConfiguration withLoaderText(String loaderText) {
        this.setLoaderText(loaderText);
        return this;
    }

    public LoaderConfiguration withHeight(Double height) {
        this.setHeight(height);
        return this;
    }

    public LoaderConfiguration withStrokeColor(String strokeColor) {
        this.setStrokeColor(strokeColor);
        return this;
    }

    public LoaderConfiguration withStrokeWidth(Integer strokeWidth) {
        this.setStrokeWidth(strokeWidth);
        return this;
    }

    public LoaderConfiguration withLoaderAlignment(FigureAlignment notesAlignment) {
        this.setLoaderAlignment(notesAlignment);
        return this;
    }

    public LoaderConfiguration withFillColor(String fillColor) {
        this.setFillColor(fillColor);
        return this;
    }

    public LoaderConfiguration withShadow(String shadow) {
        this.setShadow(shadow);
        return this;
    }

    public LoaderConfiguration withSpinnerColor(String spinnerColor) {
        this.setSpinnerColor(spinnerColor);
        return this;
    }

    public LoaderConfiguration withTextBackgroundColor(String textBackgroundColor) {
        this.setTextBackgroundColor(textBackgroundColor);
        return this;
    }

    public LoaderConfiguration withTextFillColor(String textFillColor) {
        this.setTextFillColor(textFillColor);
        return this;
    }

    public LoaderConfiguration withTextFontFamily(String textFontFamily) {
        this.setTextFontFamily(textFontFamily);
        return this;
    }

    public LoaderConfiguration withTextFontSize(double textFontSize) {
        this.setTextFontSize(textFontSize);
        return this;
    }

    public LoaderConfiguration withTextFontStyle(FontStyle textFontStyle) {
        this.setTextFontStyle(textFontStyle);
        return this;
    }

    public LoaderConfiguration withTextFontWeight(FontWeight textFontWeight) {
        this.setTextFontWeight(textFontWeight);
        return this;
    }

    public LoaderConfiguration withTextShadow(String textShadow) {
        this.setTextShadow(textShadow);
        return this;
    }

}
