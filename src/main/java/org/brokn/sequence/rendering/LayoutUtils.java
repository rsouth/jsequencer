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
