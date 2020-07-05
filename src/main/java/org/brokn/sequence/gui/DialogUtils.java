package org.brokn.sequence.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.*;

public class DialogUtils {

    private static final Logger log = Logger.getLogger(DialogUtils.class.getName());

    static class FileDialogResult {

        private final boolean okToProceed;
        private final File file;

        public FileDialogResult(boolean okToProceed, File file) {
            this.okToProceed = okToProceed;
            this.file = file;
        }

        public boolean isOkToProceed() {
            return okToProceed;
        }

        public File getFile() {
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


    public static DialogUtils.FileDialogResult openOpenFileDialog(FileNameExtensionFilter fileFilter) {
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


    public static FileDialogResult openSaveAsDialog(FileNameExtensionFilter fileFilter) {
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
                    File file = new File(fileChooser.getSelectedFile().getPath() + fileFilter.getExtensions()[0]);
                    log.info("User saving in new file " + file.getPath());
                    return new FileDialogResult(true, file);
                }

            }
        } else {
            log.info("User cancelled file save action");
            return new FileDialogResult(false, null);
        }
    }

}
