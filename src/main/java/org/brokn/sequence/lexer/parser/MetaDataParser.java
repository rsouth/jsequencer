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

    public static final String TITLE_TOKEN = ":title ";
    public static final String AUTHOR_TOKEN = ":author ";
    public static final String DATE_TOKEN = ":date";
    public static final String FONT_SIZE_TOKEN = ":fontsize";

    public MetaData parse(String input) {
        // parse title
        String title = null;
        String author = null;
        boolean showDate = false;
        float fontSize = -1;
        try {
            for (String line : input.split("\n")) {
                if (line.trim().startsWith(TITLE_TOKEN)) {
                    title = getTokenValue(line, TITLE_TOKEN);

                } else if (line.startsWith(AUTHOR_TOKEN)) {
                    author = getTokenValue(line, AUTHOR_TOKEN);

                } else if (line.trim().equals(DATE_TOKEN)) {
                    showDate = true;

                } else if (line.trim().startsWith(FONT_SIZE_TOKEN)) {
                    fontSize = getFontSize(line);

                }
            }
        } catch (Exception ex) {
            log.warning("Exception occurred when parsing MetaData, message: " + ex.getMessage());
        }

        MetaData metaData = new MetaData(title, author, showDate, fontSize);
        log.info("Parsed " + metaData);
        return metaData;
    }

    private float getFontSize(String line) {
        float fontSize = -1;
        try {
            String fontSizeString = getTokenValue(line, FONT_SIZE_TOKEN);
            fontSize = Float.parseFloat(fontSizeString);

        } catch (NumberFormatException | NullPointerException ex) {
            log.warning("Font token specified but font size is not parseable");
        }

        return fontSize;
    }

    private String getTokenValue(String line, String token) {
        return line.trim().replace(token, "").trim();
    }

}
