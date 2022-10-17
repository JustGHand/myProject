package com.pw.codeset.databean;

public class NotesBean {

    public static final int NOTE_STATE_TODO = 0;
    public static final int NOTE_STATE_DONE = 1;

    String id;
    String title;
    long date;
    String content;
    int state;

    public NotesBean() {
        date = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
