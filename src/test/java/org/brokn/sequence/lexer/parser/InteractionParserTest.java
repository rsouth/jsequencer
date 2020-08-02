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

import com.google.common.collect.Lists;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
class InteractionParserTest {

    private InteractionParser interactionParser;

    @BeforeEach
    void setUp() {
        this.interactionParser = new InteractionParser();
    }

    @Test
    void parse() {
        assertEquals(0, this.interactionParser.parse(null, null).size());
        assertEquals(0, this.interactionParser.parse(Lists.newArrayList(), null).size());
        assertEquals(0, this.interactionParser.parse(Lists.newArrayList(), "").size());
        assertEquals(0, this.interactionParser.parse(Lists.newArrayList(), "some invalid text").size());
        assertEquals(0, this.interactionParser.parse(Lists.newArrayList(new Lane(-1, null)), "some invalid text").size());
    }

    @Test
    void parseValidInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, "  Client ->Server  :   Request");
        assertEquals(1, parsed.size());
        assertEquals("Client", parsed.get(0).getFromLane().getName());
        assertEquals("Server", parsed.get(0).getToLane().getName());
        assertEquals("Request", parsed.get(0).getMessage());
        assertEquals(0, parsed.get(0).getIndex());
    }

    @Test
    void parseValidSelfReferentialInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, "  Server ->    Server  :   Request");
        assertEquals(1, parsed.size());
        assertEquals("Server", parsed.get(0).getFromLane().getName());
        assertEquals("Server", parsed.get(0).getToLane().getName());
        assertEquals("Request", parsed.get(0).getMessage());
        assertEquals(0, parsed.get(0).getIndex());
    }

    @Test
    void parseValidSelfReferentialInteraction_AsSecondInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, "Client-->Client: Thinks\n  Client ->>    Server  :   Request");

        // first interaction is self-referential
        assertEquals(2, parsed.size());
        assertEquals(0, parsed.get(0).getIndex());
        assertEquals("Client", parsed.get(0).getFromLane().getName());
        assertEquals("Client", parsed.get(0).getToLane().getName());
        assertEquals("Thinks", parsed.get(0).getMessage());
        assertSame(Interaction.InteractionType.Reply, parsed.get(0).getInteractionType());
        assertTrue(parsed.get(0).isSynchronous());

        // second interaction index should be 2 because self-referential interactions count as 2
        // i.e. index=1 was the 'return' leg of the self-referential interaction
        assertEquals(2, parsed.get(1).getIndex());
        assertEquals("Client", parsed.get(1).getFromLane().getName());
        assertEquals("Server", parsed.get(1).getToLane().getName());
        assertEquals("Request", parsed.get(1).getMessage());
        assertSame(Interaction.InteractionType.Message, parsed.get(1).getInteractionType());
        assertFalse(parsed.get(1).isSynchronous());
    }

    @Test
    void parseAsyncInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, " Client ->> Server");
        assertEquals(1, parsed.size());
        assertFalse(parsed.get(0).isSynchronous());
    }

    @Test
    void parseSyncInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, " Client -> Server");
        assertEquals(1, parsed.size());
        assertTrue(parsed.get(0).isSynchronous());

        parsed = this.interactionParser.parse(lanes, " Client-->>   Server");
        assertEquals(1, parsed.size());
        assertFalse(parsed.get(0).isSynchronous());
        assertSame(Interaction.InteractionType.Reply, parsed.get(0).getInteractionType());
    }

    @Test
    void parseReplyInteraction() {
        ArrayList<Lane> lanes = Lists.newArrayList(new Lane(0, "Client"), new Lane(1, "Server"));
        List<Interaction> parsed = this.interactionParser.parse(lanes, " Client --> Server");
        assertEquals(1, parsed.size());
        assertSame(Interaction.InteractionType.Reply, parsed.get(0).getInteractionType());

        parsed = this.interactionParser.parse(lanes, " Client-->>   Server");
        assertEquals(1, parsed.size());
        assertFalse(parsed.get(0).isSynchronous());
        assertSame(Interaction.InteractionType.Reply, parsed.get(0).getInteractionType());
    }

}