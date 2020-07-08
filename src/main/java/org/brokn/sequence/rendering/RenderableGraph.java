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

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderableGraph that = (RenderableGraph) o;
        return metaData.equals(that.metaData) &&
                lanes.equals(that.lanes) &&
                interactions.equals(that.interactions) &&
                renderableMetaData.equals(that.renderableMetaData) &&
                renderableLanes.equals(that.renderableLanes) &&
                renderableInteractions.equals(that.renderableInteractions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaData, lanes, interactions, renderableMetaData, renderableLanes, renderableInteractions);
    }
}
