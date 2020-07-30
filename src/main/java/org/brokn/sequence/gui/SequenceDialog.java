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
import com.intellij.uiDesigner.core.Spacer;
import org.brokn.sequence.cli.HeadlessCli;
import org.brokn.sequence.lexer.parser.MetaDataParser;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.logging.LogManager;

import static javax.swing.JOptionPane.*;
import static org.brokn.sequence.gui.DialogUtils.copyToClipboard;
import static org.brokn.sequence.gui.DialogUtils.exportAsImage;

public class SequenceDialog extends JFrame {

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

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private JPanel contentPane;
    private JTabbedPane tabContainer;
    private JPanel statusBarPanel;
    private JButton newFileButton;
    private JToolBar toolBar;
    private JButton openButton;
    private JButton closeButton;
    private JButton saveButton;
    private JButton clipboardButton;
    private JButton exportButton;
    private JButton exampleButton;

    public SequenceDialog() {
        super("Sequencer");
        $$$setupUI$$$();
        setLocationByPlatform(true);
        setContentPane(contentPane);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Close action
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                for (int i = 0; i < tabContainer.getTabCount(); i++) {
                    tabContainer.setSelectedIndex(i);
                    if (dirtyFileCheck((TabDocument) tabContainer.getComponentAt(i)) == CANCEL_OPTION) {
                        return;
                    }
                }
                System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);

