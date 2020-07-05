package org.brokn.sequence.gui;

import org.brokn.sequence.lexer.parser.InteractionParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class DocumentState {

    private static final Logger log = Logger.getLogger(DocumentState.class.getName());

    private File file;
    private String text;

    public DocumentState(File file, String text) {
        assert text != null;

        this.file = file;
        this.text = text;
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

    public boolean isDirty(String currentState) {
        // if the file is found on the classpath (i.e. it's the example-file.seq) then it's never dirty
        if(isClasspathFile(this.file)) {
            return false;
        }

        if(this.text == null && currentState != null) {
            return true;
        }

        if(this.text != null && currentState != null) {
            return !this.text.equals(currentState);
        }

        // todo improve the above logic...
        throw new IllegalStateException("shouldn't get here");
    }

    public void saveSourceFile(File file, String text) {
        try {
            try (PrintWriter out = new PrintWriter(file)) {
                log.info("Opened file [" + file + "] for writing");
                out.print(text);

                // update document state
                this.file = file;
                this.text = text;
            }
        } catch (FileNotFoundException e) {
            log.severe("Failed to save source to file");
            e.printStackTrace();
        }
    }

    /**
     * Export the current canvas as an image file.
     *
     * @param selectedFile
     */
    public void exportAsImage(File selectedFile, JPanel canvas) {
        BufferedImage bImg = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
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

}
