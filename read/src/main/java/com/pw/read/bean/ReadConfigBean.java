package com.pw.read.bean;

import com.pw.read.utils.Constant;

public class ReadConfigBean {

    private int fontSize;
    private int mTextInterval;
    private int mTextPara;
    private int mTitleInterval;
    private int mTextMargin;
    private int mTitlePara;
    private int mDrawTopBottomMargin;
    private int mDrawLeftRightMargin;

    public ReadConfigBean() {
        fontSize = Constant.DEFAULT_FONT_SIZE;
        mTextInterval = Constant.DEFAULT_TEXT_INTERVAL;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getmTextInterval() {
        return mTextInterval;
    }

    public void setmTextInterval(int mTextInterval) {
        this.mTextInterval = mTextInterval;
    }

    public int getmTextPara() {
        return mTextPara;
    }

    public void setmTextPara(int mTextPara) {
        this.mTextPara = mTextPara;
    }

    public int getmTitleInterval() {
        return mTitleInterval;
    }

    public void setmTitleInterval(int mTitleInterval) {
        this.mTitleInterval = mTitleInterval;
    }

    public int getmTextMargin() {
        return mTextMargin;
    }

    public void setmTextMargin(int mTextMargin) {
        this.mTextMargin = mTextMargin;
    }

    public int getmTitlePara() {
        return mTitlePara;
    }

    public void setmTitlePara(int mTitlePara) {
        this.mTitlePara = mTitlePara;
    }

    public int getmDrawTopBottomMargin() {
        return mDrawTopBottomMargin;
    }

    public void setmDrawTopBottomMargin(int mDrawTopBottomMargin) {
        this.mDrawTopBottomMargin = mDrawTopBottomMargin;
    }

    public int getmDrawLeftRightMargin() {
        return mDrawLeftRightMargin;
    }

    public void setmDrawLeftRightMargin(int mDrawLeftRightMargin) {
        this.mDrawLeftRightMargin = mDrawLeftRightMargin;
    }
}
