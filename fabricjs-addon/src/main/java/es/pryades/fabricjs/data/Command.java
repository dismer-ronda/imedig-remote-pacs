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
package es.pryades.fabricjs.data;

import java.io.Serializable;

/**
 *
 * @author geanny
 */
public class Command implements Serializable {

	private static final long serialVersionUID = -6148502873284399177L;
	
	private String canvasAction;
    private String payload;

    public Command(String canvasAction, String payload) {
        this.canvasAction = canvasAction;
        this.payload = payload;
    }

    public String getCanvasAction() {
        return canvasAction;
    }

    public void setCanvasAction(String canvasAction) {
        this.canvasAction = canvasAction;
    }
           
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
