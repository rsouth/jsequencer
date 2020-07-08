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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.showMessageDialog;

public class DocumentState {

    private static final Logger log = Logger.getLogger(DocumentState.class.getName());

    private File file;
    private String initialText;
    private String currentText = "";

    public DocumentState(File file, String initialText) {
        assert initialText != null;

        this.file = file;
        this.initialText = initialText;
        this.currentText = initialText;
    }

    public DocumentState() {
        this(null, "");
    }

    public File getFile() {
        return this.file;
    }

    private boolean isClasspathFile(File file) {
        if(this.file == null || ClassLoader.getSystemResource(file.getName()) == null) {
            return false;
        } else {
            return true;
        }
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

    public void saveSourceFile(File file, String text) {
        try (PrintWriter out = new PrintWriter(file,"UTF-8")) {
            log.info("Opened file [" + file + "] for writing");
            out.print(text);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // update document state
        this.file = file;
        this.initialText = text;
    }

    /**
     * Export the current canvas as an image file.
     *
     * @param selectedFile
     */
    public void exportAsImage(File selectedFile, JPanel canvas) {
        Dimension clip = canvas.getPreferredSize();
        log.info("Export to file, dims: " + clip);
        if (clip.width <= 0 || clip.height <= 0) {
            log.severe("Cannot export to file; clip size too small");
            showMessageDialog(null, "Cannot export image, clip area is too small", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BufferedImage bImg = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        canvas.paintAll(cg);
        try {
            log.info("Opening file [" + selectedFile + "] for graphics export");
            if (ImageIO.write(bImg, "png", selectedFile)) {
                log.info("Successfully exported diagram to file [" + selectedFile + "]");
            }
        } catch (IOException e) {
            log.severe("Failed to export diagram to file [" + selectedFile + "]");
            e.printStackTrace();
        }
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
            return true;
        }
    }
}
