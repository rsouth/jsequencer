package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;

import javax.swing.*;
import java.awt.*;

import static org.brokn.sequence.rendering.Canvas.VERTICAL_GAP;

public class RenderableInteraction {

    private final Interaction interaction;

    private final RenderableGraph renderableGraph;

    private static final int ARROWHEAD_LENGTH = 10;

    private static final int MESSAGE_PADDING = 5;

    public RenderableInteraction(RenderableGraph renderableGraph, Interaction interaction) {
        this.renderableGraph = renderableGraph;
        this.interaction = interaction;
    }

    public void draw(Graphics g) {
        int verticalOffset = renderableGraph.getMetaDataHeight(g);

        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int toLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getToLane());
        int interactionFromXPosition = fromLaneXPosition + (RenderableLane.LANE_WIDTH / 2);
        int interactionToXPosition = toLaneXPosition + (RenderableLane.LANE_WIDTH / 2);
        int interactionYPosition = verticalOffset + VERTICAL_GAP + (this.interaction.getIndex() * VERTICAL_GAP);

        // render line
        g.drawLine(interactionFromXPosition, interactionYPosition, interactionToXPosition, interactionYPosition);

        // Render message
        if(this.interaction.getMessage() != null) {
            boolean isRight = interactionFromXPosition < interactionToXPosition;
            int messageWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), this.interaction.getMessage());
            int labelX = isRight ? interactionFromXPosition + MESSAGE_PADDING : interactionFromXPosition - (messageWidth + MESSAGE_PADDING);
            g.drawString(this.interaction.getMessage(), labelX, interactionYPosition - MESSAGE_PADDING);
        }

        new RenderableArrowhead().draw(g, this.interaction, interactionToXPosition, interactionYPosition);
    }

    static class RenderableArrowhead {

        public void draw(Graphics g, Interaction interaction, int lineEndX, int lineEndY) {
            int fromLaneIndex = interaction.getFromLane().getIndex();
            int toLaneIndex = interaction.getToLane().getIndex();

            boolean isPointingRight = fromLaneIndex < toLaneIndex;
            if (isPointingRight) {
                // draw >
                g.drawLine(lineEndX - ARROWHEAD_LENGTH, lineEndY - ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX - ARROWHEAD_LENGTH, lineEndY + ARROWHEAD_LENGTH, lineEndX, lineEndY);

            } else {
                // draw <
                g.drawLine(lineEndX + ARROWHEAD_LENGTH, lineEndY - ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX + ARROWHEAD_LENGTH, lineEndY + ARROWHEAD_LENGTH, lineEndX, lineEndY);
            }

        }

    }

}
