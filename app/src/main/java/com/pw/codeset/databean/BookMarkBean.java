package com.pw.codeset.databean;

public class BookMarkBean {
    private String bookId;
    private int chapterPos;
    private int charPos;
    private String chapterTitle;
    private String lineContent;

    public BookMarkBean(String bookId, int chapterPos, int charPos) {
        this.bookId = bookId;
        this.chapterPos = chapterPos;
        this.charPos = charPos;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getChapterPos() {
        return chapterPos;
    }

    public void setChapterPos(int chapterPos) {
        this.chapterPos = chapterPos;
    }

    public int getCharPos() {
        return charPos;
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

    public String getLineContent() {
        return lineContent;
    }

    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }
}
