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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaneParserTest {

    private LaneParser laneParser;

    @BeforeEach
    void setUp() {
        this.laneParser = new LaneParser();
    }

    @Test
    void parseBadData() {
        assertEquals(0, this.laneParser.parse(null).size());
        assertEquals(0, this.laneParser.parse("").size());
    }

    @Test
    void parseValidCase_EmptyToLane() {
        // One Lane
        List<Lane> testOne = this.laneParser.parse(" Client    ->  ");
        assertEquals(1, testOne.size());
        assertEquals("Client", testOne.get(0).getName());
        assertEquals(0, testOne.get(0).getIndex());
    }

    @Test
    void parseValidCase_CompleteExample() {
        // Complete Example
        List<Lane> testTwo = this.laneParser.parse("  Client  ->   Server");
        assertEquals(2, testTwo.size());
        assertEquals("Client", testTwo.get(0).getName());
        assertEquals("Server", testTwo.get(1).getName());
        assertEquals(0, testTwo.get(0).getIndex());
        assertEquals(1, testTwo.get(1).getIndex());
    }

    @Test
    void parseInvalidCase_OneLane() {
        // One Lane
        List<Lane> testOne = this.laneParser.parse(" Client ");
        assertEquals(1, testOne.size());
        assertEquals("Client", testOne.get(0).getName());
        assertEquals(0, testOne.get(0).getIndex());
    }

    @Test
    void parseInvalidCase_NoFromLane() {
        // Invalid Examples
        List<Lane> testThree = this.laneParser.parse("    ->   Server");
        assertEquals(0, testThree.size());
    }

    @Test
    void parseValidCase_TooManyLanes() {
        // Example with 3 lanes
        List<Lane> testTwo = this.laneParser.parse("  Client  ->   Server  -> Database");
        assertEquals(0, testTwo.size());
    }

    @Test
    void parseValidCase_BadlyFormatted() {
        // Incorrect syntax
        List<Lane> testTwo = this.laneParser.parse("  Client  -> :Server");
        assertEquals(0, testTwo.size());
    }


}