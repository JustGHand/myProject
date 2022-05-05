package com.pw.codeset.activity.model;

import java.util.List;
import java.util.Map;

public class NoteSaveModel {

    Map<String, NoteModel> models;

    public Map<String, NoteModel> getModels() {
        return models;
    }

    public void setModels(Map<String, NoteModel> models) {
        this.models = models;
    }

    public static class NoteModel{

        String content;
        String tag;
        Long timeStamp;

        public NoteModel() {
            this.timeStamp = System.currentTimeMillis();
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

}
