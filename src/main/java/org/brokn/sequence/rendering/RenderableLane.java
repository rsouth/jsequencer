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

import org.brokn.sequence.model.Lane;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.computeStringWidth;
import static org.brokn.sequence.rendering.LayoutUtils.drawStringWithFont;

public class RenderableLane {

    public static final int LANE_WIDTH = 150;

    public static final int LANE_GAP = 50;

    public static final int LANE_BOX_HEIGHT = 30;

    public static final int LANE_BOX_PADDING = 20;

    private final RenderableGraph renderableGraph;

    private final Lane lane;

    public RenderableLane(final RenderableGraph renderableGraph, final Lane lane) {
        this.renderableGraph = renderableGraph;
        this.lane = lane;
    }

    public void draw(Graphics g) {
        int headerOffset = renderableGraph.getMetaDataHeight(g);

        // X position of the lane
        int laneXPosition = LayoutUtils.getLaneXPosition(this.lane);

        // draw lane name
        Font titleFont = getSizeAdjustedFont(g, this.lane.getName());
        int textWidth = computeStringWidth(g.getFontMetrics(titleFont), this.lane.getName());
        int textXPosition = (laneXPosition + LANE_WIDTH / 2) - (textWidth / 2);
        int textYPosition = headerOffset + LANE_BOX_PADDING;
        drawStringWithFont(g, titleFont, textXPosition, textYPosition, this.lane.getName());

        // draw box
        int boxWidth = Math.min(textWidth, LANE_WIDTH) + (LANE_BOX_PADDING * 2);
        int boxXPosition = laneXPosition + (LANE_WIDTH / 2) - (Math.min(textWidth, LANE_WIDTH) / 2) - LANE_BOX_PADDING;
        g.drawRoundRect(boxXPosition, headerOffset, boxWidth, LANE_BOX_HEIGHT, 10, 10);

        // draw vertical line
        int y1 = headerOffset + LANE_BOX_HEIGHT;
        int y2 = headerOffset + (renderableGraph.interactions.size() * Canvas.VERTICAL_GAP) + getVerticalLinePadding();
        g.drawLine((laneXPosition + LANE_WIDTH / 2), y1, (laneXPosition + LANE_WIDTH / 2), y2);

    }

    /**
     * Adjusts the font size to fit the available space (within the width of the lane)
     * @param g
     * @param text
     * @return
     */
    public Font getSizeAdjustedFont(Graphics g, String text) {
        Font originalFont = g.getFont();
        for (float size = originalFont.getSize(); size > 0; size -= 0.1) {
            Font tryFont = originalFont.deriveFont(Font.BOLD, size);
            int width = SwingUtilities.computeStringWidth(g.getFontMetrics(tryFont), text);
            if(width < LANE_WIDTH) {
                return tryFont;
            }
        }
        return originalFont;
    }

    public static int getVerticalLinePadding() {
        return Canvas.VERTICAL_GAP + LANE_BOX_HEIGHT;
    }

}