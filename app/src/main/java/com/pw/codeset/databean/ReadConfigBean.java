package com.pw.codeset.databean;

import androidx.annotation.ColorInt;

public class ReadConfigBean {

    public static final int PAGEMODE_SCROLL = 1;
    public static final int DEFAULT_FONTSIZE = 14;

    Integer fontSize;
    Integer pageMode;
    int fontColor;

    public int getFontSize() {
        if (fontSize == null) {
            fontSize = DEFAULT_FONTSIZE;
        }
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getPageMode() {
        if (pageMode == null) {
            pageMode = PAGEMODE_SCROLL;
        }
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }
}
