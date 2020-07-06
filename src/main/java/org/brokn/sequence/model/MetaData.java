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
