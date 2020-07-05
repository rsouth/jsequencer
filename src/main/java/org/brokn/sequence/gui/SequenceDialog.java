package org.brokn.sequence.gui;

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
    private JButton buttonExampleFile;

    public SequenceDialog() {
        super("Sequencer");
        setLocationByPlatform(true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonExport);


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

        // button callbacks
        buttonExport.addActionListener(e -> onExport());
        buttonSave.addActionListener(e -> onSave());
        buttonOpen.addActionListener(e -> openFile());
        buttonExampleFile.addActionListener(e -> openExampleFile());

        // scale slider callback
        scaleSlider.addChangeListener(e -> ((Canvas) canvasContainer).updateScale(((JSlider) e.getSource()).getValue()));
    }

    private void openExampleFile() {
        String exampleText = "# header\n" +
                ":title Sequence Diagram Example\n" +
                ":author John Smith\n" +
                ":authorEmail john.smith@example.com\n" +
                ":date\n" +
                "\n" +
                "# Client / Server Response\n" +
                "Client -> Server: Request\n" +
                "Server -> Service: Query\n" +
                "Service -> Server: Data\n" +
                "Server -> Client: Response\n" +
                "\n";
        this.textArea1.setText(exampleText);
        this.triggerReload();
    }

    private void openFile() {
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("SEQ Files", "seq");
        DialogUtils.FileDialogResult openFileDialogResult = DialogUtils.openOpenFileDialog(fileFilter);
        if (openFileDialogResult.isOkToProceed()) {
            File file = openFileDialogResult.getFile();
            log.info("Opening file [" + file + "] for reading");
            try (BufferedReader out = new BufferedReader(new FileReader(file))) {
                java.util.List<String> lines = new ArrayList<>();
                String line;
                while ((line = out.readLine()) != null) {
                    lines.add(line);
                }

                // replace the contents of the JTextArea with the file contents
                this.textArea1.setText(String.join("\n", lines));
                log.info("Updated document with contents of file [" + file + "]");

                // trigger update of the diagram
                triggerReload();

            } catch (IOException e) {
                log.severe("An exception occured while reading file [" + file + "]");
                e.printStackTrace();
            }
        }
    }

    private void triggerReload() {
        for (KeyListener keyListener : this.textArea1.getKeyListeners()) {
            // todo nicer than this pls.
            keyListener.keyTyped(new KeyEvent(this.textArea1, 0, 1L, 0, 0, '0'));
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
                log.info("Opened file [" + file + "] for writing");
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
            log.info("Opening file [" + selectedFile + "] for graphics export");
            if (ImageIO.write(bImg, "png", selectedFile)) {
                log.info("Successfully exported diagram to file [" + selectedFile + "]");
            }
        } catch (IOException e) {
            log.severe("Failed to export diagram to file [" + selectedFile + "]");
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

}
