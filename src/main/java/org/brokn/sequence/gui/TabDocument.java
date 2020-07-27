package org.brokn.sequence.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableDiagram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class TabDocument extends JSplitPane implements TextChangedListener {

    private DocumentState documentState;

    private final JTextArea textArea;

    private final Canvas canvas;

    public TabDocument(File file) {
        this();
        this.documentState = new DocumentState(this, file);
    }

    public TabDocument() {
        // setup left pane (text area)
        JScrollPane textScrollPane = new JScrollPane();

        JPanel textAreaContainer = new JPanel(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));

        textArea = new JTextArea();
        Font testFont = new Font("Courier New", Font.PLAIN, 12);
        textArea.setFont(testFont);
        textArea.setFocusCycleRoot(true);
        textAreaContainer.add(textArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));

        JScrollPane imageScrollPane = new JScrollPane();
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.WHITE);

        imageScrollPane.setViewportView(canvas);
        textScrollPane.setViewportView(textAreaContainer);

        this.setLeftComponent(textScrollPane);
        this.setRightComponent(imageScrollPane);

        this.documentState = new DocumentState(this, false);

        // listen for keystrokes to kick off updating the diagram
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (documentState.updateText(textArea.getText())) {
                    triggerModelUpdate();
                }
            }
        });

        // Set the split pane divider location
        this.addHierarchyListener(e -> SwingUtilities.invokeLater(() -> {
            int splitPaneWidth = getWidth();
            double dividerLocation = (splitPaneWidth / 4.) / 1000;
//            logger.atInfo().log("JSplitPane width [" + splitPaneWidth + "], setting divider location to [" + dividerLocation + "]");
            setDividerLocation(dividerLocation);
        }));

    }

    private JTabbedPane getTabbedPane() {
        return (JTabbedPane) this.getParent();
    }

    private int getTabIndex() {
        return getTabbedPane().indexOfComponent(this);
    }

    @Override
    public void onTextChanged(String newText) {
        SwingUtilities.invokeLater(() -> {
            updateTabTitle();

            final int caretPosition = this.textArea.getCaretPosition();
            this.textArea.setText(newText);
            this.textArea.setCaretPosition(caretPosition);
            triggerModelUpdate();
        });
    }

    private void triggerModelUpdate() {

        SwingUtilities.invokeLater(() -> {
            Lexer lexer = new Lexer();
            // update the canvas model, which triggers a re-paint
            String text = documentState.getCurrentText();
            RenderableDiagram model = lexer.parse(text);
            canvas.updateModel(model);
        });

    }

    private void updateTabTitle() {
        final File file = this.documentState.getFile();
        String currentTitle = file == null ? "Untitled" : file.getName();

        if (documentState.isDirty()) {
            // suffix tab title with *
            this.getTabbedPane().setTitleAt(getTabIndex(), currentTitle + " *");
        } else {
            this.getTabbedPane().setTitleAt(getTabIndex(), currentTitle);
        }
    }

    public DocumentState getDocumentState() {
        return this.documentState;
    }

    public void replaceDocument(File file) {
        this.documentState = new DocumentState(this, file);
    }


    public Canvas getCanvas() {
        return this.canvas;
    }

    public void resetDocument() {
        this.documentState = new DocumentState(this, false);
    }

    public void loadExampleContext() {
        this.documentState = new DocumentState(this, true);
    }
}
