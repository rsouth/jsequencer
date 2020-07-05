package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

import javax.swing.*;
import java.awt.*;

public class RenderableLane {

    public static final int LANE_WIDTH = 50;

    public static final int LANE_GAP = 100;

    private final RenderableGraph renderableGraph;
    private final Lane lane;

    public RenderableLane(final RenderableGraph renderableGraph, final Lane lane) {
        this.renderableGraph = renderableGraph;
        this.lane = lane;
    }

    public void draw(Graphics g) {
        int verticalOffset = renderableGraph.getMetaDataHeight(g);

        int x = LayoutUtils.columnXPosition(this.lane);
        g.drawRoundRect(x, verticalOffset + (Canvas.VERTICAL_GAP / 2), LANE_WIDTH, 30, 5, 5);
        int textWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont()), this.lane.getName());
        g.drawString(this.lane.getName(), (x + LANE_WIDTH / 2) - (textWidth / 2), verticalOffset + (Canvas.VERTICAL_GAP / 2) + 15);

        // draw vertical line
        int y1 = verticalOffset + (Canvas.VERTICAL_GAP / 2) + 30;
        int y2 = verticalOffset + Canvas.VERTICAL_GAP + (Canvas.VERTICAL_GAP / 2) + 30 + (renderableGraph.interactions.size() * Canvas.VERTICAL_GAP);
        g.drawLine((x + LANE_WIDTH / 2), y1, (x + LANE_WIDTH / 2), y2);

    }

}