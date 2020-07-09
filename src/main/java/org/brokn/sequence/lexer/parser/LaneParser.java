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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.brokn.sequence.model.Lane;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Splitter.on;
import static org.brokn.sequence.lexer.parser.InteractionParser.INTERACTION_MESSAGE_TOKEN;
import static org.brokn.sequence.lexer.parser.InteractionParser.INTERACTION_TOKEN;

/**
 * Parse unique Lanes from the input text.
 * A Lane is a sender or a receiver of a message.
 * A Lane is assigned an index based on the order they appear.
 */
public class LaneParser {

    private static final Logger log = Logger.getLogger(LaneParser.class.getName());

    private final Splitter lineSplitter = Splitter.onPattern("\n").omitEmptyStrings().trimResults();

    private final Splitter interactionSplitter = on(InteractionParser.INTERACTION_TOKEN).omitEmptyStrings().trimResults();

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
            List<String> strings = Lists.newArrayList(Sets.newHashSet(knownLanes));
            for (int i = 0; i < strings.size(); i++) {
                foundLanes.add(new Lane(i, strings.get(i)));
            }

        } catch (Exception ex) {
            log.warning("Exception while parsing lanes, exception: " + ex.getMessage());
        }

        log.info("Found [" + foundLanes.size() + "] Lanes " + foundLanes);
        return foundLanes;
    }

    private List<String> parseLaneNames(String line) {
        List<String> laneNames = new ArrayList<>();

        try {
            List<String> lanesSplit = interactionSplitter.splitToList(line);

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

            // too many ->'s
            if(lanesSplit.size() < 1 || lanesSplit.size() > 2) {
                throw new IllegalStateException("Invalid line " + line);
            }

        } catch (ArrayIndexOutOfBoundsException | IllegalStateException ex) {
            log.warning("Exception thrown when parsing lane, message: " + ex.getMessage());
            return Lists.newArrayList();
        }

        return laneNames;
    }

}
