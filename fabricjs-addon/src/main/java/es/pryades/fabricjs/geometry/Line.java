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

package es.pryades.fabricjs.geometry;

import es.pryades.fabricjs.data.Point;
import es.pryades.fabricjs.enums.FigureType;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author geanny
 */
public class Line extends Figure implements Serializable{

	private static final long serialVersionUID = -2943342223515659395L;

	public Line(List<Point> points) {
        super(FigureType.LINE, points);        
    }

    public Line(List<Point> points, String text) {
        super(FigureType.LINE, points, text);
    }
    
    
        
    
}
