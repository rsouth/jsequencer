package org.brokn.sequence.rendering;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class Canvas extends JPanel {

    private static final Logger log = Logger.getLogger(Canvas.class.getName());

    // Vertical gap between anything separated vertically (nodes/interactions/notes)
    public static final int VERTICAL_GAP = 50;

    private double scale = 1;

    private RenderableGraph renderableGraph;

    public Canvas() {
        setVisible(true);
        setSize(1, 1);
    }

    public void updateModel(final RenderableGraph model) {
        this.renderableGraph = model;
        doLayout();
        paintComponent(getGraphics());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.renderableGraph == null) {
            drawHelp(g);

        } else {
            // update scale
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(scale, scale);

            // render the diagram
            this.renderableGraph.draw(g);

        }
    }

    private void drawHelp(Graphics g) {
        new RenderableHelpMessage().draw(g);
    }

    public void updateScale(int value) {
        this.scale = 1 + (value / 10.0);
        log.info("Updated canvas scale to " + this.scale);

        doLayout();
        paintComponent(getGraphics());
    }
}
