package com.xd.base.ad;

import android.graphics.Rect;

import java.util.Objects;

/**
 * Created by YYSky on 2019/3/22.
 */

public class YYFrame {
    private int x;
    private int y;
    private int width;
    private int height;

    public static YYFrame YYFrameZero(){
        YYFrame frame = new YYFrame();
        return frame;
    }

    public YYFrame(){
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    public YYFrame(int ax,int ay,int awidth,int aheight){
        x = ax;
        y = ay;
        width = awidth;
        height = aheight;
    }

    public boolean isZeroFrame(){
        if (x == 0 && y == 0 && width == 0 && height == 0){
            return true;
        }
        return false;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getRect() {
        Rect rect = new Rect(x, y, x + width, y + height);
        return rect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YYFrame frame = (YYFrame) o;
        return x == frame.x && y == frame.y && width == frame.width && height == frame.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
