package org.brokn.sequence.rendering.interaction;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.rendering.interaction.arrowhead.RenderableArrowhead;
import org.brokn.sequence.rendering.interaction.arrowhead.RenderableArrowheadFactory;
import org.brokn.sequence.rendering.utils.LayoutUtils;

import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.*;
import static org.brokn.sequence.rendering.utils.LayoutHelper.CANVAS_VERTICAL_GAP;

public class RenderablePointToPointInteraction extends RenderableInteraction {

    public RenderablePointToPointInteraction(Interaction interaction) {
        super(interaction);
    }

    @Override
    public void draw(Graphics g, int verticalOffset) {
        super.draw(g, verticalOffset);

        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int toLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getToLane());

        int lineFromX = fromLaneXPosition + (LANE_WIDTH / 2);
        int lineToX = toLaneXPosition + (LANE_WIDTH / 2);

        int lineY = verticalOffset + CANVAS_VERTICAL_GAP + (this.interaction.getIndex() * CANVAS_VERTICAL_GAP);

        // Render line
        g.drawLine(lineFromX, lineY, lineToX, lineY);

        // Render message
        renderInteractionMessage(g, lineFromX, lineY, lineToX);

        // Render Arrowhead
        RenderableArrowheadFactory.create(interaction).draw(g, lineToX, lineY);
    }

}
