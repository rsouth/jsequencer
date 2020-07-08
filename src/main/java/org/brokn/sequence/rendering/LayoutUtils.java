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

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class LayoutUtils {

    static int getLaneXPosition(Lane lane) {
        int multi = lane.getIndex();
        if (multi == 0) {
            return 10;
        } else {
            int x = (multi * RenderableLane.LANE_WIDTH) + (multi * RenderableLane.LANE_GAP);
            return 10 + x;
        }
    }

    static Rectangle getStringBounds(Graphics2D g2, Font font, String str) {
        if(str == null) {
            // null string will have 0 height
            return new Rectangle(0, 0, 0, 0);
        }

        Font originalFont = g2.getFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        Rectangle pixelBounds = gv.getPixelBounds(null, 0, 0);

        g2.setFont(originalFont);
        return pixelBounds;
    }

    static void drawStringWithFont(Graphics g, Font font, int x, int y, String text) {
        Font originalFont = g.getFont();
        g.setFont(font);
        g.drawString(text, x, y);
        g.setFont(originalFont);
    }

}
