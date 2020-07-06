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
        this.setRenderingHints(g);
        this.setTheme(g, this.metaData);

        // Draw MetaData
        renderableMetaData.draw(g);

        // Draw Lanes
        this.renderableLanes.forEach(renderableLane -> renderableLane.draw(g));

        // Draw Interactions
        this.renderableInteractions.forEach(renderableInteraction -> renderableInteraction.draw(g));
    }

    private void setTheme(Graphics g, MetaData metaData) {
        if (metaData.getFontSize() > 0) {
            g.setFont(g.getFont().deriveFont(metaData.getFontSize()));
        } else {
            g.setFont(g.getFont().deriveFont(14f));
        }
    }

    public Dimension computeDiagramSize(Graphics g, boolean drawBorder) {
        int height = renderableMetaData.calculateHeaderHeight(g);
        height += RenderableLane.getVerticalLinePadding();
        height += (this.interactions.size() * Canvas.VERTICAL_GAP);
        height += 50;

        int width = RenderableLane.LANE_WIDTH * renderableLanes.size() + (RenderableLane.LANE_GAP * renderableLanes.size());

        Dimension diagramDimensions = new Dimension(width, height);
        if(drawBorder) {
            g.drawRect(0, 0, diagramDimensions.width, diagramDimensions.height);
        }

        return diagramDimensions;
    }

    private void setRenderingHints(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
