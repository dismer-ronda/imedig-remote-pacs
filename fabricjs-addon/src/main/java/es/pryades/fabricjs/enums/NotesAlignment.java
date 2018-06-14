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
package es.pryades.fabricjs.enums;

import java.io.Serializable;

/**
 *
 * @author geanny
 */
public enum NotesAlignment implements Serializable {

    TOP_LEFT("top", "left"),
    TOP_RIGHT("top", "right"),
    TOP_CENTER("top", "center"),
    BOTTOM_LEFT("bottom", "left"),
    BOTTOM_RIGHT("bottom", "right"),
    BOTTOM_CENTER("bottom", "center"),
    MIDDLE_LEFT("center", "left"),
    MIDDLE_RIGHT("center", "right"),
    MIDDLE_CENTER("center", "center");

    private final String originX;
    private final String originY;

    private NotesAlignment(String originY, String originX) {
        this.originX = originX;
        this.originY = originY;
    }

    public String getOriginX() {
        return originX;
    }

    public String getOriginY() {
        return originY;
    }
    
    

}
