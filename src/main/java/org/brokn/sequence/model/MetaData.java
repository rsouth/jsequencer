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

package org.brokn.sequence.model;

public class MetaData {

    private final String title;

    private final String author;

    private final boolean showDate;

    private final float fontSize;

    public MetaData(String title, String author, boolean showDate, float fontSize) {
        this.title = title;
        this.author = author;
        this.showDate = showDate;
        this.fontSize = fontSize;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public float getFontSize() {
        return fontSize;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", showDate=" + showDate +
                ", fontSize=" + fontSize +
                '}';
    }

}
