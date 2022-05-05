package com.xd.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Target;

import com.bumptech.glide.Glide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class ImageUtils {

    public interface PaletteListener{
        void onComplete(int bodyColor,int textColor,int titleColor);

        void onError();
    }
    /**
     * 颜色加深算法
     */
    public static int setColorBurn(int rgb, float val) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        r = (int) Math.floor(r * (1f - val));
        g = (int) Math.floor(g * (1f - val));
        b = (int) Math.floor(b * (1f - val));
        return Color.rgb(r, g, b);
    }

    /**
     * 颜色浅化算法
     */
    public static int setColorShallow(int rgb, float val) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        r = (int) Math.floor(r * (1f + val));
        g = (int) Math.floor(g * (1f + val));
        b = (int) Math.floor(b * (1f + val));
        return Color.rgb(r, g, b);
    }
    /**
     * 主色调工具类
     */
        /**
         * 设置图片主色调
         *
         * @param bitmap
         * @return
         */
        public static void setPaletteColor(Bitmap bitmap, final View view,int type,PaletteListener listener) {//4最佳
            if (bitmap == null) {
                return;
            }
            Palette.from(bitmap)
                    .maximumColorCount(10).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@NonNull Palette palette) {
//                List<Palette.Swatch> list = palette.getSwatches();
//                int colorSize = 0;
//                Palette.Swatch maxSwatch = null;
//                for (int i = 0; i < list.size(); i++) {
//                    Palette.Swatch swatch = list.get(i);
//                    if (swatch != null) {
//                        int population = swatch.getPopulation();
//                        if (colorSize < population) {
//                            colorSize = population;
//                            maxSwatch = swatch;
//                        }
//                    }
//                }
//                    Palette.Swatch s1 = palette.getVibrantSwatch();       //获取到充满活力的这种色调
//                    Palette.Swatch s2 = palette.getDarkVibrantSwatch();    //获取充满活力的黑
//                    Palette.Swatch s3 = palette.getLightVibrantSwatch();   //获取充满活力的亮
//                    Palette.Swatch s4 = palette.getMutedSwatch();           //获取柔和的色调
//                    Palette.Swatch s5 = palette.getDarkMutedSwatch();      //获取柔和的黑
//                    Palette.Swatch s6 = palette.getLightMutedSwatch();    //获取柔和的亮
                        if(palette==null)return;
                        Palette.Swatch swatch = getMostPopulousSwatch(palette);
                        int lightness = isDark(palette);
                        boolean isDark;
                        //判断是否是黑色主题（其实Demo中用不到，因为没做主题切换）
                        if (lightness == LIGHTNESS_UNKNOWN) {
                            isDark = isDark(bitmap, bitmap.getWidth() / 2, 0);
                        } else {
                            isDark = lightness == IS_DARK;
                        }
                        int mainColor = Color.TRANSPARENT;
                        if (swatch != null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                            mainColor = scrimify(swatch.getRgb(), isDark, SCRIM_ADJUSTMENT);
                        }
                        listener.onComplete(mainColor, swatch.getBodyTextColor(),swatch.getTitleTextColor());
                        //palette取色不一定取得到某些特定的颜色，这里通过取多种颜色来避免取不到颜色的情况
//                        if (palette.getDarkVibrantColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
//                            listener.onComplete(palette.getDarkVibrantColor(Color.TRANSPARENT), palette.getVibrantColor(Color.TRANSPARENT),s.getTitleTextColor());
//                        } else if (palette.getDarkMutedColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
//                            listener.onComplete(palette.getDarkMutedColor(Color.TRANSPARENT), palette.getMutedColor(Color.TRANSPARENT),s.getTitleTextColor());
//                        } else {
//                            listener.onComplete(palette.getLightMutedColor(Color.TRANSPARENT), palette.getLightVibrantColor(Color.TRANSPARENT),s.getTitleTextColor());
//                        }
//                        view.setBackgroundColor(s.getRgb());
//                        listener.onComplete(palette.getDarkMutedSwatch().getRgb(),palette.getLightMutedSwatch().getRgb(),s.getTitleTextColor());

                }
            });

        }

    private static final float SCRIM_ADJUSTMENT = 0.075f;
    public static final int IS_LIGHT = 0;
    public static final int IS_DARK = 1;
    public static final int LIGHTNESS_UNKNOWN = 2;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IS_LIGHT, IS_DARK, LIGHTNESS_UNKNOWN})
    public @interface Lightness {
    }


    /**
     * Check that the lightness value (0–1)
     */
    public static boolean isDark(float[] hsl) { // @Size(3)
        return hsl[2] < 0.5f;
    }

    /**
     * Convert to HSL & check that the lightness value
     */
    public static boolean isDark(@ColorInt int color) {
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        return isDark(hsl);
    }

    public static @Lightness int isDark(Palette palette) {
        Palette.Swatch mostPopulous = getMostPopulousSwatch(palette);
        if (mostPopulous == null) return LIGHTNESS_UNKNOWN;
        return isDark(mostPopulous.getHsl()) ? IS_DARK : IS_LIGHT;
    }
    public static boolean isDark(@NonNull Bitmap bitmap, int backupPixelX, int backupPixelY) {
        // first try palette with a small color quant size
        Palette palette = Palette.from(bitmap).maximumColorCount(3).generate();
        if (palette != null && palette.getSwatches().size() > 0) {
            return isDark(palette) == IS_DARK;
        } else {
            // if palette failed, then check the color of the specified pixel
            return isDark(bitmap.getPixel(backupPixelX, backupPixelY));
        }
    }
    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

    public static float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }

    public static @ColorInt
    int scrimify(@ColorInt int color,
                 boolean isDark,
                 @FloatRange(from = 0f, to = 1f) float lightnessMultiplier) {
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);

        if (!isDark) {
            lightnessMultiplier += 1f;
        } else {
            lightnessMultiplier = 1f - lightnessMultiplier;
        }


        hsl[2] = constrain(0f, 1f, hsl[2] * lightnessMultiplier);
        return ColorUtils.HSLToColor(hsl);
    }

    public static Drawable getDrawable(Context context,WeakReference<Drawable> weak, int res){
        Drawable drawable = null;
        if (weak == null || weak.get() == null){
            drawable = ContextCompat.getDrawable(context,res);
            weak = new WeakReference<Drawable>(drawable);
        }
        else {
            drawable = weak.get();
        }
        return  drawable;
    }

    public static void loadImageToView(Context context, ImageView tarView, String url) {
        Glide.with(context).load(url).into(tarView);
    }
}
