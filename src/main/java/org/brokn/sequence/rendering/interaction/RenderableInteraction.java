package org.brokn.sequence.rendering.interaction;

import org.brokn.sequence.model.Interaction;

import javax.swing.*;
import java.awt.*;

import static org.brokn.sequence.rendering.utils.LayoutHelper.MESSAGE_PADDING;

public abstract class RenderableInteraction {

    protected final Interaction interaction;

    public RenderableInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public void draw(Graphics g, int verticalOffset) {
        if(this.interaction.getModifiers().contains(Interaction.Modifiers.REPLY)) {
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            ((Graphics2D)g).setStroke(dashed);
        }
    }

    protected void renderInteractionMessage(Graphics g, int interactionFromXPosition, int interactionFromYPosition, int interactionToXPosition) {
        this.interaction.getMessage().ifPresent(message -> {
            boolean isRightFacing = interactionFromXPosition < interactionToXPosition;
            int messageWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), message);
            int labelX = isRightFacing ? interactionFromXPosition + MESSAGE_PADDING : interactionFromXPosition - (messageWidth + MESSAGE_PADDING);
            g.drawString(message, labelX, interactionFromYPosition - MESSAGE_PADDING);
        });
    }

}
