package com.pw.codeset.utils;

import android.graphics.drawable.Drawable;

import com.pw.codeset.R;
import com.pw.codeset.application.MyApp;

import java.util.Random;

public class ResourceUtils {

    public static float getDefaultPadding() {
        return getResDimen(R.dimen.view_defaultpadding);
    }
    public static float getDefaultMargin() {
        return getResDimen(R.dimen.view_defaultmargin);
    }

    public static int getRandomColorIndex(Integer exclude,int maxRandomTime) {
        if (exclude == null) {
            exclude = -1;
        }
        Random random = new Random();
        int randomColor = -1;
        randomColor =random.nextInt(5);
        if (randomColor == exclude && maxRandomTime > 0) {
            maxRandomTime--;
            randomColor = getRandomColorIndex(exclude,maxRandomTime);
        }

        return randomColor;
    }

    public static int[] getRandomTagColor(int colorIndex) {
        int[] result = new int[2];
        switch (colorIndex) {
            case 0:
                result[0] = ResourceUtils.getResColor(R.color.tag_back_color1);
                result[1] = ResourceUtils.getResColor(R.color.tag_text_color1);
                break;
            case 1:
                result[0] = ResourceUtils.getResColor(R.color.tag_back_color2);
                result[1] = ResourceUtils.getResColor(R.color.tag_text_color2);
                break;
            case 2:
                result[0] = ResourceUtils.getResColor(R.color.tag_back_color3);
                result[1] = ResourceUtils.getResColor(R.color.tag_text_color3);
                break;
            case 3:
                result[0] = ResourceUtils.getResColor(R.color.tag_back_color4);
                result[1] = ResourceUtils.getResColor(R.color.tag_text_color4);
                break;
            case 4:
                result[0] = ResourceUtils.getResColor(R.color.tag_back_color5);
                result[1] = ResourceUtils.getResColor(R.color.tag_text_color5);
                break;
        }
        return result;
    }



    public static String getResString(int stringId) {
        return MyApp.getInstance().getResources().getString(stringId);
    }
    public static String getFormatResString(int stringId,Object... formats) {
        return MyApp.getInstance().getResources().getString(stringId,formats);
    }

    public static String[] getStringArrays(int resId) {
        return MyApp.getInstance().getResources().getStringArray(resId);
    }

    public static int[] getIntArrays(int resId) {
        return MyApp.getInstance().getResources().getIntArray(resId);
    }

    public static float getResDimen(int dimenId) {
        return MyApp.getInstance().getResources().getDimensionPixelOffset(dimenId);
    }

    public static int getResColor(int colorId) {
        return MyApp.getInstance().getResources().getColor(colorId);
    }

    public static Drawable getResDrawable(int drawableId) {
        return MyApp.getInstance().getResources().getDrawable(drawableId).mutate();
    }

    public static Integer getResInteger(int intId) {
        return MyApp.getInstance().getResources().getInteger(intId);
    }
}
