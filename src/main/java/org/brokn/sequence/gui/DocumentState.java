/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class DocumentState {

    private static final Logger log = Logger.getLogger(DocumentState.class.getName());

    private File file;
    private String initialText;
    private String currentText = "";
    private final TextChangedListener textChangedListener;

    public DocumentState(TextChangedListener listener, File file, String initialText) {
        assert initialText != null;
        this.textChangedListener = listener;
        this.file = file;
        this.initialText = initialText;
        this.currentText = initialText;
        this.textChangedListener.onTextChanged(this.initialText);
    }

    public DocumentState(TextChangedListener listener) {
        this(listener, null, "");
    }

    public File getFile() {
        return this.file;
    }

    private boolean isClasspathFile(File file) {
        return this.file != null && ClassLoader.getSystemResource(file.getName()) != null;
    }

    public boolean isDirty() {
        // if the file is found on the classpath (i.e. it's the example-file.seq) then it's never dirty
        if(isClasspathFile(this.file)) {
            return false;
        }

        if(this.initialText == null && this.currentText != null) {
            return true;
        }

        if(this.initialText != null && this.currentText != null) {
            return !this.initialText.equals(this.currentText);
        }

        // todo improve the above logic...
        throw new IllegalStateException("shouldn't get here");
    }

    public void saveSourceFile(File file) {
        try (PrintWriter out = new PrintWriter(file,"UTF-8")) {
            log.info("Opened file [" + file + "] for writing");
            out.print(this.currentText);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // update document state
        this.file = file;
        this.initialText = this.currentText;

        // trigger a refresh
        this.textChangedListener.onTextChanged(this.currentText);
    }

    public String getCurrentText() {
        return this.currentText;
    }

    public boolean updateText(String text) {
        if(this.currentText.equals(text)) {
            // no change
            return false;
        } else {
            this.currentText = text;
            this.textChangedListener.onTextChanged(this.currentText);
            return true;
        }
    }
}
