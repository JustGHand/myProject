package com.pw.read.manager;

import com.pw.read.bean.ChaptersBean;

import java.io.BufferedReader;
import java.util.List;

public interface ReadDataInterface {
    public abstract List<? extends ChaptersBean> getChapterList();

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    public abstract BufferedReader getChapterReader(ChaptersBean chapter);
}
