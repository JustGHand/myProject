package com.pw.base.widget.stylewidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.pw.base.R;

public class StyleImageView extends AppCompatImageView {
    public StyleImageView(@NonNull Context context) {
        super(context);
    }

    public StyleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public StyleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private @StyleRes int styleId;

    private Drawable styleDrawable;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.ydxReadStyle);
        styleDrawable = typedArray.getDrawable(R.styleable.ydxReadStyle_backicon);
        updateTextStyle();
    }


    private void updateTextStyle() {
        if (styleDrawable != null) {
            setImageDrawable(styleDrawable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        updateTextStyle();
        super.onDraw(canvas);
    }
}
