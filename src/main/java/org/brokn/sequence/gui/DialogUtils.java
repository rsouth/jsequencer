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

import org.brokn.sequence.rendering.Canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.*;

public class DialogUtils {

    private static final Logger log = Logger.getLogger(DialogUtils.class.getName());

    static class FileDialogResult {

        private final boolean okToProceed;
        private final File file;

        FileDialogResult(boolean okToProceed, File file) {
            this.okToProceed = okToProceed;
            this.file = file;
        }

        boolean isOkToProceed() {
            return okToProceed;
        }

        File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return "FileDialogResult{" +
                    "okToProceed=" + okToProceed +
                    ", file=" + file +
                    '}';
        }

    }

    static DialogUtils.FileDialogResult openOpenFileDialog(FileNameExtensionFilter fileFilter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        fileChooser.changeToParentDirectory();
        fileChooser.setMultiSelectionEnabled(false);

        log.info("Showing Open File dialog");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists() && selectedFile.getPath().endsWith(".seq")) {
                log.info("User opening .seq file [" + selectedFile.getPath() + "]");
                return new FileDialogResult(true, selectedFile);
            } else {
                log.warning("User tried to open file [" + selectedFile + "] but it does not exist or is not a .seq file");
                return new FileDialogResult(false, null);
            }
        } else {
            log.info("User cancelled file open action");
            return new FileDialogResult(false, null);
        }
    }

    static FileDialogResult openSaveAsDialog(FileNameExtensionFilter fileFilter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        fileChooser.changeToParentDirectory();
        fileChooser.setMultiSelectionEnabled(false);

        log.info("Showing save file dialog with filter [" + fileFilter + "]");
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().exists()) {
                int input = JOptionPane.showConfirmDialog(null, "File already exists, overwrite?", "BUUZZZZZ...", YES_NO_OPTION);
                switch (input) {
                    case YES_OPTION:
                        log.info("User chose to save to filename [" + fileChooser.getSelectedFile().getPath() + "]");
                        return new FileDialogResult(true, fileChooser.getSelectedFile());

                    case NO_OPTION:
                    default:
                        log.info("User chose not to overwrite existing file [" + fileChooser.getSelectedFile().getPath() + "]");
                        return new FileDialogResult(false, null);
                }


            } else {

                if (fileChooser.getSelectedFile().getPath().endsWith(fileFilter.getExtensions()[0])) {
                    log.info("User saving in new file " + fileChooser.getSelectedFile().getPath());
                    return new FileDialogResult(true, fileChooser.getSelectedFile());

                } else {
                    File file = new File(fileChooser.getSelectedFile().getPath() + "." + fileFilter.getExtensions()[0]);
                    log.info("User saving in new file " + file.getPath());
                    return new FileDialogResult(true, file);
                }

            }
        } else {
            log.info("User cancelled file save action");
            return new FileDialogResult(false, null);
        }
    }

    static boolean isValidClip(Dimension clip) {
        if (clip.width <= 0 || clip.height <= 0) {
            log.severe("Clip area too small to create image " + clip);
            showMessageDialog(null, "Clip area is too small to create Image", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    static void copyToClipboard(Canvas canvas) {
        Dimension clip = canvas.getPreferredSize();
        log.info("Copy to clipboard, dims: " + clip);

        if(isValidClip(clip)) {
            BufferedImage bImg = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D cg = bImg.createGraphics();
            canvas.paintAll(cg);

            TransferableImage transferableImage = new TransferableImage(bImg);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            clipboard.setContents(transferableImage, null);
        }
    }

    /**
     * Export the current canvas as an image file.
     *
     * @param selectedFile
     */
    public static void exportAsImage(File selectedFile, Canvas canvas) {
        Dimension clip = canvas.getPreferredSize();
        log.info("Export to file, dims: " + clip);
        if (isValidClip(clip)) {
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
    }

}
