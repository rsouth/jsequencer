package org.brokn.sequence.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

    private DocumentState documentState = new DocumentState();

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
                if (dirtyFileCheck() != JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }
            }
        });

        // listen for keystrokes to kick off updating the diagram
        textArea1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                super.keyTyped(event);

                SwingUtilities.invokeLater(() -> {
                    String allText = ((JTextArea) event.getSource()).getText();
                    RenderableGraph model = new Lexer().parse(allText);
                    ((Canvas) canvasContainer).updateModel(model);
                });

            }
        });

        // button callbacks
        buttonExport.addActionListener(e -> onExport());
        buttonSave.addActionListener(e -> onSave());
        buttonOpen.addActionListener(e -> openFile());
        buttonExampleFile.addActionListener(e -> openExampleFile());

        // scale slider callback
        scaleSlider.addChangeListener(e -> ((Canvas) canvasContainer).updateScale(((JSlider) e.getSource()).getValue()));
    }

    private int dirtyFileCheck() {
        if (this.documentState.isDirty(this.textArea1.getText())) {
            int option = JOptionPane.showConfirmDialog(null,
                    "Text has changed since last save, save it?", "Suuuure?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            switch (option) {
                case JOptionPane.YES_OPTION:
                    if (documentState.getFile() == null) {
                        // open the save dialog
                        onSave();
                    } else {
                        // just save again to the same file
                        this.documentState.saveSourceFile(documentState.getFile(), this.textArea1.getText());
                    }
                    return option;

                case JOptionPane.NO_OPTION:
                case JOptionPane.CANCEL_OPTION:
                default:
                    // don't save or quit the application
                    // no-op
                    return option;

            }
        } else {
            // todo check if this is the right thing to do
            return JOptionPane.NO_OPTION;
        }
    }

    /**
     * Handle 'save source' button click
     */
    private void onSave() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pls use .seq extension...", "seq");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            this.documentState.saveSourceFile(fileDialogResult.getFile(), this.textArea1.getText());
        }
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
     * Show Open File dialog and if the user selects a file, replace the current document
     */
    private void openFile() {
        if (dirtyFileCheck() == JOptionPane.CANCEL_OPTION) {
            return;
        }

        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("SEQ Files", "seq");
        DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
        if (openFileDialogResult.isOkToProceed()) {
            try {
                File file = openFileDialogResult.getFile();
                log.info("Opening file [" + file + "] for reading");
                updateDocument(file, new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void openExampleFile() {
        if (dirtyFileCheck() == JOptionPane.CANCEL_OPTION) {
            return;
        }

        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("example-file.seq");
        if (systemResourceAsStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(systemResourceAsStream);
            updateDocument(null, inputStreamReader);
        }
    }

    private void updateDocument(File file, Reader inputReader) {
        java.util.List<String> lines = new ArrayList<>();
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

        // update text area
        this.textArea1.setText(String.join("\n", lines));
        log.info("Updated document with contents of file [" + file + "]");

        // trigger a refresh of the diagram
        // todo must be a cleaner way to do this
        for (KeyListener keyListener : this.textArea1.getKeyListeners()) {
            keyListener.keyTyped(new KeyEvent(this.textArea1, 0, 1L, 0, 0, '0'));
        }

        // update document state
        this.documentState = new DocumentState(file, this.textArea1.getText());
    }

    private void createUIComponents() {
        this.contentPane = new JPanel();

        this.canvasContainer = new Canvas();
        this.canvasContainer.setBorder(BorderFactory.createDashedBorder(Color.BLUE));

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
        panel2.add(toolBar1, BorderLayout.NORTH);
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
        final JSplitPane splitPane1 = new JSplitPane();
        tabContainer.addTab("Untitled", splitPane1);
        splitPane1.setRightComponent(canvasContainer);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel3);
        textArea1 = new JTextArea();
        textArea1.setFocusCycleRoot(true);
        Font textArea1Font = this.$$$getFont$$$("Courier New", Font.PLAIN, 12, textArea1.getFont());
        if (textArea1Font != null) textArea1.setFont(textArea1Font);
        panel3.add(textArea1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
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
