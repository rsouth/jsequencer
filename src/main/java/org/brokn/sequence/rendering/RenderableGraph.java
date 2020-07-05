package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderableGraph {

    protected final List<Lane> lanes = new ArrayList<>();

    protected final List<Interaction> interactions = new ArrayList<>();

    public RenderableGraph(List<Lane> lanes, List<Interaction> interactions) {
        this.lanes.addAll(lanes);
        this.interactions.addAll(interactions);
    }

    public void draw(Graphics g) {
        // Draw Nodes
        for (int i = 0; i < this.lanes.toArray().length; i++) {
            Lane lane = this.lanes.get(i);
            new RenderableLane(lane).draw(g, this);
        }

        // Draw Interactions
        if (this.interactions.size() > 0) {
            for (int i = 0; i < this.interactions.toArray().length; i++) {
                Interaction s = this.interactions.get(i);
                new RenderableInteraction(s).draw(g, i);
            }
        }
    }
}
