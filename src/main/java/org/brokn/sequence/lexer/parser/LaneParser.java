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

import org.brokn.sequence.model.Lane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Parse unique Lanes from the input text.
 * A Lane is a sender or a receiver of a message.
 * A Lane is assigned an index based on the order they appear.
 */
public class LaneParser {

    private static final Logger log = Logger.getLogger(LaneParser.class.getName());

    public List<Lane> parse(final String input) {
        List<Lane> lanes = new ArrayList<>();

        try {
            String[] lines = input.split("\n");

            Set<String> knownLanes = new HashSet<>();

            for (String line : lines) {
                if (line.contains("->")) {
                    String[] split = line.split("->");
                    for (int i = 0; i < split.length; i++) {
                        String fromNode = split[0].trim();
                        String toNode = split[1].trim();

                        if (toNode.contains(":")) {
                            String[] split1 = toNode.split(":");
                            toNode = split1[0];
                        }

                        if (!knownLanes.contains(fromNode)) {
                            lanes.add(new Lane(lanes.size(), fromNode));
                            knownLanes.add(fromNode);
                        }

                        if (!knownLanes.contains(toNode)) {
                            lanes.add(new Lane(lanes.size(), toNode));
                            knownLanes.add(toNode);
                        }

                    }
                }
            }

        } catch (Exception ex) {
            log.warning("Exception while parsing lanes, exception: " + ex.getMessage());
        }

        log.info("Found [" + lanes.size() + "] Lanes " + lanes);
        return lanes;
    }

}
