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

import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.ARROWHEAD_LENGTH;

public final class RenderableArrowhead {

    private RenderableArrowhead() { }

    public static void draw(Graphics g, Interaction interaction, int lineEndX, int lineEndY) {
        int fromLaneIndex = interaction.getFromLane().getIndex();
        int toLaneIndex = interaction.getToLane().getIndex();

        boolean isPointingRight = fromLaneIndex < toLaneIndex;
        if (isPointingRight) {
            // draw >
            if(interaction.getModifiers().contains(Interaction.Modifiers.ASYNC)) {
                g.drawLine(lineEndX - ARROWHEAD_LENGTH, lineEndY - ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX - ARROWHEAD_LENGTH, lineEndY + ARROWHEAD_LENGTH, lineEndX, lineEndY);

            } else {
                // solid == sync, open == async
                g.fillPolygon(
                        new int[]{lineEndX - ARROWHEAD_LENGTH, lineEndX, lineEndX - ARROWHEAD_LENGTH},
                        new int[]{lineEndY - ARROWHEAD_LENGTH, lineEndY, lineEndY + ARROWHEAD_LENGTH}, 3);
            }

        } else {
            // draw <
            if(interaction.getModifiers().contains(Interaction.Modifiers.ASYNC)) {
                g.drawLine(lineEndX + ARROWHEAD_LENGTH, lineEndY - ARROWHEAD_LENGTH, lineEndX, lineEndY);
                g.drawLine(lineEndX + ARROWHEAD_LENGTH, lineEndY + ARROWHEAD_LENGTH, lineEndX, lineEndY);

            } else {
                // solid == sync, open == async
                g.fillPolygon(
                        new int[]{lineEndX + ARROWHEAD_LENGTH, lineEndX, lineEndX + ARROWHEAD_LENGTH},
                        new int[]{lineEndY - ARROWHEAD_LENGTH, lineEndY, lineEndY + ARROWHEAD_LENGTH}, 3);
            }
        }

    }

}