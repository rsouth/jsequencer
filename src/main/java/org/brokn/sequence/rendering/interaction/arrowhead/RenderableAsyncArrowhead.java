package org.brokn.sequence.rendering.interaction.arrowhead;

import org.brokn.sequence.model.Interaction;

import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.ARROWHEAD_LENGTH;

public class RenderableAsyncArrowhead extends RenderableArrowhead {

    RenderableAsyncArrowhead(Interaction interaction) {
        super(interaction);
    }

    @Override
    public void draw(Graphics g, int lineEndX, int lineEndY) {
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
