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
import org.brokn.sequence.model.Lane;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.brokn.sequence.lexer.parser.InteractionParser.INTERACTION_MESSAGE_TOKEN;
import static org.brokn.sequence.lexer.parser.InteractionParser.INTERACTION_TOKEN;

/**
 * Parse unique Lanes from the input text.
 * A Lane is a sender or a receiver of a message.
 * A Lane is assigned an index based on the order they appear.
 */
public class LaneParser {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final Splitter lineSplitter = Splitter.onPattern("\n").omitEmptyStrings().trimResults();

    private final Splitter interactionSplitter = on(InteractionParser.INTERACTION_TOKEN).omitEmptyStrings().trimResults();
    private final Splitter interactionSplitter2 = on("-->").omitEmptyStrings().trimResults();

    private final Splitter laneAndMessageSplitter = on(INTERACTION_MESSAGE_TOKEN).omitEmptyStrings().trimResults();

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
            if(isValid(line)) {
                List<String> lanesSplit = new ArrayList<>();
                if(line.contains("-->")) {
                    lanesSplit.addAll(interactionSplitter2.splitToList(line));
                } else {
                    lanesSplit.addAll(interactionSplitter.splitToList(line));
                }

                // 'from' lane is the first entry
                if (lanesSplit.size() >= 1) {
                    String fromNode = lanesSplit.get(0);
                    laneNames.add(fromNode);
                }

                // 'to' lane is the second entry, but any message (":msg") must be removed first
                if (lanesSplit.size() == 2) {
                    String secondPart = lanesSplit.get(1);
                    List<String> toNodeAndMsg = laneAndMessageSplitter.splitToList(secondPart);
                    if (toNodeAndMsg.size() >= 1) {
                        laneNames.add(toNodeAndMsg.get(0));
                    }
                }

            }

        } catch (ArrayIndexOutOfBoundsException | IllegalStateException ex) {
            logger.atWarning().log("Exception thrown when parsing lane, parsed lane names [" + laneNames + "], message: " + ex.getMessage());
            return newArrayList(laneNames);
        }

        logger.atInfo().log("Parsed lane names: " + laneNames);
        return newArrayList(laneNames);
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
