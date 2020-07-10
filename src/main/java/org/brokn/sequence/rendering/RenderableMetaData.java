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
import org.brokn.sequence.model.MetaData;
import org.brokn.sequence.rendering.utils.LayoutHelper;

import java.awt.*;
import java.time.LocalDate;

import static org.brokn.sequence.rendering.utils.LayoutHelper.DIAGRAM_PADDING;
import static org.brokn.sequence.rendering.utils.LayoutHelper.HEADER_V_GAP;
import static org.brokn.sequence.rendering.utils.LayoutUtils.drawStringWithFont;
import static org.brokn.sequence.rendering.utils.LayoutUtils.getStringBounds;

public class RenderableMetaData {

    private final MetaData model;

    public RenderableMetaData(final MetaData metaData) {
        this.model = metaData;
    }

    public void draw(final Graphics g) {

        // draw title
        if (this.model.getTitle() != null) {
            Font titleFont = getTitleFont(g);
            int titleHeight = getStringBounds((Graphics2D) g, titleFont, this.model.getTitle()).height;
            drawStringWithFont(g, titleFont, DIAGRAM_PADDING, DIAGRAM_PADDING + titleHeight, this.model.getTitle());
        }

        // draw author name
        if (this.model.getAuthor() != null) {
            int heightSoFar = DIAGRAM_PADDING + getTitleHeight(g) + (getTitleHeight(g) > 0 ? HEADER_V_GAP : 0);
            int authorHeight = getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            int y = heightSoFar + authorHeight;
            g.drawString(this.model.getAuthor(), DIAGRAM_PADDING, y);
        }

        // draw current date (10th June 2020 so no regional ambiguity)
        if (this.model.isShowDate()) {
            int heightSoFar = DIAGRAM_PADDING +
                    getTitleHeight(g) + (getTitleHeight(g) > 0 ? HEADER_V_GAP : 0) +
                    getAuthorHeight(g) + (getAuthorHeight(g) > 0 ? HEADER_V_GAP : 0);
            int dateHeight = getStringBounds((Graphics2D) g, g.getFont(), "date").height;
            int y = heightSoFar + dateHeight;
            g.drawString(LocalDate.now().toString(), DIAGRAM_PADDING, y);
        }
    }

    public int calculateHeaderHeight(Graphics g) {
        int totalHeight = 20;

        //title
        if (this.model.getTitle() != null) {
            totalHeight += getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;
        }

        // author
        if (this.model.getAuthor() != null) {
            totalHeight += getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            totalHeight += LayoutHelper.RM_VERTICAL_GAP;
        }

        // date
        if (this.model.isShowDate()) {
            totalHeight += getStringBounds((Graphics2D) g, g.getFont(), "date").height;
            if (this.model.getTitle() != null || this.model.getAuthor() != null) {
                totalHeight += LayoutHelper.RM_VERTICAL_GAP;
            }
        }

        return totalHeight;
    }

    private int getTitleHeight(final Graphics g) {
        return this.model.getTitle() == null ? 0 : getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;
    }

    private int getAuthorHeight(final Graphics g) {
        return this.model.getAuthor() == null ? 0 : getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
    }

    private Font getTitleFont(final Graphics g) {
        return g.getFont().deriveFont(Font.BOLD, 20);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderableMetaData that = (RenderableMetaData) o;
        return Objects.equal(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(model);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("model", model)
                .toString();
    }
}
