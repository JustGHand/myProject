package com.xd.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.xd.base.R;

public class DayNightRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {
    public DayNightRadioButton(Context context) {
        super(context);
    }

    public DayNightRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightRadioButton);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightRadioButton_radioNightmode, false);
        dayModeTextColor = getTextColors();
        dayModeBackground = getBackground();
        dayDrawableLeft = getCompoundDrawables()[0];
        dayDrawableTop = getCompoundDrawables()[1];
        dayDrawableRight = getCompoundDrawables()[2];
        dayDrawableBottom = getCompoundDrawables()[3];
        nightModeTextColor = typedArray.getColorStateList(R.styleable.DayNightRadioButton_nightRadioColor);
        nightModeBackground = typedArray.getDrawable(R.styleable.DayNightRadioButton_nightRadioBackground);
        nightDrawableLeft = typedArray.getDrawable(R.styleable.DayNightRadioButton_nightRadioDrawableLeft);
        nightDrawableRight = typedArray.getDrawable(R.styleable.DayNightRadioButton_nightRadioDrawableRight);
        nightDrawableTop = typedArray.getDrawable(R.styleable.DayNightRadioButton_nightRadioDrawableTop);
        nightDrawableBottom = typedArray.getDrawable(R.styleable.DayNightRadioButton_nightRadioDrawableBottom);
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