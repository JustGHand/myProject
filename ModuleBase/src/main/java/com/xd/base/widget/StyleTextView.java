package com.pw.base.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.pw.base.R;

public class StyleTextView extends androidx.appcompat.widget.AppCompatTextView {
    public StyleTextView(Context context) {
        super(context);
    }

    public StyleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public StyleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private boolean isNightMode;
    private ColorStateList dayModeTextColor;
    private ColorStateList nightModeTextColor;
    private Drawable dayModeBackground;
    private Drawable nightModeBackground;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ydxReadStyle);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightTextView_textNightmode, false);
        dayModeTextColor = getTextColors();
        dayModeBackground = getBackground();
        nightModeTextColor = typedArray.getColorStateList(R.styleable.DayNightTextView_nightTextColor);
        nightModeBackground = typedArray.getDrawable(R.styleable.DayNightTextView_nightBackground);
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
        toggleTextColor();
    }

    private void toggleTextColor() {
        if (isNightMode) {
            if (nightModeTextColor != null) {
                setTextColor(nightModeTextColor);
            }

            if (nightModeBackground != null) {
                setBackground(nightModeBackground);
            }
        }else {
            setTextColor(dayModeTextColor);
            setBackground(dayModeBackground);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        toggleTextColor();
        super.onDraw(canvas);
    }
}
