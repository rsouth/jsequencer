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
import org.apache.commons.lang3.StringUtils;
import org.brokn.sequence.model.Lane;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.brokn.sequence.lexer.parser.InteractionParser.*;
import static org.brokn.sequence.model.Interaction.formatToken;

/**
 * Parse unique Lanes from the input text.
 * A Lane is a sender or a receiver of a message.
 * A Lane is assigned an index based on the order they appear.
 */
public class LaneParser {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final Splitter lineSplitter = Splitter.onPattern("\n").omitEmptyStrings().trimResults();

    public List<Lane> parse(final String input) {
        List<Lane> foundLanes = new ArrayList<>();

        try {
            List<String> inputLines = lineSplitter.splitToList(input);
            List<String> knownLanes = new ArrayList<>();

            // iterate input, parsing [FirstLane] -> [SecondLane] : Message
            for (String line : inputLines) {
                if (line.contains(INTERACTION_TOKEN)) {
                    knownLanes.addAll(parseLaneNames(line));
                }
            }

            // create a new Lane for each unique lane name we found
            List<String> uniqueLanes = newArrayList(newLinkedHashSet(knownLanes));
            for (int i = 0; i < uniqueLanes.size(); i++) {
                foundLanes.add(new Lane(i, uniqueLanes.get(i)));
            }

        } catch (Exception ex) {
            logger.atWarning().log("Exception while parsing lanes, exception: " + ex.getMessage());
        }

        logger.atInfo().log("Found [" + foundLanes.size() + "] Lanes " + foundLanes);
        return foundLanes;
    }

    private List<String> parseLaneNames(String line) {
        Set<String> laneNames = new LinkedHashSet<>();

        try {
            if (isValid(line)) {
                final String token = formatToken(parseInteractionType(line));

                // 'from' lane is the first entry
                laneNames.add(parseFromNodeName(line, token));

                // 'to' lane is the second entry, but any message (":msg") must be removed first
                laneNames.add(parseToNodeName(line, token));

            }
        } catch (InteractionParsingException ex) {
            logger.atWarning().log("Exception thrown when parsing lane, parsed lane names [" + laneNames + "], message: " + ex.getMessage());
            return newArrayList(laneNames);
        }

        logger.atInfo().log("Parsed lane names: " + laneNames);
        return newArrayList(laneNames);
    }

    public static String parseFromNodeName(String line, String token) throws InteractionParsingException {
        final List<String> strings = Splitter.on(token).trimResults().splitToList(line);
        if(strings.size() == 0) {
            String error = MessageFormat.format("Error parsing From node name from {0} with token {1}", line, token);
            throw new InteractionParsingException(error);
        }
        return strings.get(0);
    }

    public static String parseToNodeName(String line, String token) throws InteractionParsingException {
        final List<String> strings = Splitter.on(token).trimResults().splitToList(line);
        if(strings.size() < 2) {
            String error = MessageFormat.format("Error parsing To node name from {0} with token {1}", line, token);
            throw new InteractionParsingException(error);
        }

        if(StringUtils.isEmpty(strings.get(1))) {
            String error = MessageFormat.format("To node name is empty in line {0} with token {1}", line, token);
            throw new InteractionParsingException(error);
        }

        if(strings.get(1).contains(":")) {
            return strings.get(1).substring(0, strings.get(1).indexOf(":")).trim();
        }
        return strings.get(1).trim();
    }

    private boolean isValid(String line) {
        // it is not valid to have no 'from' Lane
        if(line.startsWith(INTERACTION_TOKEN)) {
            return false;
        }

        // only expect one instance of "->"
        if(line.indexOf(INTERACTION_TOKEN) != line.lastIndexOf(INTERACTION_TOKEN)) {
            return false;
        }

        // cannot have a message (:) without a toNode being named
        if(line.contains(INTERACTION_MESSAGE_TOKEN) && !line.contains(INTERACTION_TOKEN)) {
            return false;
        }

        // check that message token : is AFTER the toNode name
        if(line.contains(INTERACTION_TOKEN) && line.contains(INTERACTION_MESSAGE_TOKEN)) {
            if(line.split(INTERACTION_TOKEN)[1].trim().startsWith(INTERACTION_MESSAGE_TOKEN)) {
                // first instance of : must be after the toNode name
                return false;
            }
        }

        return true;
    }

}
