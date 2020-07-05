package org.brokn.sequence.lexer;

import org.brokn.sequence.lexer.parser.InteractionParser;
import org.brokn.sequence.lexer.parser.LaneParser;
import org.brokn.sequence.lexer.parser.MetaDataParser;
import org.brokn.sequence.model.Interaction;
import org.brokn.sequence.model.Lane;
import org.brokn.sequence.model.MetaData;
import org.brokn.sequence.rendering.RenderableGraph;

import java.util.List;

public class Lexer {

    private final MetaDataParser metaDataParser = new MetaDataParser();

    private final LaneParser laneParser = new LaneParser();

    private final InteractionParser interactionParser = new InteractionParser();

    public RenderableGraph parse(String input) {
        MetaData metaData = metaDataParser.parse(input);
        List<Lane> lanes = laneParser.parse(input);
        List<Interaction> interactions = interactionParser.parse(lanes, input);

        return new RenderableGraph(metaData, lanes, interactions);
    }

}
