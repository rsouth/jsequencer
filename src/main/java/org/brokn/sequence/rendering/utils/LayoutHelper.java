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

package org.brokn.sequence.rendering.utils;

public final class LayoutHelper {

    private LayoutHelper() { }

    // Vertical gap between anything separated vertically (nodes/interactions/notes)
    // taken from Canvas
    public static final int CANVAS_VERTICAL_GAP = 50;

    // taken from RenderableInteraction
    public static final int RI_ARROWHEAD_LENGTH = 10;
    public static final int RI_MESSAGE_X_PADDING = 5;
    // end taken from RenderablsInteraction


    // taken from RenderableMetadata
    public static final int RM_VERTICAL_GAP = 20;
    public static final int RM_DOCUMENT_MARGIN = 10;
    // end taken from RenderableMetaData


    // taken from RenderableLane
    public static final int LANE_WIDTH = 150;
    public static final int LANE_GAP = 50;
    public static final int LANE_BOX_HEIGHT = 30;
    public static final int LANE_BOX_PADDING = 20;
    // end taken from RenderableLane

}
