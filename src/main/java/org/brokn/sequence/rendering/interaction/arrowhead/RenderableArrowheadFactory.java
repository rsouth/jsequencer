package org.brokn.sequence.rendering.interaction.arrowhead;

import org.brokn.sequence.model.Interaction;

public class RenderableArrowheadFactory {

    public static RenderableArrowhead create(Interaction interaction) {
        return interaction.getModifiers().contains(Interaction.Modifiers.ASYNC)
                ? new RenderableAsyncArrowhead(interaction)
                : new RenderableSyncArrowhead(interaction);
    }

}
