package org.brokn.sequence.rendering.interaction;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.rendering.interaction.arrowhead.RenderableArrowhead;
import org.brokn.sequence.rendering.interaction.arrowhead.RenderableArrowheadFactory;
import org.brokn.sequence.rendering.utils.LayoutUtils;

import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.*;
import static org.brokn.sequence.rendering.utils.LayoutHelper.CANVAS_VERTICAL_GAP;

public class RenderableSelfReferentialInteraction extends RenderableInteraction {

    public RenderableSelfReferentialInteraction(Interaction interaction) {
        super(interaction);
    }

    @Override
    public void draw(Graphics g, int verticalOffset) {
        super.draw(g, verticalOffset);

        int fromLineX = LayoutUtils.getLaneXPosition(this.interaction.getFromLane()) + (LANE_WIDTH / 2);
        int lineToX = fromLineX + (LANE_WIDTH / 2) + (LANE_GAP / 2);

        int fromLineY = verticalOffset + CANVAS_VERTICAL_GAP + (this.interaction.getIndex() * CANVAS_VERTICAL_GAP);
        int toLineY = fromLineY + CANVAS_VERTICAL_GAP;

        // render line
        g.drawLine(fromLineX, fromLineY, lineToX, fromLineY);

        // vertical line
        g.drawLine(lineToX, fromLineY, lineToX, toLineY);

        // second line
        g.drawLine(fromLineX, toLineY, lineToX, toLineY);

        // Render message
        renderInteractionMessage(g, fromLineX, fromLineY, lineToX);

        // Render Arrowhead
        RenderableArrowheadFactory.create(interaction).draw(g, fromLineX, toLineY);
    }

}
