package org.brokn.sequence.rendering;

import org.brokn.sequence.model.MetaData;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class RenderableMetaData {

    int verticalGap = 10;

    private final MetaData model;

    public RenderableMetaData(MetaData metaData) {
        this.model = metaData;
    }

    public int calculateHeaderHeight(Graphics g) {
        int totalHeight = 10;

        //title
        if(this.model.getTitle() != null) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, this.model.getTitle()).height;
//            totalHeight += verticalGap;
        }

        // author
        if(this.model.getAuthorName() != null) {
            Font originalFont = g.getFont();
            g.setFont(getTitleFont(g));
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, this.model.getAuthorName()).height;
            g.setFont(originalFont);
//            totalHeight += verticalGap;
        }

        // author email
        if(this.model.getAuthorEmail() != null) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, this.model.getAuthorEmail()).height;
//            totalHeight += verticalGap;
        }

        // date
        if(this.model.isShowDate()) {
            totalHeight += LayoutUtils.getStringBounds((Graphics2D) g, "date").height;
//            totalHeight += verticalGap;
        }

        return totalHeight;
    }

    private Font getTitleFont(Graphics g) {
        return new Font(g.getFont().getName(), Font.BOLD, 20);
    }

    public void draw(Graphics g) {

        // draw title
        if(this.model.getTitle() != null) {
            Font originalFont = g.getFont();
            Font titleFont = new Font(originalFont.getName(), Font.BOLD, 20);
            g.setFont(titleFont);

            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getTitle()).height;

            g.drawString(this.model.getTitle(), 10, 10 + titleHeight);
            g.setFont(originalFont);

        }

        // draw author name
        if(this.model.getAuthorName() != null) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getTitle()).height;// 0 if no title shown
            g.drawString(this.model.getAuthorName(), 10, 10 + titleHeight*2 + verticalGap*2);
        }

        // draw author email
        if(this.model.getAuthorEmail() != null) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getTitle()).height;// 0 if no title shown
            int authorWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), this.model.getAuthorName());
            g.drawString("(" + this.model.getAuthorEmail() + ")", 15 + authorWidth, 10 + titleHeight*2 + verticalGap*2);
        }

        // draw current date (10th June 2020 so no regional ambiguity)
        // todo worth adding ability to format the date?
        if(this.model.isShowDate()) {
            int titleHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getTitle()).height;// 0 if no title shown
            int authorHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getAuthorName()).height;
            int emailHeight = LayoutUtils.getStringBounds((Graphics2D) g, this.model.getAuthorEmail()).height;
            int spacing = 0;
            if(authorHeight > 0) {
                spacing += verticalGap;
            }
            if(emailHeight > 0) {
                spacing += verticalGap;
            }
            int priotHeight = titleHeight + authorHeight + emailHeight + spacing;

            g.drawString(LocalDate.now().toString(), 10, 10 + priotHeight);
        }
    }
}
