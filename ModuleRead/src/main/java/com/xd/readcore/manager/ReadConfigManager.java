package com.xd.readcore.manager;

import com.xd.readcore.bean.PageMode;
import com.xd.readcore.bean.PageStyle;

public class ReadConfigManager {

    public static ReadConfigManager mInstance;

    public static ReadConfigManager getInstance(ReadDispatch readDispatch) {
        if (mInstance == null) {
            synchronized (ReadConfigManager.class) {
                if (mInstance ==null)
                    mInstance = new ReadConfigManager(readDispatch);
            }
        }
        return mInstance;
    }

    private ReadDispatch mReadDispatch;

    public ReadConfigManager(ReadDispatch readDispatch) {
        mReadDispatch = readDispatch;
    }

    // 页面的翻页效果模式
    private PageMode mPageMode;
    // 加载器的颜色主题
    private PageStyle mPageStyle;

    public void init() {
        mPageMode = mReadDispatch.getPageMode();
        mPageStyle = mReadDispatch.getPageStyle();
    }

    public void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;
    }

    public PageMode getPageMode() {
        if (mPageMode==PageMode.AUTO) {
            mPageMode = PageMode.SIMULATION;
        }
        return mPageMode;
    }

    public PageStyle getPageStyle() {
        return mPageStyle;
    }

    public void setPageStyle(PageStyle pageStyle) {
        mPageStyle = pageStyle;
    }

    public void destroy() {

        mInstance = null;
    }
}
