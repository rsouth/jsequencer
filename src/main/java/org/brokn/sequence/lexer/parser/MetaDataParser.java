package org.brokn.sequence.lexer.parser;

import org.brokn.sequence.model.MetaData;

import java.util.logging.Logger;

/**
 * MetaData Grammar:
 * :title My Title Here
 * :author Author Name Here
 * :authorEmail Author's email address
 * :date
 *
 */
public class MetaDataParser {

    private static final Logger log = Logger.getLogger(MetaDataParser.class.getName());

    private static final String TITLE_TOKEN = ":title ";
    private static final String AUTHOR_TOKEN = ":author ";
    private static final String DATE_TOKEN = ":date";

    public MetaData parse(String input) {
        // parse title
        String title = null;
        String author = null;
        boolean showDate = false;
        for (String line : input.split("\n")) {
            if (line.trim().startsWith(TITLE_TOKEN)) {
                try {
                    title = line.trim().replace(":title", "").trim();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    title = null;
                    log.warning("incomplete title token");
                }

            } else if (line.startsWith(AUTHOR_TOKEN)) {
                try {
                    author = line.trim().replace(AUTHOR_TOKEN, "").trim();
                } catch(ArrayIndexOutOfBoundsException ex) {
                    author = null;
                    log.warning("incomplete author token");
                }

            } else if (line.trim().equals(DATE_TOKEN)) {
                showDate = true;
            }
        }

        MetaData metaData = new MetaData(title, author, showDate);
        log.info("Parsed " + metaData);
        return metaData;
    }
}
