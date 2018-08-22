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
public class TooltipConfiguration extends FontConfiguration implements Serializable {

    private String backgroundColor;
    private Integer margin;

    public TooltipConfiguration() {
    	this.margin = 0;
    	this.backgroundColor = "transparent";
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public TooltipConfiguration withBackgroundColor(String backgroundColor) {
        this.setBackgroundColor(backgroundColor);
        return this;
    }

    public TooltipConfiguration withMargin(Integer margin) {
        this.setMargin(margin);
        return this;
    }

    public TooltipConfiguration withTextBackgroundColor(String textBackgroundColor) {
        this.setTextBackgroundColor(textBackgroundColor);
        return this;
    }

    public TooltipConfiguration withTextFillColor(String textFillColor) {
        this.setTextFillColor(textFillColor);
        return this;
    }

    public TooltipConfiguration withTextFontFamily(String textFontFamily) {
        this.setTextFontFamily(textFontFamily);
        return this;
    }

    public TooltipConfiguration withTextFontSize(double textFontSize) {
        this.setTextFontSize(textFontSize);
        return this;
    }

    public TooltipConfiguration withTextFontStyle(FontStyle textFontStyle) {
        this.setTextFontStyle(textFontStyle);
        return this;
    }

    public TooltipConfiguration withTextFontWeight(FontWeight textFontWeight) {
        this.setTextFontWeight(textFontWeight);
        return this;
    }

    public TooltipConfiguration withTextShadow(String textShadow) {
        this.setTextShadow(textShadow);
        return this;
    }

}
