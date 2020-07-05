package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderableGraph {

    protected final MetaData metaData;

    protected final List<Lane> lanes = new ArrayList<>();

    protected final List<Interaction> interactions = new ArrayList<>();

    private RenderableMetaData renderableMetaData;

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
        for (int i = 0; i < this.lanes.toArray().length; i++) {
            Lane lane = this.lanes.get(i);
            new RenderableLane(lane).draw(g, this);
        }

        // Draw Interactions
        if (this.interactions.size() > 0) {
            for (int i = 0; i < this.interactions.toArray().length; i++) {
                Interaction s = this.interactions.get(i);
                new RenderableInteraction(s).draw(g, this, i);
            }
        }
    }

    private void initRenderables() {
        this.renderableMetaData = new RenderableMetaData(metaData);
    }

    protected int getMetaDataHeight(Graphics g) {
        if(this.renderableMetaData == null) {
            throw new IllegalStateException("getHeaderHeight - renderableMetaData is NULL");
        }

        return this.renderableMetaData.calculateHeaderHeight(g);
    }

}
