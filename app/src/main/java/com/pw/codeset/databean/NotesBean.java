package com.pw.codeset.databean;

import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.MD5Utils;
import com.xd.baseutils.utils.NStringUtils;

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
        int randomNum = (int) (Math.random() * 1000);
        String idTxt = NStringUtils.dateConvert(date, Constant.DATA_PARTNER_WITH_LINE_TILE_SECOND) + randomNum;
        id = MD5Utils.strToMd5By16(idTxt);
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
