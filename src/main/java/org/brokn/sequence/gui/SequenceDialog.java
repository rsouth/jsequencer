package org.brokn.sequence.gui;

import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SequenceDialog extends JFrame {

    private static Logger log = Logger.getLogger(SequenceDialog.class.getName());

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

    public SequenceDialog() {
        super("Sequencer");
        setLocationByPlatform(true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonExport);

        buttonExport.addActionListener(e -> onExport());
        buttonSave.addActionListener(e -> onSave());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        buttonOpen.addActionListener(e -> openFile());

        scaleSlider.addChangeListener(e -> ((Canvas) canvasContainer).updateScale(((JSlider) e.getSource()).getValue()));
    }

    private void openFile() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("SEQ Files", "seq");
        DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
        if (openFileDialogResult.isOkToProceed()) {
            try (BufferedReader out = new BufferedReader(new FileReader(openFileDialogResult.getFile()))) {
                java.util.List<String> lines = new ArrayList<>();
                String line;
                while ((line = out.readLine()) != null) {
                    lines.add(line);
                }

                this.textArea1.setText(String.join("\n", lines));
                for (KeyListener keyListener : this.textArea1.getKeyListeners()) {
                    // todo nicer than this pls.
                    keyListener.keyTyped(new KeyEvent(this.textArea1, 0, 1L, 0, 0, '0'));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onSave() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pls use .seq extension...", "seq");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            saveSourceFile(fileDialogResult.getFile());
        }
    }

    private void onExport() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pls use .png extension...", "png");
        DialogUtils.FileDialogResult fileDialogResult = DialogUtils.openSaveAsDialog(fileFilter);
        if (fileDialogResult.isOkToProceed()) {
            exportAsImage(fileDialogResult.getFile());
        }
    }

    private void saveSourceFile(File file) {
        try {
            String currentSource = this.textArea1.getText();
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(currentSource);
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
    private void exportAsImage(File selectedFile) {
        BufferedImage bImg = new BufferedImage(this.canvasContainer.getWidth(), this.canvasContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        this.canvasContainer.paintAll(cg);
        try {
            if (ImageIO.write(bImg, "png", selectedFile)) {
                log.info("-- saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
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

}
