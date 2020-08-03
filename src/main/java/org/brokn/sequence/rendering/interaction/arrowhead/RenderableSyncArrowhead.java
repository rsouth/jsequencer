package org.brokn.sequence.rendering.interaction.arrowhead;

import org.brokn.sequence.model.Interaction;

import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.ARROWHEAD_LENGTH;

public class RenderableSyncArrowhead extends RenderableArrowhead {

    RenderableSyncArrowhead(Interaction interaction) {
        super(interaction);
    }

    @Override
    public void draw(Graphics g, int lineEndX, int lineEndY) {
        int fromLaneIndex = interaction.getFromLane().getIndex();
        int toLaneIndex = interaction.getToLane().getIndex();
        boolean isPointingRight = fromLaneIndex < toLaneIndex;
        if (isPointingRight) {
            // draw >
            g.fillPolygon(
                    new int[]{lineEndX - ARROWHEAD_LENGTH, lineEndX, lineEndX - ARROWHEAD_LENGTH},
                    new int[]{lineEndY - ARROWHEAD_LENGTH, lineEndY, lineEndY + ARROWHEAD_LENGTH}, 3);

        } else {
            // draw <
            g.fillPolygon(
                    new int[]{lineEndX + ARROWHEAD_LENGTH, lineEndX, lineEndX + ARROWHEAD_LENGTH},
                    new int[]{lineEndY - ARROWHEAD_LENGTH, lineEndY, lineEndY + ARROWHEAD_LENGTH}, 3);
        }

    }
}
