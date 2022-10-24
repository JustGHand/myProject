package com.pw.read.manager;

import com.pw.read.bean.ChaptersBean;

import java.io.BufferedReader;

public class ReadManager {
    public static ReadManager mInstance;
    public static ReadManager getInstance() {
        if (mInstance == null) {
            synchronized (ReadManager.class) {
                if (mInstance == null) {
                    mInstance = new ReadManager();
                }
            }
        }
        return mInstance;
    }

    ReadDataInterface mInterface;

    public ReadManager() {

    }

    public void init() {

    }

    public void openBook() {

    }

    public BufferedReader getChapterContent(ChaptersBean chaptersBean) throws Exception{
        if (mInterface != null) {
            return mInterface.getChapterReader(chaptersBean);
        }
        return null;
    }

}
