package org.brokn.sequence.rendering.interaction;

import org.brokn.sequence.model.Interaction;

public class RenderableInteractionFactory {

    public static RenderableInteraction create(Interaction interaction) {
        if(interaction.getModifiers().contains(Interaction.Modifiers.SELFREF)) {
            return new RenderableSelfReferentialInteraction(interaction);
        } else {
            return new RenderablePointToPointInteraction(interaction);
        }
    }

}
