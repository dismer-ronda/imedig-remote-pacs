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

import es.pryades.fabricjs.enums.NotesAlignment;
import es.pryades.fabricjs.enums.TextAlign;
import java.io.Serializable;

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
    
    

}
