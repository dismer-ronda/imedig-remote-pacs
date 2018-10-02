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
import es.pryades.fabricjs.enums.RulerPosition;
import es.pryades.fabricjs.enums.StrokeLineCap;
import java.util.List;

/**
 *
 * @author geanny
 */
public class RulerConfiguration extends FigureConfiguration {

    private double pixels;
    private Integer space;
    private Integer segmentSize;
    private Integer split;
    private RulerPosition position;

    public RulerConfiguration() {
        this.pixels = 0;
        this.space = 10;
        this.segmentSize = 10;
        this.split = 0;
        this.position = RulerPosition.RIGHT;
    }

    private RulerConfiguration(RulerConfiguration configuration) {
        super(configuration);
        this.pixels = configuration.getPixels();
        this.segmentSize = configuration.getSegmentSize();
        this.space = configuration.getSpace();
        this.split = configuration.getSplit();
        this.position = configuration.getPosition();
    }

    public double getPixels() {
        return pixels;
    }

    public void setPixels(double pixels) {
        this.pixels = pixels;
    }

    public Integer getSpace() {
        return space;
    }

    public void setSpace(Integer space) {
        this.space = space;
    }

    public Integer getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(Integer segmentSize) {
        this.segmentSize = segmentSize;
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
    }

    public RulerPosition getPosition() {
        return position;
    }

    public void setPosition(RulerPosition position) {
        this.position = position;
    }
    
     public RulerConfiguration withPosition(RulerPosition position) {
        this.setPosition(position);
        return this;
    }

    public RulerConfiguration withPixels(double pixels) {
        this.setPixels(pixels);
        return this;
    }

    public RulerConfiguration withSpace(Integer space) {
        this.setSpace(space);
        return this;
    }

    public RulerConfiguration withSegmentSize(Integer segmentSize) {
        this.setSegmentSize(segmentSize);
        return this;
    }

    public RulerConfiguration withSplit(Integer split) {
        this.setSplit(split);
        return this;
    }

    @Override
    public RulerConfiguration withBackgroundColor(String backgroundColor) {
        this.setBackgroundColor(backgroundColor);
        return this;
    }

    @Override
    public RulerConfiguration withStrokeWidth(double strokeWidth) {
        this.setStrokeWidth(strokeWidth);
        return this;
    }

    @Override
    public RulerConfiguration withFillColor(String fillColor) {
        this.setFillColor(fillColor);
        return this;
    }

    @Override
    public RulerConfiguration withStrokeColor(String strokeColor) {
        this.setStrokeColor(strokeColor);
        return this;
    }

    @Override
    public RulerConfiguration withStrokeDashArray(List<Double> strokeDashArray) {
        this.setStrokeDashArray(strokeDashArray);
        return this;
    }

    @Override
    public RulerConfiguration withStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.setStrokeLineCap(strokeLineCap);
        return this;
    }

    @Override
    public RulerConfiguration withVisible(boolean visible) {
        this.setVisible(visible);
        return this;
    }

    @Override
    public RulerConfiguration withTextBackgroundColor(String textBackgroundColor) {
        this.setTextBackgroundColor(textBackgroundColor);
        return this;
    }

    @Override
    public RulerConfiguration withTextFillColor(String textFillColor) {
        this.setTextFillColor(textFillColor);
        return this;
    }

    @Override
    public RulerConfiguration withTextFontFamily(String textFontFamily) {
        this.setTextFontFamily(textFontFamily);
        return this;
    }

    @Override
    public RulerConfiguration withTextFontSize(double textFontSize) {
        this.setTextFontSize(textFontSize);
        return this;
    }

    @Override
    public RulerConfiguration withTextFontStyle(FontStyle textFontStyle) {
        this.setTextFontStyle(textFontStyle);
        return this;
    }

    @Override
    public RulerConfiguration withTextFontWeight(FontWeight textFontWeight) {
        this.setTextFontWeight(textFontWeight);
        return this;
    }

    @Override
    public RulerConfiguration withAction(CanvasAction action) {
        this.setAction(action);
        return this;
    }

    @Override
    public RulerConfiguration withCursor(String cursor) {
        this.setCursor(cursor);
        return this;
    }

    @Override
    public RulerConfiguration withCancelDrawKeyCode(Integer cancelDrawKeyCode) {
        this.setCancelDrawKeyCode(cancelDrawKeyCode);
        return this;
    }

    @Override
    public RulerConfiguration withTextShadow(String textShadow) {
        this.setTextShadow(textShadow);
        return this;
    }

    @Override
    public RulerConfiguration withHoverColor(String hoverColor) {
        this.setHoverColor(hoverColor);
        return this;
    }

    @Override
    public RulerConfiguration withFigureShadow(String figureShadow) {
        this.setFigureShadow(figureShadow);
        return this;
    }

    @Override
    public RulerConfiguration clone() {
        return new RulerConfiguration(this);
    }

}
