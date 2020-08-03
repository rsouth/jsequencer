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

import com.google.common.base.Splitter;
import com.google.common.flogger.FluentLogger;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.brokn.sequence.lexer.parser.LaneParser.parseFromNodeName;
import static org.brokn.sequence.lexer.parser.LaneParser.parseToNodeName;

/**
 * Parse interactions between Lanes.
 * An interaction is any message between a pair of Lanes.
 */
public class InteractionParser {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    static final String INTERACTION_TOKEN = "->";

    static final String INTERACTION_MESSAGE_TOKEN = ":";

    private final Splitter newLineSplitter = Splitter.on("\n");

    public @Nonnull List<Interaction> parse(@Nonnull List<Lane> lanes, @Nonnull String input) {
        List<Interaction> interactions = new ArrayList<>();

        try {
            List<String> lines = newLineSplitter.splitToList(input);
            int interactionCount = 0;
            for (String line : lines) {
                // lines with -> are 'interactions', but they may be ->, -->, ->> or -->>
                if (line.contains(INTERACTION_TOKEN)) {
                    // parse interaction type
                    EnumSet<Interaction.Modifiers> modifiers = parseInteractionType(line);
                    String token = Interaction.formatToken(modifiers);

                    // parse involved nodes
                    String fromNode = parseFromNodeName(line, token);
                    String toNode   = parseToNodeName(line, token);

                    // parse interaction message
                    String message = parseInteractionMessage(line);

                    // Create Interaction and add to list.
                    if(fromNode.length() > 0 && toNode.length() > 0) {
                        interactions.add(new Interaction(laneByName(lanes, fromNode), laneByName(lanes, toNode), message, interactionCount, modifiers));
                        interactionCount++;
                        if(fromNode.equals(toNode)) {
                            // self-referential so increment interaction count one more time, for the interaction back to self
                            interactionCount++;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.atWarning().log("Exception while parsing interactions, exception: " + ex.getMessage());
        }

        logger.atInfo().log("Found [" + interactions.size() + "] interactions " + interactions);
        return interactions;

    }

    public static EnumSet<Interaction.Modifiers> parseInteractionType(String line) {
        EnumSet<Interaction.Modifiers> modifiers = EnumSet.noneOf(Interaction.Modifiers.class);
        int interactionTokenIndex = line.indexOf("->");

        if((interactionTokenIndex - 1 >= 0) && line.charAt(interactionTokenIndex-1) == '-') {
            modifiers.add(Interaction.Modifiers.REPLY);
        }

        if((line.length() > interactionTokenIndex + 2) && line.charAt(interactionTokenIndex+2) == '>') {
            modifiers.add(Interaction.Modifiers.ASYNC);
        }

        return modifiers;
    }

    private String parseInteractionMessage(String toNode) throws InteractionParsingException {
        String message = null;
        try {
            if (toNode.contains(":")) {
                int messageStartIndex = toNode.indexOf(INTERACTION_MESSAGE_TOKEN);
                message = toNode.substring(messageStartIndex + 1).trim();
            }
        } catch (IndexOutOfBoundsException ex) {
            final String error = MessageFormat.format("Interaction message is incomplete, not parsing message from {0}", toNode);
            logger.atWarning().log(error);
            throw new InteractionParsingException(error, ex);
        }
        return message;
    }

    private Lane laneByName(List<Lane> lanes, String name) {
        Optional<Lane> laneOptional = lanes.stream().filter(lane -> lane.getName().equals(name)).findFirst();
        return laneOptional.orElseThrow(() -> new IllegalStateException("LEXER :: Got interaction for unknown Lane [" + name + "]"));
    }

    static class InteractionParsingException extends Exception {
        public InteractionParsingException(String message) {
            super(message);
        }

        public InteractionParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
