package com.xd.readcore.bean;

/**
 * Created by YYSky on 2019/2/22.
 */

public class LayoutMode {
//    JincouMode("紧凑", 3),
//    ShushiMode("舒适", 2),
//    SongsanMode("松散", 1),
//    DefaultMode("默认", 0);

    private int mTextInterval;
    private int mTextPara;
    private int mTitleInterval;
    private int mTextMargin;
    private int mTitlePara;
    private int mDrawTopBottomMargin;
    private int mDrawLeftRightMargin;

    public LayoutMode(int textInterVal,int textPara,int titleInterval,int textMargin,int titlePara,int drawTopBottomMargin,int drawLeftRightMargin) {
        mTextInterval= textInterVal;
        mTextPara = textPara;
        mTitleInterval = titleInterval;
        mTextMargin = textMargin;
        mTitlePara = titlePara;
        mDrawTopBottomMargin = drawTopBottomMargin;
        mDrawLeftRightMargin = drawLeftRightMargin;
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
