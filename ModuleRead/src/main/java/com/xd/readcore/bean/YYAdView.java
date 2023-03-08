package com.pw.readcore.bean;

import android.view.View;

import com.pw.base.ad.YYFrame;

import java.io.Serializable;

/**
 * Created by YYSky on 2019/3/20.
 */

public class YYAdView implements Serializable {

    YYAdView() {

    }

    private int mLineWidth;
    private int mLineHeight;
    private int mStartX;
    private int mStartY;
    private View mAdView;

    public YYAdView(View adView, int x, int y, int width, int height){
        mAdView = adView;
        mStartX = x;
        mStartY = y;
        mLineWidth = width;
        mLineHeight = height;
    }

    public YYAdView(YYFrame frame){
        mStartX = frame.getX();
        mStartY = frame.getY();
        mLineWidth = frame.getWidth();
        mLineHeight = frame.getHeight();
        mAdView = null;
    }

    public YYFrame getAdFrame(){
        YYFrame frame = new YYFrame();
        frame.setX(mStartX);
        frame.setY(mStartY);
        frame.setWidth(mLineWidth);
        frame.setHeight(mLineHeight);
        return frame;
    }

    public int getmLineWidth() {
        return mLineWidth;
    }

    public void setmLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public int getmLineHeight() {
        return mLineHeight;
    }

    public void setmLineHeight(int mLineHeight) {
        this.mLineHeight = mLineHeight;
    }

    public int getmStartX() {
        return mStartX;
    }

    public void setmStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public int getmStartY() {
        return mStartY;
    }

    public void setmStartY(int mStartY) {
        this.mStartY = mStartY;
    }

    public View getmAdView() {
        return mAdView;
    }

    public void setmAdView(View mAdView) {
        this.mAdView = mAdView;
    }

}
