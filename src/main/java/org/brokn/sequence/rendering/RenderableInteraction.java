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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.rendering.utils.LayoutConstants;
import org.brokn.sequence.rendering.utils.LayoutUtils;

import javax.swing.*;
import java.awt.*;

public class RenderableInteraction {

    private final Interaction interaction;

    public RenderableInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public void draw(Graphics g, int verticalOffset) {
        boolean isSelfReferential = this.interaction.getFromLane().equals(this.interaction.getToLane());

        if (isSelfReferential) {
            drawSelfReferentialInteraction(g, verticalOffset);
        } else {
            drawPointToPointInteraction(g, verticalOffset);
        }
    }

    private void drawSelfReferentialInteraction(Graphics g, int verticalOffset) {
        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int interactionFromXPosition = fromLaneXPosition + (LayoutConstants.LANE_WIDTH / 2);

        int lineFromX = interactionFromXPosition + LayoutConstants.LANE_WIDTH / 2;
        int lineToX = lineFromX + (LayoutConstants.LANE_GAP / 2);

        int fromLineY = verticalOffset + LayoutConstants.CANVAS_VERTICAL_GAP + (this.interaction.getIndex() * LayoutConstants.CANVAS_VERTICAL_GAP);
        int toLineY = fromLineY + LayoutConstants.CANVAS_VERTICAL_GAP;

        // render line
        g.drawLine(interactionFromXPosition, fromLineY, lineToX, fromLineY);

        // vertical line
        g.drawLine(lineToX, fromLineY, lineToX, toLineY);

        // second line
        g.drawLine(interactionFromXPosition, toLineY, lineToX, toLineY);

        // Render message
        renderInteractionMessage(g, interactionFromXPosition, fromLineY, lineToX);

        RenderableArrowhead.draw(g, this.interaction, interactionFromXPosition, toLineY);
    }

    private void drawPointToPointInteraction(Graphics g, int verticalOffset) {
        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int toLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getToLane());

        int lineFromX = fromLaneXPosition + (LayoutConstants.LANE_WIDTH / 2);
        int lineToX = toLaneXPosition + (LayoutConstants.LANE_WIDTH / 2);

        int lineY = verticalOffset + LayoutConstants.CANVAS_VERTICAL_GAP + (this.interaction.getIndex() * LayoutConstants.CANVAS_VERTICAL_GAP);

        // render line
        g.drawLine(lineFromX, lineY, lineToX, lineY);

        // Render message
        renderInteractionMessage(g, lineFromX, lineY, lineToX);

        RenderableArrowhead.draw(g, this.interaction, lineToX, lineY);
    }

    private void renderInteractionMessage(Graphics g, int interactionFromXPosition, int interactionFromYPosition, int interactionToXPosition) {
        if (this.interaction.getMessage() != null) {
            boolean isRight = interactionFromXPosition < interactionToXPosition;
            int messageWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), this.interaction.getMessage());
            int labelX = isRight ? interactionFromXPosition + LayoutConstants.RI_MESSAGE_X_PADDING : interactionFromXPosition - (messageWidth + LayoutConstants.RI_MESSAGE_X_PADDING);
            g.drawString(this.interaction.getMessage(), labelX, interactionFromYPosition - LayoutConstants.RI_MESSAGE_X_PADDING);
        }
    }

    static class RenderableArrowhead {

        private RenderableArrowhead() {
        }

        public static void draw(Graphics g, Interaction interaction, int lineEndX, int lineEndY) {
            int fromLaneIndex = interaction.getFromLane().getIndex();
            int toLaneIndex = interaction.getToLane().getIndex();

            boolean isPointingRight = fromLaneIndex < toLaneIndex;
            if (isPointingRight) {
                // draw >
                g.drawLine(lineEndX - LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndY - LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX - LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndY + LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndX, lineEndY);

            } else {
                // draw <
                g.drawLine(lineEndX + LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndY - LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX + LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndY + LayoutConstants.RI_ARROWHEAD_LENGTH, lineEndX, lineEndY);
            }

        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderableInteraction that = (RenderableInteraction) o;
        return Objects.equal(interaction, that.interaction);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(interaction);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("interaction", interaction)
                .toString();
    }

}
