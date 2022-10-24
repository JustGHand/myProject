package com.pw.read.bean;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by newbiechen on 2018/2/5.
 * 作用：页面的展示风格。
 */

public class PageStyle implements Serializable {
//    BG_0(R.color.nb_read_font_1, R.color.nb_read_bg_1,0),
//    BG_1(R.color.nb_read_font_2, R.color.nb_read_bg_2,0),
//    BG_2(R.color.theme_leather_font,R.color.nb_read_bg_leather, R.drawable.theme_leather_bg),
//    BG_3(R.color.nb_read_font_3, R.color.nb_read_bg_3,0),
//    BG_4(R.color.nb_read_font_4, R.color.nb_read_bg_4,0),
//    BG_5(R.color.nb_read_font_5, R.color.nb_read_bg_5,0),
//    NIGHT(R.color.nb_read_font_night, R.color.nb_read_bg_night,0),;

    private int fontColor;
    private int bgColor;
    private int bgDrawable;
    private int highlightColor;
    private int highlightTextColor;

    PageStyle() {

    }

    public PageStyle(@ColorRes int fontColor, @ColorRes int bgColor, @ColorRes int highlightColor, @ColorRes int highlightTextColor, @DrawableRes int bgDrawable) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
        this.highlightColor = highlightColor;
        this.bgDrawable = bgDrawable;
        this.highlightTextColor = highlightTextColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public int getHighlightColor() {
        return highlightColor;
    }

    public int getHighlightTextColor() {
        return highlightTextColor;
    }
}
