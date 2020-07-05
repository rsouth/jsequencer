package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;

import java.awt.*;

import static org.brokn.sequence.rendering.Canvas.VERTICAL_GAP;

public class RenderableInteraction {
    private final Interaction interaction;

    public RenderableInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void draw(Graphics g, int interactionCount) {

        int fromColumn = LayoutUtils.columnXPosition(this.getInteraction().getFromLane());
        int toColumn = LayoutUtils.columnXPosition(this.getInteraction().getToLane());
        int fromX = fromColumn + (RenderableLane.NODE_WIDTH / 2);
        int toX = toColumn + (RenderableLane.NODE_WIDTH / 2);
        int y = VERTICAL_GAP + ((VERTICAL_GAP / 2) + 30) + (interactionCount * VERTICAL_GAP);

        // todo above isn't the side, it's the # of THIS interaction......

        boolean isRight = fromX < toX;
        int labelX = isRight ? fromX + 50 : fromX - 50;
        g.drawString(this.interaction.getMessage(), labelX, y);
        g.drawLine(fromX, y, toX, y);

        new RenderableArrowhead().draw(g, interaction, toX, y);
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
