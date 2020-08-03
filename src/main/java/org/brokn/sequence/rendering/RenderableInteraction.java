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
import org.brokn.sequence.rendering.utils.LayoutUtils;

import javax.swing.*;
import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.*;

public final class RenderableInteraction {

    private final Interaction interaction;

    public RenderableInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public void draw(Graphics g, int verticalOffset) {
        Graphics2D g2d = (Graphics2D) g.create();

        if(this.interaction.getModifiers().contains(Interaction.Modifiers.REPLY)) {
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            g2d.setStroke(dashed);
        }

        if (this.interaction.getModifiers().contains(Interaction.Modifiers.SELFREF)) {
            drawSelfReferentialInteraction(g2d, verticalOffset);
        } else {
            drawPointToPointInteraction(g2d, verticalOffset);
        }
    }

    private void drawSelfReferentialInteraction(Graphics g, int verticalOffset) {
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

        RenderableArrowhead.draw(g, this.interaction, fromLineX, toLineY);
    }

    private void drawPointToPointInteraction(Graphics g, int verticalOffset) {
        int fromLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getFromLane());
        int toLaneXPosition = LayoutUtils.getLaneXPosition(this.interaction.getToLane());

        int lineFromX = fromLaneXPosition + (LANE_WIDTH / 2);
        int lineToX = toLaneXPosition + (LANE_WIDTH / 2);

        int lineY = verticalOffset + CANVAS_VERTICAL_GAP + (this.interaction.getIndex() * CANVAS_VERTICAL_GAP);

        // render line
        g.drawLine(lineFromX, lineY, lineToX, lineY);

        // Render message
        renderInteractionMessage(g, lineFromX, lineY, lineToX);

        RenderableArrowhead.draw(g, this.interaction, lineToX, lineY);
    }

    private void renderInteractionMessage(Graphics g, int interactionFromXPosition, int interactionFromYPosition, int interactionToXPosition) {
        this.interaction.getMessage().ifPresent(message -> {
            boolean isRightFacing = interactionFromXPosition < interactionToXPosition;
            int messageWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), message);
            int labelX = isRightFacing ? interactionFromXPosition + MESSAGE_PADDING : interactionFromXPosition - (messageWidth + MESSAGE_PADDING);
            g.drawString(message, labelX, interactionFromYPosition - MESSAGE_PADDING);
        });
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
