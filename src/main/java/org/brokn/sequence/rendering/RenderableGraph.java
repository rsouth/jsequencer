package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderableGraph {

    // Model objects
    protected final MetaData metaData;
    protected final List<Lane> lanes = new ArrayList<>();
    protected final List<Interaction> interactions = new ArrayList<>();

    // Renderable objects
    private RenderableMetaData renderableMetaData;
    private final List<RenderableLane> renderableLanes = new ArrayList<>();
    private final List<RenderableInteraction> renderableInteractions = new ArrayList<>();

    public RenderableGraph(MetaData metaData, List<Lane> lanes, List<Interaction> interactions) {
        this.metaData = metaData;
        this.lanes.addAll(lanes);
        this.interactions.addAll(interactions);

        initRenderables();
    }

    public void draw(Graphics g) {
        // Draw MetaData
        renderableMetaData.draw(g);

        // Draw Lanes
        this.renderableLanes.forEach(renderableLane -> renderableLane.draw(g));

        // Draw Interactions
        this.renderableInteractions.forEach(renderableInteraction -> renderableInteraction.draw(g));
    }

    private void initRenderables() {
        this.renderableMetaData = new RenderableMetaData(metaData);
        this.lanes.forEach(lane -> renderableLanes.add(new RenderableLane(this, lane)));
        this.interactions.forEach(interaction -> renderableInteractions.add(new RenderableInteraction(this, interaction)));
    }

    protected int getMetaDataHeight(Graphics g) {
        if(this.renderableMetaData == null) {
            throw new IllegalStateException("getHeaderHeight - renderableMetaData is NULL");
        }

        return this.renderableMetaData.calculateHeaderHeight(g);
    }

}
