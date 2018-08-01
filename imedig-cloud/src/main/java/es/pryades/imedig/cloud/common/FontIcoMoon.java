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
package es.pryades.imedig.cloud.common;

import com.vaadin.server.FontIcon;
import com.vaadin.server.GenericFontIcon;

/**
 *
 * @author geanny
 */
public enum FontIcoMoon implements FontIcon {

    // @formatter:off
	RULE(0Xe915),
	PROTRACTOR(0Xe900);
    // @formatter:on
    public static final String FONT_FAMILY = "IcoMoon";
    private final int codepoint;

    FontIcoMoon(int codepoint) {
        this.codepoint = codepoint;
    }

    @Override
    public String getMIMEType() {
        throw new UnsupportedOperationException(
                FontIcon.class.getSimpleName() + " should not be used where a MIME type is needed.");
    }

    @Override
    public String getFontFamily() {
        return FONT_FAMILY;
    }

    @Override
    public int getCodepoint() {
        return codepoint;
    }

    @Override
    public String getHtml() {
        return GenericFontIcon.getHtml(FontIcoMoon.FONT_FAMILY, codepoint);
    }
}
