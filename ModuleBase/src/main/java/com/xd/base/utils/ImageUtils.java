package com.xd.base.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import java.lang.ref.WeakReference;

public class ImageUtils {


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
