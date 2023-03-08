package com.pw.readcore.bean;

import java.io.Serializable;

public class BookRecordBean implements Serializable {
    //阅读到了第几章
    private int chapter;
    //当前的页码
    private int pagePos;
    //当前页第一个字的pos
    private int charPos;

    public BookRecordBean() {
    }
    public int getChapter() {
        return this.chapter;
    }
    public void setChapter(int chapter) {
        this.chapter = chapter;
    }
    public int getPagePos() {
        return this.pagePos;
    }
    public void setPagePos(int pagePos) {
        this.pagePos = pagePos;
    }
    public int getCharPos() {
        return this.charPos;
    }
    public void setCharPos(int charPos) {
        this.charPos = charPos;
    }
}
