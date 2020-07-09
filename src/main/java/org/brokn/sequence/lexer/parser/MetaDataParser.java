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
                    try {
                        title = line.trim().replace(":title", "").trim();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        title = null;
                        log.warning("incomplete title token");
                    }

                } else if (line.startsWith(AUTHOR_TOKEN)) {
                    try {
                        author = line.trim().replace(AUTHOR_TOKEN, "").trim();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        author = null;
                        log.warning("incomplete author token");
                    }

                } else if (line.trim().equals(DATE_TOKEN)) {
                    showDate = true;

                } else if (line.trim().startsWith(FONT_SIZE_TOKEN)) {
                    try {
                        String fontSizeString = line.trim().replace(FONT_SIZE_TOKEN, "").trim();
                        fontSize = Float.parseFloat(fontSizeString);

                    } catch (NumberFormatException | NullPointerException ex) {
                        fontSize = -1;
                        log.warning("Font token specified but font size is not parseable");
                    }

                }
            }
        } catch (Exception ex) {
            log.warning("Exception occurred when parsing MetaData, message: " + ex.getMessage());
        }

        MetaData metaData = new MetaData(title, author, showDate, fontSize);
        log.info("Parsed " + metaData);
        return metaData;
    }
}
