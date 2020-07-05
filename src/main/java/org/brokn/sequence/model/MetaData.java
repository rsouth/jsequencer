package org.brokn.sequence.model;

public class MetaData {

    private final String title;

    private final String author;

    private final boolean showDate;

    public MetaData(String title, String author, boolean showDate) {
        this.title = title;
        this.author = author;
        this.showDate = showDate;
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

    @Override
    public String toString() {
        return "MetaData{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", showDate=" + showDate +
                '}';
    }
}