                // Add a default tab
                addTab(null);
            }
        });

        // todo temp 'new' button, give it an icon, rename etc...
        newFileButton.addActionListener(e -> onNewFile());
        openButton.addActionListener(e -> openFile());
        closeButton.addActionListener(e -> onCloseFile());
        saveButton.addActionListener(e -> {
            if (getActiveTab().getDocumentState().getFile() == null) {
                onSaveAs();
            } else {
                getActiveTab().getDocumentState().saveSourceFile(getActiveTab().getDocumentState().getFile());
            }
        });
        clipboardButton.addActionListener(e -> copyToClipboard(getActiveTab().getCanvas()));
        exportButton.addActionListener(e -> onExport());
        exampleButton.addActionListener(e -> openExampleFile());
    }

    /**
     * Show Open File dialog and if the user selects a file, replace the current document
     */
    private void openFile() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Sequencer SEQ Files", "seq");
        DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
        if (openFileDialogResult.isOkToProceed()) {
            File file = openFileDialogResult.getFile();
            logger.atInfo().log("Opening file [" + file + "] for reading");
            if (getActiveTab().getDocumentState().getFile() == null && !getActiveTab().getDocumentState().isDirty()) {
                getActiveTab().replaceDocument(file);
            } else {
                addTab(file);
            }
        }
    }

    /**
     * Show example file context, demonstrating all features
     */
    private void openExampleFile() {
        addTab(null);
        getActiveTab().loadExampleContext();
    }


    private void onCloseFile() {
        if (dirtyFileCheck(getActiveTab()) != CANCEL_OPTION) {
            tabContainer.remove(getActiveTab());
        }
        if (tabContainer.getTabCount() == 0) {
            addTab(null);
        }
    }

    private void addTab(@Nullable File file) {
        TabDocument newTab;
        if (file != null) {
            newTab = new TabDocument(file);
            this.tabContainer.addTab("", newTab);

        } else {
            newTab = new TabDocument();
            this.tabContainer.addTab("Untitled", newTab);
        }

        // Add tab title component
        final int index = tabContainer.indexOfComponent(newTab);
        tabContainer.setTabComponentAt(index, newTab.getTabDocumentTitle());
        ((TabDocumentTitle) newTab.getTabDocumentTitle()).addCloseButtonListener(e -> onCloseFile());

        // Set previous tab state to inactive
        int prevIndex = tabContainer.getSelectedIndex();
        ((TabDocumentTitle) tabContainer.getTabComponentAt(prevIndex)).setActiveTab(false);
        // Set new tab to active
        ((TabDocumentTitle) newTab.getTabDocumentTitle()).setActiveTab(true);
        tabContainer.setSelectedComponent(newTab);
        getActiveTab().transferFocus();

        // Listener for tab click events, to switch tabs
        ((TabDocumentTitle) newTab.getTabDocumentTitle()).addTabClickListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // deactivate the previous tab
                final int prevIndex = tabContainer.getSelectedIndex();
                ((TabDocumentTitle) tabContainer.getTabComponentAt(prevIndex)).setActiveTab(false);

                // activate the newly selected tab
                final int clickedTabIndex = tabContainer.indexOfTabComponent(e.getComponent().getParent().getParent());
                ((TabDocumentTitle) tabContainer.getTabComponentAt(clickedTabIndex)).setActiveTab(true);
                tabContainer.setSelectedIndex(clickedTabIndex);
            }
        });

    }

    private TabDocument getActiveTab() {
        return (TabDocument) this.tabContainer.getSelectedComponent();
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
    private int dirtyFileCheck(TabDocument tabDocument) {
        if (tabDocument.getDocumentState().isDirty()) {
            int option = JOptionPane.showConfirmDialog(null, "Text has been changed, save it?", "Suuuure?", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE);
            if (option == YES_OPTION) {
                if (this.getActiveTab().getDocumentState().getFile() == null) {
                    // open the save dialog
                    onSaveAs();

                } else {
                    // just save again to the same file
                    this.getActiveTab().getDocumentState().saveSourceFile(this.getActiveTab().getDocumentState().getFile());
                }
            }
            return option;
        }

        return NO_OPTION;
    }

    /**
     * Handle 'New File' action
     */
    private void onNewFile() {
        if (getActiveTab().getDocumentState().getFile() == null && !getActiveTab().getDocumentState().isDirty()) {
            getActiveTab().resetDocument();
        } else {
            addTab(null);
        }
    }

    /**
     * Handle 'export image' action
     */
    private void onExport() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            exportAsImage(fileDialogResult.getFile(), this.getActiveTab().getCanvas());
        }
    }

    /**
     * Handle 'save source' action
     */
    private void onSaveAs() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("sequencer files (*.seq)", "seq");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            this.getActiveTab().getDocumentState().saveSourceFile(fileDialogResult.getFile());
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (args.length > 1) {
            logger.atInfo().log("use cli");
            new HeadlessCli().run(args);
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SequenceDialog dialog = new SequenceDialog();
            dialog.pack();

            // show the window.
            dialog.setVisible(true);
        }
    }

    private final AbstractAction onFileMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                logger.atInfo().log("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.FILE_SAVE -> {
                        if (getActiveTab().getDocumentState().getFile() == null) {
                            onSaveAs();
                        } else {
                            getActiveTab().getDocumentState().saveSourceFile(getActiveTab().getDocumentState().getFile());
                        }
                    }
                    case MenuBar.FILE_NEW -> onNewFile();
                    case MenuBar.FILE_OPEN -> openFile();
                    case MenuBar.FILE_CLOSE -> onCloseFile();
                    case MenuBar.FILE_SAVE_AS -> onSaveAs();
                    default -> logger.atWarning().log("File Menu: Unknown option selected");
                }
            }
        }
    };

    private final AbstractAction onDiagramMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                logger.atInfo().log("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.DIAGRAM_ADD_TITLE -> {
                        String title = showInputDialog(null, "Enter Title:", "Title", QUESTION_MESSAGE);
                        getActiveTab().getDocumentState().addTokenToSource(MetaDataParser.TITLE_TOKEN, title);
                    }
                    case MenuBar.DIAGRAM_ADD_AUTHOR -> {
                        String authorName = showInputDialog(null, "Enter Author Name:", "Author Name", QUESTION_MESSAGE);
                        getActiveTab().getDocumentState().addTokenToSource(MetaDataParser.AUTHOR_TOKEN, authorName);
                    }
                    case MenuBar.DIAGRAM_ADD_DATE -> getActiveTab().getDocumentState().addTokenToSource(MetaDataParser.DATE_TOKEN, "");
                    case MenuBar.DIAGRAM_COPY_TO_CLIPBOARD -> copyToClipboard(getActiveTab().getCanvas());
                    case MenuBar.DIAGRAM_EXPORT_AS -> onExport();
                    default -> logger.atWarning().log("Diagram Menu: Unknown option selected");
                }
            }
        }
    };

    private final AbstractAction onHelpMenuItemClicked = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                String menuItemText = ((JMenuItem) e.getSource()).getText();
                logger.atInfo().log("File menu [" + menuItemText + "] clicked");

                switch (menuItemText) {
                    case MenuBar.HELP_GRAMMAR -> showMessageDialog(null, "todo", "Grammar", INFORMATION_MESSAGE);
                    case MenuBar.HELP_ABOUT -> {
                        AboutDialog aboutDialog = new AboutDialog();
                        aboutDialog.showAbout(SequenceDialog.this);
                    }
                    case MenuBar.HELP_EXAMPLE_FILE -> openExampleFile();
                    default -> logger.atWarning().log("Help Menu: Unknown option selected");
                }
            }
        }
    };

    private void createUIComponents() {
        this.contentPane = new JPanel();
        this.setTitle(MessageFormat.format("Sequencer (java: {0})", System.getProperty("java.version")));

        // Menu Bar
        MenuBar newMenuBar = new MenuBar(onFileMenuItemClicked, onDiagramMenuItemClicked, onHelpMenuItemClicked);
        this.setJMenuBar(newMenuBar);

        // Status bar
        this.statusBarPanel = new SeqStatusBar(e -> onExport(), e -> copyToClipboard(getActiveTab().getCanvas()));
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
        contentPane.add(statusBarPanel, BorderLayout.SOUTH);
        toolBar = new JToolBar();
        toolBar.setVisible(true);
        contentPane.add(toolBar, BorderLayout.NORTH);
        newFileButton = new JButton();
        newFileButton.setBorderPainted(true);
        newFileButton.setContentAreaFilled(true);
        newFileButton.setHideActionText(true);
        newFileButton.setHorizontalTextPosition(0);
        newFileButton.setIcon(new ImageIcon(getClass().getResource("/icons/new-file.png")));
        newFileButton.setIconTextGap(0);
        newFileButton.setInheritsPopupMenu(true);
        newFileButton.setMaximumSize(new Dimension(35, 32));
        newFileButton.setMinimumSize(new Dimension(35, 32));
        newFileButton.setOpaque(true);
        newFileButton.setPreferredSize(new Dimension(40, 40));
        newFileButton.setText("");
        toolBar.add(newFileButton);
        openButton = new JButton();
        openButton.setBorderPainted(true);
        openButton.setContentAreaFilled(true);
        openButton.setHideActionText(true);
        openButton.setHorizontalTextPosition(0);
        openButton.setIcon(new ImageIcon(getClass().getResource("/icons/open-file.png")));
        openButton.setMaximumSize(new Dimension(35, 32));
        openButton.setMinimumSize(new Dimension(35, 32));
        openButton.setPreferredSize(new Dimension(40, 40));
        openButton.setText("");
        toolBar.add(openButton);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar.add(toolBar$Separator1);
        closeButton = new JButton();
        closeButton.setBorderPainted(true);
        closeButton.setContentAreaFilled(true);
        closeButton.setHideActionText(true);
        closeButton.setHorizontalTextPosition(0);
        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/close-file.png")));
        closeButton.setMaximumSize(new Dimension(35, 32));
        closeButton.setMinimumSize(new Dimension(35, 32));
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setText("");
        toolBar.add(closeButton);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        toolBar.add(toolBar$Separator2);
        saveButton = new JButton();
        saveButton.setBorderPainted(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setHideActionText(true);
        saveButton.setHorizontalTextPosition(0);
        saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/save-file.png")));
        saveButton.setMaximumSize(new Dimension(35, 32));
        saveButton.setMinimumSize(new Dimension(35, 32));
        saveButton.setPreferredSize(new Dimension(40, 40));
        saveButton.setText("");
        toolBar.add(saveButton);
        final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
        toolBar.add(toolBar$Separator3);
        clipboardButton = new JButton();
        clipboardButton.setBorderPainted(true);
        clipboardButton.setContentAreaFilled(true);
        clipboardButton.setHideActionText(true);
        clipboardButton.setHorizontalTextPosition(0);
        clipboardButton.setIcon(new ImageIcon(getClass().getResource("/icons/clipboard.png")));
        clipboardButton.setMaximumSize(new Dimension(35, 32));
        clipboardButton.setMinimumSize(new Dimension(35, 32));
        clipboardButton.setPreferredSize(new Dimension(40, 40));
        clipboardButton.setText("");
        toolBar.add(clipboardButton);
        exportButton = new JButton();
        exportButton.setBorderPainted(true);
        exportButton.setContentAreaFilled(true);
        exportButton.setHideActionText(true);
        exportButton.setHorizontalTextPosition(0);
        exportButton.setIcon(new ImageIcon(getClass().getResource("/icons/export-image.png")));
        exportButton.setMaximumSize(new Dimension(35, 32));
        exportButton.setMinimumSize(new Dimension(35, 32));
        exportButton.setPreferredSize(new Dimension(40, 40));
        exportButton.setText("");
        toolBar.add(exportButton);
        final Spacer spacer1 = new Spacer();
        toolBar.add(spacer1);
        exampleButton = new JButton();
        exampleButton.setText("Example");
        toolBar.add(exampleButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
