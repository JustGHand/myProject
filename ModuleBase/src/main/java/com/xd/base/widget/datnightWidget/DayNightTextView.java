package com.xd.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.xd.base.R;

public class DayNightTextView extends androidx.appcompat.widget.AppCompatTextView {
    public DayNightTextView(Context context) {
        super(context);
    }

    public DayNightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private boolean isNightMode;
    private ColorStateList dayModeTextColor;
    private ColorStateList nightModeTextColor;
    private Drawable dayModeBackground;
    private Drawable nightModeBackground;
    private Drawable nightDrawableLeft;
    private Drawable nightDrawableRight;
    private Drawable nightDrawableTop;
    private Drawable nightDrawableBottom;
    private Drawable dayDrawableLeft;
    private Drawable dayDrawableRight;
    private Drawable dayDrawableTop;
    private Drawable dayDrawableBottom;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightTextView);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightTextView_textNightmode, false);
        dayModeTextColor = getTextColors();
        dayModeBackground = getBackground();
        dayDrawableLeft = getCompoundDrawables()[0];
        dayDrawableTop = getCompoundDrawables()[1];
        dayDrawableRight = getCompoundDrawables()[2];
        dayDrawableBottom = getCompoundDrawables()[3];
        nightModeTextColor = typedArray.getColorStateList(R.styleable.DayNightTextView_nightTextColor);
        nightModeBackground = typedArray.getDrawable(R.styleable.DayNightTextView_nightBackground);
        nightDrawableLeft = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableLeft);
        nightDrawableRight = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableRight);
        nightDrawableTop = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableTop);
        nightDrawableBottom = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableBottom);
        toggleTextColor();
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
            Drawable[] drawables = getCompoundDrawables();
            if (nightDrawableLeft != null) {
                drawables[0] = nightDrawableLeft;
            }
            if (nightDrawableTop != null) {
                drawables[1] = nightDrawableTop;
            }
            if (nightDrawableRight != null) {
                drawables[2] = nightDrawableRight;
            }
            if (nightDrawableBottom != null) {
                drawables[3] = nightDrawableBottom;
            }
            setCompoundDrawablesWithIntrinsicBounds(drawables[0],drawables[1],drawables[2],drawables[3]);
        }else {
            setTextColor(dayModeTextColor);
            setBackground(dayModeBackground);

            setCompoundDrawablesWithIntrinsicBounds(dayDrawableLeft,dayDrawableTop,dayDrawableRight,dayDrawableBottom);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
