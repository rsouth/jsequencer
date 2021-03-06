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

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    private RenderableDiagram renderableDiagram;

    public Canvas() {
        setVisible(true);
        setSize(1, 1);
        setDoubleBuffered(true);
    }

    public void updateModel(final RenderableDiagram model) {
        RenderableDiagram previousModel = this.renderableDiagram;
        this.renderableDiagram = model;

        if (!model.equals(previousModel)) {
            doLayout();
            paintComponent(getGraphics());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.renderableDiagram != null) {
            // update scale
            Graphics2D g2 = (Graphics2D) g;
            double scale = 1;
            g2.scale(scale, scale);

            // render the diagram
            this.renderableDiagram.draw(g);
            setPreferredSize(this.renderableDiagram.computeDiagramSize(g, false));
        }
    }

}
