package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;

import java.awt.*;

import static org.brokn.sequence.rendering.Canvas.VERTICAL_GAP;

public class RenderableInteraction {

    private final Interaction interaction;

    private final RenderableGraph renderableGraph;

    public RenderableInteraction(RenderableGraph renderableGraph, Interaction interaction) {
        this.renderableGraph = renderableGraph;
        this.interaction = interaction;
    }

    public void draw(Graphics g) {
        int verticalOffset = renderableGraph.getMetaDataHeight(g);

        int fromColumn = LayoutUtils.columnXPosition(this.interaction.getFromLane());
        int toColumn = LayoutUtils.columnXPosition(this.interaction.getToLane());
        int fromX = fromColumn + (RenderableLane.LANE_WIDTH / 2);
        int toX = toColumn + (RenderableLane.LANE_WIDTH / 2);
        int y = verticalOffset + VERTICAL_GAP + ((VERTICAL_GAP / 2) + 30) + (this.interaction.getIndex() * VERTICAL_GAP);

        boolean isRight = fromX < toX;
        int labelX = isRight ? fromX + 50 : fromX - 50;
        g.drawString(this.interaction.getMessage(), labelX, y);
        g.drawLine(fromX, y, toX, y);

        new RenderableArrowhead().draw(g, this.interaction, toX, y);
    }

    static class RenderableArrowhead {

        public void draw(Graphics g, Interaction interaction, int lineEndX, int lineEndY) {

            int fromIdx = interaction.getFromLane().getIndex();
            int toIdx = interaction.getToLane().getIndex();

            boolean isRight = fromIdx < toIdx;

            if (isRight) {
                // draw >
                g.drawLine(lineEndX - 10, lineEndY - 10, lineEndX, lineEndY);
                g.drawLine(lineEndX - 10, lineEndY + 10, lineEndX, lineEndY);

            } else {
                // draw <
                g.drawLine(lineEndX + 10, lineEndY - 10, lineEndX, lineEndY);
                g.drawLine(lineEndX + 10, lineEndY + 10, lineEndX, lineEndY);
            }

        }

    }

}
