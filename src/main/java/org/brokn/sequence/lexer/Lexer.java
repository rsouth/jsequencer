package org.brokn.sequence.lexer;

import org.brokn.sequence.lexer.parser.InteractionParser;
import org.brokn.sequence.lexer.parser.LaneParser;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.rendering.RenderableGraph;

import java.util.*;

public class Lexer {

    private final LaneParser laneParser = new LaneParser();

    private final InteractionParser interactionParser = new InteractionParser();

    /**
     * Grammar:
     *
     * Sigma -> ROX: the object
     * ^ Sigma and ROX are both Lanes. 'the object' is the message from Sigma to ROX. 'the object' is optional.
     *
     * # comments
     */
    public RenderableGraph parse(String input) {
        List<Lane> lanes = laneParser.parse(input);
        List<Interaction> interactions = interactionParser.parse(lanes, input);

        return new RenderableGraph(lanes, interactions);
    }

}
