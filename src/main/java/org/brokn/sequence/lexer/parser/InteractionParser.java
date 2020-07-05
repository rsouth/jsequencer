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

            for (String line : lines) {

                // lines with -> are 'interactions'
                if (line.contains(INTERACTION_TOKEN)) {
                    String[] split = line.split(INTERACTION_TOKEN);
                    String fromNode = split[0].trim();
                    String toNode = split[1].trim();

                    String message = "";
                    if (toNode.contains(":")) {
                        String[] split1 = toNode.split(":");
                        message = split1[1];
                        toNode = split1[0];
                    }


                    interactions.add(new Interaction(laneByName(lanes, fromNode), laneByName(lanes, toNode), message));
                }
            }

        } catch (Exception ex) {
            log.warning("Exception while parsing interactions, exception: " + ex.getMessage());
            return new ArrayList<>();
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
