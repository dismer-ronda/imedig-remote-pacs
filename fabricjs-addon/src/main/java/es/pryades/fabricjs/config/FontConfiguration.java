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

import es.pryades.fabricjs.enums.FontStyle;
import es.pryades.fabricjs.enums.FontWeight;

/**
 *
 * @author geanny
 */
public class FontConfiguration implements Serializable {

    private String textBackgroundColor;
    private String textFillColor;
    private String textFontFamily;
    private double textFontSize;
    private String textFontStyle;
    private String textFontWeight;
    private String textShadow;

    public FontConfiguration() {
        this.textBackgroundColor = "transparent";
        this.textFillColor = "black";
        this.textFontFamily = "Times New Roman";
        this.textFontSize = 16;
        this.textFontStyle = FontStyle.NORMAL.name().toLowerCase();
        this.textFontWeight = "normal";
    }

    public FontConfiguration(FontConfiguration configuration) {
        this.textBackgroundColor = configuration.getTextBackgroundColor();
        this.textFillColor = configuration.getTextFillColor();
        this.textFontFamily = configuration.getTextFontFamily();
        this.textFontSize = configuration.getTextFontSize();
        this.textFontStyle = configuration.getTextFontStyle().getValue();
        this.textFontWeight = configuration.getTextFontWeight().getValue();
        this.textShadow = configuration.getTextShadow();
    }

    public String getTextShadow() {
        return textShadow;
    }

    public void setTextShadow(String textShadow) {
        this.textShadow = textShadow;
    }
        
    public String getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public void setTextBackgroundColor(String textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
    }

    public String getTextFillColor() {
        return textFillColor;
    }

    public void setTextFillColor(String textFillColor) {
        this.textFillColor = textFillColor;
    }

    public String getTextFontFamily() {
        return textFontFamily;
    }

    public void setTextFontFamily(String textFontFamily) {
        this.textFontFamily = textFontFamily;
    }

    public double getTextFontSize() {
        return textFontSize;
    }

    public void setTextFontSize(double textFontSize) {
        this.textFontSize = textFontSize;
    }

    public FontStyle getTextFontStyle() {

        if (textFontStyle.isEmpty()) {
            return FontStyle.NONE;
        }
        return FontStyle.valueOf(textFontStyle.toUpperCase());
    }

    public void setTextFontStyle(FontStyle textFontStyle) {
        this.textFontStyle = textFontStyle.getValue();
    }

    public FontWeight getTextFontWeight() {
        return FontWeight.valueOf(textFontWeight.endsWith("00") ? "FW" + textFontWeight : textFontWeight.toUpperCase());
    }

    public void setTextFontWeight(FontWeight textFontWeight) {
        this.textFontWeight = textFontWeight.getValue();
    }


}
