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
            return new ArrayList<>();
        }

        log.info("Found [" + lanes.size() + "] Lanes " + lanes);
        return lanes;
    }

}
