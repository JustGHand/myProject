package com.pw.readcore.bean;

import java.io.Serializable;

/**
 * Created by newbiechen on 17-7-1.
 */

public abstract class TxtChapter implements Serializable {

    public String chapterId;

    //章节名(共用)
    public String title;

    //章节内容在文章中的起始位置(本地)
    public long start;
    //章节内容在文章中的终止位置(本地)
    public long end;

    //章节内容字数
    public int textCount;

    public TxtChapter() {

    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setTextCount(int count) {
        textCount = count;
    }

    public int getTextCount() {
        return textCount;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
