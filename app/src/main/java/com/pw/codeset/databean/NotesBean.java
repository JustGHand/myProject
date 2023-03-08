package com.pw.codeset.databean;

import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.MD5Utils;
import com.pw.baseutils.utils.ArrayUtils;
import com.pw.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class NotesBean {

    public static final int NOTE_STATE_TODO = 0;
    public static final int NOTE_STATE_DONE = 1;

    String id;
    String title;
    long date;
    String content;
    int state;
    Integer pwCalendarId;
    List<String> labelList;
    List<String> imageList;

    public NotesBean() {
        date = System.currentTimeMillis();
        int randomNum = (int) (Math.random() * 1000);
        String idTxt = NStringUtils.dateConvert(date, Constant.DATA_PARTNER_WITH_LINE_TILE_SECOND) + randomNum;
        id = MD5Utils.strToMd5By16(idTxt);
        pwCalendarId = -1;
    }

    public String getId() {
        return id;
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

    public int getPwCalendarId() {
        if (pwCalendarId == null) {
            pwCalendarId = -1;
        }
        return pwCalendarId;
    }

    public void setPwCalendarId(int pwCalendarId) {
        this.pwCalendarId = pwCalendarId;
    }

    public List<String> getLabel() {
        return labelList;
    }

    public boolean haveLabel(String label) {
        if (ArrayUtils.isArrayEnable(labelList)) {
            if (labelList.contains(label)) {
                return true;
            }
        }
        return false;
    }

    public void setLabel(List<String> labelList) {
        this.labelList = labelList;
    }

    public void addLabel(String label) {
        if (labelList == null) {
            labelList = new ArrayList<>();
        }
        if (!haveLabel(label)) {
            labelList.add(label);
        }
    }

    public void removeLabel(String label) {
        if (haveLabel(label)) {
            labelList.remove(label);
        }
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public void addImage(String imagePath) {
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        imageList.add(imagePath);
    }

    public void removeImage(String imagePath) {
        if (imageList != null && imageList.contains(imagePath)) {
            imageList.remove(imagePath);
        }
    }

    public boolean haveDone() {
        return state == NOTE_STATE_DONE;
    }

    public int compareTo(NotesBean notesBean) {
        if (notesBean == null) {
            return -1;
        }
        if (this.state < notesBean.state) {
            return -1;
        } else if (this.state > notesBean.state) {
            return 1;
        }
        if (this.date > notesBean.date) {
            return -1;
        } else if (this.date < notesBean.date) {
            return 1;
        }else {
            return 0;
        }
    }
}
