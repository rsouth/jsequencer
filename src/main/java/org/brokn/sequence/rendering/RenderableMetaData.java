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

import org.brokn.sequence.model.MetaData;

import java.awt.*;
import java.time.LocalDate;

import static org.brokn.sequence.rendering.LayoutUtils.drawStringWithFont;

public class RenderableMetaData {

    private static final int VERTICAL_GAP = 20;

    private static final int DOCUMENT_MARGIN = 10;

    private final MetaData model;

    public RenderableMetaData(MetaData metaData) {
        this.model = metaData;
    }

    public int calculateHeaderHeight(Graphics g) {
        int totalHeight = 10;

        //title
        if(this.model.getTitle() != null) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;
        }

        // author
        if(this.model.getAuthor() != null) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            totalHeight += VERTICAL_GAP;
        }

        // date
        if(this.model.isShowDate()) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), "date").height;
            if(this.model.getTitle() != null || this.model.getAuthor() != null) {
                totalHeight += VERTICAL_GAP;
            }
        }

        return totalHeight;
    }

    public void draw(Graphics g) {

        // draw title
        if(this.model.getTitle() != null) {
            Font titleFont = getTitleFont(g);
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, titleFont, this.model.getTitle()).height;
            drawStringWithFont(g, titleFont, DOCUMENT_MARGIN, DOCUMENT_MARGIN + titleHeight, this.model.getTitle());
        }

        // draw author name
        if(this.model.getAuthor() != null) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;// 0 if no title shown
            int authorHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            int y = titleHeight + (this.model.getTitle() == null ? DOCUMENT_MARGIN : VERTICAL_GAP) + authorHeight;
            g.drawString(this.model.getAuthor(), DOCUMENT_MARGIN, y);
        }

        // draw current date (10th June 2020 so no regional ambiguity)
        // todo worth adding ability to format the date?
        if(this.model.isShowDate()) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;// 0 if no title shown
            int authorHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            int dateHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), "date").height;

            int spacing = (this.model.getTitle() != null || this.model.getAuthor() != null ? VERTICAL_GAP : 0);
            int y = titleHeight + authorHeight + dateHeight + spacing;

            g.drawString(LocalDate.now().toString(), DOCUMENT_MARGIN, y);
        }
    }

    private Font getTitleFont(Graphics g) {
        return g.getFont().deriveFont(Font.BOLD, 20);
    }

}
