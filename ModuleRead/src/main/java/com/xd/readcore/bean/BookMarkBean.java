package com.xd.readcore.bean;

import java.io.Serializable;

public class BookMarkBean implements Serializable {
    //阅读到了第几章
    private int chapter;
    //当前的页码
    private int pagePos;
    //当前页第一个字的pos
    private int charPos;
    //书签章节名
    private String chapterTitle;
    //书签页前两行文字
    private String markSimpleText;
    //保存书签时间
    private String markCreatTime;

    public BookMarkBean() {
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

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getMarkSimpleText() {
        return markSimpleText;
    }

    public void setMarkSimpleText(String markSimpleText) {
        this.markSimpleText = markSimpleText;
    }

    public String getMarkCreatTime() {
        return markCreatTime;
    }

    public void setMarkCreatTime(String markCreatTime) {
        this.markCreatTime = markCreatTime;
    }

}
