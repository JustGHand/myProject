package com.pw.readcore.bean;

import java.io.Serializable;

public class ChapterBean implements Serializable {
    ChapterBean() {

    }

    private String bookId;
    private String siteId;

    private String link;

    private String title;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
