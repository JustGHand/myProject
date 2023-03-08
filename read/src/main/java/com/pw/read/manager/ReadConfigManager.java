package com.pw.read.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.pw.read.bean.LayoutMode;
import com.pw.read.bean.ReadConfigBean;
import com.pw.read.utils.Constant;
import com.pw.read.utils.FileSaveUtils;
import com.pw.baseutils.utils.NStringUtils;

public class ReadConfigManager {
    public static ReadConfigManager mInstance;

    public static ReadConfigManager getInstance() {
        if (mInstance == null) {
            synchronized (ReadConfigManager.class) {
                if (mInstance == null) {
                    mInstance = new ReadConfigManager();
                }
            }
        }
        return mInstance;
    }

    ReadConfigManager() {
        init();
    }

    Context mContext;
    ReadConfigBean mConfig;

    private void init() {
        readConfigFromFile();
    }

    public void updateContext(Context context) {
        mContext = context;
        readConfigFromFile();
    }

    public int getFontSize() {
        if (mConfig == null) {
            return Constant.DEFAULT_FONT_SIZE;
        }
        return mConfig.getFontSize();
    }

    public void setFontSize(int fontSize) {
        if (mConfig != null) {
            mConfig.setFontSize(fontSize);
        }
        saveConfigToFile();
    }

    public LayoutMode getLayoutMode() {
        int textInterval = getTextInterval();
        int pagePadding = getPagePadding();
        LayoutMode layoutMode = new LayoutMode(textInterval, textInterval + 10, textInterval + 10, (textInterval / 5) + 1, textInterval + 30, 0, pagePadding);
        return layoutMode;
    }

    public void saveLayoutMode(LayoutMode layoutMode) {
        if (layoutMode != null) {
            if (mConfig != null) {
                mConfig.setmTextInterval(layoutMode.getmTextInterval());
                mConfig.setmDrawLeftRightMargin(layoutMode.getmDrawLeftRightMargin());
            }
        }
        saveConfigToFile();
    }

    public int getTextInterval() {
        if (mConfig == null) {
            return Constant.DEFAULT_TEXT_INTERVAL;
        }
        return mConfig.getmTextInterval();
    }

    public void setTextInterval(int interval) {
        if (mConfig != null) {
            mConfig.setmTextInterval(interval);
        }
        saveConfigToFile();
    }

    public void setPagePadding(int pagePadding) {
        if (mConfig != null) {
            mConfig.setmDrawLeftRightMargin(pagePadding);
        }
        saveConfigToFile();
    }

    public int getPagePadding() {
        if (mConfig == null) {
            return Constant.DEFAULT_PAGE_PADDING;
        }
        return mConfig.getmDrawLeftRightMargin();
    }


    public void destroy() {
        mConfig = null;
        mContext = null;
        mInstance = null;
    }




    private void saveConfigToFile() {
        if (mContext != null && mConfig != null) {
            String configStr = new Gson().toJson(mConfig);
            FileSaveUtils.saveReadConfigStr(mContext, configStr);
        }
    }

    private void readConfigFromFile() {
        if (mContext == null) {
            mConfig = null;
            return;
        }
        String configStr = FileSaveUtils.getReadConfigStr(mContext);
        if (NStringUtils.isNotBlank(configStr)) {
            mConfig = new Gson().fromJson(configStr, ReadConfigBean.class);
        }else {
            mConfig = new ReadConfigBean();
        }
    }
}
