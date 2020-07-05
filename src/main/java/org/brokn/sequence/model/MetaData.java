package org.brokn.sequence.model;

public class MetaData {

    private final String title;

    private final String authorName;

    private final String authorEmail;

    private final boolean showDate;

    public MetaData(String title, String authorName, String authorEmail, boolean showDate) {
        this.title = title;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.showDate = showDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public boolean isShowDate() {
        return showDate;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "title='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", showDate=" + showDate +
                '}';
    }
}
