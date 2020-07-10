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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;
import org.brokn.sequence.rendering.utils.LayoutHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderableDiagram {

    // Model objects
    private final MetaData metaData;
    private final List<Lane> lanes = new ArrayList<>();
    private final List<Interaction> interactions = new ArrayList<>();

    // Renderable objects
    private RenderableMetaData renderableMetaData;
    private final List<RenderableLane> renderableLanes = new ArrayList<>();
    private final List<RenderableInteraction> renderableInteractions = new ArrayList<>();

    public RenderableDiagram(MetaData metaData, List<Lane> lanes, List<Interaction> interactions) {
        this.metaData = metaData;
        this.lanes.addAll(lanes);
        this.interactions.addAll(interactions);

        initRenderables();
    }

    public void draw(Graphics g) {
        this.setRenderingHints(g);
        this.setFontSize(g, this.metaData.getFontSize());

        // Draw MetaData
        this.renderableMetaData.draw(g);
        int headerOffset = this.renderableMetaData.calculateHeaderHeight(g);

        // Draw Lanes
        int totalInteractions = 1 + this.interactions.stream().mapToInt(Interaction::getIndex).max().orElse(1);
        this.renderableLanes.forEach(renderableLane -> renderableLane.draw(g, headerOffset, totalInteractions));

        // Draw Interactions
        this.renderableInteractions.forEach(renderableInteraction -> renderableInteraction.draw(g, headerOffset));
    }

    public Dimension computeDiagramSize(Graphics g, boolean drawBorder) {
        int height = renderableMetaData.calculateHeaderHeight(g);
        height += RenderableLane.getVerticalLinePadding();
        height += (1 + this.interactions.stream().mapToInt(Interaction::getIndex).max().orElse(0) * LayoutHelper.CANVAS_VERTICAL_GAP);
        height += 50;

        int width = LayoutHelper.LANE_WIDTH * renderableLanes.size() + (LayoutHelper.LANE_GAP * renderableLanes.size());

        Dimension diagramDimensions = new Dimension(width, height);
        if (drawBorder) {
            g.drawRect(0, 0, diagramDimensions.width, diagramDimensions.height);
        }

        return diagramDimensions;
    }

    private void setRenderingHints(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private void setFontSize(Graphics g, float fontSize) {
        if (fontSize > 0) {
            g.setFont(g.getFont().deriveFont(fontSize));
        } else {
            g.setFont(g.getFont().deriveFont(14f));
        }
    }

    private void initRenderables() {
        this.renderableMetaData = new RenderableMetaData(metaData);
        this.lanes.forEach(lane -> renderableLanes.add(new RenderableLane(lane)));
        this.interactions.forEach(interaction -> renderableInteractions.add(new RenderableInteraction(interaction)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderableDiagram that = (RenderableDiagram) o;
        return Objects.equal(metaData, that.metaData) &&
                Objects.equal(lanes, that.lanes) &&
                Objects.equal(interactions, that.interactions) &&
                Objects.equal(renderableMetaData, that.renderableMetaData) &&
                Objects.equal(renderableLanes, that.renderableLanes) &&
                Objects.equal(renderableInteractions, that.renderableInteractions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(metaData, lanes, interactions, renderableMetaData, renderableLanes, renderableInteractions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("metaData", metaData)
                .add("lanes", lanes)
                .add("interactions", interactions)
                .add("renderableMetaData", renderableMetaData)
                .add("renderableLanes", renderableLanes)
                .add("renderableInteractions", renderableInteractions)
                .toString();
    }

}
