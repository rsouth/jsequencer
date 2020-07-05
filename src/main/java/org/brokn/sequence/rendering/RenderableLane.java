package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

import javax.swing.*;
import java.awt.*;

public class RenderableLane {

    public static final int LANE_WIDTH = 50;

    public static final int LANE_GAP = 100;

    private final Lane model;

    public RenderableLane(final Lane model) {
        this.model = model;
    }

    public void draw(Graphics g, RenderableGraph renderableGraph) {
        int verticalOffset = renderableGraph.getMetaDataHeight(g);

        int x = LayoutUtils.columnXPosition(this.model);
        g.drawRoundRect(x, verticalOffset + (Canvas.VERTICAL_GAP / 2), LANE_WIDTH, 30, 5, 5);
        int textWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont()), this.model.getName());
        g.drawString(this.model.getName(), (x + LANE_WIDTH / 2) - (textWidth / 2), verticalOffset + (Canvas.VERTICAL_GAP / 2) + 15);

        // draw vertical line
        // number of the last interaction for this node * verticalGap ??
        int y1 = verticalOffset + (Canvas.VERTICAL_GAP / 2) + 30;
        int y2 = verticalOffset + Canvas.VERTICAL_GAP + (Canvas.VERTICAL_GAP / 2) + 30 + (renderableGraph.interactions.size() * Canvas.VERTICAL_GAP);
        g.drawLine((x + LANE_WIDTH / 2), y1, (x + LANE_WIDTH / 2), y2);

    }

}