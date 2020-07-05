package org.brokn.sequence.rendering;

import org.brokn.sequence.model.MetaData;

import java.awt.*;
import java.time.LocalDate;

public class RenderableMetaData {

    private static final int VERTICAL_GAP = 20;

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
        }

        // date
        if(this.model.isShowDate()) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), "date").height;
        }

        return totalHeight;
    }

    public void draw(Graphics g) {

        // draw title
        if(this.model.getTitle() != null) {
            Font originalFont = g.getFont();
            g.setFont(getTitleFont(g));

            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;
            g.drawString(this.model.getTitle(), 10, 10 + titleHeight);

            g.setFont(originalFont);
        }

        // draw author name
        if(this.model.getAuthor() != null) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;// 0 if no title shown
            int authorHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            int y = titleHeight + (this.model.getTitle() == null ? 10 : VERTICAL_GAP) + authorHeight;
            g.drawString(this.model.getAuthor(), 10, y);
        }

        // draw current date (10th June 2020 so no regional ambiguity)
        // todo worth adding ability to format the date?
        if(this.model.isShowDate()) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, getTitleFont(g), this.model.getTitle()).height;// 0 if no title shown
            int authorHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), this.model.getAuthor()).height;
            int dateHeight = LayoutUtils.getStringBounds((Graphics2D) g, g.getFont(), "date").height;

            int spacing = (this.model.getTitle() != null || this.model.getAuthor() != null ? VERTICAL_GAP : 0);
            int y = titleHeight + authorHeight + dateHeight + spacing;

            g.drawString(LocalDate.now().toString(), 10, y);
        }
    }

    private Font getTitleFont(Graphics g) {
        return new Font(g.getFont().getName(), Font.BOLD, 20);
    }

}
