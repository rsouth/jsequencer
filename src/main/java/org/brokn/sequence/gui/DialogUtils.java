package org.brokn.sequence.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import static javax.swing.JOptionPane.*;

public class DialogUtils {

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
    }


    public static DialogUtils.FileDialogResult openOpenFileDialog(FileNameExtensionFilter fileFilter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        fileChooser.changeToParentDirectory();
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().exists()) {
                System.out.println("User chose to save to filename " + fileChooser.getSelectedFile().getPath());
                return new FileDialogResult(true, fileChooser.getSelectedFile());
            } else {
                return new FileDialogResult(false, null);
            }
        }

        // todo refactor the logic in this method...
        System.err.println("The logic in this method SUCKS. Theoretically we shouldn't reach this");
        throw new IllegalStateException("Neither successfully not unsuccessfully saved, fix this logic!!!!!");
    }


    public static FileDialogResult openSaveAsDialog(FileNameExtensionFilter fileFilter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        fileChooser.changeToParentDirectory();
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().exists()) {
                int input = JOptionPane.showConfirmDialog(null, "File already exists, overwrite?", "BUUZZZZZ...", YES_NO_OPTION);
                switch (input) {
                    case YES_OPTION:
                        System.out.println("User chose to save to filename " + fileChooser.getSelectedFile().getPath());
                        return new FileDialogResult(true, fileChooser.getSelectedFile());

                    case NO_OPTION:
                        System.out.println("User chose not to overwrite existing file " + fileChooser.getSelectedFile().getPath());
                        return new FileDialogResult(false, null);

                    default:
                        System.err.println("wat.");
                        return new FileDialogResult(false, null);
                }


            } else {

                if (fileChooser.getSelectedFile().getPath().endsWith(fileFilter.getExtensions()[0])) {
                    System.out.println("User saving in new file " + fileChooser.getSelectedFile().getPath());
                    return new FileDialogResult(true, fileChooser.getSelectedFile());

                } else {
                    File file = new File(fileChooser.getSelectedFile().getPath() + fileFilter.getExtensions()[0]);
                    System.out.println("User saving in new file " + file.getPath());
                    return new FileDialogResult(true, file);
                }

            }
        }

        // todo refactor the logic in this method...
        System.err.println("The logic in this method SUCKS. Theoretically we shouldn't reach this");
        throw new IllegalStateException("Neither successfully not unsuccessfully saved, fix this logic!!!!!");
    }

}
