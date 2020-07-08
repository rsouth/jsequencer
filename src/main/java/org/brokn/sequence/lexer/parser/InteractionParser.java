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

package org.brokn.sequence.lexer.parser;

import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Parse interactions between Lanes.
 * An interaction is any message between a pair of Lanes.
 */
public class InteractionParser {

    private static final Logger log = Logger.getLogger(InteractionParser.class.getName());

    private static final String INTERACTION_TOKEN = "->";

    public List<Interaction> parse(List<Lane> lanes, String input) {
        List<Interaction> interactions = new ArrayList<>();

        try {
            String[] lines = input.split("\n");
            int interactionCount = 0;
            for (String line : lines) {
                // lines with -> are 'interactions'
                if (line.contains(INTERACTION_TOKEN)) {
                    String[] split = line.split(INTERACTION_TOKEN);
                    String fromNode = split[0].trim();
                    String toNode = split[1].trim();

                    // parse interaction message
                    String message = null;
                    try {
                        if (toNode.contains(":")) {
                            String[] split1 = toNode.split(":");
                            message = split1[1].trim();
                            toNode = split1[0].trim();
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        log.warning("Interaction message is incomplete, not parsing");
                    }

                    if(fromNode.length() > 0 && toNode.length() > 0) {
                        interactions.add(new Interaction(laneByName(lanes, fromNode), laneByName(lanes, toNode), message, interactionCount));
                        interactionCount++;
                    }
                }
            }

        } catch (Exception ex) {
            log.warning("Exception while parsing interactions, exception: " + ex.getMessage());
        }

        log.info("Found [" + interactions.size() + "] interactions " + interactions);
        return interactions;

    }

    private Lane laneByName(List<Lane> lanes, String name) {
        Optional<Lane> laneOptional = lanes.stream().filter(lane -> lane.getName().equals(name)).findFirst();
        if (!laneOptional.isPresent()) {
            throw new IllegalStateException("LEXER :: Got interaction for unknown Lane");
        }
        return laneOptional.get();
    }

}
