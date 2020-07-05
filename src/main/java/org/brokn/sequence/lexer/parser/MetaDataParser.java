package org.brokn.sequence.lexer.parser;

import org.brokn.sequence.model.MetaData;

/**
 * MetaData Grammar:
 * :title My Title Here
 * :author Author Name Here
 * :authorEmail author@email.here
 * :date
 *
 */
public class MetaDataParser {

    private static final String TITLE_TOKEN = ":title ";
    private static final String AUTHOR_NAME_TOKEN = ":author ";
    private static final String AUTHOR_EMAIL_TOKEN = ":authorEmail ";
    private static final String DATE_TOKEN = ":date";

    public MetaData parse(String input) {
        // parse title
        String title = null;
        String authorName = null;
        String authorMail = null;
        boolean showDate = false;
        for (String line : input.split("\n")) {
            if (line.trim().startsWith(TITLE_TOKEN)) {
                try {
                    title = line.trim().replace(":title", "").trim();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    title = null;
                    System.out.println("incomplete title token");
                }

            } else if (line.startsWith(AUTHOR_NAME_TOKEN)) {
                try {
                    authorName = line.trim().replace(AUTHOR_NAME_TOKEN, "").trim();
                } catch(ArrayIndexOutOfBoundsException ex) {
                    authorName = null;
                    System.out.println("incomplete author token");
                }

            } else if (line.startsWith(AUTHOR_EMAIL_TOKEN)) {
                try {
                    authorMail = line.trim().replace(AUTHOR_EMAIL_TOKEN, "").trim();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    authorMail = null;
                    System.out.println("incomplete authorEmail token");
                }

            } else if (line.trim().equals(DATE_TOKEN)) {
                showDate = true;
            }
        }

        MetaData metaData = new MetaData(title, authorName, authorMail, showDate);
        System.out.println("Parsed " + metaData);
        return metaData;
    }
}
