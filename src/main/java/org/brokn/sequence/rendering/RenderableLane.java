package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

import javax.swing.*;
import java.awt.*;

public class RenderableLane {

    public static final int NODE_WIDTH = 50;

    public static final int NODE_GAP = 100;

    private final Lane model;

    public RenderableLane(final Lane model) {
        this.model = model;
    }

    public void draw(Graphics g, RenderableGraph renderableGraph) {
        int x = LayoutUtils.columnXPosition(this.model);
        g.drawRoundRect(x, Canvas.VERTICAL_GAP / 2, NODE_WIDTH, 30, 5, 5);
        int textWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont()), this.model.getName());
        g.drawString(this.model.getName(), (x + NODE_WIDTH / 2) - (textWidth / 2), (Canvas.VERTICAL_GAP / 2) + 15);

        // draw vertical line
        // number of the last interaction for this node * verticalGap ??
        int y2 = Canvas.VERTICAL_GAP + (Canvas.VERTICAL_GAP / 2) + 30 + (renderableGraph.interactions.size() * Canvas.VERTICAL_GAP);
        g.drawLine((x + NODE_WIDTH / 2), (Canvas.VERTICAL_GAP / 2) + 30, (x + NODE_WIDTH / 2), y2);

    }

}