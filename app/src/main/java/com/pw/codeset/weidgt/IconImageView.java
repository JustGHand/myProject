package com.pw.codeset.weidgt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class IconImageView extends androidx.appcompat.widget.AppCompatTextView {

    public IconImageView(Context context) {
        super(context);
        initView(context);
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        this.setTypeface(typeface);
    }

}
