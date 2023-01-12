package com.pw.codeset.databean;

public class BookBean {

    public static final int BOOKFILETYPE_TXT = 1;
    public static final int BOOKFILETYPE_EPUB = 2;

    String bookId;
    String bookPath;
    int fileType;

    String bookName;
    Long lastReadTime;

    int totalCharCount;

    public BookBean() {
    }

    public BookBean(String bookId, String bookPath, int fileType, String bookName) {
        this.bookId = bookId;
        this.bookPath = bookPath;
        this.fileType = fileType;
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(Long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public int getTotalCharCount() {
        return totalCharCount;
    }

    public void setTotalCharCount(int totalCharCount) {
        this.totalCharCount = totalCharCount;
    }
}
