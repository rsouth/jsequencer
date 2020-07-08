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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.lexer.parser.MetaDataParser;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.*;

public class SequenceDialog extends JFrame {

    private static final Logger log = Logger.getLogger(SequenceDialog.class.getName());

    static {
        try {
            // initialise logging properties
            InputStream stream = ClassLoader.getSystemResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private JPanel contentPane;
    private JButton buttonExport;
    private JTextArea textArea1;
    private JPanel canvasContainer;
    private JButton buttonSave;
    private JButton buttonOpen;
    private JSlider scaleSlider;
    private JTabbedPane tabContainer;
    private JButton buttonExampleFile;
    private JButton buttonCopyToClipboard;
    private JSplitPane splitPane;
    private JButton buttonNewFile;
    private JPanel statusBarPanel;

    private DocumentState documentState = new DocumentState();

    private final Lexer lexer = new Lexer();

    public SequenceDialog() {
        super("Sequencer");
        $$$setupUI$$$();
        setLocationByPlatform(true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonExport);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Close action
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (dirtyFileCheck() != CANCEL_OPTION) {
                    System.exit(0);
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                SwingUtilities.invokeLater(() -> {
                    int splitPaneWidth = splitPane.getWidth();
                    double dividerLocation = (splitPaneWidth / 4.) / 1000;
                    log.info("JSplitPane width [" + splitPaneWidth + "], setting divider location to [" + dividerLocation + "]");
                    splitPane.setDividerLocation(dividerLocation);
                });
            }
        });

        // button callbacks
        buttonExport.addActionListener(e -> onExport());
        buttonSave.addActionListener(e -> onSaveAs());
        buttonOpen.addActionListener(e -> openFile());
        buttonExampleFile.addActionListener(e -> openExampleFile());
        buttonCopyToClipboard.addActionListener(e -> onCopyToClipboard());
        buttonNewFile.addActionListener(e -> onNewFile());

        // scale slider callback
        scaleSlider.addChangeListener(e -> ((Canvas) canvasContainer).updateScale(((JSlider) e.getSource()).getValue()));

        // listen for keystrokes to kick off updating the diagram
        textArea1.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (documentState.updateText(textArea1.getText())) {
                    triggerModelUpdate();
                }
            }

        });
    }

    /**
     * Handle 'save source' button click
     */
    private void onSaveAs() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pls use .seq extension...", "seq");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            saveFile(fileDialogResult.getFile());
        }
    }

    private void saveFile(File file) {
        this.documentState.saveSourceFile(file, this.textArea1.getText());
        triggerModelUpdate();
    }

    /**
     * Handle 'export image' button click
     */
    private void onExport() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pls use .png extension...", "png");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            this.documentState.exportAsImage(fileDialogResult.getFile(), this.canvasContainer);
        }
    }

    /**
     * Handle 'copy to clipboard' button click
     */
    private void onCopyToClipboard() {
        Dimension clip = canvasContainer.getPreferredSize();
        log.info("Copy to clipboard, dims: " + clip);

        BufferedImage bImg = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        canvasContainer.paintAll(cg);

        TransferableImage transferableImage = new TransferableImage(bImg);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(transferableImage, null);
    }

    /**
     * Show Open File dialog and if the user selects a file, replace the current document
     */
    private void openFile() {
        if (dirtyFileCheck() == CANCEL_OPTION) {
            return;
        }

        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("SEQ Files", "seq");
        DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
        if (openFileDialogResult.isOkToProceed()) {
            File file = openFileDialogResult.getFile();
            try (FileReader fileReader = new FileReader(file)) {
                log.info("Opening file [" + file + "] for reading");
                replaceDocument(file, fileReader);
            } catch (IOException e) {
                log.severe("Exception occurred while opening file [" + file + "]");
                e.printStackTrace();
            }
        }
    }


    private void onNewFile() {
        if (dirtyFileCheck() == CANCEL_OPTION) {
            return;
        }

        replaceDocument(null, null);
    }

    private void openExampleFile() {
        if (dirtyFileCheck() == CANCEL_OPTION) {
            return;
        }

        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("example-file.seq");
        if (systemResourceAsStream != null) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(systemResourceAsStream)) {
                replaceDocument(null, inputStreamReader);
            } catch (IOException e) {
                log.severe("Exception occurred while opening example file");
                e.printStackTrace();
            }
        }
    }

    private void replaceDocument(File file, Reader inputReader) {
        List<String> lines = new ArrayList<>();
        if (inputReader != null) {
            try (BufferedReader reader = new BufferedReader(inputReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

            } catch (IOException e) {
                // todo fix this logging - show filename? something else? just exception msg??
                if (file == null) {
                    log.severe("An exception occured while loading file from classpath: " + e.getMessage());
                } else {
                    log.severe("An exception occurred while reading file []");
                }
                e.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            // update document state
            this.documentState = new DocumentState(file, String.join("\n", lines));

            // update filename in tab
            this.tabContainer.setTitleAt(0, file == null ? "Untitled" : file.getName());

            // update text area
            this.textArea1.setText(documentState.getCurrentText());
            log.info("Updated document with contents of file [" + file + "]");

            // trigger a refresh of the diagram
            triggerModelUpdate();
        });
    }

    /**
     * Check the state of the source vs the last time it was saved.
     * If the state is dirty, ask the user if they want to save.
     * If yes:
     * and the file was previously saved, save.
     * and the file was not previously saved, open a save file dialog.
     *
     * @return
     */
    private int dirtyFileCheck() {
        if (this.documentState.isDirty()) {
            int option = JOptionPane.showConfirmDialog(null, "Text has been changed, save it?", "Suuuure?", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE);
            if (option == YES_OPTION) {
                if (documentState.getFile() == null) {
                    // open the save dialog
                    onSaveAs();

                } else {
                    // just save again to the same file
                    this.documentState.saveSourceFile(documentState.getFile(), this.textArea1.getText());
                }
            }
            return option;
        } else {
            // todo check if this is the right thing to do
            return NO_OPTION;
        }
    }

    private void triggerModelUpdate() {
        String text = documentState.getCurrentText();

        SwingUtilities.invokeLater(() -> {
            String currentTitle = tabContainer.getTitleAt(0);
            if (documentState.isDirty()) {
                // update tab title
                if (!currentTitle.endsWith("*")) {
                    tabContainer.setTitleAt(0, currentTitle + " *");
                }
            } else {
                if (currentTitle.endsWith("*")) {
                    String rawTitle = currentTitle.replace(" *", "");
                    tabContainer.setTitleAt(0, rawTitle);
                }
            }

            // update the canvas model
            RenderableGraph model = lexer.parse(text);
            ((Canvas) canvasContainer).updateModel(model);
        });
    }

    private void addTokenToSource(String token, String param) {
        List<String> lines = new ArrayList<>(Arrays.asList(this.textArea1.getText().split("\n")));

        int idx = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(token)) {
                idx = i;
            }
        }

        if (idx < 0) {
            lines.add(0, token + param);
        } else {
            lines.remove(idx);
            lines.add(idx, token + param);
        }

        this.textArea1.setText(String.join("\n", lines));
        this.documentState.updateText(this.textArea1.getText());
        triggerModelUpdate();
    }

    private void createUIComponents() {
        this.contentPane = new JPanel();

        this.canvasContainer = new Canvas();
        this.canvasContainer.setBackground(Color.WHITE);

        try {
            int buttonW = 100, buttonH = 60;
            int iconW = 40, iconH = 40;

            // Save source button
            BufferedImage saveFileIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/save-file.png"));
            Image saveFileIconScaled = saveFileIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonSave = new JButton("Save Source", new ImageIcon(saveFileIconScaled));
            this.buttonSave.setSize(buttonW, buttonH);

            // Export image button
            BufferedImage exportImageIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/export-image.png"));
            Image exportImageIconScaled = exportImageIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonExport = new JButton("Export Image", new ImageIcon(exportImageIconScaled));
            this.buttonExport.setSize(buttonW, buttonH);

            // Open file button
            BufferedImage openFileIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/open-file.png"));
            Image openFileIconScaled = openFileIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonOpen = new JButton("Open", new ImageIcon(openFileIconScaled));
            this.buttonOpen.setSize(buttonW, buttonH);

            // Example file button
            BufferedImage exampleFileIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/example-file.png"));
            Image exampleFileIconScaled = exampleFileIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonExampleFile = new JButton("Example File", new ImageIcon(exampleFileIconScaled));
            this.buttonExampleFile.setSize(buttonW, buttonH);

            // Copy to Clipboard button
            BufferedImage copyToClipboardIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/copy-to-clipboard.png"));
            Image copyToClipboardIconScaled = copyToClipboardIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonCopyToClipboard = new JButton("Example File", new ImageIcon(copyToClipboardIconScaled));
            this.buttonCopyToClipboard.setSize(buttonW, buttonH);

            // New File button
            BufferedImage newFileIconRaw = ImageIO.read(ClassLoader.getSystemResource("icons/new-file.png"));
            Image newFileIconScaled = newFileIconRaw.getScaledInstance(iconW, iconH, Image.SCALE_SMOOTH);
            this.buttonNewFile = new JButton("Example File", new ImageIcon(newFileIconScaled));
            this.buttonNewFile.setSize(buttonW, buttonH);

            // Menu Bar
            MenuBar newMenuBar = new MenuBar(onFileMenuItemClicked, onDiagramMenuItemClicked, onHelpMenuItemClicked);
            this.setJMenuBar(newMenuBar);

            // Status bar
            this.statusBarPanel = new SeqStatusBar(contentPane, e -> onExport(), e -> onCopyToClipboard());
            this.contentPane.add(statusBarPanel, BorderLayout.SOUTH);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SequenceDialog dialog = new SequenceDialog();
        dialog.pack();

        // set initial focus to the text area
        dialog.textArea1.requestFocusInWindow();

        // show the window.
        dialog.setVisible(true);
    }

    private final AbstractAction onFileMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                log.info("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.FILE_NEW:
                        onNewFile();
                        break;

                    case MenuBar.FILE_OPEN:
                        openFile();
                        break;

                    case MenuBar.FILE_SAVE:
                        if (documentState.getFile() == null) {
                            onSaveAs();
                        } else {
                            saveFile(documentState.getFile());
                        }
                        break;

                    case MenuBar.FILE_SAVE_AS:
                        onSaveAs();
                        break;
                }
            }
        }
    };

    private final AbstractAction onDiagramMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                log.info("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.DIAGRAM_ADD_TITLE:
                        String title = showInputDialog(null, "Enter Title:", "Title", QUESTION_MESSAGE);
                        addTokenToSource(MetaDataParser.TITLE_TOKEN, title);
                        break;

                    case MenuBar.DIAGRAM_ADD_AUTHOR:
                        String authorName = showInputDialog(null, "Enter Author Name:", "Author Name", QUESTION_MESSAGE);
                        addTokenToSource(MetaDataParser.AUTHOR_TOKEN, authorName);
                        break;

                    case MenuBar.DIAGRAM_ADD_DATE:
                        addTokenToSource(MetaDataParser.DATE_TOKEN, "");
                        break;

                    case MenuBar.DIAGRAM_COPY_TO_CLIPBOARD:
                        onCopyToClipboard();
                        break;

                    case MenuBar.DIAGRAM_EXPORT_AS:
                        onExport();
                        break;

                }
            }
        }
    };

    private final AbstractAction onHelpMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                log.info("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.HELP_GRAMMAR:
                        String title = showInputDialog(null, "Enter Title:", "Title", QUESTION_MESSAGE);
                        addTokenToSource(MetaDataParser.TITLE_TOKEN, title);
                        break;

                    case MenuBar.HELP_ABOUT:
                        JOptionPane.showMessageDialog(null, "github.com/rsouth/sequencer", "sequencer", INFORMATION_MESSAGE);
                        break;

                    case MenuBar.HELP_EXAMPLE_FILE:
                        openExampleFile();
                        break;
                }
            }
        }
    };

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(1200, 900));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.CENTER);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setPreferredSize(new Dimension(300, 60));
        toolBar1.setVisible(false);
        panel2.add(toolBar1, BorderLayout.NORTH);
        buttonNewFile.setHorizontalTextPosition(0);
        buttonNewFile.setText("New");
        buttonNewFile.setVerticalTextPosition(3);
        toolBar1.add(buttonNewFile);
        buttonOpen.setActionCommand("Open");
        buttonOpen.setHorizontalTextPosition(0);
        buttonOpen.setPreferredSize(new Dimension(100, 50));
        buttonOpen.setText("Open");
        buttonOpen.setVerticalTextPosition(3);
        toolBar1.add(buttonOpen);
        buttonSave.setHorizontalTextPosition(0);
        buttonSave.setPreferredSize(new Dimension(100, 50));
        buttonSave.setText("Save Source");
        buttonSave.setVerticalTextPosition(3);
        toolBar1.add(buttonSave);
        buttonExport.setActionCommand("Export");
        buttonExport.setHorizontalTextPosition(0);
        buttonExport.setPreferredSize(new Dimension(100, 50));
        buttonExport.setText("Export .png");
        buttonExport.setVerticalTextPosition(3);
        toolBar1.add(buttonExport);
        buttonCopyToClipboard.setHorizontalTextPosition(0);
        buttonCopyToClipboard.setText("To Clipboard");
        buttonCopyToClipboard.setVerticalTextPosition(3);
        toolBar1.add(buttonCopyToClipboard);
        scaleSlider = new JSlider();
        scaleSlider.setMaximum(10);
        scaleSlider.setMinimum(-10);
        scaleSlider.setMinorTickSpacing(1);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setValue(1);
        toolBar1.add(scaleSlider);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        buttonExampleFile.setText("Example File");
        toolBar1.add(buttonExampleFile);
        tabContainer = new JTabbedPane();
        panel2.add(tabContainer, BorderLayout.CENTER);
        splitPane = new JSplitPane();
        tabContainer.addTab("Untitled", splitPane);
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane1);
        scrollPane1.setViewportView(canvasContainer);
        final JScrollPane scrollPane2 = new JScrollPane();
        splitPane.setLeftComponent(scrollPane2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane2.setViewportView(panel3);
        textArea1 = new JTextArea();
        textArea1.setFocusCycleRoot(true);
        Font textArea1Font = this.$$$getFont$$$("Courier New", Font.PLAIN, 12, textArea1.getFont());
        if (textArea1Font != null) textArea1.setFont(textArea1Font);
        panel3.add(textArea1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        contentPane.add(statusBarPanel, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
