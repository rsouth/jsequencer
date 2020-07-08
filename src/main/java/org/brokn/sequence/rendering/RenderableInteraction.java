/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static org.brokn.sequence.rendering.Canvas.VERTICAL_GAP;
import static org.brokn.sequence.rendering.RenderableLane.LANE_GAP;

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

        boolean isSelfReferential = this.interaction.getFromLane().equals(this.interaction.getToLane());

        if (isSelfReferential) {
            drawSelfReferentialInteraction(g, verticalOffset);
        } else {
            drawPointToPointInteraction(g, verticalOffset);
        }
    }

    private void drawSelfReferentialInteraction(Graphics g, int verticalOffset) {
        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int interactionFromXPosition = fromLaneXPosition + (RenderableLane.LANE_WIDTH / 2);

        int lineFromX = interactionFromXPosition + RenderableLane.LANE_WIDTH / 2;
        int lineToX = lineFromX + (LANE_GAP / 2);

        int fromLineY = verticalOffset + VERTICAL_GAP + (this.interaction.getIndex() * VERTICAL_GAP);
        int toLineY = fromLineY + VERTICAL_GAP;

        // render line
        g.drawLine(interactionFromXPosition, fromLineY, lineToX, fromLineY);

        // vertical line
        g.drawLine(lineToX, fromLineY, lineToX, toLineY);

        // second line
        g.drawLine(interactionFromXPosition, toLineY, lineToX, toLineY);

        // Render message
        renderInteractionMessage(g, interactionFromXPosition, fromLineY, lineToX);

        new RenderableArrowhead().draw(g, this.interaction, interactionFromXPosition, toLineY);
    }

    private void drawPointToPointInteraction(Graphics g, int verticalOffset) {
        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int toLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getToLane());

        int lineFromX = fromLaneXPosition + (RenderableLane.LANE_WIDTH / 2);
        int lineToX = toLaneXPosition + (RenderableLane.LANE_WIDTH / 2);

        int lineY = verticalOffset + VERTICAL_GAP + (this.interaction.getIndex() * VERTICAL_GAP);

        // render line
        g.drawLine(lineFromX, lineY, lineToX, lineY);

        // Render message
        renderInteractionMessage(g, lineFromX, lineY, lineToX);

        new RenderableArrowhead().draw(g, this.interaction, lineToX, lineY);
    }

    private void renderInteractionMessage(Graphics g, int interactionFromXPosition, int interactionFromYPosition, int interactionToXPosition) {
        if (this.interaction.getMessage() != null) {
            boolean isRight = interactionFromXPosition < interactionToXPosition;
            int messageWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), this.interaction.getMessage());
            int labelX = isRight ? interactionFromXPosition + MESSAGE_PADDING : interactionFromXPosition - (messageWidth + MESSAGE_PADDING);
            g.drawString(this.interaction.getMessage(), labelX, interactionFromYPosition - MESSAGE_PADDING);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderableInteraction that = (RenderableInteraction) o;
        return interaction.equals(that.interaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interaction);
    }

    @Override
    public String toString() {
        return "RenderableInteraction{" +
                "interaction=" + interaction +
                '}';
    }
}
