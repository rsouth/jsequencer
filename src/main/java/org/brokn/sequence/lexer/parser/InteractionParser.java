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

    public List<Interaction> parse(List<Lane> lanes, String input) {
        List<Interaction> nodes = new ArrayList<>();

        try {
            String[] lines = input.split("\n");

            for (String line : lines) {

                // lines with -> are 'interactions'
                if (line.contains("->")) {
                    String[] split = line.split("->");
                    String fromNode = split[0].trim();
                    String toNode = split[1].trim();

                    String message = "";
                    if (toNode.contains(":")) {
                        String[] split1 = toNode.split(":");
                        message = split1[1];
                        toNode = split1[0];
                    }


                    nodes.add(new Interaction(laneByName(lanes, fromNode), laneByName(lanes, toNode), message));
                }
            }

        } catch (Exception ex) {
            return new ArrayList<>();
        }

        log.info("found interactions " + nodes);
        return nodes;

    }

    private Lane laneByName(List<Lane> lanes, String name) {
        Optional<Lane> laneOptional = lanes.stream().filter(lane -> lane.getName().equals(name)).findFirst();
        if (!laneOptional.isPresent()) {
            throw new IllegalStateException("LEXER :: Got interaction for unknown Lane");
        }
        return laneOptional.get();
    }

}
