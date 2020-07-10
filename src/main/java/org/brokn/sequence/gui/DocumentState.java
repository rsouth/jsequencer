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

import com.google.common.flogger.FluentLogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.brokn.sequence.gui.GuiUtils.replaceTokenAtLine;

public class DocumentState {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private File file;
    private String initialText;
    private String currentText = "";
    private final TextChangedListener textChangedListener;

    DocumentState(TextChangedListener listener, File file, String initialText) {
        assert initialText != null;
        this.textChangedListener = listener;
        this.file = file;
        this.initialText = initialText;
        this.currentText = initialText;
        this.textChangedListener.onTextChanged(this.initialText);
    }

    DocumentState(TextChangedListener listener) {
        this(listener, null, "");
    }

    File getFile() {
        return this.file;
    }

    boolean isDirty() {
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

    void saveSourceFile(File file) {
        try (PrintWriter out = new PrintWriter(file,"UTF-8")) {
            logger.atInfo().log("Opened file [" + file + "] for writing");
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

    String getCurrentText() {
        return this.currentText;
    }

    boolean updateText(String text) {
        if(this.currentText.equals(text)) {
            // no change
            return false;
        } else {
            this.currentText = text;
            this.textChangedListener.onTextChanged(this.currentText);
            return true;
        }
    }

    void addTokenToSource(String token, String param) {
        List<String> lines = new ArrayList<>(Arrays.asList(this.currentText.split("\n")));

        // Find line index containing the token currently.
        // There may, erroneously, be more than one. In that case we will take only the first - we'll remove the rest.
        int tokenLineIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(token)) {
                tokenLineIndex = i;
                break;
            }
        }

        // replace the first existing token, if any, and remove all others
        replaceTokenAtLine(token + param, lines, tokenLineIndex);

        // update the document
        this.updateText(String.join("\n", lines));
    }

    private boolean isClasspathFile(File file) {
        return this.file != null && ClassLoader.getSystemResource(file.getName()) != null;
    }

}
