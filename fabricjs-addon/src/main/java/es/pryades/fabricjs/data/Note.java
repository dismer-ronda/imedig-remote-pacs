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

import es.pryades.fabricjs.config.NotesConfiguration;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author geanny
 */
public class Note implements Serializable {

	private static final long serialVersionUID = -9081680055142615149L;
	
	private String text;
    private String key;
    private NotesConfiguration notesConfiguration;

    public Note(String text) {
        this.text = text;
        this.notesConfiguration = new NotesConfiguration();
        this.key = this.notesConfiguration.getNotesAlignment().getOriginX() + this.notesConfiguration.getNotesAlignment().getOriginY();
    }

    public Note(String text, NotesConfiguration notesConfiguration) {

        this.text = text;
        if (notesConfiguration == null) {
            this.notesConfiguration = new NotesConfiguration();
        } else {
            this.notesConfiguration = notesConfiguration;
        }

        this.key = this.notesConfiguration.getNotesAlignment().getOriginX() + this.notesConfiguration.getNotesAlignment().getOriginY();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NotesConfiguration getNotesConfiguration() {
        return notesConfiguration;
    }

    public void setNotesConfiguration(NotesConfiguration notesConfiguration) {
        this.notesConfiguration = notesConfiguration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Note other = (Note) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

}
