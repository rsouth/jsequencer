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

package org.brokn.sequence.lexer;

import org.brokn.sequence.lexer.parser.InteractionParser;
import org.brokn.sequence.lexer.parser.LaneParser;
import org.brokn.sequence.lexer.parser.MetaDataParser;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;
import org.brokn.sequence.rendering.RenderableDiagram;

import java.util.List;

public class Lexer {

    private final MetaDataParser metaDataParser = new MetaDataParser();

    private final LaneParser laneParser = new LaneParser();

    private final InteractionParser interactionParser = new InteractionParser();

    public RenderableDiagram parse(String input) {
        MetaData metaData = metaDataParser.parse(input);
        List<Lane> lanes = laneParser.parse(input);
        List<Interaction> interactions = interactionParser.parse(lanes, input);

        return new RenderableDiagram(metaData, lanes, interactions);
    }

}
