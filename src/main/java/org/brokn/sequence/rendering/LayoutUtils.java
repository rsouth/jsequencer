package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.logging.Logger;

public class LayoutUtils {

    private static final Logger log = Logger.getLogger(LayoutUtils.class.getName());

    static int columnXPosition(Lane lane) {
        int multi = lane.getIndex();
        if (multi == 0) {
            log.info("x for " + lane.getName() + " is 10");
            return 10;
        } else {
            int x = (multi * RenderableLane.NODE_WIDTH) + (multi * RenderableLane.NODE_GAP);
            log.info("x for " + lane.getName() + " is " + x);
            return 10 + x;
        }
    }

    static Rectangle getStringBounds(Graphics2D g2, String str) {
        if(str == null) {
            // null string will have 0 height
            return new Rectangle(0, 0, 0, 0);
        }

        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, 0, 0);
    }

}
