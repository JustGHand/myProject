package com.pw.readcore.manager;

import com.pw.readcore.bean.LayoutMode;
import com.pw.readcore.bean.PageMode;
import com.pw.readcore.bean.PageStyle;
import com.pw.readcore.bean.BookRecordBean;
import com.pw.readcore.bean.TxtChapter;

import java.io.BufferedReader;
import java.util.List;

public abstract class ExternalManager {
    public abstract int getPageCountPerAd();

    public abstract int getPageCountPerFullPageAd();

    public abstract int getTextSize();
    public abstract LayoutMode getLayoutMode();
    public abstract boolean getHaveDisplayCutout();
    public abstract int getDisplayCutoutRight();
    public abstract int getAutoReadSpeed();
    public abstract String getFontPath();
    public abstract PageMode getPageMode();
    public abstract PageStyle getPageStyle();

    public abstract int getPageAdEnterY();

    public abstract void readError();

    public abstract void saveBookRecord(BookRecordBean bookRecordBean);

    public abstract BookRecordBean getBookRecord();

    public abstract int getAdRemoveTime();

    /**
     * 刷新章节列表
     */
    public abstract List<? extends TxtChapter> getChapterList();

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    public abstract BufferedReader getChapterReader(TxtChapter chapter) throws Exception;

    /**
     * 章节数据是否存在
     *
     * @return
     */
    public abstract boolean hasChapterData(TxtChapter chapter);
}
