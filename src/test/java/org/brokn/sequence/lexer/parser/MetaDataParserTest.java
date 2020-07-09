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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaDataParserTest {

    private MetaDataParser metaDataParser;

    @BeforeEach
    void setUp() {
        this.metaDataParser = new MetaDataParser();
    }

    @Test
    void parse() {
        assertNull(this.metaDataParser.parse(null).getTitle());
        assertNull(this.metaDataParser.parse(null).getAuthor());
        assertEquals(-1.0f, this.metaDataParser.parse(null).getFontSize());
    }
}