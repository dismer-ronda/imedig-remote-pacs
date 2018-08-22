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

import es.pryades.fabricjs.enums.FontStyle;
import es.pryades.fabricjs.enums.FontWeight;
import es.pryades.fabricjs.enums.NotesAlignment;
import es.pryades.fabricjs.enums.StrokeLineCap;
import es.pryades.fabricjs.enums.TextAlign;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author geanny
 */
public class NotesConfiguration extends FontConfiguration implements Serializable {

    private double textLeft;
    private double textTop;
    private String originX;
    private String originY;
    private String textAlign;

    public NotesConfiguration() {
        super();
        this.originX = NotesAlignment.TOP_LEFT.getOriginX();
        this.originY = NotesAlignment.TOP_LEFT.getOriginY();
        this.textLeft = 0;
        this.textTop = 0;
        this.textAlign = TextAlign.LEFT.name().toLowerCase();
    }

    public NotesAlignment getNotesAlignment() {
        return NotesAlignment.valueOf((originY + "_" + originX).toUpperCase());
    }

    public void setNotesAlignment(NotesAlignment notesAlignment) {
        this.originX = notesAlignment.getOriginX();
        this.originY = notesAlignment.getOriginY();
    }

    public double getTextLeft() {
        return textLeft;
    }

    public void setTextLeft(double textLeft) {
        this.textLeft = textLeft;
    }

    public double getTextTop() {
        return textTop;
    }

    public void setTextTop(double textTop) {
        this.textTop = textTop;
    }

    public TextAlign getTextAlign() {
        return TextAlign.valueOf(textAlign.toUpperCase().replaceAll("-", "_"));
    }

    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign.name().toLowerCase().replaceAll("_", "-");
    }

    public NotesConfiguration withNotesAlignment(NotesAlignment notesAlignment) {
        this.setNotesAlignment(notesAlignment);
        return this;
    }

    public NotesConfiguration withTextLeft(double textLeft) {
        this.setTextLeft(textLeft);
        return this;
    }

    public NotesConfiguration withTextTop(double textTop) {
        this.setTextTop(textTop);
        return this;
    }

    public NotesConfiguration withTextAlign(TextAlign textAlign) {
        this.setTextAlign(textAlign);
        return this;
    }

    public NotesConfiguration withTextBackgroundColor(String textBackgroundColor) {
        this.setTextBackgroundColor(textBackgroundColor);
        return this;
    }

    public NotesConfiguration withTextFillColor(String textFillColor) {
        this.setTextFillColor(textFillColor);
        return this;
    }

    public NotesConfiguration withTextFontFamily(String textFontFamily) {
        this.setTextFontFamily(textFontFamily);
        return this;
    }

    public NotesConfiguration withTextFontSize(double textFontSize) {
        this.setTextFontSize(textFontSize);
        return this;
    }

    public NotesConfiguration withTextFontStyle(FontStyle textFontStyle) {
        this.setTextFontStyle(textFontStyle);
        return this;
    }

    public NotesConfiguration withTextFontWeight(FontWeight textFontWeight) {
        this.setTextFontWeight(textFontWeight);
        return this;
    }
    
    public NotesConfiguration withTextShadow(String textShadow) {
        this.setTextShadow(textShadow);
        return this;
    }

}
