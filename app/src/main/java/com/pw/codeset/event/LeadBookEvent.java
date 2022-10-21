package com.pw.codeset.event;

public class LeadBookEvent {
    public LeadBookEvent() {
    }

    public LeadBookEvent(String filePath) {
        this.filePath = filePath;
        this.isAdd = true;
    }

    public LeadBookEvent(String filePath, boolean isAdd) {
        this.filePath = filePath;
        this.isAdd = isAdd;
    }

    String filePath;
    boolean isAdd;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }
}
