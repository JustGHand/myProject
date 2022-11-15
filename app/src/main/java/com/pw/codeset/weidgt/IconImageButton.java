package com.pw.codeset.weidgt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class IconImageButton extends androidx.appcompat.widget.AppCompatButton {

    public IconImageButton(Context context) {
        super(context);
        initView(context);
    }

    public IconImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IconImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        this.setTypeface(typeface);
    }

}
