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

/**
 *
 * @author geanny
 */
public class Shadow implements Serializable {

	private static final long serialVersionUID = 6289818299755535546L;
	
	private Integer blur;
    private String color;
    private Integer offsetX;
    private Integer offsetY;

    public Shadow() {
        this.blur = 0;
        this.color = "";
        this.offsetX = 0;
        this.offsetY = 0;
    }

       
    public Shadow( String color,Integer offsetX, Integer offsetY,Integer blur) {
        this.blur = blur;
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    

    public Integer getBlur() {
        return blur;
    }

    public void setBlur(Integer blur) {
        this.blur = blur;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(Integer offsetX) {
        this.offsetX = offsetX;
    }

    public Integer getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(Integer offsetY) {
        this.offsetY = offsetY;
    }

}
