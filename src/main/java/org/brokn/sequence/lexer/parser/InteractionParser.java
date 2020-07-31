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

import com.google.common.flogger.FluentLogger;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parse interactions between Lanes.
 * An interaction is any message between a pair of Lanes.
 */
public class InteractionParser {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    static final String INTERACTION_TOKEN = "->";

    static final String INTERACTION_REPLY_TOKEN = "-->";

    static final String INTERACTION_MESSAGE_TOKEN = ":";

    public @Nonnull List<Interaction> parse(@Nonnull List<Lane> lanes, @Nonnull String input) {
        List<Interaction> interactions = new ArrayList<>();

        try {
            String[] lines = input.split("\n");
            int interactionCount = 0;
            for (String line : lines) {
                // lines with -> are 'interactions', but they may be ->, -->, ->> or -->>
                if (line.contains(INTERACTION_TOKEN)) {
                    Interaction.InteractionType type = Interaction.InteractionType.Message;
                    String token = parseInteractionToken(line);
                    if(token.contains("--")) { /// INTERACTION_REPLY_TOKEN)) {
                        type = Interaction.InteractionType.Reply;
                    }

                    String[] split = line.split(token);
                    String fromNode = split[0].trim();
                    String toNode = split[1].trim();

                    // parse interaction message
                    String message = null;
                    try {
                        if (toNode.contains(":")) {
                            int messageStartIndex = toNode.indexOf(INTERACTION_MESSAGE_TOKEN);
                            String tmp = toNode;
                            toNode = tmp.substring(0, messageStartIndex).trim();
                            message = tmp.substring(messageStartIndex + 1).trim();
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        logger.atWarning().log("Interaction message is incomplete, not parsing");
                    }

                    if(fromNode.length() > 0 && toNode.length() > 0) {
                        interactions.add(new Interaction(laneByName(lanes, fromNode), laneByName(lanes, toNode), message, interactionCount, type, !token.contains(">>")));
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

    /**
     * I feel bad about this.
     * @param line
     * @return
     */
    public static String parseInteractionToken(String line) {
        if(line.contains("-->>")) {
            return "-->>";
        } else if (line.contains("->>")) {
            return "->>";
        } else if(line.contains("-->")) {
            return "-->";
        } else {
            return "->";
        }
    }

    private Lane laneByName(List<Lane> lanes, String name) {
        Optional<Lane> laneOptional = lanes.stream().filter(lane -> lane.getName().equals(name)).findFirst();
        return laneOptional.orElseThrow(() -> new IllegalStateException("LEXER :: Got interaction for unknown Lane [" + name + "]"));
    }

}
