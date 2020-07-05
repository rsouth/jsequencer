package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.computeStringWidth;
import static org.brokn.sequence.rendering.LayoutUtils.MARGIN;

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
        int headerOffset = renderableGraph.getMetaDataHeight(g);

        // X position of the lane
        int laneXPosition = LayoutUtils.getLaneXPosition(this.lane);

        // draw lane name
        int textWidth = computeStringWidth(g.getFontMetrics(g.getFont()), this.lane.getName());
        int stringXPosition = (laneXPosition + LANE_WIDTH / 2) - (textWidth / 2);
        g.drawString(this.lane.getName(), stringXPosition, headerOffset + (Canvas.VERTICAL_GAP / 2) + 20);

        // draw box
        g.drawRoundRect(laneXPosition, headerOffset + (Canvas.VERTICAL_GAP / 2), LANE_WIDTH, 30, 10, 10);

        // draw vertical line
        int y1 = headerOffset + (Canvas.VERTICAL_GAP / 2) + 30;
        int y2 = headerOffset + Canvas.VERTICAL_GAP + (Canvas.VERTICAL_GAP / 2) + 30 + (renderableGraph.interactions.size() * Canvas.VERTICAL_GAP);
        g.drawLine((laneXPosition + LANE_WIDTH / 2), y1, (laneXPosition + LANE_WIDTH / 2), y2);

    }

}