package com.pw.codeset.activity.read.read;

import androidx.annotation.ColorInt;

import com.google.gson.Gson;
import com.pw.codeset.databean.ReadConfigBean;
import com.pw.codeset.utils.SaveFileUtils;
import com.xd.baseutils.utils.NStringUtils;

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

    ReadConfigBean mConfig;

    ReadConfigManager() {
        readConfigFromFile();
    }

    public int getFontSize() {
        if (mConfig == null) {
            return ReadConfigBean.DEFAULT_FONTSIZE;
        }
        return mConfig.getFontSize();
    }

    public void setFontSize(int fontSize) {
        if (mConfig == null) {
            mConfig = new ReadConfigBean();
        }
        mConfig.setFontSize(fontSize);
        saveConfigToFile();
    }

    public int getPageMode() {
        if (mConfig == null) {
            return ReadConfigBean.PAGEMODE_SCROLL;
        }
        return mConfig.getPageMode();
    }

    public void setPageMode(int pageMode) {
        if (mConfig == null) {
            mConfig = new ReadConfigBean();
        }
        mConfig.setPageMode(pageMode);
        saveConfigToFile();
    }

    public int getFontColor() {
        if (mConfig == null) {
            return 0;
        }
        return mConfig.getFontColor();
    }

    public void setFontColor(int color) {
        if (mConfig == null) {
            mConfig = new ReadConfigBean();
        }
        mConfig.setFontColor(color);
        saveConfigToFile();
    }







    private void saveConfigToFile() {
        if (mConfig != null) {
            String configStr = new Gson().toJson(mConfig);
            SaveFileUtils.saveReadConfigStr(configStr);
        }
    }

    private void readConfigFromFile() {
        String configStr = SaveFileUtils.getReadConfigStr();
        if (NStringUtils.isNotBlank(configStr)) {
            mConfig = new Gson().fromJson(configStr, ReadConfigBean.class);
        }else {
            mConfig = new ReadConfigBean();
        }
    }

}
