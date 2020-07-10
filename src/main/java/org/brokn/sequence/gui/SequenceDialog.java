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

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.brokn.sequence.cli.HeadlessCli;
import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.lexer.parser.MetaDataParser;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableDiagram;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.*;
import static org.brokn.sequence.gui.DialogUtils.copyToClipboard;
import static org.brokn.sequence.gui.DialogUtils.exportAsImage;

public class SequenceDialog extends JFrame implements TextChangedListener {

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

    private static final Logger log = Logger.getLogger(SequenceDialog.class.getName());

    private JPanel contentPane;
    private JTextArea textArea1;
    private JPanel canvasContainer;
    private JTabbedPane tabContainer;
    private JSplitPane splitPane;
    private JPanel statusBarPanel;

    private DocumentState documentState;

    private final Lexer lexer = new Lexer();

    public SequenceDialog() {
        super("Sequencer");
        $$$setupUI$$$();
        setLocationByPlatform(true);
        setContentPane(contentPane);

        this.documentState = new DocumentState(this);

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
     * Show Open File dialog and if the user selects a file, replace the current document
     */
    private void openFile() {
        if (dirtyFileCheck() != CANCEL_OPTION) {
            FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Sequencer SEQ Files", "seq");
            DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
            if (openFileDialogResult.isOkToProceed()) {
                try {
                    File file = openFileDialogResult.getFile();
                    log.info("Opening file [" + file + "] for reading");
                    //noinspection UnstableApiUsage
                    List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);
                    replaceDocument(file, lines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Opens example-file.seq from resources
     */
    private void openExampleFile() {
        if (dirtyFileCheck() != CANCEL_OPTION) {
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("example-file.seq");
            if (systemResourceAsStream != null) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(systemResourceAsStream)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
                        replaceDocument(null, lines);
                    }
                } catch (IOException e) {
                    log.severe("Exception occurred while opening example file");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Replace the current document with the parameters
     *
     * @param file
     * @param lines
     */
    private void replaceDocument(File file, List<String> lines) {
        SwingUtilities.invokeLater(() -> {
            // update document state
            this.documentState = new DocumentState(SequenceDialog.this, file, String.join("\n", lines));

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
                    this.documentState.saveSourceFile(documentState.getFile());
                }
            }
            return option;
        }

        return NO_OPTION;
    }

    private void triggerModelUpdate() {
        SwingUtilities.invokeLater(() -> {
            // update the canvas model, which triggers a re-paint
            String text = documentState.getCurrentText();
            RenderableDiagram model = lexer.parse(text);
            ((Canvas) canvasContainer).updateModel(model);
        });
    }

    @Override
    public void onTextChanged(String newText) {
        SwingUtilities.invokeLater(() -> {
            final File file = documentState.getFile();
            String currentTitle = file == null ? "Untitled" : file.getName();
            if (documentState.isDirty()) {
                // suffix tab title with *
                tabContainer.setTitleAt(0, currentTitle + " *");
            } else {
                tabContainer.setTitleAt(0, currentTitle);
            }

            this.textArea1.setText(newText);
            triggerModelUpdate();
        });
    }

    /**
     * Handle 'New File' action
     */
    private void onNewFile() {
        if (dirtyFileCheck() != CANCEL_OPTION) {
            replaceDocument(null, Lists.newArrayList());
        }
    }

    /**
     * Handle 'export image' action
     */
    private void onExport() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            exportAsImage(fileDialogResult.getFile(), (Canvas) this.canvasContainer);
        }
    }

    /**
     * Handle 'save source' action
     */
    private void onSaveAs() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("sequencer files (*.seq)", "seq");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            this.documentState.saveSourceFile(fileDialogResult.getFile());
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (args.length > 1) {
            System.out.println("use cli");
            new HeadlessCli().run(args);
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SequenceDialog dialog = new SequenceDialog();
            dialog.pack();

            // set initial focus to the text area
            dialog.textArea1.requestFocusInWindow();

            // show the window.
            dialog.setVisible(true);
        }
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
                            documentState.saveSourceFile(documentState.getFile());
                        }
                        break;

                    case MenuBar.FILE_SAVE_AS:
                        onSaveAs();
                        break;

                    default:
                        log.warning("File Menu: Unknown option selected");
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
                        documentState.addTokenToSource(MetaDataParser.TITLE_TOKEN, title);
                        break;

                    case MenuBar.DIAGRAM_ADD_AUTHOR:
                        String authorName = showInputDialog(null, "Enter Author Name:", "Author Name", QUESTION_MESSAGE);
                        documentState.addTokenToSource(MetaDataParser.AUTHOR_TOKEN, authorName);
                        break;

                    case MenuBar.DIAGRAM_ADD_DATE:
                        documentState.addTokenToSource(MetaDataParser.DATE_TOKEN, "");
                        break;

                    case MenuBar.DIAGRAM_COPY_TO_CLIPBOARD:
                        copyToClipboard((Canvas) canvasContainer);
                        break;

                    case MenuBar.DIAGRAM_EXPORT_AS:
                        onExport();
                        break;

                    default:
                        log.warning("Diagram Menu: Unknown option selected");

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
                        showMessageDialog(null, "todo", "Grammar", INFORMATION_MESSAGE);
                        break;

                    case MenuBar.HELP_ABOUT:
                        showMessageDialog(null, "github.com/rsouth/sequencer", "sequencer", INFORMATION_MESSAGE);
                        break;

                    case MenuBar.HELP_EXAMPLE_FILE:
                        openExampleFile();
                        break;

                    default:
                        log.warning("Help Menu: Unknown option selected");
                }
            }
        }
    };

    private void createUIComponents() {
        this.contentPane = new JPanel();
        this.canvasContainer = new Canvas();
        this.canvasContainer.setIgnoreRepaint(true);
        this.canvasContainer.setBackground(Color.WHITE);

        // Menu Bar
        MenuBar newMenuBar = new MenuBar(onFileMenuItemClicked, onDiagramMenuItemClicked, onHelpMenuItemClicked);
        this.setJMenuBar(newMenuBar);

        // Status bar
        this.statusBarPanel = new SeqStatusBar(contentPane, e -> onExport(), e -> copyToClipboard((Canvas) canvasContainer));
        this.contentPane.add(statusBarPanel, BorderLayout.SOUTH);
    }

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
